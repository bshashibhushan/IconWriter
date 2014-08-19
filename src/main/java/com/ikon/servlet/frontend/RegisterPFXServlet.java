package com.ikon.servlet.frontend;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
 
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ikon.dao.DigitalSignatureDAO;
import com.ikon.dao.bean.DigitalSignature;
import com.ikon.util.WebUtils;
 
public class RegisterPFXServlet extends HttpServlet {
     
    private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(RegisterPFXServlet.class);
     
    @SuppressWarnings("deprecation")
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	String pfxPass = WebUtils.getString(request, "pfxPass");
    	String user = WebUtils.getString(request, "user");
       
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload fileUpload  = new ServletFileUpload(factory);
         
        if (!ServletFileUpload.isMultipartContent(request)) {
              try {                 
                throw new FileUploadException("error multipart request not found");
            } catch (FileUploadException e) {
                log.error(e.getMessage(), e);
            }
        }
                         
        try { 
            List<FileItem> items = fileUpload.parseRequest(request);
             
            if (items == null) {           
                response.getWriter().write("File not correctly uploaded");
                return;
            }
             
            Iterator<FileItem> iter = items.iterator();
 
            while (iter.hasNext()) {
                FileItem item = (FileItem) iter.next();
                 
               if (item.getName() != null) {                                                        
                    DigitalSignature sign = new DigitalSignature();
                    sign.setUserId(user);
                    sign.setPfxfile(Hibernate.createBlob(item.get()));
                    sign.setPfxpassword(pfxPass);
                    DigitalSignatureDAO.getInstance().registerPFX(sign);
                    log.info("Signature successfully added for User: " + user);
               }
            }
             
            PrintWriter out = response.getWriter();
            response.setHeader("Content-Type", "text/html");
            out.println("Upload OK");
            out.flush();
            out.close();
 
        } catch (SizeLimitExceededException e) {
            log.error(e.getMessage(), e);         
        } catch (Exception e) {
            e.printStackTrace();
            PrintWriter out = response.getWriter();
            response.setHeader("Content-Type", "text/html");
            log.error(e.getMessage(), e);
            out.flush();
            out.close();
        }
         
    }
     
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
 
}