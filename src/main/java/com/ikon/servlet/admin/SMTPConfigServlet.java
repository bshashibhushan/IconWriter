package com.ikon.servlet.admin;

import java.io.IOException;

import javax.mail.AuthenticationFailedException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jfree.util.Log;

import com.ikon.core.DatabaseException;
import com.ikon.core.SMTPException;
import com.ikon.dao.SMTPUtilsDAO;
import com.ikon.dao.bean.SMTPConfig;
import com.ikon.util.SMTPUtils;
import com.ikon.util.WebUtils;

/**
 * Servlet implementation class SMTPConfig
 */
public class SMTPConfigServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SMTPConfigServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try{
			ServletContext sc = getServletContext();
			
			if(SMTPUtilsDAO.getSMTPServerDetails()!=null){
				SMTPConfig config = SMTPUtilsDAO.getSMTPServerDetails();	
				sc.setAttribute("smtphost", config.getSmtphost());
				sc.setAttribute("smtpport", config.getSmtpport());
				sc.setAttribute("username", config.getUsername());
				sc.setAttribute("password", "");
			}		
			
			sc.getRequestDispatcher("/admin/smtp_config.jsp").forward(request, response);
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String action = WebUtils.getString(request, "action");
			if (action.equals("check")) {
				try {  
					check(request, response);
				} catch (AuthenticationFailedException e) {
					request.setAttribute("ERROR", "Authentication Failed ");
					request.getRequestDispatcher("/admin/smtp_config.jsp").forward(request, response);
				} catch (SMTPException e) {
					if(e.getMessage().contains("Authentication")){
						request.setAttribute("ERROR", "Authentication Required");
					} else{
						request.setAttribute("ERROR", "Connection Couldn't be established");
					}
					request.getRequestDispatcher("/admin/smtp_config.jsp").forward(request, response);
				} 
			} 	
	}

	private void check(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, SMTPException, AuthenticationFailedException {
		Log.info("Verifying SMTP Configuration");
		SMTPConfig sc = new SMTPConfig();
		sc.setUsername(WebUtils.getString(request, "username"));
		sc.setPassword(WebUtils.getString(request, "password"));
		sc.setSmtphost(WebUtils.getString(request, "smtphost"));
		sc.setSmtpport(WebUtils.getString(request, "smtpport"));
		sc.setSSL(false);
		// test Connection
		SMTPUtils.testConnection(sc);
		
		try{
			if(SMTPUtilsDAO.getSMTPServerDetails()!=null){
				SMTPUtilsDAO.deleteSMTPServerDetails();
			}
			
			SMTPUtilsDAO.saveSMTPServerDetails(sc);
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		request.setAttribute("SUCCESS", "Connection established successfully.");
		request.getRequestDispatcher("/admin/user_list.jsp").forward(request, response);
	}
	
}
