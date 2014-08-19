package com.ikon.servlet.frontend;

import bsh.EvalError;
import com.lowagie.text.DocumentException;
import com.ikon.api.OKMDocument;
import com.ikon.automation.AutomationException;
import com.ikon.bean.Document;
import com.ikon.core.AccessDeniedException;
import com.ikon.core.ConversionException;
import com.ikon.core.DatabaseException;
import com.ikon.core.FileSizeExceededException;
import com.ikon.core.ItemExistsException;
import com.ikon.core.LockException;
import com.ikon.core.PathNotFoundException;
import com.ikon.core.RepositoryException;
import com.ikon.core.UnsupportedMimeTypeException;
import com.ikon.core.UserQuotaExceededException;
import com.ikon.core.VersionException;
import com.ikon.core.VirusDetectedException;
import com.ikon.extension.core.ExtensionException;
import com.ikon.dao.StampImageDAO;
import com.ikon.dao.StampTextDAO;
import com.ikon.dao.bean.StampImage;
import com.ikon.dao.bean.StampText;
import com.ikon.frontend.client.service.OKMStampService;
import com.ikon.frontend.client.OKMException;
import com.ikon.frontend.client.bean.GWTStamp;
import com.ikon.frontend.client.constants.service.ErrorCode;
import com.ikon.servlet.frontend.OKMRemoteServiceServlet;
import com.ikon.util.DocConverter;
import com.ikon.util.GWTUtil;
import com.ikon.util.PDFUtils;
import com.ikon.util.PathUtils;
import com.ikon.util.SecureStore;
import com.ikon.util.UserActivity;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StampServlet extends OKMRemoteServiceServlet
  implements OKMStampService
{
  private static final long serialVersionUID = 1L;
  private static Logger log = LoggerFactory.getLogger(StampServlet.class);

  public List<GWTStamp> findAll() throws OKMException
  {
    log.debug("findAll()");
    updateSessionManager();
    List<GWTStamp> stampList = new ArrayList<GWTStamp>();
    String remoteUser = getThreadLocalRequest().getRemoteUser();
    try {
      for (StampText stampText : StampTextDAO.findByUser(remoteUser)) {
        stampList.add(GWTUtil.copy(stampText));
      }
      for (StampImage stampImage : StampImageDAO.findByUser(remoteUser))
        stampList.add(GWTUtil.copy(stampImage));
    }
    catch (DatabaseException e) {
      log.error(e.getMessage(), e);
      throw new OKMException(ErrorCode.get("026", "024"), e.getMessage());
    }

    return stampList;
  }

  public void Stamp(long id, int type, String path) throws OKMException
  {
    log.debug("Stamp({}, {})", new Object[] { Long.valueOf(id), Integer.valueOf(type) });
    updateSessionManager();
    File tmp = null;
    File tmpPdf = null;
    File tmpStampPdf = null;
    try
    {
      Document doc = OKMDocument.getInstance().getProperties(null, path);
      tmp = File.createTempFile("okm", ".tmp");
      tmpPdf = File.createTempFile("okm", ".pdf");
      tmpStampPdf = File.createTempFile("okm", ".pdf");
      FileOutputStream fos = null;
      String remoteUser = getThreadLocalRequest().getRemoteUser();

      if (doc.getMimeType().equals("application/pdf"))
        fos = new FileOutputStream(tmpPdf);
      else {
        fos = new FileOutputStream(tmp);
      }

      InputStream is = OKMDocument.getInstance().getContent(null, path, false);
      IOUtils.copy(is, fos);
      fos.flush();
      fos.close();
      is.close();

      if (!doc.getMimeType().equals("application/pdf")) {
        DocConverter converter = DocConverter.getInstance();

        if (doc.getMimeType().startsWith("image/"))
          converter.img2pdf(tmp, doc.getMimeType(), tmpPdf);
        else {
          converter.doc2pdf(tmp, doc.getMimeType(), tmpPdf);
        }

      }

      is = new FileInputStream(tmpPdf);
      fos = new FileOutputStream(tmpStampPdf);

      switch (type) {
      case 0:
        StampText st = StampTextDAO.findByPk(id);
        PDFUtils.stampText(is, st.getText(), st.getLayer(), st.getOpacity(), st.getSize(), Color.decode(st.getColor()), st.getRotation(), st.getAlign(), st.getExprX(), st.getExprY(), fos);
		UserActivity.log(remoteUser, "STAMP_TEXT", doc.getUuid(), path, null);

        break;
      case 1:
        StampImage si = StampImageDAO.findByPk(id);
        byte[] image = SecureStore.b64Decode(si.getImageContent());
        PDFUtils.stampImage(is, image, si.getLayer(), si.getOpacity(), si.getExprX(), si.getExprY(), fos);
		UserActivity.log(remoteUser, "STAMP_IMAGE", doc.getUuid(), path, null);
      }

      fos.close();
      is.close();

      is = new FileInputStream(tmpStampPdf);

      if (!doc.getMimeType().equals("application/pdf")) {
        Document newDoc = new Document();
        String parentFld = PathUtils.getParent(path);
        String docName = PathUtils.getName(path);
        int idx = docName.lastIndexOf('.');

        if (idx > 0) {
          docName = docName.substring(0, idx);
        }

        newDoc.setPath(parentFld + "/" + docName + ".pdf");
        OKMDocument.getInstance().create(null, newDoc, is);
      } else {
        OKMDocument.getInstance().checkout(null, path);
        OKMDocument.getInstance().checkin(null, path, is, "Stamped");
      }
      is.close();
    }
    catch (DatabaseException e) {
      log.error(e.getMessage(), e);
      throw new OKMException(ErrorCode.get("026", "024"), e.getMessage());
    } catch (RepositoryException e) {
      log.error(e.getMessage(), e);
      throw new OKMException(ErrorCode.get("026", "001"), e.getMessage());
    } catch (PathNotFoundException e) {
      log.error(e.getMessage(), e);
      throw new OKMException(ErrorCode.get("026", "015"), e.getMessage());
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      throw new OKMException(ErrorCode.get("026", "013"), e.getMessage());
    } catch (NumberFormatException e) {
      log.error(e.getMessage(), e);
      throw new OKMException(ErrorCode.get("026", "028"), e.getMessage());
    } catch (DocumentException e) {
      log.error(e.getMessage(), e);
      throw new OKMException(ErrorCode.get("026", "030"), e.getMessage());
    } catch (EvalError e) {
      log.error(e.getMessage(), e);
      throw new OKMException(ErrorCode.get("026", "031"), e.getMessage());
    } catch (UnsupportedMimeTypeException e) {
      log.error(e.getMessage(), e);
      throw new OKMException(ErrorCode.get("026", "010"), e.getMessage());
    } catch (FileSizeExceededException e) {
      log.error(e.getMessage(), e);
      throw new OKMException(ErrorCode.get("026", "011"), e.getMessage());
    } catch (UserQuotaExceededException e) {
      log.error(e.getMessage(), e);
      throw new OKMException(ErrorCode.get("026", "023"), e.getMessage());
    } catch (VirusDetectedException e) {
      log.error(e.getMessage(), e);
      throw new OKMException(ErrorCode.get("026", "032"), e.getMessage());
    } catch (ItemExistsException e) {
      log.error(e.getMessage(), e);
      throw new OKMException(ErrorCode.get("026", "003"), e.getMessage());
    } catch (AccessDeniedException e) {
      log.error(e.getMessage(), e);
      throw new OKMException(ErrorCode.get("026", "009"), e.getMessage());
    } catch (LockException e) {
      log.error(e.getMessage(), e);
      throw new OKMException(ErrorCode.get("026", "004"), e.getMessage());
    } catch (VersionException e) {
      log.error(e.getMessage(), e);
      throw new OKMException(ErrorCode.get("026", "016"), e.getMessage());
    } catch (ConversionException e) {
      log.error(e.getMessage(), e);
      throw new OKMException(ErrorCode.get("026", "033"), e.getMessage());
    } catch (ExtensionException e) {
      log.error(e.getMessage(), e);
      throw new OKMException(ErrorCode.get("026", "041"), e.getMessage());
    } catch (AutomationException e) {
      log.error(e.getMessage(), e);
      throw new OKMException(ErrorCode.get("026", "046"), e.getMessage());
    }
    finally {
      tmp.delete();
      tmpPdf.delete();
      tmpStampPdf.delete();
    }
  }
}