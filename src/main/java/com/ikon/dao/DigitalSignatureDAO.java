package com.ikon.dao;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ikon.core.DatabaseException;
import com.ikon.dao.bean.DigitalSignature;

public class DigitalSignatureDAO {
	
	private static Logger log = LoggerFactory.getLogger(DigitalSignatureDAO.class);
	private static DigitalSignatureDAO single = new DigitalSignatureDAO();
	
	private DigitalSignatureDAO(){
		//singleton
	}
	
	public static DigitalSignatureDAO getInstance() {
		return single;
	}
	
	public void registerPFX(DigitalSignature sign) throws DatabaseException{
		log.debug("registerPfx{}, {})", sign.getUserId());
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			session.save(sign);
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
	
	/**
	 * Get Sign object from database
	 */
	public DigitalSignature getUserSignature(String usrId) throws DatabaseException {
		log.debug("getUserSignature({})", usrId);
		String qs = "from DigitalSignature u where u.id=:id";
		Session session = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setString("id", usrId);
			DigitalSignature sign = (DigitalSignature) q.setMaxResults(1).uniqueResult();
			return sign;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
	
	public void deletePFX(String userId) throws DatabaseException{
		log.debug("deleteUserPfx({})", userId);
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			DigitalSignature sign = (DigitalSignature) session.load(DigitalSignature.class, userId);
			session.delete(sign);	
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}


}
