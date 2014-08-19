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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Iterator;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.ikon.bean.Document;
import com.ikon.bean.Folder;
import com.ikon.bean.Mail;
import com.ikon.bean.Version;
import com.ikon.core.AccessDeniedException;
import com.ikon.core.Config;
import com.ikon.core.DatabaseException;
import com.ikon.core.NoSuchGroupException;
import com.ikon.core.ParseException;
import com.ikon.core.PathNotFoundException;
import com.ikon.core.RepositoryException;
import com.ikon.module.DocumentModule;
import com.ikon.module.FolderModule;
import com.ikon.module.MailModule;
import com.ikon.module.ModuleManager;
import com.ikon.util.FileLogger;
import com.ikon.util.MailUtils;
import com.ikon.util.PathUtils;
import com.ikon.util.impexp.metadata.DocumentMetadata;
import com.ikon.util.impexp.metadata.FolderMetadata;
import com.ikon.util.impexp.metadata.MailMetadata;
import com.ikon.util.impexp.metadata.MetadataAdapter;
import com.ikon.util.impexp.metadata.VersionMetadata;

public class RepositoryExporter {
	private static Logger log = LoggerFactory.getLogger(RepositoryExporter.class);
	private static final String BASE_NAME = RepositoryExporter.class.getSimpleName();
	private static boolean firstTime = true;
	
	private RepositoryExporter() {
	}
	
	/**
	 * Performs a recursive repository content export with metadata
	 */
	public static ImpExpStats exportDocuments(String token, String fldPath, File fs, String metadata, boolean history,
			Writer out, InfoDecorator deco) throws PathNotFoundException, AccessDeniedException, RepositoryException,
			FileNotFoundException, IOException, DatabaseException, ParseException, NoSuchGroupException, MessagingException {
		log.debug("exportDocuments({}, {}, {}, {}, {}, {}, {})", new Object[] { token, fldPath, fs, metadata, history, out, deco });
		ImpExpStats stats;
		
		try {
			FileLogger.info(BASE_NAME, "Start repository export from ''{0}'' to ''{1}''", fldPath, fs.getPath());

			if (fs.exists()) {
				firstTime = true;
				stats = exportDocumentsHelper(token, fldPath, fs, metadata, history, out, deco);
			} else {
				throw new FileNotFoundException(fs.getPath());
			}
			
			FileLogger.info(BASE_NAME, "Repository export finalized");
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
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
			FileLogger.error(BASE_NAME, "ParseException ''{0}''", e.getMessage());
			throw e;
		} catch (NoSuchGroupException e) {
			log.error(e.getMessage(), e);
			FileLogger.error(BASE_NAME, "NoSuchGroupException ''{0}''", e.getMessage());
			throw e;
		} catch (MessagingException e) {
			log.error(e.getMessage(), e);
			FileLogger.error(BASE_NAME, "MessagingException ''{0}''", e.getMessage());
			throw e;
		}
		
		log.debug("exportDocuments: {}", stats);
		return stats;
	}
	
	/**
	 * Performs a recursive repository content export with metadata
	 */
	private static ImpExpStats exportDocumentsHelper(String token, String fldPath, File fs, String metadata,
			boolean history, Writer out, InfoDecorator deco) throws FileNotFoundException, PathNotFoundException,
			AccessDeniedException, RepositoryException, IOException, DatabaseException, ParseException, 
			NoSuchGroupException, MessagingException {
		log.debug("exportDocumentsHelper({}, {}, {}, {}, {}, {}, {})", new Object[] { token, fldPath, fs, metadata, 
				history, out, deco });
		ImpExpStats stats = new ImpExpStats();
		DocumentModule dm = ModuleManager.getDocumentModule();
		FolderModule fm = ModuleManager.getFolderModule();
		MailModule mm = ModuleManager.getMailModule();
		MetadataAdapter ma = MetadataAdapter.getInstance(token);
		Gson gson = new Gson();
		String path = null;
		File fsPath = null;
		
		if (firstTime) {
			path = fs.getPath();
			fsPath = new File(path);
			firstTime = false;
		} else {
			// Repository path needs to be "corrected" under Windoze
			path = fs.getPath() + File.separator + PathUtils.getName(fldPath).replace(':', '_');
			fsPath = new File(path);
			fsPath.mkdirs();
			FileLogger.info(BASE_NAME, "Created folder ''{0}''", fsPath.getPath());
			
			if (out != null) {
				out.write(deco.print(fldPath, 0, null));
				out.flush();
			}
		}
		
		for (Iterator<Mail> it = mm.getChildren(token, fldPath).iterator(); it.hasNext();) {
			Mail mailChild = it.next();
			path = fsPath.getPath() + File.separator + PathUtils.getName(mailChild.getPath()).replace(':', '_');
			ImpExpStats mailStats = exportMail(token, mailChild.getPath(), path + ".eml", metadata, out, deco);
			
			// Stats
			stats.setSize(stats.getSize() + mailStats.getSize());
			stats.setMails(stats.getMails() + mailStats.getMails());
		}
		
		for (Iterator<Document> it = dm.getChildren(token, fldPath).iterator(); it.hasNext();) {
			Document docChild = it.next();
			path = fsPath.getPath() + File.separator + PathUtils.getName(docChild.getPath()).replace(':', '_');
			ImpExpStats docStats = exportDocument(token, docChild.getPath(), path, metadata, history, out, deco);
			
			// Stats
			stats.setSize(stats.getSize() + docStats.getSize());
			stats.setDocuments(stats.getDocuments() + docStats.getDocuments());
		}
		
		for (Iterator<Folder> it = fm.getChildren(token, fldPath).iterator(); it.hasNext();) {
			Folder fldChild = it.next();
			ImpExpStats tmp = exportDocumentsHelper(token, fldChild.getPath(), fsPath, metadata, history, out, deco);
			path = fsPath.getPath() + File.separator + PathUtils.getName(fldChild.getPath()).replace(':', '_');
			
			// Metadata
			if (metadata.equals("JSON")) {
				FolderMetadata fmd = ma.getMetadata(fldChild);
				String json = gson.toJson(fmd);
				FileOutputStream fos = new FileOutputStream(path + Config.EXPORT_METADATA_EXT);
				IOUtils.write(json, fos);
				fos.close();
			} else if(metadata.equals("XML")){
				FileOutputStream fos = new FileOutputStream(path + ".xml");

				FolderMetadata fmd = ma.getMetadata(fldChild);
				JAXBContext jaxbContext;
				try {
					jaxbContext = JAXBContext.newInstance(FolderMetadata.class);
					Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
					 
					// output pretty printed
					jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);			 
					jaxbMarshaller.marshal(fmd, fos);
				} catch (JAXBException e) {
					log.error(e.getMessage(), e);
					FileLogger.error(BASE_NAME, "XMLException ''{0}''", e.getMessage());
				}				
			} 
			
			// Stats
			stats.setSize(stats.getSize() + tmp.getSize());
			stats.setDocuments(stats.getDocuments() + tmp.getDocuments());
			stats.setFolders(stats.getFolders() + tmp.getFolders() + 1);
			stats.setOk(stats.isOk() && tmp.isOk());
		}
		
		log.debug("exportDocumentsHelper: {}", stats);
		return stats;
	}
	
	/**
	 * Export mail from openkm repository to filesystem.
	 */
	public static ImpExpStats exportMail(String token, String mailPath, String destPath, String metadata,
			Writer out, InfoDecorator deco) throws PathNotFoundException, RepositoryException, DatabaseException,
			IOException, AccessDeniedException, ParseException, NoSuchGroupException, MessagingException {
		MailModule mm = ModuleManager.getMailModule();
		MetadataAdapter ma = MetadataAdapter.getInstance(token);
		Mail mailChild = mm.getProperties(token, mailPath);
		Gson gson = new Gson();
		ImpExpStats stats = new ImpExpStats();
		MimeMessage msg = MailUtils.create(token, mailChild);
		FileOutputStream fos = new FileOutputStream(destPath);
		msg.writeTo(fos);
		IOUtils.closeQuietly(fos);
		FileLogger.info(BASE_NAME, "Created document ''{0}''", mailChild.getPath());
		
		// Metadata
		if (metadata.equals("JSON")) {
			MailMetadata mmd = ma.getMetadata(mailChild);
			String json = gson.toJson(mmd);
			fos = new FileOutputStream(destPath + Config.EXPORT_METADATA_EXT);
			IOUtils.write(json, fos);
			IOUtils.closeQuietly(fos);
		} else if(metadata.equals("XML")){
			fos = new FileOutputStream(destPath + ".xml");

			MailMetadata mmd = ma.getMetadata(mailChild);
			JAXBContext jaxbContext;
			try {
				jaxbContext = JAXBContext.newInstance(MailMetadata.class);
				Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
				 
				// output pretty printed
				jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);			 
				jaxbMarshaller.marshal(mmd, fos);
			} catch (JAXBException e) {
				log.error(e.getMessage(), e);
				FileLogger.error(BASE_NAME, "XMLException ''{0}''", e.getMessage());
			}
		}
		
		if (out != null) {
			out.write(deco.print(mailChild.getPath(), mailChild.getSize(), null));
			out.flush();
		}
		
		// Stats
		stats.setSize(stats.getSize() + mailChild.getSize());
		stats.setMails(stats.getMails() + 1);
		
		return stats;
	}
	
	/**
	 * Export document from openkm repository to filesystem.
	 */
	public static ImpExpStats exportDocument(String token, String docPath, String destPath, String metadata,
			boolean history, Writer out, InfoDecorator deco) throws PathNotFoundException, RepositoryException,
			DatabaseException, IOException, AccessDeniedException, ParseException, NoSuchGroupException {
		DocumentModule dm = ModuleManager.getDocumentModule();
		MetadataAdapter ma = MetadataAdapter.getInstance(token);
		Document docChild = dm.getProperties(token, docPath);
		Gson gson = new Gson();
		ImpExpStats stats = new ImpExpStats();
		
		// Version history
		if (history) {
			// Create dummy document file
			new File(destPath).createNewFile();
			
			// Metadata
			if (metadata.equals("JSON")) {
				DocumentMetadata dmd = ma.getMetadata(docChild);
				String json = gson.toJson(dmd);
				FileOutputStream fos = new FileOutputStream(destPath + Config.EXPORT_METADATA_EXT);
				IOUtils.write(json, fos);
				IOUtils.closeQuietly(fos);
			} else if(metadata.equals("XML")){
				FileOutputStream fos = new FileOutputStream(destPath + ".xml");

				DocumentMetadata dmd = ma.getMetadata(docChild);
				JAXBContext jaxbContext;
				try {
					jaxbContext = JAXBContext.newInstance(DocumentMetadata.class);
					Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
					 
					// output pretty printed
					jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);			 
					jaxbMarshaller.marshal(dmd, fos);
				} catch (JAXBException e) {
					log.error(e.getMessage(), e);
					FileLogger.error(BASE_NAME, "XMLException ''{0}''", e.getMessage());
				}
			}
			
			for (Version ver : dm.getVersionHistory(token, docChild.getPath())) {
				String versionPath = destPath + "#v" + ver.getName() + "#";
				FileOutputStream vos = new FileOutputStream(versionPath);
				InputStream vis = dm.getContentByVersion(token, docChild.getPath(), ver.getName());
				IOUtils.copy(vis, vos);
				IOUtils.closeQuietly(vis);
				IOUtils.closeQuietly(vos);
				FileLogger.info(BASE_NAME, "Created document ''{0}'' version ''{1}''", docChild.getPath(), ver.getName());
				
				// Metadata
				if (metadata.equals("JSON")) {
					VersionMetadata vmd = ma.getMetadata(ver, docChild.getMimeType());
					String json = gson.toJson(vmd);
					vos = new FileOutputStream(versionPath + Config.EXPORT_METADATA_EXT);
					IOUtils.write(json, vos);
					IOUtils.closeQuietly(vos);
				} else if(metadata.equals("XML")){
					FileOutputStream fos = new FileOutputStream(destPath + ".xml");

					VersionMetadata vmd = ma.getMetadata(ver, docChild.getMimeType());
					JAXBContext jaxbContext;
					try {
						jaxbContext = JAXBContext.newInstance(VersionMetadata.class);
						Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
						 
						// output pretty printed
						jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);			 
						jaxbMarshaller.marshal(vmd, fos);
					} catch (JAXBException e) {
						log.error(e.getMessage(), e);
						FileLogger.error(BASE_NAME, "XMLException ''{0}''", e.getMessage());
					}
				}
			}
		} else {
			FileOutputStream fos = new FileOutputStream(destPath);
			InputStream is = dm.getContent(token, docChild.getPath(), false);
			IOUtils.copy(is, fos);
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(fos);
			FileLogger.info(BASE_NAME, "Created document ''{0}''", docChild.getPath());
			
			// Metadata
			if (metadata.equals("JSON")) {
				DocumentMetadata dmd = ma.getMetadata(docChild);
				String json = gson.toJson(dmd);
				fos = new FileOutputStream(destPath + Config.EXPORT_METADATA_EXT);
				IOUtils.write(json, fos);
				IOUtils.closeQuietly(fos);
			} else if(metadata.equals("XML")){
				fos = new FileOutputStream(destPath + ".xml");

				DocumentMetadata dmd = ma.getMetadata(docChild);
				JAXBContext jaxbContext;
				try {
					jaxbContext = JAXBContext.newInstance(DocumentMetadata.class);
					Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
					 
					// output pretty printed
					jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);			 
					jaxbMarshaller.marshal(dmd, fos);
				} catch (JAXBException e) {
					log.error(e.getMessage(), e);
					FileLogger.error(BASE_NAME, "XMLException ''{0}''", e.getMessage());
				}
			}
		}
		
		if (out != null) {
			out.write(deco.print(docChild.getPath(), docChild.getActualVersion().getSize(), null));
			out.flush();
		}
		
		// Stats
		stats.setSize(stats.getSize() + docChild.getActualVersion().getSize());
		stats.setDocuments(stats.getDocuments() + 1);
		
		return stats;
	}
}
