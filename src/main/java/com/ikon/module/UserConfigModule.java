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

import com.ikon.core.AccessDeniedException;
import com.ikon.core.DatabaseException;
import com.ikon.core.PathNotFoundException;
import com.ikon.core.RepositoryException;
import com.ikon.dao.bean.UserConfig;

public interface UserConfigModule {

	/**
	 * Set the user default home node
	 * 
	 * @param nodePath A node path to be set as user home folder.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public void setHome(String token, String nodePath) throws AccessDeniedException, RepositoryException,
			DatabaseException;
	
	/**
	 * Get user config
	 * 
	 * @throws PathNotFoundException If the indicated bookmark doesn't exist.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public UserConfig getConfig(String token) throws RepositoryException, DatabaseException;
}
