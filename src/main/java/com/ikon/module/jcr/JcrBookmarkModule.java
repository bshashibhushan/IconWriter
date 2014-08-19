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

package com.ikon.module.jcr;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ikon.core.AccessDeniedException;
import com.ikon.core.Config;
import com.ikon.core.DatabaseException;
import com.ikon.core.PathNotFoundException;
import com.ikon.core.RepositoryException;
import com.ikon.dao.BookmarkDAO;
import com.ikon.dao.bean.Bookmark;
import com.ikon.module.BookmarkModule;
import com.ikon.module.jcr.stuff.JCRUtils;
import com.ikon.module.jcr.stuff.JcrSessionManager;
import com.ikon.util.PathUtils;
import com.ikon.util.UserActivity;

public class JcrBookmarkModule implements BookmarkModule {
	private static Logger log = LoggerFactory.getLogger(JcrBookmarkModule.class);
	
	@Override
	public Bookmark add(String token, String nodePath, String name) throws AccessDeniedException,
			PathNotFoundException, RepositoryException, DatabaseException {
		log.debug("add({}, {}, {})", new Object[] { token, nodePath, name });
		Bookmark newBookmark = null;
		Session session = null;
		
		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}

		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			Node rootNode = session.getRootNode();
			Node node = rootNode.getNode(nodePath.substring(1));
			
			// Escape dangerous chars in name
			name = PathUtils.escape(name);

			newBookmark = new Bookmark();
			newBookmark.setUser(session.getUserID());
			newBookmark.setName(name);
			newBookmark.setNode(node.getUUID());
			newBookmark.setType(JCRUtils.getNodeType(node));
			BookmarkDAO.create(newBookmark);
			
			// Activity log
			UserActivity.log(session.getUserID(), "BOOKMARK_ADD", node.getUUID(), nodePath, name);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}

		log.debug("add: {}", newBookmark);
		return newBookmark;
	}
	
	@Override
	public Bookmark get(String token, int bmId) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("get({}, {})", token, bmId);
		Bookmark bookmark = null;
		Session session = null;
		
		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}

		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			bookmark = BookmarkDAO.findByPk(bmId);
			
			// Activity log
			UserActivity.log(session.getUserID(), "BOOKMARK_GET", Integer.toString(bmId), null, bookmark.toString());
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}

		log.debug("get: {}", bookmark);
		return bookmark;
	}

	@Override
	public void remove(String token, int bmId) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("remove({}, {})", token, bmId);
		Session session = null;
		
		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}

		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			BookmarkDAO.delete(bmId);
			
			// Activity log
			UserActivity.log(session.getUserID(), "BOOKMARK_REMOVE", Integer.toString(bmId), null, null);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}

		log.debug("remove: void");
	}

	@Override
	public Bookmark rename(String token, int bmId, String newName) throws AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("rename({}, {}, {})", new Object[] { token, bmId, newName });
		Bookmark renamedBookmark = null;
		Session session = null;
		
		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}

		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			Bookmark bm = BookmarkDAO.findByPk(bmId);
			bm.setName(newName);
			BookmarkDAO.update(bm);
			renamedBookmark = BookmarkDAO.findByPk(bmId);
						
			// Activity log
			UserActivity.log(session.getUserID(), "BOOKMARK_RENAME", Integer.toString(bmId), null, newName);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}

		log.debug("rename: {}", renamedBookmark);
		return renamedBookmark;
	}

	@Override
	public List<Bookmark> getAll(String token) throws RepositoryException,
			DatabaseException {
		log.debug("getAll({})", token);
		List<Bookmark> ret = new ArrayList<Bookmark>();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			ret = BookmarkDAO.findByUser(session.getUserID());
			
			// Activity log
			UserActivity.log(session.getUserID(), "BOOKMARK_GET_ALL", null, null, null);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}

		log.debug("getAll: {}", ret);
		return ret;
	}
}
