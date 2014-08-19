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
import com.ikon.automation.AutomationManager;
import com.ikon.automation.AutomationUtils;
import com.ikon.bean.Document;
import com.ikon.bean.FileUploadResponse;
import com.ikon.bean.Folder;
import com.ikon.bean.LockInfo;
import com.ikon.bean.Note;
import com.ikon.bean.Permission;
import com.ikon.cache.UserItemsManager;
import com.ikon.core.AccessDeniedException;
import com.ikon.core.Config;
import com.ikon.core.DatabaseException;
import com.ikon.core.ItemExistsException;
import com.ikon.core.PathNotFoundException;
import com.ikon.core.Ref;
import com.ikon.core.UserQuotaExceededException;
import com.ikon.dao.NodeBaseDAO;
import com.ikon.dao.NodeDocumentDAO;
import com.ikon.dao.NodeDocumentVersionDAO;
import com.ikon.dao.NodeFolderDAO;
import com.ikon.dao.NodeNoteDAO;
import com.ikon.dao.UserConfigDAO;
import com.ikon.dao.bean.AutomationRule;
import com.ikon.dao.bean.NodeBase;
import com.ikon.dao.bean.NodeDocument;
import com.ikon.dao.bean.NodeDocumentVersion;
import com.ikon.dao.bean.NodeFolder;
import com.ikon.dao.bean.NodeLock;
import com.ikon.dao.bean.NodeNote;
import com.ikon.dao.bean.ProfileMisc;
import com.ikon.dao.bean.UserConfig;
import com.ikon.dao.bean.cache.UserItems;
import com.ikon.module.db.stuff.DbAccessManager;
import com.ikon.module.db.stuff.DbUtils;
import com.ikon.module.db.stuff.SecurityHelper;
import com.ikon.util.CloneUtils;
import com.ikon.util.DocConverter;
import com.ikon.util.UserActivity;

public class BaseDocumentModule {
	private static Logger log = LoggerFactory.getLogger(BaseDocumentModule.class);
	
	/**
	 * Create a new document
	 */
	@SuppressWarnings("unchecked")
	public static NodeDocument create(String user, String parentPath, NodeBase parentNode, String name, String title,
			Calendar created, String mimeType, InputStream is, long size, Set<String> keywords, Set<String> categories,
			Ref<FileUploadResponse> fuResponse) throws PathNotFoundException, AccessDeniedException, ItemExistsException,
			UserQuotaExceededException,	AutomationException, DatabaseException, IOException {
		
		// Check user quota
		UserConfig uc = UserConfigDAO.findByPk(user);
		ProfileMisc pm = uc.getProfile().getPrfMisc();
		
		// System user don't care quotas
		if (!Config.SYSTEM_USER.equals(user) && pm.getUserQuota() > 0) {
			long currentQuota = 0;
			
			if (Config.USER_ITEM_CACHE) {
				UserItems ui = UserItemsManager.get(user);
				currentQuota = ui.getSize();
			} else {
				currentQuota = DbUtils.calculateQuota(user);
			}
			
			if (currentQuota + size > pm.getUserQuota() * 1024 * 1024) {
				throw new UserQuotaExceededException(Long.toString(currentQuota + size));
			}
		}
		
		// AUTOMATION - PRE
		Map<String, Object> env = new HashMap<String, Object>();
		env.put(AutomationUtils.PARENT_UUID, parentNode.getUuid());
		env.put(AutomationUtils.PARENT_PATH, parentPath);
		env.put(AutomationUtils.PARENT_NODE, parentNode);
		env.put(AutomationUtils.DOCUMENT_NAME, name);
		env.put(AutomationUtils.DOCUMENT_MIME_TYPE, mimeType);
		env.put(AutomationUtils.DOCUMENT_KEYWORDS, keywords);
		
		AutomationManager.getInstance().fireEvent(AutomationRule.EVENT_DOCUMENT_CREATE, AutomationRule.AT_PRE, env);
		parentNode = (NodeBase) env.get(AutomationUtils.PARENT_NODE);
		name = (String) env.get(AutomationUtils.DOCUMENT_NAME);
		mimeType = (String) env.get(AutomationUtils.DOCUMENT_MIME_TYPE);
		keywords = (Set<String>) env.get(AutomationUtils.DOCUMENT_KEYWORDS);
		
		// Create and add a new document node
		NodeDocument documentNode = new NodeDocument();
		documentNode.setUuid(UUID.randomUUID().toString());
		documentNode.setContext(parentNode.getContext());
		documentNode.setParent(parentNode.getUuid());
		documentNode.setAuthor(user);
		documentNode.setName(name);
		documentNode.setTitle(title);
		documentNode.setMimeType(mimeType);
		documentNode.setCreated(created != null ? created : Calendar.getInstance());
		documentNode.setLastModified(documentNode.getCreated());
		
		// Get parent node auth info
		Map<String, Integer> userPerms = parentNode.getUserPermissions();
		Map<String, Integer> rolePerms = parentNode.getRolePermissions();
		
		// Always assign all grants to creator
		if (Config.USER_ASSIGN_DOCUMENT_CREATION) {
			userPerms.put(user, Permission.ALL_GRANTS);
		}
		
		// Set auth info
		// NOTICE: Pay attention to the need of cloning
		documentNode.setUserPermissions(CloneUtils.clone(userPerms));
		documentNode.setRolePermissions(CloneUtils.clone(rolePerms));
		
		NodeDocumentDAO.getInstance().create(documentNode, is, size);
		
		// AUTOMATION - POST
		env.put(AutomationUtils.DOCUMENT_NODE, documentNode);
		AutomationManager.getInstance().fireEvent(AutomationRule.EVENT_DOCUMENT_CREATE, AutomationRule.AT_POST, env);
		
		// Update user items size
		if (Config.USER_ITEM_CACHE) {
			UserItemsManager.incSize(user, size);
			UserItemsManager.incDocuments(user, 1);
		}
		
		// Setting wizard properties
		fuResponse.set((FileUploadResponse) env.get(AutomationUtils.UPLOAD_RESPONSE));
		
		return documentNode;
	}
	
	/**
	 * Get folder properties
	 */
	public static Document getProperties(String user, NodeDocument nDocument) throws PathNotFoundException,
			DatabaseException {
		log.debug("getProperties({}, {})", user, nDocument);
		Document doc = new Document();
		
		// Properties
		String docPath = NodeBaseDAO.getInstance().getPathFromUuid(nDocument.getUuid());
		doc.setPath(docPath);
		doc.setCreated(nDocument.getCreated());
		doc.setLastModified(nDocument.getLastModified());
		doc.setAuthor(nDocument.getAuthor());
		doc.setUuid(nDocument.getUuid());
		doc.setMimeType(nDocument.getMimeType());
		doc.setCheckedOut(nDocument.isCheckedOut());
		doc.setLocked(nDocument.isLocked());
		
		if (doc.isLocked()) {
			NodeLock nLock = nDocument.getLock();
			LockInfo lock = BaseModule.getProperties(nLock, docPath);
			doc.setLockInfo(lock);
		} else {
			doc.setLockInfo(null);
		}
		
		// Get current version
		NodeDocumentVersionDAO nodeDocVerDao = NodeDocumentVersionDAO.getInstance();
		NodeDocumentVersion currentVersion = nodeDocVerDao.findCurrentVersion(doc.getUuid());
		doc.setActualVersion(BaseModule.getProperties(currentVersion));
		
		// Get permissions
		if (Config.SYSTEM_READONLY) {
			doc.setPermissions(Permission.NONE);
		} else {
			DbAccessManager am = SecurityHelper.getAccessManager();
			
			if (am.isGranted(nDocument, Permission.READ)) {
				doc.setPermissions(Permission.READ);
			}
			
			if (am.isGranted(nDocument, Permission.WRITE)) {
				doc.setPermissions(doc.getPermissions() | Permission.WRITE);
			}
			
			if (am.isGranted(nDocument, Permission.DELETE)) {
				doc.setPermissions(doc.getPermissions() | Permission.DELETE);
			}
			
			if (am.isGranted(nDocument, Permission.SECURITY)) {
				doc.setPermissions(doc.getPermissions() | Permission.SECURITY);
			}
		}
		
		// Document conversion capabilities
		DocConverter convert = DocConverter.getInstance();
		doc.setConvertibleToPdf(convert.convertibleToPdf(doc.getMimeType()));
		doc.setConvertibleToSwf(convert.convertibleToSwf(doc.getMimeType()));
		
		// Get user subscription & keywords
		doc.setSubscriptors(nDocument.getSubscriptors());
		doc.setSubscribed(nDocument.getSubscriptors().contains(user));
		doc.setKeywords(nDocument.getKeywords());
		
		// Get categories
		Set<Folder> categories = new HashSet<Folder>();
		NodeFolderDAO nFldDao = NodeFolderDAO.getInstance();
		Set<NodeFolder> resolvedCategories = nFldDao.resolveCategories(nDocument.getCategories());
		
		for (NodeFolder nfldCat : resolvedCategories) {
			categories.add(BaseFolderModule.getProperties(user, nfldCat));
		}
		
		doc.setCategories(categories);
		
		// Get notes
		List<Note> notes = new ArrayList<Note>();
		List<NodeNote> nNoteList = NodeNoteDAO.getInstance().findByParent(nDocument.getUuid());
		
		for (NodeNote nNote : nNoteList) {
			notes.add(BaseNoteModule.getProperties(nNote, nNote.getUuid()));
		}
		
		doc.setNotes(notes);
		
		log.debug("getProperties: {}", doc);
		return doc;
	}
	
	/**
	 * Retrieve the content input stream from a document
	 * 
	 * @param user The user who make the content petition.
	 * @param docPath Path of the document to get the content.
	 * @param checkout If the content is retrieved due to a checkout or not.
	 * @param extendedSecurity If the extended security DOWNLOAD permission should be evaluated.
	 * This is used to enable the document preview.
	 */
	public static InputStream getContent(String user, String docPath, boolean checkout, boolean extendedSecurity)
			throws IOException, PathNotFoundException, AccessDeniedException, DatabaseException {
		String docUuid = NodeBaseDAO.getInstance().getUuidFromPath(docPath);
		InputStream is = NodeDocumentVersionDAO.getInstance().getCurrentContentByParent(docUuid);
		
		// Activity log
		UserActivity.log(user, (checkout ? "GET_DOCUMENT_CONTENT_CHECKOUT" : "GET_DOCUMENT_CONTENT"), docUuid, docPath, Integer.toString(is.available()));
		
		return is;
	}
	
	/**
	 * Is invoked from DbDocumentNode and DbFolderNode.
	 */
	public static NodeDocument copy(String user, NodeDocument srcDocNode, String dstPath, NodeBase dstNode,
			String docName) throws PathNotFoundException, AccessDeniedException, ItemExistsException,
			UserQuotaExceededException, AutomationException, DatabaseException, IOException {
		log.debug("copy({}, {}, {}, {}, {})", new Object[] { user, srcDocNode, dstNode, docName });
		InputStream is = null;
		NodeDocument newDocument = null;
		
		try {
			Set<String> keywords = new HashSet<String>();
			Set<String> categories = new HashSet<String>();
			
			Ref<FileUploadResponse> fuResponse = new Ref<FileUploadResponse>(new FileUploadResponse());
			is = NodeDocumentVersionDAO.getInstance().getCurrentContentByParent(srcDocNode.getUuid());
			NodeDocumentVersion nDocVer = NodeDocumentVersionDAO.getInstance().findCurrentVersion(srcDocNode.getUuid());
			newDocument = create(user, dstPath, dstNode, docName, srcDocNode.getTitle(), Calendar.getInstance(),
					srcDocNode.getMimeType(), is, nDocVer.getSize(), keywords, categories, fuResponse);
		} finally {
			IOUtils.closeQuietly(is);
		}
		
		log.debug("copy: {}", newDocument);
		return newDocument;
	}
}
