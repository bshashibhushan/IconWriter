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

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ikon.util.backup.BackupUtilityService;

/**
 * Language servlet
 */
public class BackupUtilServlet extends BaseServlet {
	private static final long serialVersionUID = -1207795712115903141L;

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		String method = request.getMethod();
		if (isAdmin(request)) {
			if (method.equals(METHOD_GET)) {
				doGet(request, response);
			} else if (method.equals(METHOD_POST)) {
				doPost(request, response);
			}
		}
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		BackupUtilityService backupUtilityService = new BackupUtilityService();
		ServletContext sc = getServletContext();
		String action = request.getParameter("action");
		
		if ("ftp".equals(action)) {
			String serverId = request.getParameter("serverId");
			String userName = request.getParameter("userName");
			String password = request.getParameter("password");
			try {
				backupUtilityService.ftpBackup(serverId, userName, password);
				
				} catch (Exception e) {
				request.setAttribute("errMessage", e.getMessage());
				sc.getRequestDispatcher("/admin/backup_utilities.jsp").forward(
						request, response);
			}
			request.setAttribute("successMessage", "Backup Taken to FTP Server");
			sc.getRequestDispatcher("/admin/success_message.jsp").forward(
					request, response);

		} else if ("storage".equals(action)) {
			String storagePath = request.getParameter("storagePath");
			backupUtilityService.backupStorage(storagePath);
			
			//backupUtilityService.backupdb("okmdb"," root", "root", storagePath+"\\backup.sql");
			request.setAttribute("successMessage", "Backup Taken to Storage Device");
			sc.getRequestDispatcher("/admin/success_message.jsp").forward(
					request, response);
		}

	}

}
