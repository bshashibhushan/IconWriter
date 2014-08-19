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

package com.ikon.servlet.admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ikon.core.Config;
import com.ikon.core.DatabaseException;
import com.ikon.util.WebUtils;

/**
 * Activity log servlet
 */
public class ExportActivityLogServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(ExportActivityLogServlet.class);
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		log.debug("doGet({}, {})", request, response);
		request.setCharacterEncoding("UTF-8");
		String action = WebUtils.getString(request, "action");
		String userId = request.getRemoteUser();
		updateSessionManager(request);
		
		try {
			if(action.equals("export")) {
				export(userId, request, response);
			} 
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		}
	}

	private void export(String userId, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException, IOException, DatabaseException {
		log.debug("export({}, {}, {})", new Object[] { userId, request, response });
		
		// Disable browser cache
		response.setHeader("Expires", "Sat, 6 May 1971 12:00:00 GMT");
		response.setHeader("Cache-Control", "max-age=0, must-revalidate");
		response.addHeader("Cache-Control", "post-check=0, pre-check=0");
		
		File folder = new File(Config.HOME_DIR + "/temp/export/");
		for(String fileName : folder.list()){
			response.setHeader("Content-disposition", "inline; filename=\"" + fileName +"\"");		
			response.setContentType("application/vnd.ms-excel; charset=UTF-8");
			int read=0;
			byte[] bytes = new byte[1024];
			OutputStream os = response.getOutputStream();
		 
			InputStream is = new FileInputStream(new File(Config.HOME_DIR + "/temp/export/" + fileName));
			while((read = is.read(bytes))!= -1){
				os.write(bytes, 0, read);
			}
			os.flush();
			os.close();
			is.close();
		}		
		
		log.debug("export: void");
	}
}
