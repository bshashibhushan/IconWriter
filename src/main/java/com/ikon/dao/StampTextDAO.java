package com.ikon.dao;

import com.ikon.core.DatabaseException;
import com.ikon.dao.HibernateUtil;
import com.ikon.dao.bean.StampText;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StampTextDAO
{
  private static Logger log = LoggerFactory.getLogger(StampTextDAO.class);

  public static long create(StampText st)
    throws DatabaseException
  {
    log.debug("create({})", st);
    Session session = null;
    Transaction tx = null;
    try
    {
      session = HibernateUtil.getSessionFactory().openSession();
      tx = session.beginTransaction();
      Long id = (Long)session.save(st);
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

  public static void update(StampText st)
    throws DatabaseException
  {
    log.debug("update({})", st);
    Session session = null;
    Transaction tx = null;
    try
    {
      session = HibernateUtil.getSessionFactory().openSession();
      tx = session.beginTransaction();
      session.update(st);
      HibernateUtil.commit(tx);
    }
    catch (HibernateException e) {
      throw new DatabaseException(e.getMessage(), e);
    } finally {
      HibernateUtil.close(session);
    }

    log.debug("update: void");
  }

  public static void active(int stId, boolean active)
    throws DatabaseException
  {
    log.debug("active({}, {})", Integer.valueOf(stId), Boolean.valueOf(active));
    String qs = "update StampText st set st.active=:active where st.id=:id";
    Session session = null;
    Transaction tx = null;
    try
    {
      session = HibernateUtil.getSessionFactory().openSession();
      tx = session.beginTransaction();
      Query q = session.createQuery(qs);
      q.setBoolean("active", active);
      q.setInteger("id", stId);
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

  public static void delete(int stId)
    throws DatabaseException
  {
    log.debug("delete({})", Long.valueOf(stId));
    Session session = null;
    Transaction tx = null;
    try
    {
      session = HibernateUtil.getSessionFactory().openSession();
      tx = session.beginTransaction();
      StampText st = (StampText)session.load(StampText.class, Long.valueOf(stId));
      session.delete(st);
      HibernateUtil.commit(tx);
    }
    catch (HibernateException e) {
      throw new DatabaseException(e.getMessage(), e);
    } finally {
      HibernateUtil.close(session);
    }

    log.debug("delete: void");
  }

  public static StampText findByPk(long stId)
    throws DatabaseException
  {
    log.debug("findByPk({})", Long.valueOf(stId));
    String qs = "from StampText st where st.id=:id";
    Session session = null;
    try
    {
      session = HibernateUtil.getSessionFactory().openSession();
      Query q = session.createQuery(qs);
      q.setLong("id", stId);
      StampText ret = (StampText)q.setMaxResults(1).uniqueResult();
      log.debug("findByPk: {}", ret);
      return ret;
    } catch (HibernateException e) {
      throw new DatabaseException(e.getMessage(), e);
    } finally {
      HibernateUtil.close(session);
    }
  }

  public static List<StampText> findAll()
    throws DatabaseException
  {
    log.debug("findAll()");
    String qs = "from StampText st order by st.id";
    Session session = null;
    try
    {
      session = HibernateUtil.getSessionFactory().openSession();
      Query q = session.createQuery(qs);
      @SuppressWarnings("unchecked")
	List<StampText> ret = q.list();
      log.debug("findAll: {}", ret);
      return ret;
    } catch (HibernateException e) {
      throw new DatabaseException(e.getMessage(), e);
    } finally {
      HibernateUtil.close(session);
    }
  }

  public static List<StampText> findByUser(String usrId)
    throws DatabaseException
  {
    log.debug("findByUser({})", usrId);
    String qs = "from StampText st where :user in elements(st.users)and st.active=:active order by st.id";

    Session session = null;
    try
    {
      session = HibernateUtil.getSessionFactory().openSession();
      Query q = session.createQuery(qs);
      q.setString("user", usrId);
      q.setBoolean("active", true);
      @SuppressWarnings("unchecked")
	List<StampText> ret = q.list();
      log.debug("findByUser: {}", ret);
      return ret;
    } catch (HibernateException e) {
      throw new DatabaseException(e.getMessage(), e);
    } finally {
      HibernateUtil.close(session);
    }
  }
}