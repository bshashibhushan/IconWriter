package com.ikon.dao;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.ikon.core.DatabaseException;
import com.ikon.dao.bean.SMTPConfig;

public class SMTPUtilsDAO {
	
	public static void saveSMTPServerDetails(SMTPConfig sc) throws DatabaseException{
		 Session session = null;
		 Transaction tx = null;
		    
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
		    session.save(sc);
		    HibernateUtil.commit(tx);
		 } catch (HibernateException e) {
		    HibernateUtil.rollback(tx);
		    throw new DatabaseException(e.getMessage(), e);
		 } finally {
		    HibernateUtil.close(session);
		 }		
	}

	public static SMTPConfig getSMTPServerDetails() throws DatabaseException{
		Session session = null;
		Transaction tx = null;
		Query q = null;
		String query = "from SMTPConfig sc";		
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			q = session.createQuery(query);
			tx = session.beginTransaction();
			SMTPConfig ret = (SMTPConfig) q.setMaxResults(1).uniqueResult();
			Hibernate.initialize(ret);
			HibernateUtil.commit(tx);
			return ret;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
	
	public static boolean deleteSMTPServerDetails() throws DatabaseException{		
		Session session = null;
		Transaction tx = null;
		Query q = null;
		String query = "from SMTPConfig sc";		
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			q = session.createQuery(query);
			tx = session.beginTransaction();
			SMTPConfig ret = (SMTPConfig) q.setMaxResults(1).uniqueResult();
			session.delete(ret);
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
		return true;		
	}
}
