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

import org.owasp.encoder.Encode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import com.ikon.cache.UserNodeKeywordsManager;
import com.ikon.core.AccessDeniedException;
import com.ikon.core.Config;
import com.ikon.core.DatabaseException;
import com.ikon.core.LockException;
import com.ikon.core.PathNotFoundException;
import com.ikon.core.RepositoryException;
import com.ikon.core.VersionException;
import com.ikon.dao.NodeBaseDAO;
import com.ikon.dao.bean.NodeBase;
import com.ikon.module.PropertyModule;
import com.ikon.module.db.base.BaseNotificationModule;
import com.ikon.spring.PrincipalUtils;
import com.ikon.util.UserActivity;

public class DbPropertyModule implements PropertyModule {
	private static Logger log = LoggerFactory.getLogger(DbPropertyModule.class);
	
	@Override
	public void addCategory(String token, String nodePath, String catId) throws VersionException, LockException,
			PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("addCategory({}, {}, {})", new Object[] { token, nodePath, catId });
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
			
			String uuid = NodeBaseDAO.getInstance().getUuidFromPath(nodePath);
			NodeBase nNode = NodeBaseDAO.getInstance().findByPk(uuid);
			NodeBaseDAO.getInstance().addCategory(uuid, catId);
			
			// Check subscriptions
			BaseNotificationModule.checkSubscriptions(nNode, auth.getName(), "ADD_CATEGORY", null);

			// Check scripting
			//BaseScriptingModule.checkScripts(session, documentNode, documentNode, "ADD_CATEGORY");

			// Activity log
			UserActivity.log(auth.getName(), "ADD_CATEGORY", uuid, nodePath, catId);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}
		
		log.debug("addCategory: void");
	}
	
	@Override
	public void removeCategory(String token, String nodePath, String catId) throws VersionException, LockException,
			PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("removeCategory({}, {}, {})", new Object[] { token, nodePath, catId });
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
			
			String uuid = NodeBaseDAO.getInstance().getUuidFromPath(nodePath);
			NodeBase nNode = NodeBaseDAO.getInstance().findByPk(uuid);
			NodeBaseDAO.getInstance().removeCategory(uuid, catId);

			// Check subscriptions
			BaseNotificationModule.checkSubscriptions(nNode, auth.getName(), "REMOVE_CATEGORY", null);

			// Check scripting
			//BaseScriptingModule.checkScripts(session, documentNode, documentNode, "REMOVE_CATEGORY");

			// Activity log
			UserActivity.log(auth.getName(), "REMOVE_CATEGORY", uuid, nodePath, catId);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}
		
		log.debug("removeCategory: void");
	}
	
	@Override
	public String addKeyword(String token, String nodePath, String keyword) throws VersionException, LockException,
			PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("addKeyword({}, {}, {})", new Object[] { token, nodePath, keyword });
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
			
			String uuid = NodeBaseDAO.getInstance().getUuidFromPath(nodePath);
			NodeBase nNode = NodeBaseDAO.getInstance().findByPk(uuid);
			
			if (keyword != null) {
				if (Config.SYSTEM_KEYWORD_LOWERCASE) {
					keyword = keyword.toLowerCase();
				}
				
				keyword = Encode.forHtml(keyword);
				NodeBaseDAO.getInstance().addKeyword(uuid, keyword);
				
				// Update cache
				if (Config.USER_KEYWORDS_CACHE) {
					UserNodeKeywordsManager.add(auth.getName(), uuid, keyword);
				}
				
				// Check subscriptions
				BaseNotificationModule.checkSubscriptions(nNode, auth.getName(), "ADD_KEYWORD", null);
				
				// Check scripting
				//BaseScriptingModule.checkScripts(session, documentNode, documentNode, "ADD_KEYWORD");
				
				// Activity log
				UserActivity.log(auth.getName(), "ADD_KEYWORD", uuid, nodePath, keyword);
			}
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}
		
		log.debug("addKeyword: {}", keyword);
		return keyword;
	}
	
	@Override
	public void removeKeyword(String token, String nodePath, String keyword) throws VersionException, LockException,
			PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("removeCategory({}, {}, {})", new Object[] { token, nodePath, keyword });
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
			
			String uuid = NodeBaseDAO.getInstance().getUuidFromPath(nodePath);
			NodeBase nNode = NodeBaseDAO.getInstance().findByPk(uuid);
			NodeBaseDAO.getInstance().removeKeyword(uuid, keyword);
			
			// Update cache
			if (Config.USER_KEYWORDS_CACHE) {
				UserNodeKeywordsManager.remove(auth.getName(), uuid, keyword);
			}
			
			// Check subscriptions
			BaseNotificationModule.checkSubscriptions(nNode, auth.getName(), "REMOVE_KEYWORD", null);

			// Check scripting
			//BaseScriptingModule.checkScripts(session, documentNode, documentNode, "REMOVE_KEYWORD");

			// Activity log
			UserActivity.log(auth.getName(), "REMOVE_KEYWORD", uuid, nodePath, keyword);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}
		
		log.debug("removeCategory: void");
	}
}
