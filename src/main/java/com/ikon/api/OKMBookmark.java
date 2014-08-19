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

package com.ikon.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ikon.core.AccessDeniedException;
import com.ikon.core.DatabaseException;
import com.ikon.core.PathNotFoundException;
import com.ikon.core.RepositoryException;
import com.ikon.dao.bean.Bookmark;
import com.ikon.module.BookmarkModule;
import com.ikon.module.ModuleManager;

/**
 * @author pavila
 *
 */
public class OKMBookmark implements BookmarkModule {
	private static Logger log = LoggerFactory.getLogger(OKMBookmark.class);
	private static OKMBookmark instance = new OKMBookmark();

	private OKMBookmark() {}
	
	public static OKMBookmark getInstance() {
		return instance;
	}

	@Override
	public Bookmark add(String token, String nodePath, String name) throws AccessDeniedException, 
			PathNotFoundException, RepositoryException, DatabaseException {
		log.debug("add({}, {}, {})", new Object[] { token, nodePath, name });
		BookmarkModule bm = ModuleManager.getBookmarkModule();
		Bookmark bookmark = bm.add(token, nodePath, name);
		log.debug("add: {}", bookmark);
		return bookmark;
	}
	
	@Override
	public Bookmark get(String token, int bmId) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("get({}, {})", new Object[] { token, bmId });
		BookmarkModule bm = ModuleManager.getBookmarkModule();
		Bookmark bookmark = bm.get(token, bmId);
		log.debug("get: {}", bookmark);
		return bookmark;
	}

	@Override
	public void remove(String token, int bmId) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("remove({}, {})", token, bmId);
		BookmarkModule bm = ModuleManager.getBookmarkModule();
		bm.remove(token, bmId);
		log.debug("remove: void");
	}
	
	@Override
	public Bookmark rename(String token, int bmId, String newName) throws AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("rename({}, {}, {})", new Object[] { token, bmId, newName });
		BookmarkModule bm = ModuleManager.getBookmarkModule();
		Bookmark bookmark = bm.rename(token, bmId, newName);
		log.debug("rename: {}", bookmark);
		return bookmark;
	}

	@Override
	public List<Bookmark> getAll(String token) throws RepositoryException, DatabaseException {
		log.debug("getAll({})", token);
		BookmarkModule bm = ModuleManager.getBookmarkModule();
		List<Bookmark> col = bm.getAll(token);
		log.debug("getAll: {}", col);
		return col;
	}
}