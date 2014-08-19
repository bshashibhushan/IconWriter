package com.ikon.module.jcr;

import javax.jcr.Node;
import javax.jcr.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ikon.core.AccessDeniedException;
import com.ikon.core.Config;
import com.ikon.core.DatabaseException;
import com.ikon.core.RepositoryException;
import com.ikon.dao.UserConfigDAO;
import com.ikon.dao.bean.UserConfig;
import com.ikon.module.UserConfigModule;
import com.ikon.module.jcr.stuff.JCRUtils;
import com.ikon.module.jcr.stuff.JcrSessionManager;
import com.ikon.util.UserActivity;

public class JcrUserConfigModule implements UserConfigModule {
	private static Logger log = LoggerFactory.getLogger(JcrUserConfigModule.class);
	
	@Override
	public void setHome(String token, String nodePath) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("setHome({}, {})", token, nodePath);
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
			UserConfig uc = new UserConfig();
			uc.setHomePath(nodePath);
			uc.setHomeNode(node.getUUID());
			uc.setHomeType(JCRUtils.getNodeType(node));
			uc.setUser(session.getUserID());
			UserConfigDAO.setHome(uc);
			
			// Activity log
			UserActivity.log(session.getUserID(), "USER_CONFIG_SET_HOME", node.getUUID(), nodePath, null);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token == null) JCRUtils.logout(session);
		}

		log.debug("setHome: void");
	}
	
	@Override
	public UserConfig getConfig(String token) throws RepositoryException, DatabaseException {
		log.debug("getConfig({})", token);
		UserConfig ret = new UserConfig();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			ret = UserConfigDAO.findByPk(session, session.getUserID());
			
			// Activity log
			UserActivity.log(session.getUserID(), "USER_CONFIG_GET_CONFIG", null, null, null);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}

		log.debug("getConfig: {}", ret);
		return ret;
	}
}
