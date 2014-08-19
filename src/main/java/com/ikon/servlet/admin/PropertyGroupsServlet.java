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
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import javax.jcr.LoginException;
import javax.jcr.Session;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.jackrabbit.core.nodetype.InvalidNodeTypeDefException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ikon.api.OKMPropertyGroup;
import com.ikon.bean.PropertyGroup;
import com.ikon.bean.form.FormElement;
import com.ikon.core.Config;
import com.ikon.core.DatabaseException;
import com.ikon.core.ParseException;
import com.ikon.core.RepositoryException;
import com.ikon.module.db.DbRepositoryModule;
import com.ikon.module.jcr.JcrRepositoryModule;
import com.ikon.util.XMLUtils;
import com.ikon.util.FormUtils;
import com.ikon.util.UserActivity;
import com.ikon.util.WebUtils;

/**
 * Property groups servlet
 * 
 * @author Paco Avila
 */
public class PropertyGroupsServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(PropertyGroupsServlet.class);
	private static final File PROPERTY_GROUPS_XML = new File(Config.HOME_DIR + "/" + "PropertyGroups.xml");
	
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
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		log.debug("doGet({}, {})", request, response);
		request.setCharacterEncoding("UTF-8");
		String action = WebUtils.getString(request, "action");
		Session session = null;
		updateSessionManager(request);
		
		try {
			if(!action.equals(null)){
				writeToPropertyGroupsXML(request, response);
				register(session, request, response);
			}
				
			list(request, response);
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		log.debug("doGet({}, {})", request, response);
		request.setCharacterEncoding("UTF-8");
		String action = WebUtils.getString(request, "action");
		String pgLabel = WebUtils.getString(request, "label");
		String label = WebUtils.getString(request, "value");
		Session session = null;
		updateSessionManager(request);
		
		try {
			// session = JCRUtils.getSession();
			if(action.equals("validatepgName")){
				validatePropertyGroupName(label, request, response);
			} else if (action.equals("register")) {
				register(session, request, response);
			} else if (action.equals("edit")) {
				edit(pgLabel, request, response);
			} else if(action.equals("delete")){
				XMLUtils xmlUtils = new XMLUtils(PROPERTY_GROUPS_XML);				
				xmlUtils.deletePropertyGroup(pgLabel);
				list(request, response);
			}
			
			if (action.equals("") || action.equals("register")) {
				list(request, response);
			}
		} catch (LoginException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} catch (org.apache.jackrabbit.core.nodetype.compact.ParseException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} catch (InvalidNodeTypeDefException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// JCRUtils.logout(session);
		}
	}
	
	/**
	 * Puts the parameterMap into the NavigableMap. The map is polled first twice to save action and label. Then the resulting
	 * array values is saved as a linkedHashMap to save order.
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	private void writeToPropertyGroupsXML(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		//get values from form and sort it using navigableSet
		NavigableMap<String, String[]> propertyGroupMap = new TreeMap<String, String[]>(request.getParameterMap());
		String action = propertyGroupMap.pollFirstEntry().getValue()[0];
		String propertyGroupLabel = propertyGroupMap.pollFirstEntry().getValue()[0];
		
		Map<String, String> propertyMap = new LinkedHashMap<String, String>();
		for(String[] values : propertyGroupMap.values()){
				propertyMap.put(values[0], values[1]);
		}
				
		XMLUtils xmlUtils = new XMLUtils(PROPERTY_GROUPS_XML);
		if(action.equals("register")){
			xmlUtils.addPropertyGroup(propertyGroupLabel, propertyMap);
		} else if(action.equals("edit")) {
			xmlUtils.editPropertyGroup(propertyGroupLabel, propertyMap);
		}
	}
	/**
	 * Register property group
	 */
	private void register(Session session, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, ParseException, 
			org.apache.jackrabbit.core.nodetype.compact.ParseException, 
			javax.jcr.RepositoryException, InvalidNodeTypeDefException, DatabaseException {
		log.debug("register({}, {}, {})", new Object[] { session, request, response });
		
		// If it is ok, register it
		FileInputStream fis = null;
		
		try {
			if (Config.REPOSITORY_NATIVE) {
				DbRepositoryModule.registerPropertyGroups(Config.PROPERTY_GROUPS_XML);
			} else if (session != null) {
				// Check xml property groups definition
				FormUtils.resetPropertyGroupsForms();
				FormUtils.parsePropertyGroupsForms(Config.PROPERTY_GROUPS_XML);
				
				fis = new FileInputStream(Config.PROPERTY_GROUPS_CND);
				JcrRepositoryModule.registerCustomNodeTypes(session, fis);
			}
		} finally {
			IOUtils.closeQuietly(fis);
		}
		
		// Activity log
		UserActivity.log(request.getRemoteUser(), "ADMIN_PROPERTY_GROUP_REGISTER", null, null, Config.PROPERTY_GROUPS_CND);
		log.debug("register: void");
	}

	/**
	 * List property groups
	 * @throws Exception 
	 */
	private void list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		log.debug("list({}, {})", new Object[] { request, response });
		ServletContext sc = getServletContext();
		
		XMLUtils utils = new XMLUtils(PROPERTY_GROUPS_XML);
		if(utils.isPGXMLEmpty()){
			sc.getRequestDispatcher("/admin/property_group_register.jsp").forward(request, response);
		} else {
		
			FormUtils.resetPropertyGroupsForms();
			OKMPropertyGroup okmPropGroups = OKMPropertyGroup.getInstance();
			List<PropertyGroup> groups = okmPropGroups.getAllGroups(null);
			Map<PropertyGroup, List<Map<String, String>>> pGroups = new LinkedHashMap<PropertyGroup, List<Map<String,String>>>();
			
			for (PropertyGroup group : groups) {
				List<FormElement> mData = okmPropGroups.getPropertyGroupForm(null, group.getName());
				List<Map<String, String>> fMaps = new ArrayList<Map<String,String>>();
				
				for (FormElement fe : mData) {
					fMaps.add(FormUtils.toString(fe));
				}
				
				pGroups.put(group, fMaps);
			}
			
			sc.setAttribute("pGroups", pGroups);
			sc.getRequestDispatcher("/admin/property_groups_list.jsp").forward(request, response);
			
			// Activity log
			UserActivity.log(request.getRemoteUser(), "ADMIN_PROPERTY_GROUP_LIST", null, null, null);
		}
		log.debug("list: void");
	}
	
	/**
	 * Edit property groups
	 * @throws RepositoryException 
	 * @throws ParseException 
	 */
	private void edit(String pgLabel, HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException, DatabaseException, ParseException, RepositoryException {
		log.debug("edit({}, {})", new Object[] { request, response });
		
		String pgName = "okg:" + pgLabel.toLowerCase().replace(" ", "");
		
		List<FormElement> mData = OKMPropertyGroup.getInstance().getPropertyGroupForm(null, pgName);
		List<Map<String, String>> propertyMap = new ArrayList<Map<String,String>>();
		
		for (FormElement fe : mData) {
			propertyMap.add(FormUtils.toString(fe));
		}
				
		ServletContext sc = getServletContext();
		sc.setAttribute("propertyMap", propertyMap);
		sc.setAttribute("pgLabel" , pgLabel);
		request.getRequestDispatcher("/admin/property_groups_edit.jsp").forward(request, response);	
		
		log.debug("edit: void");
	}
	
	private void validatePropertyGroupName(String pgLabel, HttpServletRequest request, HttpServletResponse response) throws IOException, 
			ParseException, RepositoryException, DatabaseException{
		
		PrintWriter out = response.getWriter();
		response.setContentType("text/json");
		boolean isExists = false;
		
		String pgName = "okg:" + pgLabel.toLowerCase().replace(" ", "");
		
		List<PropertyGroup> list = OKMPropertyGroup.getInstance().getAllGroups(null);
		for(PropertyGroup group : list){
			if(group.getName().equals(pgName)){
				isExists = true;
				break;
			}
		}
		
		if (!isExists) {
			out.print("{ \"success\": true }");
		} else {
			out.print("{ \"success\": false, \"message\": \"Property Group Name is already taken.\" }");
		}
		
		out.flush();
		out.close();
		
	}
}
