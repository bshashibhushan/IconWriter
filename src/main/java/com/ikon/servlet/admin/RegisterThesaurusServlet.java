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
import java.io.PrintWriter;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ikon.automation.AutomationException;
import com.ikon.bean.Repository;
import com.ikon.core.AccessDeniedException;
import com.ikon.core.Config;
import com.ikon.core.DatabaseException;
import com.ikon.core.ItemExistsException;
import com.ikon.core.LockException;
import com.ikon.core.MimeTypeConfig;
import com.ikon.core.PathNotFoundException;
import com.ikon.core.RepositoryException;
import com.ikon.extension.core.ExtensionException;
import com.ikon.kea.tree.KEATree;

/**
 * Register thesaurus servlet
 */
public class RegisterThesaurusServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static final String[][] breadcrumb = new String[][] {
		new String[] { "experimental.jsp", "Experimental" },
	};
	
	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
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
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		int level = (request.getParameter("level") != null && !request.getParameter("level").equals("")) ? Integer
				.parseInt(request.getParameter("level"))
				: 0;
		updateSessionManager(request);
		PrintWriter out = response.getWriter();
		response.setContentType(MimeTypeConfig.MIME_HTML);
		header(out, "Register thesaurus", breadcrumb);
		out.flush();

		if (!Config.KEA_THESAURUS_OWL_FILE.equals("")) {
			out.println("<b>Starting thesaurus creation, this could take some hours.</b><br>");
			out.println("<b>Don't close this window meanwhile openkm is creating thesaurus.</b><br>");
			out.println("It'll be displayed creation information while creating nodes until level "
					+ (level + 1) + ", please be patient because tree deep level could be big.<br><br>");
			out.flush();
			
			try {
				KEATree.generateTree(null, level, "/"+Repository.THESAURUS, new Vector<String>(), out);
			} catch (PathNotFoundException e) {
				sendErrorRedirect(request, response, e);
			} catch (ItemExistsException e) {
				sendErrorRedirect(request, response, e);
			} catch (AccessDeniedException e) {
				sendErrorRedirect(request, response, e);
			} catch (RepositoryException e) {
				sendErrorRedirect(request, response, e);
			} catch (DatabaseException e) {
				sendErrorRedirect(request, response, e);
			} catch (ExtensionException e) {
				sendErrorRedirect(request, response, e);
			} catch (AutomationException e) {
				sendErrorRedirect(request, response, e);
			} catch (LockException e) {
				sendErrorRedirect(request, response, e);
			}
			
			out.println("<br><b>Finished thesaurus creation.</b><br>");
		} else {
			out.println("<b>Error - there's no thesaurus file defined in openkm.cfg</b>");
		}
		
		footer(out);
		out.flush();
		out.close();
	}
}
