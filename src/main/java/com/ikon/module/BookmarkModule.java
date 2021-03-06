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

package com.ikon.module;

import java.util.List;

import com.ikon.core.AccessDeniedException;
import com.ikon.core.DatabaseException;
import com.ikon.core.PathNotFoundException;
import com.ikon.core.RepositoryException;
import com.ikon.dao.bean.Bookmark;

public interface BookmarkModule {

	/**
	 * Add a new bookmark which points to this document.
	 * 
	 * @param token The session authorization token.
	 * @param nodePath A node path to be bookmarked.
	 * @param name The name of the bookmark.
	 * @return A bookmark object with the new created bookmark properties.
	 * @throws AccessDeniedException If there is any security problem: 
	 * you can't modify the node because of lack of permissions.
	 * @thows PathNotFoundException If there is no node with this nodePath.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public Bookmark add(String token, String nodePath, String name) throws AccessDeniedException,
			PathNotFoundException, RepositoryException, DatabaseException;

	/**
	 * Get info from a previously created bookmark.
	 * 
	 * @param token The session authorization token.
	 * @param bmId The unique bookmark id.
	 * @return The bookmark object.
	 * @throws AccessDeniedException If there is any security problem: 
	 * you can't modify the node because of lack of permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public Bookmark get(String token, int bmId) throws AccessDeniedException, RepositoryException,
			DatabaseException;
	
	/**
	 * Remove a bookmark.
	 * 
	 * @param token The session authorization token.
	 * @param bmId The bookmark id to be deleted.
	 * @throws AccessDeniedException If there is any security problem: 
	 * you can't modify the node because of lack of permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public void remove(String token, int bmId) throws AccessDeniedException, RepositoryException,
			DatabaseException;
	
	/**
	 * Rename a previous stored bookmark.
	 * 
	 * @param token The session authorization token.
	 * @param bmId The actual bookmark id.
	 * @param newName The new bookmark name.
	 * @return The updated bookmark properties.
	 * @throws AccessDeniedException If there is any security problem: 
	 * you can't modify the node because of lack of permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public Bookmark rename(String token, int bmId, String newName) throws AccessDeniedException, 
			RepositoryException, DatabaseException;
	
	/**
	 * Retrive an user bookmark collection.
	 * 
	 * @param token The session authorization token.
	 * @return All the user bookmarks.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public List<Bookmark> getAll(String token) throws RepositoryException, DatabaseException;
}
