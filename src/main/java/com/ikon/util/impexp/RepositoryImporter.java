/**
 * openkm, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2013 Paco Avila & Josep Llort
 * 
 * No bytes were intentionally harmed during the development of this application.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.ikon.util.impexp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.ikon.automation.AutomationException;
import com.ikon.bean.Document;
import com.ikon.bean.Folder;
import com.ikon.bean.Mail;
import com.ikon.core.AccessDeniedException;
import com.ikon.core.Config;
import com.ikon.core.DatabaseException;
import com.ikon.core.FileSizeExceededException;
import com.ikon.core.ItemExistsException;
import com.ikon.core.LockException;
import com.ikon.core.PathNotFoundException;
import com.ikon.core.RepositoryException;
import com.ikon.core.UnsupportedMimeTypeException;
import com.ikon.core.UserQuotaExceededException;
import com.ikon.core.VersionException;
import com.ikon.core.VirusDetectedException;
import com.ikon.extension.core.ExtensionException;
import com.ikon.module.DocumentModule;
import com.ikon.module.FolderModule;
import com.ikon.module.MailModule;
import com.ikon.module.ModuleManager;
import com.ikon.spring.PrincipalUtils;
import com.ikon.util.FileLogger;
import com.ikon.util.MailUtils;
import com.ikon.util.impexp.metadata.DocumentMetadata;
import com.ikon.util.impexp.metadata.FolderMetadata;
import com.ikon.util.impexp.metadata.MailMetadata;
import com.ikon.util.impexp.metadata.MetadataAdapter;
import com.ikon.util.impexp.metadata.VersionMetadata;

public class RepositoryImporter {
	private static Logger log = LoggerFactory.getLogger(RepositoryImporter.class);
	private static final String BASE_NAME = RepositoryImporter.class.getSimpleName();
	
	private RepositoryImporter() {
	}
	
	/**
	 * Import documents from filesystem into document repository.
	 */
	public static ImpExpStats importDocuments(String token, File fs, String fldPath, String metadata, boolean history, boolean uuid,
			Writer out, InfoDecorator deco) throws PathNotFoundException, ItemExistsException, AccessDeniedException, RepositoryException,
			FileNotFoundException, IOException, DatabaseException, ExtensionException, AutomationException {
		log.debug("importDocuments({}, {}, {}, {}, {}, {}, {}, {})",
				new Object[] { token, fs, fldPath, metadata, history, uuid, out, deco });
		ImpExpStats stats;
		
		try {
			FileLogger.info(BASE_NAME, "Start repository import from ''{0}'' to ''{1}''", fs.getPath(), fldPath);
			
			if (fs.exists()) {
				stats = importDocumentsHelper(token, fs, fldPath, metadata, history, uuid, out, deco);
			} else {
				throw new FileNotFoundException(fs.getPath());
			}
			
			FileLogger.info(BASE_NAME, "Repository import finalized");
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			FileLogger.error(BASE_NAME, "PathNotFoundException ''{0}''", e.getMessage());
			throw e;
		} catch (AccessDeniedException e) {
			log.error(e.getMessage(), e);
			FileLogger.error(BASE_NAME, "AccessDeniedException ''{0}''", e.getMessage());
			throw e;
		} catch (FileNotFoundException e) {
			log.error(e.getMessage(), e);
			FileLogger.error(BASE_NAME, "FileNotFoundException ''{0}''", e.getMessage());
			throw e;
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			FileLogger.error(BASE_NAME, "RepositoryException ''{0}''", e.getMessage());
			throw e;
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			FileLogger.error(BASE_NAME, "IOException ''{0}''", e.getMessage());
			throw e;
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			FileLogger.error(BASE_NAME, "DatabaseException ''{0}''", e.getMessage());
			throw e;
		}
		
		log.debug("importDocuments: {}", stats);
		return stats;
	}
	
	/**
	 * Import documents from filesystem into document repository (recursive).
	 */
	private static ImpExpStats importDocumentsHelper(String token, File fs, String fldPath, String metadata, boolean history,
			boolean uuid, Writer out, InfoDecorator deco) throws FileNotFoundException, PathNotFoundException, AccessDeniedException,
			RepositoryException, IOException, DatabaseException, ExtensionException, AutomationException {
		log.debug("importDocumentsHelper({}, {}, {}, {}, {}, {}, {}, {})", new Object[] { token, fs, fldPath, metadata, history, uuid, out,
				deco });
		File[] files = fs.listFiles(new RepositoryImporter.NoVersionFilenameFilter());
		ImpExpStats stats = new ImpExpStats();
		FolderModule fm = ModuleManager.getFolderModule();
		MetadataAdapter ma = MetadataAdapter.getInstance(token);
		ma.setRestoreUuid(uuid);
		Gson gson = new Gson();
		
		for (int i = 0; i < files.length; i++) {
			String fileName = files[i].getName();
			
			if (!fileName.endsWith(Config.EXPORT_METADATA_EXT) && !fileName.endsWith(".xml")) {
				if (files[i].isDirectory()) {
					Folder fld = new Folder();
					boolean api = false;
					int importedFolder = 0;
					log.info("Directory: {}", files[i]);
					
					try {
						if (metadata.equals("JSON")) {
							// Read serialized folder metadata
							File jsFile = new File(files[i].getPath() + Config.EXPORT_METADATA_EXT);
							log.info("Folder Metadata: {}", jsFile.getPath());
							
							if (jsFile.exists() && jsFile.canRead()) {
								FileReader fr = new FileReader(jsFile);
								FolderMetadata fmd = gson.fromJson(fr, FolderMetadata.class);
								fr.close();
								
								// Apply metadata
								fld.setPath(fldPath + "/" + fileName);
								fmd.setPath(fld.getPath());
								ma.importWithMetadata(fmd);
								
								if (out != null) {
									out.write(deco.print(files[i].getPath(), files[i].length(), null));
									out.flush();
								}
							} else {
								log.warn("Unable to read metadata file: {}", jsFile.getPath());
								api = true;
							}
						} else if(metadata.equals("XML")){
							// Read serialized folder metadata
							File jsFile = new File(files[i].getPath() + ".xml");
							log.info("Folder Metadata: {}", jsFile.getPath());
							
							if (jsFile.exists() && jsFile.canRead()) {
								FileReader fr = new FileReader(jsFile);
								JAXBContext jaxbContext = JAXBContext.newInstance(FolderMetadata.class);
								 
								Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
								FolderMetadata fmd = (FolderMetadata) jaxbUnmarshaller.unmarshal(fr);
								fr.close();
								
								// Apply metadata
								fld.setPath(fldPath + "/" + fileName);
								fmd.setPath(fld.getPath());
								ma.importWithMetadata(fmd);
								
								if (out != null) {
									out.write(deco.print(files[i].getPath(), files[i].length(), null));
									out.flush();
								}
							} else {
								log.warn("Unable to read metadata file: {}", jsFile.getPath());
								api = true;
							}
						} else {
							api = true;
						}
						
						if (api) {
							fld.setPath(fldPath + "/" + fileName);
							fm.create(token, fld);
							FileLogger.info(BASE_NAME, "Created folder ''{0}''", fld.getPath());
							
							if (out != null) {
								out.write(deco.print(files[i].getPath(), files[i].length(), null));
								out.flush();
							}
						}
						
						importedFolder = 1;
					} catch (ItemExistsException e) {
						log.warn("ItemExistsException: {}", e.getMessage());
						
						if (out != null) {
							out.write(deco.print(files[i].getPath(), files[i].length(), "ItemExists"));
							out.flush();
						}
						
						stats.setOk(false);
						FileLogger.error(BASE_NAME, "ItemExistsException ''{0}''", fld.getPath());
					} catch (JsonParseException e) {
						log.warn("JsonParseException: {}", e.getMessage());
						
						if (out != null) {
							out.write(deco.print(files[i].getPath(), files[i].length(), "Json"));
							out.flush();
						}
						
						stats.setOk(false);
						FileLogger.error(BASE_NAME, "JsonParseException ''{0}''", fld.getPath());
					} catch (JAXBException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					ImpExpStats tmp = importDocumentsHelper(token, files[i], fld.getPath(), metadata, history, uuid, out, deco);
					
					// Stats
					stats.setOk(stats.isOk() && tmp.isOk());
					stats.setSize(stats.getSize() + tmp.getSize());
					stats.setMails(stats.getMails() + tmp.getMails());
					stats.setDocuments(stats.getDocuments() + tmp.getDocuments());
					stats.setFolders(stats.getFolders() + tmp.getFolders() + importedFolder);
				} else {
					log.info("File: {}", files[i]);
					
					if (fileName.endsWith(".eml")) {
						log.info("Mail: {}", files[i]);
						ImpExpStats tmp = importMail(token, fs, fldPath, fileName, files[i], metadata, out, deco);
						
						// Stats
						stats.setOk(stats.isOk() && tmp.isOk());
						stats.setSize(stats.getSize() + tmp.getSize());
						stats.setMails(stats.getMails() + tmp.getMails());
					} else {
						log.info("Document: {}", files[i]);
						ImpExpStats tmp = importDocument(token, fs, fldPath, fileName, files[i], metadata, history, out, deco);
						
						// Stats
						stats.setOk(stats.isOk() && tmp.isOk());
						stats.setSize(stats.getSize() + tmp.getSize());
						stats.setDocuments(stats.getDocuments() + tmp.getDocuments());
					}
				}
			}
		}
		
		log.debug("importDocumentsHelper: {}", stats);
		return stats;
	}
	
	/**
	 * Import document.
	 */
	private static ImpExpStats importDocument(String token, File fs, String fldPath, String fileName, File fDoc, String metadata,
			boolean history, Writer out, InfoDecorator deco) throws IOException, RepositoryException, DatabaseException,
			PathNotFoundException, AccessDeniedException, ExtensionException, AutomationException {
		FileInputStream fisContent = new FileInputStream(fDoc);
		MetadataAdapter ma = MetadataAdapter.getInstance(token);
		DocumentModule dm = ModuleManager.getDocumentModule();
		ImpExpStats stats = new ImpExpStats();
		int size = fisContent.available();
		Document doc = new Document();
		Gson gson = new Gson();
		boolean api = false;
		
		try {
			// Metadata
			if (!metadata.equals("none")) {
				boolean isJsonFile = metadata.equals("JSON");
				// Read serialized document metadata
				File jsFile = null;
				if(isJsonFile)
					jsFile = new File(fDoc.getPath() + Config.EXPORT_METADATA_EXT);
				else 
					jsFile = new File(fDoc.getPath() + ".xml");

				log.info("Document Metadata File: {}", jsFile.getPath());
				
				if (jsFile.exists() && jsFile.canRead()) {
					FileReader fr = new FileReader(jsFile);
					DocumentMetadata dmd = null;
					
					//for json file metadata
					if(isJsonFile)
						dmd = gson.fromJson(fr, DocumentMetadata.class);
					else {
						JAXBContext jaxbContext = JAXBContext.newInstance(DocumentMetadata.class);
						 
						Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
						dmd = (DocumentMetadata) jaxbUnmarshaller.unmarshal(fr);
					}
					 
					doc.setPath(fldPath + "/" + fileName);
					dmd.setPath(doc.getPath());
					IOUtils.closeQuietly(fr);
					log.info("Document Metadata: {}", dmd);
					
					if (history && containsHistoryFiles(fs, fileName)) {
						File[] vhFiles = fs.listFiles(new RepositoryImporter.VersionFilenameFilter(fileName));
						List<File> listFiles = Arrays.asList(vhFiles);
						Collections.sort(listFiles, FilenameVersionComparator.getInstance());
						boolean first = true;
						
						for (File vhf : vhFiles) {
							String vhfName = vhf.getName();
							int idx = vhfName.lastIndexOf('#', vhfName.length() - 2);
							String verName = vhfName.substring(idx + 2, vhfName.length() - 1);
							FileInputStream fis = new FileInputStream(vhf);
							File jsVerFile = new File(vhf.getPath() + Config.EXPORT_METADATA_EXT);
							log.info("Document Version Metadata File: {}", jsVerFile.getPath());
							
							if (jsVerFile.exists() && jsVerFile.canRead()) {
								FileReader verFr = new FileReader(jsVerFile);
								VersionMetadata vmd = gson.fromJson(verFr, VersionMetadata.class);
								IOUtils.closeQuietly(verFr);
								
								if (first) {
									dmd.setVersion(vmd);
									size = fis.available();
									ma.importWithMetadata(dmd, fis);
									first = false;
								} else {
									log.info("Document Version Metadata: {}", vmd);
									size = fis.available();
									ma.importWithMetadata(doc.getPath(), vmd, fis);
								}
							} else {
								log.warn("Unable to read metadata file: {}", jsVerFile.getPath());
							}
							
							IOUtils.closeQuietly(fis);
							FileLogger.info(BASE_NAME, "Created document ''{0}'' version ''{1}''", doc.getPath(), verName);
							log.info("Created document '{}' version '{}'", doc.getPath(), verName);
						}
					} else {
						// Apply metadata
						ma.importWithMetadata(dmd, fisContent);
						FileLogger.info(BASE_NAME, "Created document ''{0}''", doc.getPath());
						log.info("Created document '{}'", doc.getPath());
					}
				} else {
					log.warn("Unable to read metadata file: {}", jsFile.getPath());
					api = true;
				}
			} else {
				api = true;
			}
			
			if (api) {
				doc.setPath(fldPath + "/" + fileName);
				
				// Version history
				if (history) {
					File[] vhFiles = fs.listFiles(new RepositoryImporter.VersionFilenameFilter(fileName));
					List<File> listFiles = Arrays.asList(vhFiles);
					Collections.sort(listFiles, FilenameVersionComparator.getInstance());
					boolean first = true;
					
					for (File vhf : vhFiles) {
						String vhfName = vhf.getName();
						int idx = vhfName.lastIndexOf('#', vhfName.length() - 2);
						String verName = vhfName.substring(idx + 2, vhfName.length() - 1);
						FileInputStream fis = new FileInputStream(vhf);
						
						if (first) {
							dm.create(token, doc, fis);
							first = false;
						} else {
							dm.checkout(token, doc.getPath());
							dm.checkin(token, doc.getPath(), fis, "Imported from administration");
						}
						
						IOUtils.closeQuietly(fis);
						FileLogger.info(BASE_NAME, "Created document ''{0}'' version ''{1}''", doc.getPath(), verName);
						log.info("Created document '{}' version '{}'", doc.getPath(), verName);
					}
				} else {
					dm.create(token, doc, fisContent);
					FileLogger.info(BASE_NAME, "Created document ''{0}''", doc.getPath());
					log.info("Created document ''{}''", doc.getPath());
				}
			}
			
			if (out != null) {
				out.write(deco.print(fDoc.getPath(), fDoc.length(), null));
				out.flush();
			}
			
			// Stats
			stats.setSize(stats.getSize() + size);
			stats.setDocuments(stats.getDocuments() + 1);
		} catch (UnsupportedMimeTypeException e) {
			log.warn("UnsupportedMimeTypeException: {}", e.getMessage());
			
			if (out != null) {
				out.write(deco.print(fDoc.getPath(), fDoc.length(), "UnsupportedMimeType"));
				out.flush();
			}
			
			stats.setOk(false);
			FileLogger.error(BASE_NAME, "UnsupportedMimeTypeException ''{0}''", doc.getPath());
		} catch (FileSizeExceededException e) {
			log.warn("FileSizeExceededException: {}", e.getMessage());
			
			if (out != null) {
				out.write(deco.print(fDoc.getPath(), fDoc.length(), "FileSizeExceeded"));
				out.flush();
			}
			
			stats.setOk(false);
			FileLogger.error(BASE_NAME, "FileSizeExceededException ''{0}''", doc.getPath());
		} catch (UserQuotaExceededException e) {
			log.warn("UserQuotaExceededException: {}", e.getMessage());
			
			if (out != null) {
				out.write(deco.print(fDoc.getPath(), fDoc.length(), "UserQuotaExceeded"));
				out.flush();
			}
			
			stats.setOk(false);
			FileLogger.error(BASE_NAME, "UserQuotaExceededException ''{0}''", doc.getPath());
		} catch (VirusDetectedException e) {
			log.warn("VirusWarningException: {}", e.getMessage());
			
			if (out != null) {
				out.write(deco.print(fDoc.getPath(), fDoc.length(), "VirusWarningException"));
				out.flush();
			}
			
			stats.setOk(false);
			FileLogger.error(BASE_NAME, "VirusWarningException ''{0}''", doc.getPath());
		} catch (ItemExistsException e) {
			log.warn("ItemExistsException: {}", e.getMessage());
			
			if (out != null) {
				out.write(deco.print(fDoc.getPath(), fDoc.length(), "ItemExists"));
				out.flush();
			}
			
			stats.setOk(false);
			FileLogger.error(BASE_NAME, "ItemExistsException ''{0}''", doc.getPath());
		} catch (LockException e) {
			log.warn("LockException: {}", e.getMessage());
			
			if (out != null) {
				out.write(deco.print(fDoc.getPath(), fDoc.length(), "Lock"));
				out.flush();
			}
			
			stats.setOk(false);
			FileLogger.error(BASE_NAME, "LockException ''{0}''", doc.getPath());
		} catch (VersionException e) {
			log.warn("VersionException: {}", e.getMessage());
			
			if (out != null) {
				out.write(deco.print(fDoc.getPath(), fDoc.length(), "Version"));
				out.flush();
			}
			
			stats.setOk(false);
			FileLogger.error(BASE_NAME, "VersionException ''{0}''", doc.getPath());
		} catch (JsonParseException e) {
			log.warn("JsonParseException: {}", e.getMessage());
			
			if (out != null) {
				out.write(deco.print(fDoc.getPath(), fDoc.length(), "Json"));
				out.flush();
			}
			
			stats.setOk(false);
			FileLogger.error(BASE_NAME, "JsonParseException ''{0}''", doc.getPath());
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(fisContent);
		}
		
		return stats;
	}
	
	/**
	 * Import mail.
	 */
	private static ImpExpStats importMail(String token, File fs, String fldPath, String fileName, File fDoc, String metadata,
			Writer out, InfoDecorator deco) throws IOException, PathNotFoundException, AccessDeniedException,
			RepositoryException, DatabaseException, ExtensionException, AutomationException {
		FileInputStream fisContent = new FileInputStream(fDoc);
		MetadataAdapter ma = MetadataAdapter.getInstance(token);
		MailModule mm = ModuleManager.getMailModule();
		Properties props = System.getProperties();
        props.put("mail.host", "smtp.dummydomain.com");
        props.put("mail.transport.protocol", "smtp");
		ImpExpStats stats = new ImpExpStats();
		int size = fisContent.available();
		Mail mail = new Mail();
		Gson gson = new Gson();
		boolean api = false;
		
		try {
			// Metadata
			if (metadata.equals("JSON")) {
				// Read serialized document metadata
				File jsFile = new File(fDoc.getPath() + Config.EXPORT_METADATA_EXT);
				log.info("Document Metadata File: {}", jsFile.getPath());
				
				if (jsFile.exists() && jsFile.canRead()) {
					FileReader fr = new FileReader(jsFile);
					MailMetadata mmd = gson.fromJson(fr, MailMetadata.class);
					mail.setPath(fldPath + "/" + fileName);
					mmd.setPath(mail.getPath());
					IOUtils.closeQuietly(fr);
					log.info("Mail Metadata: {}", mmd);
					
					// Apply metadata
					ma.importWithMetadata(mmd);
					
					// Add attachments
					Session mailSession = Session.getDefaultInstance(props, null);
					MimeMessage msg = new MimeMessage(mailSession, fisContent);
					mail = MailUtils.messageToMail(msg);
					mail.setPath(fldPath + "/" + mmd.getName());
					MailUtils.addAttachments(null, mail, msg, PrincipalUtils.getUser());
					
					FileLogger.info(BASE_NAME, "Created document ''{0}''", mail.getPath());
					log.info("Created document '{}'", mail.getPath());
				} else {
					log.warn("Unable to read metadata file: {}", jsFile.getPath());
					api = true;
				}
			} else {
				api = true;
			}
			
			if (api) {
				Session mailSession = Session.getDefaultInstance(props, null);
				MimeMessage msg = new MimeMessage(mailSession, fisContent);
				mail = MailUtils.messageToMail(msg);
				mail.setPath(fldPath + "/" + fileName);
				mm.create(token, mail);
				MailUtils.addAttachments(null, mail, msg, PrincipalUtils.getUser());
				
				FileLogger.info(BASE_NAME, "Created mail ''{0}''", mail.getPath());
				log.info("Created mail ''{}''", mail.getPath());
			}
			
			if (out != null) {
				out.write(deco.print(fDoc.getPath(), fDoc.length(), null));
				out.flush();
			}
			
			// Stats
			stats.setSize(stats.getSize() + size);
			stats.setMails(stats.getMails() + 1);
		} catch (UnsupportedMimeTypeException e) {
			log.warn("UnsupportedMimeTypeException: {}", e.getMessage());
			
			if (out != null) {
				out.write(deco.print(fDoc.getPath(), fDoc.length(), "UnsupportedMimeType"));
				out.flush();
			}
			
			stats.setOk(false);
			FileLogger.error(BASE_NAME, "UnsupportedMimeTypeException ''{0}''", mail.getPath());
		} catch (FileSizeExceededException e) {
			log.warn("FileSizeExceededException: {}", e.getMessage());
			
			if (out != null) {
				out.write(deco.print(fDoc.getPath(), fDoc.length(), "FileSizeExceeded"));
				out.flush();
			}
			
			stats.setOk(false);
			FileLogger.error(BASE_NAME, "FileSizeExceededException ''{0}''", mail.getPath());
		} catch (UserQuotaExceededException e) {
			log.warn("UserQuotaExceededException: {}", e.getMessage());
			
			if (out != null) {
				out.write(deco.print(fDoc.getPath(), fDoc.length(), "UserQuotaExceeded"));
				out.flush();
			}
			
			stats.setOk(false);
			FileLogger.error(BASE_NAME, "UserQuotaExceededException ''{0}''", mail.getPath());
		} catch (VirusDetectedException e) {
			log.warn("VirusWarningException: {}", e.getMessage());
			
			if (out != null) {
				out.write(deco.print(fDoc.getPath(), fDoc.length(), "VirusWarningException"));
				out.flush();
			}
			
			stats.setOk(false);
			FileLogger.error(BASE_NAME, "VirusWarningException ''{0}''", mail.getPath());
		} catch (ItemExistsException e) {
			log.warn("ItemExistsException: {}", e.getMessage());
			
			if (out != null) {
				out.write(deco.print(fDoc.getPath(), fDoc.length(), "ItemExists"));
				out.flush();
			}
			
			stats.setOk(false);
			FileLogger.error(BASE_NAME, "ItemExistsException ''{0}''", mail.getPath());
		} catch (JsonParseException e) {
			log.warn("JsonParseException: {}", e.getMessage());
			
			if (out != null) {
				out.write(deco.print(fDoc.getPath(), fDoc.length(), "Json"));
				out.flush();
			}
			
			stats.setOk(false);
			FileLogger.error(BASE_NAME, "JsonParseException ''{0}''", mail.getPath());
		} catch (MessagingException e) {
			log.warn("MessagingException: {}", e.getMessage());
			
			if (out != null) {
				out.write(deco.print(fDoc.getPath(), fDoc.length(), "Json"));
				out.flush();
			}
			
			stats.setOk(false);
			FileLogger.error(BASE_NAME, "MessagingException ''{0}''", mail.getPath());
		} finally {
			IOUtils.closeQuietly(fisContent);
		}
		
		return stats;
	}
	
	/**
	 * Filter filename matching document versions.
	 */
	public static class VersionFilenameFilter implements FilenameFilter {
		private String fileName = null;
		
		public VersionFilenameFilter(String fileName) {
			this.fileName = fileName;
		}
		
		@Override
		public boolean accept(File dir, String name) {
			if (name.startsWith(fileName + "#") && name.endsWith("#")) {
				int idx = name.lastIndexOf('#', name.length() - 2);
				
				if (idx > 0 && idx < name.length()) {
					return name.charAt(idx + 1) == 'v';
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
	}
	
	/**
	 * Filter filename not matching document versions.
	 */
	public static class NoVersionFilenameFilter implements FilenameFilter {
		@Override
		public boolean accept(File dir, String name) {
			if (name.endsWith("#")) {
				int idx = name.lastIndexOf('#', name.length() - 2);
				
				if (idx > 0 && idx < name.length()) {
					return name.charAt(idx + 1) != 'v';
				} else {
					return true;
				}
			} else {
				return true;
			}
		}
	}
	
	private static boolean containsHistoryFiles(File fs, String fileName){
		File[] vhFiles = fs.listFiles(new RepositoryImporter.VersionFilenameFilter(fileName));
		List<File> listFiles = Arrays.asList(vhFiles);
		
		return listFiles.size()==0?false:true;
	}
}
