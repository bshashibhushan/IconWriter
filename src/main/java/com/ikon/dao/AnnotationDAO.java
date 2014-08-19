package com.ikon.dao;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.ikon.core.DatabaseException;
import com.ikon.dao.bean.Annotation;
 
public class AnnotationDAO {
	
	public static void saveAnnotation(Annotation sa) throws DatabaseException{
		 Session session = null;
		 Transaction tx = null;
		    
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
		    session.save(sa);
		    HibernateUtil.commit(tx);
		 } catch (HibernateException e) {
		    HibernateUtil.rollback(tx);
		    throw new DatabaseException(e.getMessage(), e);
		 } finally {
		    HibernateUtil.close(session);
		 }		
	}
	
	public static void updateAnnotation(Annotation sa) throws DatabaseException{
		 Session session = null;
		 Transaction tx = null;
		    
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
		    session.update(sa);
		    HibernateUtil.commit(tx);
		 } catch (HibernateException e) {
		    HibernateUtil.rollback(tx);
		    throw new DatabaseException(e.getMessage(), e);
		 } finally {
		    HibernateUtil.close(session);
		 }		
	}
	
	public static Annotation getAnnotationDetails(String uuid) throws DatabaseException{
		Session session = null;
		Transaction tx = null;
		Query q = null;
		String query = "from Annotation a where a.uuid=:uuid";		
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			q = session.createQuery(query);
			q.setString("uuid", uuid);
			tx = session.beginTransaction();
			Annotation ret = (Annotation) q.setMaxResults(1).uniqueResult();
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

}
