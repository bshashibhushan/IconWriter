/**
 *  openkm, Open Document Management System (http://www.openkm.com)
 *  Copyright (c) 2006-2011  Paco Avila & Josep Llort
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

package com.ikon.servlet.frontend;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ikon.api.OKMDocument;
import com.ikon.api.OKMRepository;
import com.ikon.core.AccessDeniedException;
import com.ikon.core.DatabaseException;
import com.ikon.core.PathNotFoundException;
import com.ikon.core.RepositoryException;
import com.ikon.dao.StapleGroupDAO;
import com.ikon.dao.bean.Staple;
import com.ikon.dao.bean.StapleGroup;
import com.ikon.extension.servlet.BaseServlet;
import com.ikon.util.ArchiveUtils;
import com.ikon.util.FileUtils;
import com.ikon.util.WebUtils;

/**
 * Staple Download Servlet
 */
public class StaplingDownloadServlet extends BaseServlet {
	private static Logger log = LoggerFactory.getLogger(StaplingDownloadServlet.class);
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */ 
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		request.setCharacterEncoding("UTF-8");
		String  sgName = WebUtils.getString(request, "sgName");
	    String groupName = sgName.substring(0, sgName.indexOf("?"))+"_"+sgName.charAt(sgName.length()-1);
		String id = ""+groupName.charAt(groupName.length()-1);
		int groupId = Integer.parseInt(id);
		File tmpZip = File.createTempFile("okm", ".zip");
		
		try {
			System.out.println("before zip"); 
			String archive=groupName+".zip";
			response.setHeader("Content-disposition", "attachment; filename=\""+archive+"\"");
			response.setContentType("application/zip");
			OutputStream out = response.getOutputStream();
			exportZip(groupId, out);
			out.flush();
			out.close();
		} catch (RepositoryException e) {
			log.warn(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "RepositoryException: "+e.getMessage());
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		} finally {
			FileUtils.deleteQuietly(tmpZip);
		}
	}
	
	/**
	 * Generate a zip file from a repository folder path   
	 */
	private void exportZip(int sgId, OutputStream os) throws RepositoryException, IOException, 
			DatabaseException {
		log.debug("exportZip({}, {})", sgId, os);
		File tmpDir = null;
		BufferedWriter bw = null;
		OKMDocument okmDoc = OKMDocument.getInstance();
		OKMRepository okmRepo = OKMRepository.getInstance();
		
		try {
			tmpDir = FileUtils.createTempDir();
			bw = new BufferedWriter(new FileWriter(new File(tmpDir, "summary.txt")));
			StapleGroup sg = StapleGroupDAO.findByPk(sgId);
			
			for (Staple s : sg.getStaples()) {
				String uuid = s.getUuid();
				String path = null;
				
				try {
					path = okmRepo.getNodePath(null, uuid);
					int idx = path.indexOf('/', 1);
					
					if (idx > 0) {
						String relPath = path.substring(idx);
						File expFile = new File(tmpDir, relPath);
						expFile.getParentFile().mkdirs();
						FileOutputStream fos = new FileOutputStream(expFile);
						
						if (okmDoc.isValid(null, path)) {
							InputStream is = okmDoc.getContent(null, path, false);
							IOUtils.copy(is, fos);
							is.close();
						}
						
						bw.write(path + " - OK\n");
						bw.flush();
					}
				} catch (PathNotFoundException e) {
					bw.write(path != null ? path : uuid + " - " + e.getMessage() + "\n");
					bw.flush();
				} catch (AccessDeniedException e) {
					bw.write(path != null ? path : uuid + " - " + e.getMessage() + "\n");
					bw.flush();
				}
			}
			
			// Zip files
			IOUtils.closeQuietly(bw);
			ArchiveUtils.createZip(tmpDir, "stapling", os);
		} catch (IOException e) {
			log.error("Error exporting zip", e);
			throw e;
		} finally {
			if (tmpDir != null) {
				try {
					org.apache.commons.io.FileUtils.deleteDirectory(tmpDir);
				} catch (IOException e) {
					log.error("Error deleting temporal directory", e);
					throw e;
				}
			}
		}
		
		log.debug("exportZip: void");
	}
}
