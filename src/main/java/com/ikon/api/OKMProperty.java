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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ikon.core.AccessDeniedException;
import com.ikon.core.DatabaseException;
import com.ikon.core.LockException;
import com.ikon.core.PathNotFoundException;
import com.ikon.core.RepositoryException;
import com.ikon.core.VersionException;
import com.ikon.module.ModuleManager;
import com.ikon.module.PropertyModule;

/**
 * @author pavila
 *
 */
public class OKMProperty implements PropertyModule {
	private static Logger log = LoggerFactory.getLogger(OKMDocument.class);
	private static OKMProperty instance = new OKMProperty();

	private OKMProperty() {}
	
	public static OKMProperty getInstance() {
		return instance;
	}

	@Override
	public void addCategory(String token, String nodePath, String catId)	throws VersionException,
			LockException, PathNotFoundException, AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("addCategory({}, {}, {})", new Object[] { token, nodePath, catId });
		PropertyModule pm = ModuleManager.getPropertyModule();
		pm.addCategory(token, nodePath, catId);
		log.debug("addCategory: void");
	}

	@Override
	public void removeCategory(String token, String nodePath, String catId) throws VersionException,
			LockException, PathNotFoundException, AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("removeCategory({}, {}, {})", new Object[] { token, nodePath, catId });
		PropertyModule pm = ModuleManager.getPropertyModule();
		pm.removeCategory(token, nodePath, catId);
		log.debug("removeCategory: void");
	}

	@Override
	public String addKeyword(String token, String nodePath, String keyword) throws VersionException,
			LockException, PathNotFoundException, AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("addKeyword({}, {}, {})", new Object[] { token, nodePath, keyword });
		PropertyModule pm = ModuleManager.getPropertyModule();
		String ret = pm.addKeyword(token, nodePath, keyword);
		log.debug("addKeyword: {}", ret);
		return ret;
	}

	@Override
	public void removeKeyword(String token, String nodePath, String keyword) throws VersionException,
			LockException, PathNotFoundException, AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("removeKeyword({}, {}, {})", new Object[] { token, nodePath, keyword });
		PropertyModule pm = ModuleManager.getPropertyModule();
		pm.removeKeyword(token, nodePath, keyword);
		log.debug("removeKeyword: void");
	}
}
