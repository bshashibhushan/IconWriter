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

package com.ikon.extension.core;

import java.io.File;
import java.io.IOException;

import javax.jcr.Node;
import javax.jcr.Session;

import com.ikon.bean.Document;
import com.ikon.bean.Version;
import com.ikon.core.AccessDeniedException;
import com.ikon.core.DatabaseException;
import com.ikon.core.FileSizeExceededException;
import com.ikon.core.ItemExistsException;
import com.ikon.core.LockException;
import com.ikon.core.PathNotFoundException;
import com.ikon.core.Ref;
import com.ikon.core.RepositoryException;
import com.ikon.core.UnsupportedMimeTypeException;
import com.ikon.core.UserQuotaExceededException;
import com.ikon.core.VersionException;
import com.ikon.core.VirusDetectedException;

public interface DocumentExtension extends Extension {
	/**
	 * Executed BEFORE document CREATE.
	 */
	public void preCreate(Session session, Ref<Node> parentNode, Ref<File> content, Ref<Document> doc)
			throws UnsupportedMimeTypeException, FileSizeExceededException, UserQuotaExceededException,
			VirusDetectedException, ItemExistsException, PathNotFoundException, AccessDeniedException,
			RepositoryException, IOException, DatabaseException, ExtensionException;
	
	/**
	 * Executed AFTER document CREATE.
	 */
	public void postCreate(Session session, Ref<Node> parentNode, Ref<Node> docNode)
			throws UnsupportedMimeTypeException, FileSizeExceededException, UserQuotaExceededException,
			VirusDetectedException, ItemExistsException, PathNotFoundException, AccessDeniedException,
			RepositoryException, IOException, DatabaseException, ExtensionException;
	
	/**
	 * Executed BEFORE document MOVE.
	 */
	public void preMove(Session session, Ref<Node> srcDocNode, Ref<Node> dstFldNode) throws PathNotFoundException,
			ItemExistsException, AccessDeniedException, RepositoryException, DatabaseException, ExtensionException;
	
	/**
	 * Executed AFTER document MOVE.
	 * 
	 * @param oldDocPath - original docPath
	 */
	public void postMove(Session session, String oldDocPath, Ref<Node> srcFldNode, Ref<Node> dstDocNode)
			throws PathNotFoundException, ItemExistsException, AccessDeniedException, RepositoryException,
			DatabaseException, ExtensionException;
	
	/**
	 * Executed BEFORE document DELETE.
	 */
	public void preDelete(Session session, Ref<Node> refDocumentNode) throws AccessDeniedException,
			RepositoryException, PathNotFoundException, LockException, DatabaseException, ExtensionException;
	
	/**
	 * Executed POST document DELETE.
	 */
	public void postDelete(Session session, String fileName) throws AccessDeniedException, RepositoryException,
			PathNotFoundException, LockException, DatabaseException, ExtensionException;
	
	/**
	 * Executed BEFORE document SET CONTENT.
	 */
	public void preSetContent(Session session, Ref<Node> refDocumentNode) throws FileSizeExceededException,
			UserQuotaExceededException, VirusDetectedException, VersionException, LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, IOException, DatabaseException, ExtensionException;
	
	/**
	 * Executed POST document SET CONTENT.
	 */
	public void postSetContent(Session session, Ref<Node> refDocumentNode) throws FileSizeExceededException,
			UserQuotaExceededException, VirusDetectedException, VersionException, LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, IOException, DatabaseException, ExtensionException;
	
	/**
	 * Executed BEFORE document RENAME.
	 */
	public void preRename(Session session, String docPath, String newPath, Ref<Node> refDocumentNode)
			throws AccessDeniedException, RepositoryException, PathNotFoundException, ItemExistsException,
			DatabaseException, ExtensionException;
	
	/**
	 * Executed POST document RENAME.
	 */
	public void postRename(Session session, String docPath, String newPath, Ref<Node> refDocumentNode)
			throws AccessDeniedException, RepositoryException, PathNotFoundException, ItemExistsException,
			DatabaseException, ExtensionException;
	
	/**
	 * Executed BEFORE document CHECK IN.
	 */
	public void preCheckin(Session session, Ref<Node> refDocumentNode) throws AccessDeniedException,
			RepositoryException, PathNotFoundException, LockException, VersionException, DatabaseException,
			ExtensionException;
	
	/**
	 * Executed POST document CHECK IN.
	 */
	public void postCheckin(Session session, Ref<Node> docNode, Ref<Version> version) throws AccessDeniedException,
			RepositoryException, PathNotFoundException, LockException, VersionException, DatabaseException,
			ExtensionException;
	
	/**
	 * Executed BEFORE document PURGE.
	 */
	public void prePurge(Session session, Ref<Node> refDocumentNode) throws AccessDeniedException, RepositoryException,
			PathNotFoundException, DatabaseException, ExtensionException;
	
	/**
	 * Executed POST document PURGE.
	 */
	public void postPurge(Session session, String docPath) throws AccessDeniedException, RepositoryException,
			PathNotFoundException, DatabaseException, ExtensionException;
	
	/**
	 * Executed BEFORE document COPY.
	 */
	public void preCopy(Session session, Ref<Node> refSrcNode, Ref<Node> refDstFolderNode) throws ItemExistsException,
			PathNotFoundException, AccessDeniedException, RepositoryException, IOException, DatabaseException,
			UserQuotaExceededException, ExtensionException;
	
	/**
	 * Executed POST document COPY.
	 */
	public void postCopy(Session session, Ref<Node> refSrcNode, Ref<Node> refNewDocument, Ref<Node> refDstFolderNode)
			throws ItemExistsException, PathNotFoundException, AccessDeniedException, RepositoryException, IOException,
			DatabaseException, UserQuotaExceededException, ExtensionException;
	
	/**
	 * Executed BEFPRE document RESTORE VERSION.
	 */
	public void preRestoreVersion(Session session, Ref<Node> refDocumentNode) throws AccessDeniedException,
			RepositoryException, PathNotFoundException, DatabaseException, ExtensionException;
	
	/**
	 * Executed POST document RESTORE VERSION.
	 */
	public void postRestoreVersion(Session session, Ref<Node> refDocumentNode) throws AccessDeniedException,
			RepositoryException, PathNotFoundException, DatabaseException, ExtensionException;
}
