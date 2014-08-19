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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ikon.api.OKMFolder;
import com.ikon.api.OKMSearch;
import com.ikon.bean.Folder;
import com.ikon.bean.Repository;
import com.ikon.core.AccessDeniedException;
import com.ikon.core.DatabaseException;
import com.ikon.core.ItemExistsException;
import com.ikon.core.LockException;
import com.ikon.core.PathNotFoundException;
import com.ikon.core.RepositoryException;
import com.ikon.core.RetentionPolicyException;
import com.ikon.dao.RetentionPolicyDAO;
import com.ikon.frontend.client.OKMException;
import com.ikon.frontend.client.bean.GWTFolder;
import com.ikon.frontend.client.constants.service.ErrorCode;
import com.ikon.frontend.client.service.OKMFolderService;
import com.ikon.servlet.frontend.util.FolderComparator;
import com.ikon.util.GWTUtil;

/**
 * Servlet Class
 */
public class FolderServlet extends OKMRemoteServiceServlet implements OKMFolderService {
	private static Logger log = LoggerFactory.getLogger(FolderServlet.class);
	private static final long serialVersionUID = -4436438730167948558L;
	
	@Override
	public GWTFolder create(String fldPath, String fldPathParent) throws OKMException {
		log.debug("create({}, {})", fldPath, fldPathParent);
		GWTFolder gWTFolder = new GWTFolder();
		Folder folder = new Folder();
		folder.setPath(fldPathParent + "/" + fldPath);
		updateSessionManager();
		
		try {
			gWTFolder = GWTUtil.copy(OKMFolder.getInstance().create(null, folder), getUserWorkspaceSession());
		} catch (ItemExistsException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_ItemExists),
					e.getMessage());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_PathNotFound),
					e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_AccessDenied),
					e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Repository),
					e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Database),
					e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_General),
					e.getMessage());
		}
		
		log.debug("create: {}", gWTFolder);
		return gWTFolder;
	}
	
	@Override
	public void delete(String fldPath) throws OKMException {
		log.debug("delete({})", fldPath);
		updateSessionManager();
		
		try {
			
			if(isRetentionDestinationFolder(fldPath)){
				throw new RetentionPolicyException("This folder is a destination for Retention Documents. <br> Please remove the retention policy on this document.");
			} else {
				OKMFolder.getInstance().delete(null, fldPath);
			}
		} catch (LockException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Lock),
					e.getMessage());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_PathNotFound),
					e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_AccessDenied),
					e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Repository),
					e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Database),
					e.getMessage());
		} catch (RetentionPolicyException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, "1000"),
					e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_General),
					e.getMessage());
		}
		
		log.debug("delete: void");
	}
	
	private boolean isRetentionDestinationFolder(String folderPath) throws DatabaseException {
		return RetentionPolicyDAO.findBydestFolder(folderPath)==null?false:true;
	}

	@Override
	public List<GWTFolder> getCategorizedChilds(String fldPath) throws OKMException {
		log.debug("getCategorizedChilds({})", fldPath);
		List<GWTFolder> folderList = new ArrayList<GWTFolder>();
		updateSessionManager();
		
		try {
			if (fldPath.startsWith("/" + Repository.CATEGORIES)) {
				// TODO: Possible optimization getting folder really could not
				// be needed we've got UUID in GWT UI
				String uuid = OKMFolder.getInstance().getProperties(null, fldPath).getUuid();
				List<Folder> results = OKMSearch.getInstance().getCategorizedFolders(null, uuid);
				
				for (Folder folder : results) {
					folderList.add(GWTUtil.copy(folder, getUserWorkspaceSession()));
				}
			}
			Collections.sort(folderList, FolderComparator.getInstance(getLanguage()));
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Repository),
					e.getMessage());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_PathNotFound),
					e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Database),
					e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_General),
					e.getMessage());
		}
		
		log.debug("getCategorizedChilds: {}", folderList);
		return folderList;
	}
	
	@Override
	public List<GWTFolder> getThesaurusChilds(String fldPath) throws OKMException {
		log.debug("getThesaurusChilds({})", fldPath);
		List<GWTFolder> folderList = new ArrayList<GWTFolder>();
		updateSessionManager();
		
		try {
			// Thesaurus childs
			if (fldPath.startsWith("/" + Repository.THESAURUS)) {
				String keyword = fldPath.substring(fldPath.lastIndexOf("/") + 1).replace(" ", "_");
				List<Folder> results = OKMSearch.getInstance().getFoldersByKeyword(null, keyword);
				
				for (Folder fld : results) {
					folderList.add(GWTUtil.copy(fld, getUserWorkspaceSession()));
				}
			}
			Collections.sort(folderList, FolderComparator.getInstance(getLanguage()));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_General),
					e.getMessage());
		}
		
		log.debug("getThesaurusChilds: {}", folderList);
		return folderList;
	}
	
	@Override
	public List<GWTFolder> getChilds(String fldPath, boolean extraColumns) throws OKMException {
		log.debug("getChilds({})", fldPath);
		List<GWTFolder> folderList = new ArrayList<GWTFolder>();
		updateSessionManager();
		
		try {
			for (Folder folder : OKMFolder.getInstance().getChildren(null, fldPath)) {
				GWTFolder gWTFolder = (extraColumns) ? GWTUtil.copy(folder, getUserWorkspaceSession()) : GWTUtil.copy(
						folder, null);
				folderList.add(gWTFolder);
			}
			
			Collections.sort(folderList, FolderComparator.getInstance(getLanguage()));
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Repository),
					e.getMessage());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_PathNotFound),
					e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Database),
					e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_General),
					e.getMessage());
		}
		
		log.debug("getChilds: {}", folderList);
		return folderList;
	}
	
	@Override
	public GWTFolder rename(String fldId, String newName) throws OKMException {
		log.debug("rename({}, {})", fldId, newName);
		GWTFolder gWTFolder = new GWTFolder();
		updateSessionManager();
		
		try {
			gWTFolder = GWTUtil.copy(OKMFolder.getInstance().rename(null, fldId, newName), getUserWorkspaceSession());
		} catch (ItemExistsException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_ItemExists),
					e.getMessage());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_PathNotFound),
					e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_AccessDenied),
					e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Repository),
					e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Database),
					e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_General),
					e.getMessage());
		}
		
		log.debug("rename: {}", gWTFolder);
		return gWTFolder;
	}
	
	@Override
	public void move(String fldPath, String dstPath) throws OKMException {
		log.debug("move({}, {})", fldPath, dstPath);
		updateSessionManager();
		
		try {
			OKMFolder.getInstance().move(null, fldPath, dstPath);
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_PathNotFound),
					e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_AccessDenied),
					e.getMessage());
		} catch (ItemExistsException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_ItemExists),
					e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Repository),
					e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Database),
					e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_General),
					e.getMessage());
		}
		
		log.debug("move: void");
	}
	
	@Override
	public void purge(String fldPath) throws OKMException {
		log.debug("purge({})", fldPath);
		updateSessionManager();
		
		try {
			OKMFolder.getInstance().purge(null, fldPath);
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_PathNotFound),
					e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_AccessDenied),
					e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Repository),
					e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Database),
					e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_General),
					e.getMessage());
		}
		
		log.debug("purge: void");
	}
	
	@Override
	public GWTFolder getProperties(String fldPath) throws OKMException {
		log.debug("getProperties({})", fldPath);
		GWTFolder gWTFolder = new GWTFolder();
		updateSessionManager();
		
		try {
			Folder fld = OKMFolder.getInstance().getProperties(null, fldPath);
			gWTFolder = GWTUtil.copy(fld, getUserWorkspaceSession());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_PathNotFound),
					e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Repository),
					e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Database),
					e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_General),
					e.getMessage());
		}
		
		log.debug("getProperties: {}", gWTFolder);
		return gWTFolder;
	}
	
	@Override
	public void copy(String fldPath, String dstPath) throws OKMException {
		log.debug("copy({}, {})", fldPath, dstPath);
		updateSessionManager();
		
		try {
			OKMFolder.getInstance().copy(null, fldPath, dstPath);
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_PathNotFound),
					e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_AccessDenied),
					e.getMessage());
		} catch (ItemExistsException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_ItemExists),
					e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Repository),
					e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Database),
					e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_General),
					e.getMessage());
		}
		
		log.debug("copy: void");
	}
	
	@Override
	public Boolean isValid(String fldPath) throws OKMException {
		log.debug("isValid({})", fldPath);
		updateSessionManager();
		
		try {
			return Boolean.valueOf(OKMFolder.getInstance().isValid(null, fldPath));
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_PathNotFound),
					e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_AccessDenied),
					e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Repository),
					e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Database),
					e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_General),
					e.getMessage());
		}
	}
}
