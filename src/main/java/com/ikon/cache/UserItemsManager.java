package com.ikon.cache;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ikon.bean.ContentInfo;
import com.ikon.bean.Document;
import com.ikon.bean.Repository;
import com.ikon.core.DatabaseException;
import com.ikon.core.PathNotFoundException;
import com.ikon.core.RepositoryException;
import com.ikon.dao.NodeBaseDAO;
import com.ikon.dao.UserItemsDAO;
import com.ikon.dao.bean.cache.UserItems;
import com.ikon.module.db.base.BaseFolderModule;

public class UserItemsManager {
	private static Logger log = LoggerFactory.getLogger(UserItemsManager.class);
	private static Map<String, UserItems> userItemsMgr = new HashMap<String, UserItems>();
	
	/**
	 * Get stored user item
	 */
	public static UserItems get(String uid) {
		UserItems userItems = userItemsMgr.get(uid);
		
		if (userItems == null) {
			userItems = new UserItems();
			userItems.setUser(uid);
			userItemsMgr.put(uid, userItems);
		}
		
		return userItems;
	}
	
	/**
	 * Increment document number
	 */
	public static synchronized void incDocuments(String uid, int value) {
		log.debug("incDocuments({}, {})", uid, value);
		UserItems userItems = get(uid);
		userItems.setDocuments(userItems.getDocuments() + value);
	}

	/**
	 * Decrement document number
	 */
	public static synchronized void decDocuments(String uid, int value) {
		log.debug("decDocuments({}, {})", uid, value);
		UserItems userItems = get(uid);
		userItems.setDocuments(userItems.getDocuments() - value);
	}
	
	/**
	 * Increment folder number 
	 */
	public static synchronized void incFolders(String uid, int value) {
		log.debug("incFolders({}, {})", uid, value);
		UserItems userItems = get(uid);
		userItems.setFolders(userItems.getFolders() + value);
	}

	/**
	 * Decrement folder number
	 */
	public static synchronized void decFolders(String uid, int value) {
		log.debug("decFolders({}, {})", uid, value);
		UserItems userItems = get(uid);
		userItems.setFolders(userItems.getFolders() - value);
	}

	/**
	 * Increment document size
	 */
	public static synchronized void incSize(String uid, long value) {
		log.debug("incSize({}, {})", uid, value);
		UserItems userItems = get(uid);
		userItems.setSize(userItems.getSize() + value);
	}

	/**
	 * Decrement document size
	 */
	public static synchronized void decSize(String uid, long value) {
		log.debug("decSize({}, {})", uid, value);
		UserItems userItems = get(uid);
		userItems.setSize(userItems.getSize() - value);
	}
	
	/**
	 * TODO: Not fully implemented
	 */
	public static synchronized void refreshJcrUserItems(Session session) throws RepositoryException {
		log.info("refreshJcrUserItems({})", session);
		
		try {
			String statement = "/jcr:root/"+Repository.ROOT+"//element(*, openkm:document)[openkm:content/@openkm:author='"+session.getUserID()+"']";
			Workspace workspace = session.getWorkspace();
			QueryManager queryManager = workspace.getQueryManager();
			Query query = queryManager.createQuery(statement, Query.XPATH);
			QueryResult result = query.execute();
			long size = 0;
			
			for (NodeIterator nit = result.getNodes(); nit.hasNext(); ) {
				Node node = nit.nextNode();
				Node contentNode = node.getNode(Document.CONTENT);
				size += contentNode.getProperty(Document.SIZE).getLong();
			}
			
			log.info("Size: {}", size);
			UserItems userItems = new UserItems();
			userItemsMgr.put(session.getUserID(), userItems);
 		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new RepositoryException(e.getMessage(), e);
		}
 		
 		log.info("refreshJcrUserItems: void");
	}
	
	/**
	 * Refresh user item cache from database.
	 */
	public static synchronized void refreshDbUserItems() throws RepositoryException {
		log.info("refreshDbUserItems({})");
		Map<String, ContentInfo> totalUserContInfo = new HashMap<String, ContentInfo>();
		String[] bases = new String[] { Repository.ROOT, Repository.CATEGORIES, Repository.TEMPLATES,
				Repository.PERSONAL, Repository.MAIL, Repository.TRASH };
		
		try {
			for (String base : bases) {
				log.info("Calculate user content info from '{}'...", base);
				String uuid = NodeBaseDAO.getInstance().getUuidFromPath("/" + base);
				Map<String, ContentInfo> userContInfo = BaseFolderModule.getUserContentInfo(uuid);
				
				for (String user : userContInfo.keySet()) {
					ContentInfo usrTotContInfo = totalUserContInfo.get(user);
					ContentInfo usrContInfo = userContInfo.get(user);
					
					if (usrTotContInfo == null) {
						usrTotContInfo = new ContentInfo();
					}
					
					usrTotContInfo.setDocuments(usrTotContInfo.getDocuments() + usrContInfo.getDocuments());
					usrTotContInfo.setFolders(usrTotContInfo.getFolders() + usrContInfo.getFolders());
					usrTotContInfo.setMails(usrTotContInfo.getMails() + usrContInfo.getMails());
					usrTotContInfo.setSize(usrTotContInfo.getSize() + usrContInfo.getSize());
					
					totalUserContInfo.put(user, usrTotContInfo);
				}
			}
			
			for (String user : totalUserContInfo.keySet()) {
				ContentInfo contInfo = totalUserContInfo.get(user);
				UserItems userItems = new UserItems();
				userItems.setDocuments(contInfo.getDocuments());
				userItems.setFolders(contInfo.getFolders());
				userItems.setSize(contInfo.getSize());
				userItems.setUser(user);
				userItemsMgr.put(user, userItems);
			}
		} catch (PathNotFoundException e) {
			throw new RepositoryException("PathNotFoundException: " + e, e);
		} catch (DatabaseException e) {
			throw new RepositoryException("DatabaseException: " + e, e);
		}
 		
 		log.info("refreshDbUserItems: void");
	}

	/**
	 * Store data in database
	 */
	public static synchronized void serialize() throws DatabaseException {
		for (String user : userItemsMgr.keySet()) {
			UserItemsDAO.update(userItemsMgr.get(user));
		}
	}
	
	/**
	 * Read data from database
	 */
	public static synchronized void deserialize() throws DatabaseException {
		for (UserItems ui : UserItemsDAO.findAll()) {
			userItemsMgr.put(ui.getUser(), ui);
		}
	}
}
