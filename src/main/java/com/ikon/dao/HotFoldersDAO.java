package com.ikon.dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ikon.core.DatabaseException;
import com.ikon.dao.bean.HotFolders;

public class HotFoldersDAO {
	
	private static Logger log = LoggerFactory.getLogger(HotFoldersDAO.class);

	private HotFoldersDAO() {}
	
	/**
	 * Create
	 */
	public static long create(HotFolders folders) throws DatabaseException {
		log.debug("create({})", folders);
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			session.save(folders);
			HibernateUtil.commit(tx);
			return 1;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
	
	/**
	 * Update
	 */
	public static void update(HotFolders folders) throws DatabaseException {
		log.debug("update({})", folders);
		String qs = "select folders.sourcePath, folders.destinationPath from HotFolders folders where folders.id=:id";
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			
			Query q = session.createQuery(qs);
			q.setParameter("id", folders.getSourcePath());
			
			session.update(folders);
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
		
		log.debug("update: void");
	}
	
	/**
	 * Delete
	 */
	public static void delete(String foldersId) throws DatabaseException {
		log.info("delete({})", foldersId);
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			HotFolders folders = (HotFolders) session.load(HotFolders.class, foldersId);
			session.delete(folders);
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
		
		log.debug("delete: void");
	}
	
	/**
	 * Find by pk
	 */
	public static HotFolders findByPk(String foldersId) throws DatabaseException {
		log.debug("findByPk({})", foldersId);
		String qs = "from HotFolders folders where folders.id=:id";
		Session session = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setString("id", foldersId);
			HotFolders ret = (HotFolders) q.setMaxResults(1).uniqueResult();
			log.debug("findByPk: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
	
	/**
	 * Find by destination folder
	 */
	public static HotFolders findBydestFolder(String destFolderPath) throws DatabaseException {
		String qs = "from HotFolders folders where folders.destinationPath=:destinationPath";
		Session session = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setString("destinationPath", destFolderPath);
			HotFolders ret = (HotFolders) q.setMaxResults(1).uniqueResult();
			log.debug("findByPk: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
	
	
	/**
	 * Find all
	 */
	@SuppressWarnings("unchecked")
	public static List<HotFolders> findAll() throws DatabaseException {
		log.debug("findAll()");
		String qs = "from HotFolders folders order by folders.id";
		Session session = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			List<HotFolders> ret = q.list();
			log.debug("findAll: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

}
