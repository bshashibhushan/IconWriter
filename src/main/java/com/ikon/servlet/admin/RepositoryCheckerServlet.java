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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ikon.api.OKMFolder;
import com.ikon.bean.ContentInfo;
import com.ikon.bean.Repository;
import com.ikon.core.Config;
import com.ikon.core.MimeTypeConfig;
import com.ikon.util.FormatUtil;
import com.ikon.util.UserActivity;
import com.ikon.util.WebUtils;
import com.ikon.util.impexp.DbRepositoryChecker;
import com.ikon.util.impexp.HTMLInfoDecorator;
import com.ikon.util.impexp.ImpExpStats;
import com.ikon.util.impexp.JcrRepositoryChecker;

/**
 * Repository checker servlet
 */
public class RepositoryCheckerServlet extends BaseServlet {
	private static Logger log = LoggerFactory.getLogger(RepositoryCheckerServlet.class);
	private static final long serialVersionUID = 1L;
	private static final String[][] breadcrumb = new String[][] {
		new String[] { "utilities.jsp", "Utilities" },
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
		log.debug("doGet({}, {})", request, response);
		String repoPath = WebUtils.getString(request, "repoPath", "/"+Repository.ROOT);
		boolean versions = WebUtils.getBoolean(request, "versions");
		updateSessionManager(request);
		PrintWriter out = response.getWriter();
		response.setContentType(MimeTypeConfig.MIME_HTML);
		header(out, "Repository checker", breadcrumb);
		out.flush();
		
		try {
			if (!repoPath.equals("")) {
				out.println("<ul>");
				
				// Calculate number of nodes
				out.println("<li>Calculate number of nodes</li>");
				out.flush();
				response.flushBuffer();
				log.debug("Calculate number of nodes");
				
				ContentInfo cInfo = OKMFolder.getInstance().getContentInfo(null, repoPath);
				
				out.println("<li>Documents: " + cInfo.getDocuments() + "</li>");
				out.println("<li>Folders: " + cInfo.getFolders() + "</li>");
				out.println("<li>Checking repository integrity</li>");
				out.flush();
				response.flushBuffer();
				log.debug("Checking repository integrity");
				
				long begin = System.currentTimeMillis();
				ImpExpStats stats = null;
				
				if (Config.REPOSITORY_NATIVE) {
					stats = DbRepositoryChecker.checkDocuments(null, repoPath, versions, out,
							new HTMLInfoDecorator((int) cInfo.getDocuments()));
				} else {
					stats = JcrRepositoryChecker.checkDocuments(null, repoPath, versions, out,
							new HTMLInfoDecorator((int) cInfo.getDocuments()));
				}
				
				long end = System.currentTimeMillis();
				
				// Finalized
				out.println("<li>Repository check completed!</li>");
				out.println("</ul>");
				out.flush();
				log.debug("Repository check completed!");
				
				out.println("<hr/>");
				out.println("<div class=\"ok\">Path: "+repoPath+"</div>");
				out.println("<div class=\"ok\">Versions: "+versions+"</div>");
				out.println("<br/>");
				out.println("<b>Documents:</b> "+stats.getDocuments()+"<br/>");
				out.println("<b>Folders:</b> "+stats.getFolders()+"<br/>");
				out.println("<b>Size:</b> "+FormatUtil.formatSize(stats.getSize())+"<br/>");
				out.println("<b>Time:</b> "+FormatUtil.formatSeconds(end - begin)+"<br/>");
				
				// Activity log
				UserActivity.log(request.getRemoteUser(), "ADMIN_REPOSITORY_CHECKER", null, null,
						"Documents: " + stats.getDocuments() +
						", Folders: " + stats.getFolders() +
						", Size: " + FormatUtil.formatSize(stats.getSize()) +
						", Time: " + FormatUtil.formatSeconds(end - begin));
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			footer(out);
			out.flush();
			out.close();
		}
	}
}
