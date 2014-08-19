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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.artofsolving.jodconverter.office.OfficeUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.ikon.bean.ConfigStoredOption;
import com.ikon.bean.ConfigStoredSelect;
import com.ikon.core.DatabaseException;
import com.ikon.core.MimeTypeConfig;
import com.ikon.dao.ConfigDAO;
import com.ikon.dao.HibernateUtil;
import com.ikon.dao.bean.Config;
import com.ikon.servlet.admin.DatabaseQueryServlet.WorkerUpdate;
import com.ikon.util.UserActivity;
import com.ikon.util.WarUtils;
import com.ikon.util.WebUtils;

/**
 * Execute config servlet
 */
public class ConfigServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(ConfigServlet.class);
	private static Map<String, String> types = new LinkedHashMap<String, String>();
	private static final String[][] breadcrumb = new String[][] {
		new String[] { "Config", "Configuration" },
	};
	
	static {
		types.put(Config.STRING, "String");
		types.put(Config.TEXT, "Text");
		types.put(Config.BOOLEAN, "Boolean");
		types.put(Config.INTEGER, "Integer");
		types.put(Config.LONG, "Long");
		types.put(Config.SELECT, "Select");
		types.put(Config.LIST, "List");
	}
	
	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		String method = request.getMethod();
		
		if (method.equals(METHOD_GET)) {
			doGet(request, response);
		} else if (method.equals(METHOD_POST)) {
			doPost(request, response);
		}
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		log.debug("doGet({}, {})", request, response);
		request.setCharacterEncoding("UTF-8");
		String action = WebUtils.getString(request, "action");
		String filter = WebUtils.getString(request, "filter");
		String userId = request.getRemoteUser();
		updateSessionManager(request);
		
		try {
			if (action.equals("create")) {
				create(userId, types, request, response);
			} else if (action.equals("edit")) {
				edit(userId, types, request, response);
			} else if (action.equals("delete")) {
				delete(userId, types, request, response);
			} else if (action.equals("check")) {
				check(userId, request, response);
			} else if (action.equals("export")) {
				export(userId, request, response);
			} else {
				list(userId, filter, request, response);
			}
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		log.debug("doPost({}, {})", request, response);
		request.setCharacterEncoding("UTF-8");
		ServletContext sc = getServletContext();
		String action = null;
		String filter = null;
		String userId = request.getRemoteUser();
		Session dbSession = null;
		updateSessionManager(request);
		
		try {
			if (ServletFileUpload.isMultipartContent(request)) {
				InputStream is = null;
				FileItemFactory factory = new DiskFileItemFactory(); 
				ServletFileUpload upload = new ServletFileUpload(factory);
				List<FileItem> items = upload.parseRequest(request);
				Config cfg = new Config();
				byte data[] = null;
				
				for (Iterator<FileItem> it = items.iterator(); it.hasNext();) {
					FileItem item = it.next();
					
					if (item.isFormField()) {
						if (item.getFieldName().equals("action")) {
							action = item.getString("UTF-8");
						} else if (item.getFieldName().equals("filter")) {
							filter = item.getString("UTF-8");
						} else if (item.getFieldName().equals("cfg_key")) {
							cfg.setKey(item.getString("UTF-8"));
						} else if (item.getFieldName().equals("cfg_type")) {
							cfg.setType(item.getString("UTF-8"));
						} else if (item.getFieldName().equals("cfg_value")) {
							cfg.setValue(item.getString("UTF-8").trim());
						}
					} else {
						is = item.getInputStream();
						data = IOUtils.toByteArray(is);
						is.close();
					}
				}
			
				if (action.equals("create")) {
					if (Config.BOOLEAN.equals(cfg.getType())) {
						cfg.setValue(Boolean.toString(cfg.getValue() != null && !cfg.getValue().equals("")));
					} else if (Config.SELECT.equals(cfg.getType())) {
						ConfigStoredSelect stSelect = ConfigDAO.getSelect(cfg.getKey());
						
						if (stSelect != null) {
							for (ConfigStoredOption stOption : stSelect.getOptions()) {
								if (stOption.getValue().equals(cfg.getValue())) {
									stOption.setSelected(true);
								}
							}
						}
						
						cfg.setValue(new Gson().toJson(stSelect));
					}
					
					ConfigDAO.create(cfg);
					com.ikon.core.Config.reload(sc, new Properties());
					
					// Activity log
					UserActivity.log(userId, "ADMIN_CONFIG_CREATE", cfg.getKey(), null, cfg.toString());
					list(userId, filter, request, response);
				} else if (action.equals("edit")) {
					if (Config.BOOLEAN.equals(cfg.getType())) {
						cfg.setValue(Boolean.toString(cfg.getValue() != null && !cfg.getValue().equals("")));
					} else if (Config.SELECT.equals(cfg.getType())) {
						ConfigStoredSelect stSelect = ConfigDAO.getSelect(cfg.getKey());
						
						if (stSelect != null) {
							for (ConfigStoredOption stOption : stSelect.getOptions()) {
								if (stOption.getValue().equals(cfg.getValue())) {
									stOption.setSelected(true);
								} else {
									stOption.setSelected(false);
								}
							}
						}
						
						cfg.setValue(new Gson().toJson(stSelect));
					}
					
					ConfigDAO.update(cfg);
					com.ikon.core.Config.reload(sc, new Properties());
					
					// Activity log
					UserActivity.log(userId, "ADMIN_CONFIG_EDIT", cfg.getKey(), null, cfg.toString());
					list(userId, filter, request, response);
				} else if (action.equals("delete")) {
					ConfigDAO.delete(cfg.getKey());
					com.ikon.core.Config.reload(sc, new Properties());
					
					// Activity log
					UserActivity.log(userId, "ADMIN_CONFIG_DELETE", cfg.getKey(), null, null);
					list(userId, filter, request, response);
				} else if (action.equals("import")) {
					dbSession = HibernateUtil.getSessionFactory().openSession();
					importConfig(userId, request, response, data, dbSession);
					
					// Activity log
					UserActivity.log(request.getRemoteUser(), "ADMIN_CONFIG_IMPORT", null, null, null);
					list(userId, filter, request, response);
				}
			}
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		} catch (FileUploadException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} finally {
			HibernateUtil.close(dbSession);
		}
	}

	/**
	 * Create config
	 */
	private void create(String userId, Map<String, String> types, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException, DatabaseException {
		ServletContext sc = getServletContext();
		Config cfg = new Config();
		sc.setAttribute("action", WebUtils.getString(request, "action"));
		sc.setAttribute("filter", WebUtils.getString(request, "filter"));
		sc.setAttribute("persist", true);
		sc.setAttribute("types", types);
		sc.setAttribute("cfg", cfg);
		sc.getRequestDispatcher("/admin/config_edit.jsp").forward(request, response);
	}
	
	/**
	 * Edit config
	 */
	private void edit(String userId, Map<String, String> types, HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException, DatabaseException {
		ServletContext sc = getServletContext();
		String cfgKey = WebUtils.getString(request, "cfg_key");
		Config cfg = ConfigDAO.findByPk(cfgKey);
		sc.setAttribute("action", WebUtils.getString(request, "action"));
		sc.setAttribute("filter", WebUtils.getString(request, "filter"));
		sc.setAttribute("persist", true);
		sc.setAttribute("types", types);
		sc.setAttribute("cfg", cfg);
		sc.getRequestDispatcher("/admin/config_edit.jsp").forward(request, response);
	}

	/**
	 * Delete config
	 */
	private void delete(String userId, Map<String, String> types, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException, DatabaseException {
		ServletContext sc = getServletContext();
		String cfgKey = WebUtils.getString(request, "cfg_key");
		Config cfg = ConfigDAO.findByPk(cfgKey);
		sc.setAttribute("action", WebUtils.getString(request, "action"));
		sc.setAttribute("filter", WebUtils.getString(request, "filter"));
		sc.setAttribute("persist", true);
		sc.setAttribute("types", types);
		sc.setAttribute("cfg", cfg);
		sc.getRequestDispatcher("/admin/config_edit.jsp").forward(request, response);
	}

	/**
	 * List config
	 */
	private void list(String userId, String filter, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, DatabaseException {
		log.debug("list({}, {}, {}, {})", new Object[] { userId, filter, request, response });
		ServletContext sc = getServletContext();
		List<Config> list = ConfigDAO.findAll();
		
		for (Iterator<Config> it = list.iterator(); it.hasNext(); ) {
			Config cfg = it.next();
			
			if (Config.STRING.equals(cfg.getType())) {
				cfg.setType("String");
			} else if (Config.TEXT.equals(cfg.getType())) {
				cfg.setType("Text");
			} else if (Config.BOOLEAN.equals(cfg.getType())) {
				cfg.setType("Boolean");
			} else if (Config.INTEGER.equals(cfg.getType())) {
				cfg.setType("Integer");
			} else if (Config.LONG.equals(cfg.getType())) {
				cfg.setType("Long");
			} else if (Config.LIST.equals(cfg.getType())) {
				cfg.setType("List");
			} else if (Config.SELECT.equals(cfg.getType())) {
				cfg.setType("Select");
				ConfigStoredSelect stSelect = new Gson().fromJson(cfg.getValue(), ConfigStoredSelect.class);
				
				for (ConfigStoredOption stOption : stSelect.getOptions()) {
					if (stOption.isSelected()) {
						cfg.setValue(stOption.getValue());
					}
				}
			} else if (Config.HIDDEN.equals(cfg.getType())) {
				it.remove();
			}
			
			if (!Config.HIDDEN.equals(cfg.getType()) && !cfg.getKey().contains(filter)) {
				it.remove();
			}
		}
		
		sc.setAttribute("configs", list);
		sc.setAttribute("filter", filter);
		sc.getRequestDispatcher("/admin/config_list.jsp").forward(request, response);
		log.debug("list: void");
	}
	
	/**
	 * Check configuration
	 */
	private void check(String userId, HttpServletRequest request, HttpServletResponse response) throws
			ServletException, IOException, DatabaseException {
		log.debug("check({}, {}, {})", new Object[] { userId, request, response });
		PrintWriter out = response.getWriter();
		response.setContentType(MimeTypeConfig.MIME_HTML);
		header(out, "Configuration check", breadcrumb);
		out.flush();
		
		try {
			out.println("<ul>");
			
			out.print("<li>");
			out.print("<b>" + com.ikon.core.Config.PROPERTY_SYSTEM_SWFTOOLS_PDF2SWF + "</b>");
			checkExecutable(out, com.ikon.core.Config.SYSTEM_SWFTOOLS_PDF2SWF);
			out.print("</li>");
			
			out.print("<li>");
			out.print("<b>" + com.ikon.core.Config.PROPERTY_SYSTEM_IMAGEMAGICK_CONVERT + "</b>");
			checkExecutable(out, com.ikon.core.Config.SYSTEM_IMAGEMAGICK_CONVERT);
			out.print("</li>");
			
			out.print("<li>");
			out.print("<b>" + com.ikon.core.Config.PROPERTY_SYSTEM_OCR + "</b>");
			checkExecutable(out, com.ikon.core.Config.SYSTEM_OCR);
			out.print("</li>");
			
			out.print("<li>");
			out.print("<b>" + com.ikon.core.Config.PROPERTY_SYSTEM_OPENOFFICE_PATH + "</b>");
			checkOpenOffice(out, com.ikon.core.Config.SYSTEM_OPENOFFICE_PATH);
			out.print("</li>");
			
			out.println("</ul>");
			out.flush();
		} catch (Exception e) {
			out.println("<div class=\"warn\">Exception: "+e.getMessage()+"</div>");
			out.flush();
		} finally {
			footer(out);
			out.flush();
			out.close();
		}
		
		log.debug("check: void");
	}
	
	/**
	 * File existence and if can be executed
	 */
	private void checkExecutable(PrintWriter out, String cmd) {
		if (cmd.equals("")) {
			warn(out, "Not configured");
		} else {
			int idx = cmd.indexOf(" ");
			String exec = null;
			
			if (idx > -1) {
				exec = cmd.substring(0, idx);
			} else {
				exec = cmd;
			}
			
			File prg = new File(exec);
			
			if (prg.exists() && prg.canRead() && prg.canExecute()) {
				ok(out, "OK - " + prg.getPath());
			} else {
				warn(out, "Can't read or execute: " + prg.getPath());
			}
		}
	}
	
	/**
	 * File existence and if can be executed
	 */
	private void checkOpenOffice(PrintWriter out, String path) {
		if (path.equals("")) {
			warn(out, "Not configured");
		} else {
			File prg = new File(path);
			
			if (prg.exists() && prg.canRead()) {
				File offExec = OfficeUtils.getOfficeExecutable(prg);
				
				if (offExec.exists() && offExec.canRead() && offExec.canExecute()) {
					ok(out, "OK - " + offExec.getPath());
				} else {
					warn(out, "Can't read or execute: " + offExec.getPath());
				}
			} else {
				warn(out, "Can't read: " + prg.getPath());
			}
		}
	}
	
	/**
	 * Export configuration
	 */
	private void export(String userId, HttpServletRequest request, HttpServletResponse response) throws
			DatabaseException, IOException {
		log.debug("export({}, {}, {})", new Object[] { userId, request, response });
		
		// Disable browser cache
		response.setHeader("Expires", "Sat, 6 May 1971 12:00:00 GMT");
		response.setHeader("Cache-Control", "max-age=0, must-revalidate");
		response.addHeader("Cache-Control", "post-check=0, pre-check=0");
		String fileName = "ikon_" + WarUtils.getAppVersion().getVersion() + "_cfg.sql";
		
		response.setHeader("Content-disposition", "inline; filename=\""+fileName+"\"");		
		response.setContentType("text/x-sql; charset=UTF-8");
		PrintWriter out = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), "UTF8"), true);
		
		for (Config cfg : ConfigDAO.findAll()) {
			if (!Config.HIDDEN.equals(cfg.getType())) {
				out.println("DELETE FROM ikon_CONFIG WHERE CFG_KEY='" + cfg.getKey() + "';");
			}
		}
		
		for (Config cfg : ConfigDAO.findAll()) {
			if (!Config.HIDDEN.equals(cfg.getType())) {
				StringBuffer insertCfg = new StringBuffer("INSERT INTO ikon_CONFIG (CFG_KEY, CFG_TYPE, CFG_VALUE) VALUES ('");
				insertCfg.append(cfg.getKey()).append("', '");
				insertCfg.append(cfg.getType()).append("', '");
				
				if (cfg.getValue() ==  null || cfg.getValue().equals("null")) {
					insertCfg.append("").append("');");
				} else {
					insertCfg.append(cfg.getValue()).append("');");
				}
				
				out.println(insertCfg);
			}
		}
		
		out.flush();
		log.debug("export: void");
	}
	
	/**
	 * Import configuration into database
	 */
	private void importConfig(String userId, HttpServletRequest request, HttpServletResponse response,
			final byte[] data, Session dbSession) throws DatabaseException,
			IOException, SQLException {
		log.debug("importConfig({}, {}, {}, {}, {})", new Object[] { userId, request, response, data, dbSession });
		WorkerUpdate worker = new DatabaseQueryServlet().new WorkerUpdate();
		worker.setData(data);
		dbSession.doWork(worker);
		log.debug("importConfig: void");
	}
}