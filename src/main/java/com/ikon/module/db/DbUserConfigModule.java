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

package com.ikon.module.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import com.ikon.core.AccessDeniedException;
import com.ikon.core.Config;
import com.ikon.core.DatabaseException;
import com.ikon.core.PathNotFoundException;
import com.ikon.core.RepositoryException;
import com.ikon.dao.NodeBaseDAO;
import com.ikon.dao.UserConfigDAO;
import com.ikon.dao.bean.UserConfig;
import com.ikon.module.UserConfigModule;
import com.ikon.spring.PrincipalUtils;
import com.ikon.util.UserActivity;

public class DbUserConfigModule implements UserConfigModule {
	private static Logger log = LoggerFactory.getLogger(DbUserConfigModule.class);
	
	@Override
	public void setHome(String token, String nodePath) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("setHome({}, {})", token, nodePath);
		Authentication auth = null, oldAuth = null;
		
		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}
		
		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}
			
			String nodeUuid = NodeBaseDAO.getInstance().getUuidFromPath(nodePath);
			String nodeType = NodeBaseDAO.getInstance().getNodeTypeByUuid(nodeUuid);
			UserConfig uc = new UserConfig();
			uc.setHomePath(nodePath);
			uc.setHomeNode(nodeUuid);
			uc.setHomeType(nodeType);
			uc.setUser(auth.getName());
			UserConfigDAO.setHome(uc);
			
			// Activity log
			UserActivity.log(auth.getName(), "USER_CONFIG_SET_HOME", nodeUuid, nodePath, null);
		} catch (PathNotFoundException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}
		
		log.debug("setHome: void");
	}
	
	@Override
	public UserConfig getConfig(String token) throws RepositoryException, DatabaseException {
		log.debug("getConfig({})", token);
		UserConfig ret = new UserConfig();
		Authentication auth = null, oldAuth = null;
		
		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}
			
			ret = UserConfigDAO.findByPk(auth.getName());
			
			// Activity log
			UserActivity.log(auth.getName(), "USER_CONFIG_GET_CONFIG", null, null, null);
		} catch (PathNotFoundException e) {
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}

		log.debug("getConfig: {}", ret);
		return ret;
	}
}
