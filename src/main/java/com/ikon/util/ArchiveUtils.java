/**
 *  openkm, Open Document Management System (http://www.openkm.com)
 *  Copyright (c) 2006-2013  Paco Avila & Josep Llort
 *
 *  No bytes were intentionally harmed during the development of this application.
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.ikon.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.compress.archivers.jar.JarArchiveEntry;
import org.apache.commons.compress.archivers.jar.JarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream.UnicodeExtraFieldPolicy;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArchiveUtils {
	private static Logger log = LoggerFactory.getLogger(ArchiveUtils.class);
			
	/**
	 * Recursively create ZIP archive from directory 
	 */
	public static void createZip(File path, String root, OutputStream os) throws IOException {
		log.debug("createZip({}, {}, {})", new Object[] { path, root, os });
		
		if (path.exists() && path.canRead()) {
			ZipArchiveOutputStream zaos =  new ZipArchiveOutputStream(os);
			zaos.setComment("Generated by openkm");
			zaos.setCreateUnicodeExtraFields(UnicodeExtraFieldPolicy.ALWAYS);
			zaos.setUseLanguageEncodingFlag(true);
			zaos.setFallbackToUTF8(true);
			zaos.setEncoding("UTF-8");
			
			// Prevents java.util.zip.ZipException: ZIP file must have at least one entry
			ZipArchiveEntry zae = new ZipArchiveEntry(root+"/");
			zaos.putArchiveEntry(zae);
			zaos.closeArchiveEntry();
			
			createZipHelper(path, zaos, root);
			
			zaos.flush();
			zaos.finish();
			zaos.close();
		} else {
			throw new IOException("Can't access "+path);
		}
		
		log.debug("createZip: void");
	}
	
	/**
	 * Recursively create ZIP archive from directory helper utility 
	 */
	private static void createZipHelper(File fs, ZipArchiveOutputStream zaos, String zePath) throws IOException {
		log.debug("createZipHelper({}, {}, {})", new Object[]{ fs, zaos, zePath });
		File[] files = fs.listFiles();
		
		for (int i=0; i<files.length; i++) {
			if (files[i].isDirectory()) {
				log.debug("DIRECTORY {}", files[i]);
				ZipArchiveEntry zae = new ZipArchiveEntry(zePath + "/" + files[i].getName() + "/");
				zaos.putArchiveEntry(zae);
				zaos.closeArchiveEntry();
				
				createZipHelper(files[i], zaos, zePath + "/" + files[i].getName());
			} else {
				log.debug("FILE {}", files[i]);
				ZipArchiveEntry zae = new ZipArchiveEntry(zePath + "/" + files[i].getName());
				zaos.putArchiveEntry(zae);
				FileInputStream fis = new FileInputStream(files[i]);
				IOUtils.copy(fis, zaos);
				fis.close();
				zaos.closeArchiveEntry();
			}
		}
		
		log.debug("createZipHelper: void");
	}
	
	/**
	 * Recursively create JAR archive from directory 
	 */
	public static void createJar(File path, String root, OutputStream os) throws IOException {
		log.debug("createJar({}, {}, {})", new Object[] { path, root, os });
		
		if (path.exists() && path.canRead()) {
			JarArchiveOutputStream jaos =  new JarArchiveOutputStream(os);
			jaos.setComment("Generated by openkm");
			jaos.setCreateUnicodeExtraFields(UnicodeExtraFieldPolicy.ALWAYS);
			jaos.setUseLanguageEncodingFlag(true);
			jaos.setFallbackToUTF8(true);
			jaos.setEncoding("UTF-8");
			
			// Prevents java.util.jar.JarException: JAR file must have at least one entry
			JarArchiveEntry jae = new JarArchiveEntry(root+"/");
			jaos.putArchiveEntry(jae);
			jaos.closeArchiveEntry();
			
			createJarHelper(path, jaos, root);
			
			jaos.flush();
			jaos.finish();
			jaos.close();
		} else {
			throw new IOException("Can't access "+path);
		}
		
		log.debug("createJar: void");
	}
	
	/**
	 * Recursively create JAR archive from directory helper utility 
	 */
	private static void createJarHelper(File fs, JarArchiveOutputStream jaos, String zePath) throws IOException {
		log.debug("createJarHelper({}, {}, {})", new Object[]{ fs, jaos, zePath });
		File[] files = fs.listFiles();
		
		for (int i=0; i<files.length; i++) {
			if (files[i].isDirectory()) {
				log.debug("DIRECTORY {}", files[i]);
				JarArchiveEntry jae = new JarArchiveEntry(zePath + "/" + files[i].getName() + "/");
				jaos.putArchiveEntry(jae);
				jaos.closeArchiveEntry();
				
				createJarHelper(files[i], jaos, zePath + "/" + files[i].getName());
			} else {
				log.debug("FILE {}", files[i]);
				JarArchiveEntry jae = new JarArchiveEntry(zePath + "/" + files[i].getName());
				jaos.putArchiveEntry(jae);
				FileInputStream fis = new FileInputStream(files[i]);
				IOUtils.copy(fis, jaos);
				fis.close();
				jaos.closeArchiveEntry();
			}
		}
		
		log.debug("createJarHelper: void");
	}
	
	/**
	 * Read file from ZIP
	 */
	public static byte[] readFileFromZip(ZipInputStream zis, String filename) throws IOException {
		ZipEntry zi = null;
		byte content[] = null;
		
		while ((zi = zis.getNextEntry()) != null) {
			if (filename.equals(zi.getName())) {
				IOUtils.toByteArray(zis);
				break;
			}
		}
		
		return content;
	}
	
	/**
	 * Read file from ZIP
	 */
	public static InputStream getInputStreamFromZip(ZipInputStream zis, String filename) throws IOException {
		ZipEntry zi = null;
		InputStream is = null;
		
		while ((zi = zis.getNextEntry()) != null) {
			if (filename.equals(zi.getName())) {
				is = zis;
				break;
			}
		}
		
		return is;
	}
}
