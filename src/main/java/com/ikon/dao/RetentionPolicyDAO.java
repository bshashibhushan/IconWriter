package com.ikon.dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ikon.core.DatabaseException;
import com.ikon.dao.HibernateUtil;
import com.ikon.dao.bean.RetentionPolicy;


public class RetentionPolicyDAO {

	private static Logger log = LoggerFactory.getLogger(RetentionPolicyDAO.class);

	private RetentionPolicyDAO() {}
	
	/**
	 * Create
	 */
	public static long create(RetentionPolicy policy) throws DatabaseException {
		log.debug("create({})", policy);
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			session.save(policy);
			HibernateUtil.commit(tx);
			return policy.getRetentionDays();
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
	public static void update(RetentionPolicy policy) throws DatabaseException {
		log.debug("update({})", policy);
		String qs = "select policy.sourcePath, policy.destinationPath, policy.retentionDays, policy.emailList from RetentionPolicy policy where policy.id=:id";
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			
			Query q = session.createQuery(qs);
			q.setParameter("id", policy.getSourcePath());
			
			session.update(policy);
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
	public static void delete(String policyId) throws DatabaseException {
		log.info("delete({})", policyId);
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			RetentionPolicy policy = (RetentionPolicy) session.load(RetentionPolicy.class, policyId);
			session.delete(policy);
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
	public static RetentionPolicy findByPk(String policyId) throws DatabaseException {
		log.debug("findByPk({})", policyId);
		String qs = "from RetentionPolicy policy where policy.id=:id";
		Session session = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setString("id", policyId);
			RetentionPolicy ret = (RetentionPolicy) q.setMaxResults(1).uniqueResult();
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
	public static RetentionPolicy findBydestFolder(String destFolderPath) throws DatabaseException {
		String qs = "from RetentionPolicy policy where policy.destinationPath=:destinationPath";
		Session session = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setString("destinationPath", destFolderPath);
			RetentionPolicy ret = (RetentionPolicy) q.setMaxResults(1).uniqueResult();
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
	public static List<RetentionPolicy> findAll() throws DatabaseException {
		log.debug("findAll()");
		String qs = "from RetentionPolicy policy order by policy.id";
		Session session = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			List<RetentionPolicy> ret = q.list();
			log.debug("findAll: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
	
}
