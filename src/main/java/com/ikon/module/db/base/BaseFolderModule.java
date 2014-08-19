/**
 * openkm, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2013 Paco Avila & Josep Llort
 * 
 * No bytes were intentionally harmed during the development of this application.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.ikon.module.db.base;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ikon.automation.AutomationException;
import com.ikon.bean.ContentInfo;
import com.ikon.bean.Folder;
import com.ikon.bean.Note;
import com.ikon.bean.Permission;
import com.ikon.cache.UserItemsManager;
import com.ikon.core.AccessDeniedException;
import com.ikon.core.Config;
import com.ikon.core.DatabaseException;
import com.ikon.core.ItemExistsException;
import com.ikon.core.PathNotFoundException;
import com.ikon.core.RepositoryException;
import com.ikon.core.UserQuotaExceededException;
import com.ikon.dao.NodeBaseDAO;
import com.ikon.dao.NodeDocumentDAO;
import com.ikon.dao.NodeDocumentVersionDAO;
import com.ikon.dao.NodeFolderDAO;
import com.ikon.dao.NodeMailDAO;
import com.ikon.dao.NodeNoteDAO;
import com.ikon.dao.bean.NodeDocument;
import com.ikon.dao.bean.NodeDocumentVersion;
import com.ikon.dao.bean.NodeFolder;
import com.ikon.dao.bean.NodeMail;
import com.ikon.dao.bean.NodeNote;
import com.ikon.module.db.stuff.DbAccessManager;
import com.ikon.module.db.stuff.SecurityHelper;
import com.ikon.util.CloneUtils;

public class BaseFolderModule {
	private static Logger log = LoggerFactory.getLogger(BaseFolderModule.class);
	
	/**
	 * Create a new folder
	 */
	public static NodeFolder create(String user, NodeFolder parentFolder, String name, Calendar created)
			throws PathNotFoundException, AccessDeniedException, ItemExistsException, DatabaseException {
		
		// Create and add a new folder node
		NodeFolder folderNode = new NodeFolder();
		folderNode.setUuid(UUID.randomUUID().toString());
		folderNode.setContext(parentFolder.getContext());
		folderNode.setParent(parentFolder.getUuid());
		folderNode.setAuthor(user);
		folderNode.setName(name);
		folderNode.setCreated(created != null ? created : Calendar.getInstance());
		
		// Get parent node auth info
		Map<String, Integer> userPerms = parentFolder.getUserPermissions();
		Map<String, Integer> rolePerms = parentFolder.getRolePermissions();
		
		// Always assign all grants to creator
		if (Config.USER_ASSIGN_DOCUMENT_CREATION) {
			userPerms.put(user, Permission.ALL_GRANTS);
		}
		
		// Set auth info
		// NOTICE: Pay attention to the need of cloning
		folderNode.setUserPermissions(CloneUtils.clone(userPerms));
		folderNode.setRolePermissions(CloneUtils.clone(rolePerms));
		
		NodeFolderDAO.getInstance().create(folderNode);
		
		if (Config.USER_ITEM_CACHE) {
			// Update user items size
			UserItemsManager.incFolders(user, 1);
		}
		
		return folderNode;
	}
	
	/**
	 * Get folder properties
	 */
	public static Folder getProperties(String user, NodeFolder nFolder) throws PathNotFoundException, DatabaseException {
		log.debug("getProperties({}, {})", user, nFolder);
		Folder fld = new Folder();
		
		// Properties
		String fldPath = NodeBaseDAO.getInstance().getPathFromUuid(nFolder.getUuid());
		fld.setPath(fldPath);
		fld.setCreated(nFolder.getCreated());
		fld.setAuthor(nFolder.getAuthor());
		fld.setUuid(nFolder.getUuid());
		fld.setHasChildren(NodeFolderDAO.getInstance().hasChildren(nFolder.getUuid()));
		
		// Get permissions
		if (Config.SYSTEM_READONLY) {
			fld.setPermissions(Permission.NONE);
		} else {
			DbAccessManager am = SecurityHelper.getAccessManager();
			
			if (am.isGranted(nFolder, Permission.READ)) {
				fld.setPermissions(Permission.READ);
			}
			
			if (am.isGranted(nFolder, Permission.WRITE)) {
				fld.setPermissions(fld.getPermissions() | Permission.WRITE);
			}
			
			if (am.isGranted(nFolder, Permission.DELETE)) {
				fld.setPermissions(fld.getPermissions() | Permission.DELETE);
			}
			
			if (am.isGranted(nFolder, Permission.SECURITY)) {
				fld.setPermissions(fld.getPermissions() | Permission.SECURITY);
			}
		}
		
		// Get user subscription & keywords
		fld.setSubscriptors(nFolder.getSubscriptors());
		fld.setSubscribed(nFolder.getSubscriptors().contains(user));
		fld.setKeywords(nFolder.getKeywords());
		
		// Get categories
		Set<Folder> categories = new HashSet<Folder>();
		NodeFolderDAO nFldDao = NodeFolderDAO.getInstance();
		Set<NodeFolder> resolvedCategories = nFldDao.resolveCategories(nFolder.getCategories());
		
		for (NodeFolder nfldCat : resolvedCategories) {
			categories.add(BaseFolderModule.getProperties(user, nfldCat));
		}
		
		fld.setCategories(categories);
		
		// Get notes
		List<Note> notes = new ArrayList<Note>();
		List<NodeNote> nNoteList = NodeNoteDAO.getInstance().findByParent(nFolder.getUuid());
		
		for (NodeNote nNote : nNoteList) {
			notes.add(BaseNoteModule.getProperties(nNote, nNote.getUuid()));
		}
		
		fld.setNotes(notes);
		
		log.debug("getProperties: {}", fld);
		return fld;
	}
	
	/**
	 * Duplicates a folder into another one
	 */
	public static NodeFolder copy(String user, NodeFolder srcFldNode, NodeFolder dstFldNode)
			throws ItemExistsException, UserQuotaExceededException, PathNotFoundException, AccessDeniedException,
			AutomationException, DatabaseException, IOException {
		log.debug("copy({}, {}, {})", new Object[] { user, srcFldNode, dstFldNode });
		InputStream is = null;
		NodeFolder newFolder = null;
		
		try {
			String name = srcFldNode.getName();
			newFolder = BaseFolderModule.create(user, dstFldNode, name, Calendar.getInstance());
			
			for (NodeFolder nFolder : NodeFolderDAO.getInstance().findByParent(srcFldNode.getUuid())) {
				copy(user, nFolder, newFolder);
			}
			
			for (NodeDocument nDocument : NodeDocumentDAO.getInstance().findByParent(srcFldNode.getUuid())) {
				String newPath = NodeBaseDAO.getInstance().getPathFromUuid(newFolder.getUuid());
				BaseDocumentModule.copy(user, nDocument, newPath, newFolder, nDocument.getName());
			}
			
			for (NodeMail nMail : NodeMailDAO.getInstance().findByParent(srcFldNode.getUuid())) {
				BaseMailModule.copy(user, nMail, newFolder);
			}
		} finally {
			IOUtils.closeQuietly(is);
		}
		
		log.debug("copy: {}", newFolder);
		return newFolder;
	}
	
	/**
	 * Check recursively if the folder contains locked nodes
	 */
	public static boolean hasLockedNodes(String fldUuid) throws PathNotFoundException, DatabaseException,
			RepositoryException {
		boolean hasLock = false;
		
		for (NodeDocument nDoc : NodeDocumentDAO.getInstance().findByParent(fldUuid)) {
			hasLock |= nDoc.isLocked();
		}
		
		for (NodeFolder nFld : NodeFolderDAO.getInstance().findByParent(fldUuid)) {
			hasLock |= hasLockedNodes(nFld.getUuid());
		}
		
		return hasLock;
	}
	
	/**
	 * Check if a node has removable childs
	 * 
	 * TODO: Is this necessary? The access manager should prevent this and make the core thrown an exception.
	 */
	public static boolean hasWriteAccess(String fldUuid) throws PathNotFoundException, DatabaseException,
			RepositoryException {
		log.debug("hasWriteAccess({})", fldUuid);
		DbAccessManager am = SecurityHelper.getAccessManager();
		boolean canWrite = true;
		
		for (NodeDocument nDoc : NodeDocumentDAO.getInstance().findByParent(fldUuid)) {
			canWrite &= am.isGranted(nDoc, Permission.WRITE);
		}
		
		for (NodeMail nMail : NodeMailDAO.getInstance().findByParent(fldUuid)) {
			canWrite &= am.isGranted(nMail, Permission.WRITE);
		}
		
		for (NodeFolder nFld : NodeFolderDAO.getInstance().findByParent(fldUuid)) {
			canWrite &= am.isGranted(nFld, Permission.WRITE);
			canWrite &= hasWriteAccess(nFld.getUuid());
		}
		
		log.debug("hasWriteAccess: {}", canWrite);
		return canWrite;
	}
	
	/**
	 * Get content info recursively
	 */
	public static ContentInfo getContentInfo(String folderUuid) throws PathNotFoundException, DatabaseException {
		log.debug("getContentInfo({})", folderUuid);
		ContentInfo contentInfo = new ContentInfo();
		
		for (NodeFolder nFld : NodeFolderDAO.getInstance().findByParent(folderUuid)) {
			ContentInfo ci = getContentInfo(nFld.getUuid());
			contentInfo.setFolders(contentInfo.getFolders() + ci.getFolders() + 1);
			contentInfo.setDocuments(contentInfo.getDocuments() + ci.getDocuments());
			contentInfo.setSize(contentInfo.getSize() + ci.getSize());
		}
		
		for (NodeDocument nDoc : NodeDocumentDAO.getInstance().findByParent(folderUuid)) {
			NodeDocumentVersion nDocVer = NodeDocumentVersionDAO.getInstance().findCurrentVersion(nDoc.getUuid());
			long size = nDocVer.getSize();
			contentInfo.setDocuments(contentInfo.getDocuments() + 1);
			contentInfo.setSize(contentInfo.getSize() + size);
		}
		
		for (NodeMail nMail : NodeMailDAO.getInstance().findByParent(folderUuid)) {
			long size = nMail.getSize();
			contentInfo.setDocuments(contentInfo.getDocuments() + 1);
			contentInfo.setSize(contentInfo.getSize() + size);
		}
		
		log.debug("getContentInfo: {}", contentInfo);
		return contentInfo;
	}
	
	/**
	 * Get content info by user recursively
	 */
	public static Map<String, ContentInfo> getUserContentInfo(String folderUuid) throws
			PathNotFoundException, DatabaseException {
		log.debug("getUserContentInfo({})", folderUuid);
		Map<String, ContentInfo> userContentInfo = new HashMap<String, ContentInfo>();
		
		for (NodeFolder nFld : NodeFolderDAO.getInstance().findByParent(folderUuid)) {
			Map<String, ContentInfo> usrContInfoRt = getUserContentInfo(nFld.getUuid());
			
			for (String user : usrContInfoRt.keySet()) {
				ContentInfo ciRt = usrContInfoRt.get(user);
				ContentInfo ci = getOrCreate(userContentInfo, user);
				ci.setDocuments(ci.getDocuments() + ciRt.getDocuments());
				ci.setSize(ci.getSize() + ciRt.getSize());
				userContentInfo.put(user, ci);
			}
			
			ContentInfo ci = getOrCreate(userContentInfo, nFld.getAuthor());
			ci.setFolders(ci.getFolders() + ci.getFolders() + 1);
			userContentInfo.put(nFld.getAuthor(), ci);
		}
		
		for (NodeDocument nDoc : NodeDocumentDAO.getInstance().findByParent(folderUuid)) {
			for (NodeDocumentVersion nDocVer : NodeDocumentVersionDAO.getInstance().findByParent(nDoc.getUuid())) {
				ContentInfo ci = getOrCreate(userContentInfo, nDocVer.getAuthor());
				ci.setSize(ci.getSize() + nDocVer.getSize());
				userContentInfo.put(nDocVer.getAuthor(), ci);
			}
			
			ContentInfo ci = getOrCreate(userContentInfo, nDoc.getAuthor());
			ci.setDocuments(ci.getDocuments() + 1);
			userContentInfo.put(nDoc.getAuthor(), ci);
		}
		
		for (NodeMail nMail : NodeMailDAO.getInstance().findByParent(folderUuid)) {
			ContentInfo ci = getOrCreate(userContentInfo, nMail.getAuthor());
			ci.setDocuments(ci.getDocuments() + 1);
			ci.setSize(ci.getSize() + nMail.getSize());
			userContentInfo.put(nMail.getAuthor(), ci);
		}
		
		log.debug("getUserContentInfo: {}", userContentInfo);
		return userContentInfo;
	}
	
	/**
	 * Helper method
	 */
	private static ContentInfo getOrCreate(Map<String, ContentInfo> userContentInfo, String user) {
		ContentInfo ci = userContentInfo.get(user);
		
		if (ci == null) {
			ci = new ContentInfo();
		}
		
		return ci;
	}
}
