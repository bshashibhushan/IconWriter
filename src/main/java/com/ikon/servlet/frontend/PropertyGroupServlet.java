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

package com.ikon.servlet.frontend;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ikon.api.OKMPropertyGroup;
import com.ikon.bean.PropertyGroup;
import com.ikon.bean.form.FormElement;
import com.ikon.core.AccessDeniedException;
import com.ikon.core.DatabaseException;
import com.ikon.core.LockException;
import com.ikon.core.NoSuchGroupException;
import com.ikon.core.NoSuchPropertyException;
import com.ikon.core.PathNotFoundException;
import com.ikon.core.RepositoryException;
import com.ikon.frontend.client.OKMException;
import com.ikon.frontend.client.bean.GWTPropertyGroup;
import com.ikon.frontend.client.bean.form.GWTFormElement;
import com.ikon.frontend.client.constants.service.ErrorCode;
import com.ikon.frontend.client.service.OKMPropertyGroupService;
import com.ikon.servlet.frontend.util.PropertyGroupComparator;
import com.ikon.util.GWTUtil;
import com.ikon.servlet.frontend.util.WorkflowUtil;
/**
 * PropertyGroup Servlet Class
 */
public class PropertyGroupServlet extends OKMRemoteServiceServlet implements OKMPropertyGroupService {
	private static Logger log = LoggerFactory.getLogger(PropertyGroupServlet.class);
	private static final long serialVersionUID = 2638205115826644606L;
	
	@Override
	public List<GWTPropertyGroup> getAllGroups() throws OKMException {
		log.debug("getAllGroups()");
		List<GWTPropertyGroup> groupList = new ArrayList<GWTPropertyGroup>();
		updateSessionManager();
		
		try {
			for (PropertyGroup pg : OKMPropertyGroup.getInstance().getAllGroups(null)) {
				if (pg.isVisible()) {
					GWTPropertyGroup group = GWTUtil.copy(pg);
					groupList.add(group);
				}
			}
			Collections.sort(groupList, PropertyGroupComparator.getInstance(getLanguage()));
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Repository),
					e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Database),
					e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_General),
					e.getMessage());
		}
		
		log.debug("getAllGroups: {}", groupList);
		return groupList;
	}
	
	@Override
	public List<GWTPropertyGroup> getAllGroups(String path) throws OKMException {
		log.debug("getAllGroups({})", path);
		List<GWTPropertyGroup> groupList = new ArrayList<GWTPropertyGroup>();
		updateSessionManager();
		
		try {
			List<GWTPropertyGroup> actualGroupsList = getGroups(path);
			for (PropertyGroup pg : OKMPropertyGroup.getInstance().getAllGroups(null)) {
				if (pg.isVisible()) {
					GWTPropertyGroup group = GWTUtil.copy(pg);
					groupList.add(group);
				}
			}
			
			// Purge from list values that are assigned to document
			if (!actualGroupsList.isEmpty()) {
				for (GWTPropertyGroup group : actualGroupsList) {
					for (GWTPropertyGroup groupListElement : groupList) {
						if (groupListElement.getName().equals(group.getName())) {
							groupList.remove(groupListElement);
							break;
						}
					}
				}
			}
			Collections.sort(groupList, PropertyGroupComparator.getInstance(getLanguage()));
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Repository),
					e.getMessage());
		} catch (OKMException e) {
			log.error(e.getMessage(), e);
			throw e;
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Database),
					e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_General),
					e.getMessage());
		}
		
		log.debug("getAllGroups: {}", groupList);
		return groupList;
	}
	
	@Override
	public void addGroup(String path, String grpName) throws OKMException {
		log.debug("addGroup({}, {})", path, grpName);
		updateSessionManager();
		
		try {
			OKMPropertyGroup.getInstance().addGroup(null, path, grpName);
            String groupName = grpName.substring(grpName.lastIndexOf(":") + 1);
			
			try{
		    	   if(WorkflowUtil.getWorkflowList().keySet().contains(groupName))
				        WorkflowUtil.startWorkflow(WorkflowUtil.getWorkflowList().get(groupName));
		       } catch (Exception e){
		    	   //do nothing
		       }
			
			
			
			
			
			
			
			
			
			
			
		} catch (NoSuchGroupException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(
					ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_NoSuchGroup),
					e.getMessage());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(
					ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_PathNotFound),
					e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(
					ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_AccessDenied),
					e.getMessage());
		} catch (LockException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Lock),
					e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Repository),
					e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Database),
					e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_General),
					e.getMessage());
		}
		
		log.debug("addGroup: void");
	}
	
	@Override
	public List<GWTPropertyGroup> getGroups(String path) throws OKMException {
		log.debug("getGroups({})", path);
		List<GWTPropertyGroup> groupList = new ArrayList<GWTPropertyGroup>();
		updateSessionManager();
		
		try {
			for (PropertyGroup pg : OKMPropertyGroup.getInstance().getGroups(null, path)) {
				if (pg.isVisible()) {
					GWTPropertyGroup group = GWTUtil.copy(pg);
					groupList.add(group);
				}
			}
			Collections.sort(groupList, PropertyGroupComparator.getInstance(getLanguage()));
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(
					ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_PathNotFound),
					e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Repository),
					e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Database),
					e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_General),
					e.getMessage());
		}
		
		log.debug("getGroups: {}", groupList);
		return groupList;
	}
	
	@Override
	public List<GWTFormElement> getProperties(String path, String grpName) throws OKMException {
		log.debug("getProperties({}, {})", path, grpName);
		List<GWTFormElement> properties = new ArrayList<GWTFormElement>();
		updateSessionManager();
		
		try {
			for (FormElement formElement : OKMPropertyGroup.getInstance().getProperties(null, path, grpName)) {
				properties.add(GWTUtil.copy(formElement));
			}
		} catch (NoSuchGroupException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(
					ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_NoSuchGroup),
					e.getMessage());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(
					ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_PathNotFound),
					e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Repository),
					e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Database),
					e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_General),
					e.getMessage());
		}
		
		log.debug("getProperties: {}", properties);
		return properties;
	}
	
	@Override
	public List<GWTFormElement> getPropertyGroupForm(String grpName) throws OKMException {
		log.debug("getPropertyGroupForm({})", grpName);
		List<GWTFormElement> gwtProperties = new ArrayList<GWTFormElement>();
		updateSessionManager();
		
		try {
			for (FormElement formElement : OKMPropertyGroup.getInstance().getPropertyGroupForm(null, grpName)) {
				gwtProperties.add(GWTUtil.copy(formElement));
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_IO),
					e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Repository),
					e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Database),
					e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_General),
					e.getMessage());
		}
		
		log.debug("getPropertyGroupForm: {}", gwtProperties);
		return gwtProperties;
	}
	
	@Override
	public void setProperties(String path, String grpName, List<GWTFormElement> formProperties) throws OKMException {
		log.debug("setProperties({}, {}, {})", new Object[] { path, grpName, formProperties });
		updateSessionManager();
		
		try {
			List<FormElement> properties = new ArrayList<FormElement>();
			
			for (GWTFormElement gWTformElement : formProperties) {
				properties.add(GWTUtil.copy(gWTformElement));
			}
			
			OKMPropertyGroup.getInstance().setProperties(null, path, grpName, properties);
		} catch (NoSuchPropertyException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService,
					ErrorCode.CAUSE_NoSuchProperty), e.getMessage());
		} catch (NoSuchGroupException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(
					ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_NoSuchGroup),
					e.getMessage());
		} catch (LockException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Lock),
					e.getMessage());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(
					ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_PathNotFound),
					e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(
					ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_AccessDenied),
					e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Repository),
					e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Database),
					e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_General),
					e.getMessage());
		}
		
		log.debug("setProperties: void");
	}
	
	@Override
	public void removeGroup(String path, String grpName) throws OKMException {
		log.debug("removeGroup({}, {})", path, grpName);
		updateSessionManager();
		
		try {
			OKMPropertyGroup.getInstance().removeGroup(null, path, grpName);
		} catch (NoSuchGroupException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(
					ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_NoSuchGroup),
					e.getMessage());
		} catch (LockException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Lock),
					e.getMessage());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(
					ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_PathNotFound),
					e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Repository),
					e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Database),
					e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_General),
					e.getMessage());
		}
		
		log.debug("removeGroup: void");
	}
}
