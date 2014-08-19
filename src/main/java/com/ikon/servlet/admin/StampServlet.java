package com.ikon.servlet.admin;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bsh.EvalError;

import com.lowagie.text.DocumentException;
import com.ikon.core.DatabaseException;
import com.ikon.core.MimeTypeConfig;
import com.ikon.dao.StampImageDAO;
import com.ikon.dao.StampTextDAO;
import com.ikon.dao.bean.StampImage;
import com.ikon.dao.bean.StampText;
import com.ikon.module.common.CommonAuthModule;
import com.ikon.principal.PrincipalAdapterException;
import com.ikon.servlet.admin.BaseServlet;
import com.ikon.util.PDFUtils;
import com.ikon.util.SecureStore;
import com.ikon.util.UserActivity;
import com.ikon.util.WebUtils;

public class StampServlet extends BaseServlet
{
  private static final long serialVersionUID = 1L;
  private static Logger log = LoggerFactory.getLogger(StampServlet.class);
 
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    log.debug("doGet({}, {})", request, response);
    request.setCharacterEncoding("UTF-8");
    String action = WebUtils.getString(request, "action");
    String userId = request.getRemoteUser();
    updateSessionManager(request);
    try
    {
      if (action.equals("textCreate"))
        textCreate(userId, request, response);
      else if (action.equals("imageCreate"))
        imageCreate(userId, request, response);
      else if (action.equals("textEdit"))
        textEdit(userId, request, response);
      else if (action.equals("imageEdit"))
        imageEdit(userId, request, response);
      else if (action.equals("textDelete"))
        textDelete(userId, request, response);
      else if (action.equals("textColor"))
        textColor(userId, request, response);
      else if (action.equals("textTest"))
        textTest(userId, request, response);
      else if (action.equals("imageDelete"))
        imageDelete(userId, request, response);
      else if (action.equals("textActive"))
        textActive(userId, request, response);
      else if (action.equals("imageActive"))
        imageActive(userId, request, response);
      else if (action.equals("imageView"))
        imageView(userId, request, response);
      else if (action.equals("imageTest")) {
        imageTest(userId, request, response);
      }

      if ((action.equals("")) || (action.equals("textList")) || (action.equals("textActive")) || ((action.startsWith("text")) && (WebUtils.getBoolean(request, "persist"))))
      {
        textList(userId, request, response);
      } else if ((action.equals("imageList")) || (action.equals("imageActive")))
        imageList(userId, request, response);
    }
    catch (DatabaseException e) {
      log.error(e.getMessage(), e);
      sendErrorRedirect(request, response, e);
    } catch (NoSuchAlgorithmException e) {
      log.error(e.getMessage(), e);
      sendErrorRedirect(request, response, e);
    } catch (PrincipalAdapterException e) {
      log.error(e.getMessage(), e);
      sendErrorRedirect(request, response, e);
    } catch (DocumentException e) {
      log.error(e.getMessage(), e);
      sendErrorRedirect(request, response, e);
    } catch (EvalError e) {
      log.error(e.getMessage(), e);
      sendErrorRedirect(request, response, e);
    } catch (Exception e) {
        log.error(e.getMessage(), e);
        sendErrorRedirect(request, response, e);
      }
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
  {
    log.debug("doPost({}, {})", request, response);
    request.setCharacterEncoding("UTF-8");
    String action = WebUtils.getString(request, "action");
    String userId = request.getRemoteUser();
    updateSessionManager(request);
    try
    {
      if (ServletFileUpload.isMultipartContent(request)) {
        InputStream is = null;
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        List<?> items = upload.parseRequest(request);
        StampImage si = new StampImage();

        for (Iterator<?> it = items.iterator(); it.hasNext(); ) {
          FileItem item = (FileItem)it.next();

          if (item.isFormField()) {
            if (item.getFieldName().equals("action"))
              action = item.getString("UTF-8");
            else if (item.getFieldName().equals("si_id"))
              si.setId(Integer.parseInt(item.getString("UTF-8")));
            else if (item.getFieldName().equals("si_name"))
              si.setName(item.getString("UTF-8"));
            else if (item.getFieldName().equals("si_description"))
              si.setDescription(item.getString("UTF-8"));
            else if (item.getFieldName().equals("si_layer"))
              si.setLayer(Integer.parseInt(item.getString("UTF-8")));
            else if (item.getFieldName().equals("si_opacity"))
              si.setOpacity(Float.parseFloat(item.getString("UTF-8")));
            else if (item.getFieldName().equals("si_expr_x"))
              si.setExprX(item.getString("UTF-8"));
            else if (item.getFieldName().equals("si_expr_y"))
              si.setExprY(item.getString("UTF-8"));
            else if (item.getFieldName().equals("si_active"))
              si.setActive(true);
            else if (item.getFieldName().equals("si_users"))
              si.getUsers().add(item.getString("UTF-8"));
          }
          else {
            is = item.getInputStream();
            si.setImageMime(MimeTypeConfig.mimeTypes.getContentType(item.getName()));
            si.setImageContent(SecureStore.b64Encode(IOUtils.toByteArray(is)));
            is.close();
          }
        }

        if (action.equals("imageCreate")) {
          long id = StampImageDAO.create(si);

          UserActivity.log(userId, "ADMIN_STAMP_IMAGE_CREATE", Long.toString(id), null, si.toString());
          imageList(userId, request, response);
        } else if (action.equals("imageEdit")) {
          StampImageDAO.update(si);

          UserActivity.log(userId, "ADMIN_STAMP_IMAGE_EDIT", Long.toString(si.getId()), null, si.toString());
          imageList(userId, request, response);
        } else if (action.equals("imageDelete")) {
          StampImageDAO.delete(si.getId());

          UserActivity.log(userId, "ADMIN_STAMP_IMAGE_DELETE", Long.toString(si.getId()), null, null);
          imageList(userId, request, response);
        }
      }
    } catch (DatabaseException e) {
      log.error(e.getMessage(), e);
      sendErrorRedirect(request, response, e);
    } catch (FileUploadException e) {
      log.error(e.getMessage(), e);
      sendErrorRedirect(request, response, e);
    }
  }

  private void textCreate(String userId, HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    log.debug("textCreate({}, {}, {})", new Object[] { userId, request, response });
    
    if (WebUtils.getBoolean(request, "persist")) {
		final String name = WebUtils.getString(request, "st_name"), description = WebUtils
				.getString(request, "st_description"), text = WebUtils
				.getString(request, "st_text"), color = WebUtils.getString(
				request, "st_color"), exprx = WebUtils.getString(request,
				"st_expr_x"), expry = WebUtils.getString(request,
				"st_expr_y");
		final int layer = WebUtils.getInt(request, "st_layer"), size = WebUtils
				.getInt(request, "st_size"), align = WebUtils.getInt(
				request, "st_align"), rotation = WebUtils.getInt(request,
				"st_rotation");
		final float opacity = WebUtils.getFloat(request, "st_opacity");
		final boolean active = WebUtils.getBoolean(request, "st_active");
		final Set<String> users = new HashSet<String>(
				WebUtils.getStringList(request, "st_users"));
		StampText st = new StampText();
		st.setName(name);
		st.setDescription(description);
		st.setText(text);
		st.setLayer(layer);
		st.setOpacity(opacity);
		st.setSize(size);
		st.setColor(color);
		st.setAlign(align);
		st.setRotation(rotation);
		st.setExprX(exprx);
		st.setExprY(expry);
		st.setActive(active);
		st.setUsers(users);

		long id = StampTextDAO.create(st);

      UserActivity.log(userId, "ADMIN_STAMP_TEXT_CREATE", Long.toString(id), null, st.toString());
    } else {
      ServletContext sc = getServletContext();
      sc.setAttribute("action", WebUtils.getString(request, "action"));
      sc.setAttribute("persist", Boolean.valueOf(true));
      sc.setAttribute("users", CommonAuthModule.getUsers(null));
      sc.setAttribute("stamp", new StampText());
      sc.getRequestDispatcher("/admin/stamp_text_edit.jsp").forward(request, response);
    }

    log.debug("textCreate: void");
  }

  private void textEdit(String userId, HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    log.debug("textEdit({}, {}, {})", new Object[] { userId, request, response });

    if (WebUtils.getBoolean(request, "persist")) {
		final String name = WebUtils.getString(request, "st_name"), description = WebUtils
				.getString(request, "st_description"), text = WebUtils
				.getString(request, "st_text"), color = WebUtils.getString(
				request, "st_color"), exprx = WebUtils.getString(request,
				"st_expr_x"), expry = WebUtils.getString(request,
				"st_expr_y");
		final int layer = WebUtils.getInt(request, "st_layer"), size = WebUtils
				.getInt(request, "st_size"), align = WebUtils.getInt(
				request, "st_align"), rotation = WebUtils.getInt(request,
				"st_rotation");
		final long id = WebUtils.getLong(request, "st_id");
		final float opacity = WebUtils.getFloat(request, "st_opacity");
		final boolean active = WebUtils.getBoolean(request, "st_active");
		final Set<String> users = new HashSet<String>(
				WebUtils.getStringList(request, "st_users"));
		StampText st = new StampText();
		st.setId(id);
		st.setName(name);
		st.setDescription(description);
		st.setText(text);
		st.setLayer(layer);
		st.setOpacity(opacity);
		st.setSize(size);
		st.setColor(color);
		st.setAlign(align);
		st.setRotation(rotation);
		st.setExprX(exprx);
		st.setExprY(expry);
		st.setActive(active);
		st.setUsers(users);

		StampTextDAO.update(st);

      UserActivity.log(userId, "ADMIN_STAMP_TEXT_EDIT", Long.toString(st.getId()), null, st.toString());
    } else {
      ServletContext sc = getServletContext();
      int stId = WebUtils.getInt(request, "st_id");
      sc.setAttribute("action", WebUtils.getString(request, "action"));
      sc.setAttribute("persist", Boolean.valueOf(true));
      sc.setAttribute("users", CommonAuthModule.getUsers(null));
      sc.setAttribute("stamp", StampTextDAO.findByPk(stId));
      sc.getRequestDispatcher("/admin/stamp_text_edit.jsp").forward(request, response);
    }

    log.debug("textEdit: void");
  }

  private void textDelete(String userId, HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    log.debug("textDelete({}, {}, {})", new Object[] { userId, request, response });

    if (WebUtils.getBoolean(request, "persist")) {
      int stId = WebUtils.getInt(request, "st_id");
      StampTextDAO.delete(stId);

      UserActivity.log(userId, "ADMIN_STAMP_TEXT_DELETE", Integer.toString(stId), null, null);
    } else {
      ServletContext sc = getServletContext();
      int stId = WebUtils.getInt(request, "st_id");
      sc.setAttribute("action", WebUtils.getString(request, "action"));
      sc.setAttribute("persist", Boolean.valueOf(true));
      sc.setAttribute("users", CommonAuthModule.getUsers(null));
      sc.setAttribute("stamp", StampTextDAO.findByPk(stId));
      sc.getRequestDispatcher("/admin/stamp_text_edit.jsp").forward(request, response);
    }

    log.debug("textDelete: void");
  }

  private void textActive(String userId, HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    log.debug("textActive({}, {}, {})", new Object[] { userId, request, response });
    int stId = WebUtils.getInt(request, "st_id");
    boolean active = WebUtils.getBoolean(request, "st_active");
    StampTextDAO.active(stId, active);

    UserActivity.log(userId, "ADMIN_STAMP_TEXT_ACTIVE", Integer.toString(stId), null, Boolean.toString(active));
    log.debug("textActive: void");
  }

  private void textList(String userId, HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException, DatabaseException, PrincipalAdapterException
  {
    log.debug("textList({}, {}, {})", new Object[] { userId, request, response });
    ServletContext sc = getServletContext();
    sc.setAttribute("stamps", StampTextDAO.findAll());
    sc.getRequestDispatcher("/admin/stamp_text_list.jsp").forward(request, response);
    log.debug("textList: void");
  }

  private void textColor(String userId, HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException, DatabaseException
  {
    log.debug("textColor({}, {}, {})", new Object[] { userId, request, response });
    int stId = WebUtils.getInt(request, "st_id");
    StampText st = StampTextDAO.findByPk(stId);
    BufferedImage bi = new BufferedImage(16, 16, 1);
    Graphics g = bi.getGraphics();
    g.setColor(Color.decode(st.getColor()));
    g.fillRect(0, 0, 16, 16);
    response.setContentType("image/jpeg");
    ImageIO.write(bi, "jpg", response.getOutputStream());
    log.debug("textColor: void");
  }

  private void textTest(String userId, HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException, DatabaseException, DocumentException, EvalError
  {
    log.debug("textTest({}, {}, {})", new Object[] { userId, request, response });
    int stId = WebUtils.getInt(request, "st_id");
    StampText st = StampTextDAO.findByPk(stId);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PDFUtils.generateSample(5, baos);
    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    baos = new ByteArrayOutputStream();
    PDFUtils.stampText(bais, st.getText(), st.getLayer(), st.getOpacity(), st.getSize(), Color.decode(st.getColor()), st.getRotation(), st.getAlign(), st.getExprX(), st.getExprY(), baos);

    bais = new ByteArrayInputStream(baos.toByteArray());
    WebUtils.sendFile(request, response, "sample.pdf", MimeTypeConfig.MIME_PDF, false, bais);
    log.debug("textTest: void");
  }

  private void imageCreate(String userId, HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException, DatabaseException, PrincipalAdapterException
  {
    log.debug("imageCreate({}, {}, {})", new Object[] { userId, request, response });
    ServletContext sc = getServletContext();
    sc.setAttribute("action", WebUtils.getString(request, "action"));
    sc.setAttribute("persist", Boolean.valueOf(true));
    sc.setAttribute("users", CommonAuthModule.getUsers(null));
    sc.setAttribute("stamp", new StampImage());
    sc.getRequestDispatcher("/admin/stamp_image_edit.jsp").forward(request, response);
    log.debug("imageCreate: void");
  }

  private void imageEdit(String userId, HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException, DatabaseException, NoSuchAlgorithmException, PrincipalAdapterException
  {
    log.debug("imageEdit({}, {}, {})", new Object[] { userId, request, response });
    ServletContext sc = getServletContext();
    int siId = WebUtils.getInt(request, "si_id");
    sc.setAttribute("action", WebUtils.getString(request, "action"));
    sc.setAttribute("persist", Boolean.valueOf(true));
    sc.setAttribute("users", CommonAuthModule.getUsers(null));
    sc.setAttribute("stamp", StampImageDAO.findByPk(siId));
    sc.getRequestDispatcher("/admin/stamp_image_edit.jsp").forward(request, response);
    log.debug("imageEdit: void");
  }

  private void imageDelete(String userId, HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException, DatabaseException, NoSuchAlgorithmException, PrincipalAdapterException
  {
    log.debug("imageDelete({}, {}, {})", new Object[] { userId, request, response });
    ServletContext sc = getServletContext();
    int siId = WebUtils.getInt(request, "si_id");
    sc.setAttribute("action", WebUtils.getString(request, "action"));
    sc.setAttribute("persist", Boolean.valueOf(true));
    sc.setAttribute("users", CommonAuthModule.getUsers(null));
    sc.setAttribute("stamp", StampImageDAO.findByPk(siId));
    sc.getRequestDispatcher("/admin/stamp_image_edit.jsp").forward(request, response);
    log.debug("imageDelete: void");
  }

  private void imageActive(String userId, HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException, DatabaseException
  {
    log.debug("imageActive({}, {}, {})", new Object[] { userId, request, response });
    int siId = WebUtils.getInt(request, "si_id");
    boolean active = WebUtils.getBoolean(request, "si_active");
    StampImageDAO.active(siId, active);

    UserActivity.log(userId, "ADMIN_STAMP_IMAGE_ACTIVE", Integer.toString(siId), null, Boolean.toString(active));
    log.debug("imageActive: void");
  }

  private void imageList(String userId, HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException, DatabaseException
  {
    log.debug("imageList({}, {}, {})", new Object[] { userId, request, response });
    ServletContext sc = getServletContext();
    sc.setAttribute("stamps", StampImageDAO.findAll());
    sc.getRequestDispatcher("/admin/stamp_image_list.jsp").forward(request, response);
    log.debug("imageList: void");
  }

  private void imageView(String userId, HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException, DatabaseException
  {
    log.debug("imageView({}, {}, {})", new Object[] { userId, request, response });
    int siId = WebUtils.getInt(request, "si_id");
    StampImage si = StampImageDAO.findByPk(siId);
    response.setContentType(si.getImageMime());
    ServletOutputStream sos = response.getOutputStream();
    sos.write(SecureStore.b64Decode(si.getImageContent()));
    sos.flush();
    sos.close();
    log.debug("imageView: void");
  }

  private void imageTest(String userId, HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException, DatabaseException, DocumentException, EvalError
  {
    log.debug("imageTest({}, {}, {})", new Object[] { userId, request, response });
    int siId = WebUtils.getInt(request, "si_id");
    StampImage si = StampImageDAO.findByPk(siId);
    byte[] image = SecureStore.b64Decode(si.getImageContent());
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PDFUtils.generateSample(5, baos);
    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    baos = new ByteArrayOutputStream();
    PDFUtils.stampImage(bais, image, si.getLayer(), si.getOpacity(), si.getExprX(), si.getExprY(), baos);
    bais = new ByteArrayInputStream(baos.toByteArray());
    WebUtils.sendFile(request, response, "sample.pdf", MimeTypeConfig.MIME_PDF, false, bais);
    log.debug("imageTest: void");
  }
}