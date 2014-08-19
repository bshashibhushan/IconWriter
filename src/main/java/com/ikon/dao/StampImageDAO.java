package com.ikon.dao;

import com.ikon.core.DatabaseException;
import com.ikon.dao.HibernateUtil;
import com.ikon.dao.bean.StampImage;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StampImageDAO
{
  private static Logger log = LoggerFactory.getLogger(StampImageDAO.class);

  public static long create(StampImage si)
    throws DatabaseException
  {
    log.debug("create({})", si);
    Session session = null;
    Transaction tx = null;
    try
    {
      session = HibernateUtil.getSessionFactory().openSession();
      tx = session.beginTransaction();
      Long id = (Long)session.save(si);
      HibernateUtil.commit(tx);
      log.debug("create: {}", id);
      return id.longValue();
    }
    catch (HibernateException e) {
      throw new DatabaseException(e.getMessage(), e);
    } finally {
      HibernateUtil.close(session);
    }
  }

  public static void update(StampImage si)
    throws DatabaseException
  {
    log.debug("update({})", si);
    String qs = "select si.imageContent, si.imageMime from StampImage si where si.id=:id";
    Session session = null;
    Transaction tx = null;
    try
    {
      session = HibernateUtil.getSessionFactory().openSession();
      tx = session.beginTransaction();

      if ((si.getImageContent() == null) || (si.getImageContent().length() == 0)) {
        Query q = session.createQuery(qs);
        q.setParameter("id", Long.valueOf(si.getId()));
        Object[] data = (Object[])q.setMaxResults(1).uniqueResult();
        si.setImageContent((String)data[0]);
        si.setImageMime((String)data[1]);
      }

      session.update(si);
      HibernateUtil.commit(tx);
    }
    catch (HibernateException e) {
      throw new DatabaseException(e.getMessage(), e);
    } finally {
      HibernateUtil.close(session);
    }

    log.debug("update: void");
  }

  public static void active(int siId, boolean active)
    throws DatabaseException
  {
    log.debug("active({}, {})", Integer.valueOf(siId), Boolean.valueOf(active));
    String qs = "update StampImage si set si.active=:active where si.id=:id";
    Session session = null;
    Transaction tx = null;
    try
    {
      session = HibernateUtil.getSessionFactory().openSession();
      tx = session.beginTransaction();
      Query q = session.createQuery(qs);
      q.setBoolean("active", active);
      q.setInteger("id", siId);
      q.executeUpdate();
      HibernateUtil.commit(tx);
    }
    catch (HibernateException e) {
      throw new DatabaseException(e.getMessage(), e);
    } finally {
      HibernateUtil.close(session);
    }

    log.debug("active: void");
  }

  public static void delete(long siId)
    throws DatabaseException
  {
    log.debug("delete({})", Long.valueOf(siId));
    Session session = null;
    Transaction tx = null;
    try
    {
      session = HibernateUtil.getSessionFactory().openSession();
      tx = session.beginTransaction();
      StampImage si = (StampImage)session.load(StampImage.class, Long.valueOf(siId));
      session.delete(si);
      HibernateUtil.commit(tx);
    }
    catch (HibernateException e) {
      throw new DatabaseException(e.getMessage(), e);
    } finally {
      HibernateUtil.close(session);
    }

    log.debug("delete: void");
  }

  public static StampImage findByPk(long siId)
    throws DatabaseException
  {
    log.debug("findByPk({})", Long.valueOf(siId));
    String qs = "from StampImage si where si.id=:id";
    Session session = null;
    Transaction tx = null;
    try
    {
      session = HibernateUtil.getSessionFactory().openSession();
      tx = session.beginTransaction();
      Query q = session.createQuery(qs);
      q.setLong("id", siId);
      StampImage ret = (StampImage)q.setMaxResults(1).uniqueResult();
      HibernateUtil.commit(tx);
      log.debug("findByPk: {}", ret);
      return ret;
    }
    catch (HibernateException e) {
      throw new DatabaseException(e.getMessage(), e);
    } finally {
      HibernateUtil.close(session);
    }
  }

  public static List<StampImage> findAll()
    throws DatabaseException
  {
    log.debug("findAll()");
    String qs = "from StampImage si order by si.id";
    Session session = null;
    Transaction tx = null;
    try
    {
      session = HibernateUtil.getSessionFactory().openSession();
      tx = session.beginTransaction();
      Query q = session.createQuery(qs);
      @SuppressWarnings("unchecked")
	List<StampImage> ret = q.list();
      HibernateUtil.commit(tx);
      log.debug("findAll: {}", ret);
      return ret;
    }
    catch (HibernateException e) {
      throw new DatabaseException(e.getMessage(), e);
    } finally {
      HibernateUtil.close(session);
    }
  }

  public static List<StampImage> findByUser(String usrId)
    throws DatabaseException
  {
    log.debug("findByUser({})", usrId);
    String qs = "from StampImage si where :user in elements(si.users)and si.active=:active order by si.id";

    Session session = null;
    try
    {
      session = HibernateUtil.getSessionFactory().openSession();
      Query q = session.createQuery(qs);
      q.setString("user", usrId);
      q.setBoolean("active", true);
      @SuppressWarnings("unchecked")
	List<StampImage> ret = q.list();
      log.debug("findByUser: {}", ret);
      return ret;
    } catch (HibernateException e) {
      throw new DatabaseException(e.getMessage(), e);
    } finally {
      HibernateUtil.close(session);
    }
  }
}