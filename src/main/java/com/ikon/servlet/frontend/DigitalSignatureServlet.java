package com.ikon.servlet.frontend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ikon.core.AccessDeniedException;
import com.ikon.core.Config;
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
import com.ikon.dao.DigitalSignatureDAO;
import com.ikon.dao.bean.DigitalSignature;
import com.ikon.extension.core.ExtensionException;
import com.ikon.api.OKMDocument;
import com.ikon.automation.AutomationException;
import com.ikon.bean.Document;

import com.ikon.frontend.client.service.OKMDigitalSignatureService;
import com.ikon.util.DocConverter;
import com.ikon.util.FileUtils;
import com.ikon.util.UserActivity;

/**
 * Servlet implementation class SignServlet
 */
public class DigitalSignatureServlet extends OKMRemoteServiceServlet implements OKMDigitalSignatureService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(DigitalSignatureServlet.class);
	private static final File SIGNED_PDF_FOLDER = new File(Config.REPOSITORY_CACHE_SIGN + File.separator + "signed/");
	private File tmp = null;
 	private File tmpPdf = null;
 	private File tmpSignPfx = null;

	@Override
	public void deletePFX(String userId) {
		try {
			DigitalSignatureDAO.getInstance().deletePFX(userId);
			log.info("PFX successfully deleted for User: " + userId);
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
		}		
	}

	@Override
	public void signDocument(String docPath)  {
		String remoteUser = getThreadLocalRequest().getRemoteUser();
        String signPath = null;
        Document doc = null;
        
        try{
        	doc = OKMDocument.getInstance().getProperties(null, docPath);
        	signPath = Config.REPOSITORY_CACHE_SIGN + File.separator + doc.getUuid();
        	tmp = File.createTempFile("okm", ".tmp");
            tmpPdf = new File(signPath + ".pdf");
            tmpSignPfx = new File(Config.REPOSITORY_CACHE_SIGN + File.separator + remoteUser + ".pfx");
            
            if (!doc.getMimeType().equals("application/pdf")) {
                DocConverter converter = DocConverter.getInstance();
                
                InputStream is = OKMDocument.getInstance().getContent(null, docPath, false);
                FileUtils.copy(is, tmp);
                is.close();  

                if (doc.getMimeType().startsWith("image/"))
                  converter.img2pdf(tmp, doc.getMimeType(), tmpPdf);
                else {
                  converter.doc2pdf(tmp, doc.getMimeType(), tmpPdf);
                }

              } else {
            	  InputStream is = OKMDocument.getInstance().getContent(null, docPath, false);
                  FileUtils.copy(is, tmpPdf);
                  is.close();   
              }    
            
            //Sign Document
            signDocument(tmpSignPfx, tmpPdf);
		    
		    FileInputStream fis = new FileInputStream(SIGNED_PDF_FOLDER + "/" + doc.getUuid() + "_signed.pdf");
		    
		    if (!doc.getMimeType().equals("application/pdf")) {
				OKMDocument.getInstance().createSimple(null, doc.getPath() + "_signed.pdf", fis);
		    } else {
		    	OKMDocument.getInstance().checkout(null, doc.getPath());
				OKMDocument.getInstance().checkin(null, doc.getPath(), fis, "Signed");
		    }
			fis.close();
			
			UserActivity.log(remoteUser, "SIGN_DOCUMENT", doc.getUuid(), doc.getPath(), null);			
			log.info("Signed Document : " + doc.getTitle());
			
        }  catch (DatabaseException e) {
            log.error(e.getMessage(), e);
          } catch (RepositoryException e) {
            log.error(e.getMessage(), e);
          } catch (PathNotFoundException e) {
            log.error(e.getMessage(), e);
          } catch (IOException e) {
            log.error(e.getMessage(), e);
          } catch (NumberFormatException e) {
            log.error(e.getMessage(), e);
          } catch (UnsupportedMimeTypeException e) {
            log.error(e.getMessage(), e);
          } catch (FileSizeExceededException e) {
            log.error(e.getMessage(), e);
          } catch (UserQuotaExceededException e) {
            log.error(e.getMessage(), e);
          } catch (VirusDetectedException e) {
            log.error(e.getMessage(), e);
          } catch (ItemExistsException e) {
            log.error(e.getMessage(), e);
          } catch (AccessDeniedException e) {
            log.error(e.getMessage(), e);
          } catch (LockException e) {
            log.error(e.getMessage(), e);
          } catch (VersionException e) {
            log.error(e.getMessage(), e);
          } catch (ConversionException e) {
            log.error(e.getMessage(), e);
          } catch (ExtensionException e) {
            log.error(e.getMessage(), e);
          } catch (AutomationException e) {
            log.error(e.getMessage(), e);
          }  finally {
				tmpPdf.delete();
				tmpSignPfx.delete();
				new File(SIGNED_PDF_FOLDER + "/" + doc.getUuid() + "_signed.pdf").delete();
		}
	}

	private void signDocument(File pfxFile, File tempPdf) {
		String remoteUser = getThreadLocalRequest().getRemoteUser();
    	DigitalSignature sign = new DigitalSignature();
    	String password = null;
	    
		try {		
	    	sign = DigitalSignatureDAO.getInstance().getUserSignature(remoteUser);

	    	if(!sign.equals(null)){
				 //get sign pfx
				password = sign.getPfxpassword();
				InputStream inputStream = sign.getPfxfile().getBinaryStream();
	
		        FileUtils.copy(inputStream, pfxFile);
		        inputStream.close();
		        
		        //Start signing process
		        Runtime rt = Runtime.getRuntime();
		        		        
		        Process pr = rt.exec("java -jar " + Config.HOME_DIR + "/External-Apps/JSignPdf/JSignPdf.jar -kst PKCS12 -ksf " + pfxFile + " -ksp " + password + " " + tempPdf + " " + "-d " + SIGNED_PDF_FOLDER + " -V");
			    BufferedReader input1 = new BufferedReader(new InputStreamReader(pr.getInputStream()));
				
			    String line=null;
	
			    while((line=input1.readLine()) != null) {
			       log.info(line);
			    }			    
	
			    int exitVal = pr.waitFor();
			    log.info("Exited with error code "+exitVal); // end of console error output
	    	} 
		
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e){
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
