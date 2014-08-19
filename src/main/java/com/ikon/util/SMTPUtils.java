package com.ikon.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Date;
import java.util.Properties;

import javax.mail.AuthenticationFailedException;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ikon.api.OKMDocument;
import com.ikon.core.AccessDeniedException;
import com.ikon.core.Config;
import com.ikon.core.DatabaseException;
import com.ikon.core.PathNotFoundException;
import com.ikon.core.RepositoryException;
import com.ikon.core.SMTPException;
import com.ikon.dao.SMTPUtilsDAO;
import com.ikon.dao.bean.SMTPConfig;

public class SMTPUtils {
	private static Logger log = LoggerFactory.getLogger(SMTPUtils.class);
	
	public static void testConnection(SMTPConfig sc) throws IOException, SMTPException, AuthenticationFailedException{
		log.info("Testing SMTP Configuration");
		final String username = sc.getUsername();
		final String password = sc.getPassword();
		Properties props = new Properties();
		props.put("mail.smtp.auth", true);
		props.put("mail.smtp.starttls.enable", true);
		props.put("mail.smtp.host", sc.getSmtphost());
		props.put("mail.smtp.port", sc.getSmtpport());
		System.out.println("host : "+sc.getSmtphost()+" port : "+sc.getSmtpport()+" username : "+username+" password : "+password+" isSSL : "+sc.isSSL());
		
		try {
			
		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });
			
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("test@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("test@gmail.com"));
			message.setSubject("openkm : Connection Successfully Established");
			message.setText("Dear openkm Subscriber,"+ "\n\n Connection SuccessFully Established!");
			Transport.send(message);
			log.info("SMTP Configuration Done!!");
		} catch (AuthenticationFailedException e) {
			log.info("Connection couldnt be established" + " :" + e.getMessage());
			throw new AuthenticationFailedException();
		} catch (MessagingException e) {
			log.info("Connection couldnt be established" + " :" + e.getMessage());
			throw new SMTPException(e);
		} 
	}
	
	public static void sendMails(String from, Collection<String> recipients,String sub, String body, String nodePath) throws DatabaseException, 
	    IOException, MessagingException, PathNotFoundException, AccessDeniedException, RepositoryException, SMTPException
	{
		if(SMTPUtilsDAO.getSMTPServerDetails()==null){
			throw new SMTPException("Please configure your SMTP server through administration");
		}
		SMTPConfig sc = SMTPUtilsDAO.getSMTPServerDetails();
		try {
			if(nodePath!=null)
			{
				sendDocument(sc, recipients, sub, body, nodePath);	
			}
			
			else
			{
				log.debug("send({}, {}, {}, {}, {})", new Object[] { from, recipients, sub, body, nodePath });
				log.debug("Inside sendMails");
				final String username = sc.getUsername();
				final String password = sc.getPassword();
				
				Properties props = new Properties();
				props.put("mail.smtp.auth", true);
				props.put("mail.smtp.starttls.enable", true);
				props.put("mail.smtp.host", sc.getSmtphost());
				props.put("mail.smtp.port", sc.getSmtpport());
		
				Session session = Session.getInstance(props,
				  new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
					}
				  });
				Message message = new MimeMessage(session);
				log.info("Sending Mail : "+recipients);

				for(String mailId: recipients)
				{
					message.setFrom(new InternetAddress(mailId));
					message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(mailId));
					message.setSubject(sub);
					message.setText(body);
					Transport.send(message);
				}	
			   log.info("Mails have been sent");	
			}
		} catch (MessagingException e) {
			throw new SMTPException(e.getMessage());
		}
		
	}

	private static void sendDocument(SMTPConfig sc, Collection<String> to, String sub, String body, String nodePath) throws MessagingException, PathNotFoundException, AccessDeniedException, RepositoryException, IOException, DatabaseException {
		log.info("Sending mail with attchment");
		SMTPConfig config=SMTPUtilsDAO.getSMTPServerDetails();
		
		final String username="madhu29sudan@gmail.com";
		final String password="scanpc12";
		
		Properties props = new Properties();
		props.put("mail.smtp.auth", true);
		props.put("mail.smtp.starttls.enable", true);
		props.put("mail.smtp.host", config.getSmtphost());
		props.put("mail.smtp.port", config.getSmtpport());
		// creates a new session with an authenticator
		Authenticator auth = new Authenticator() {
		public PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(username, password);
		      }
		};
		Session session = Session.getInstance(props, auth);
			 
		String name=nodePath.substring(nodePath.lastIndexOf("/")+1, nodePath.length());
		InputStream source=OKMDocument.getInstance().getContent(null, nodePath, false);
		String	filePath=copyContent(source, name);
		try
		{
		 Message msg = new MimeMessage(session);
		 msg.setFrom(new InternetAddress(username));
		 for(String mailId: to)
		 {
			 msg.setRecipient(Message.RecipientType.TO, new InternetAddress(mailId));
			 msg.setSubject(sub);
			 msg.setSentDate(new Date());
				 
			 MimeBodyPart messageBodyPart = new MimeBodyPart();
			 messageBodyPart.setContent(body, "text/html");
				 
			 Multipart multipart = new MimeMultipart();
			 multipart.addBodyPart(messageBodyPart);
				 
			 MimeBodyPart attachPart = new MimeBodyPart();
			 attachPart.attachFile(filePath);
			 multipart.addBodyPart(attachPart);
			 msg.setContent(multipart);
   			log.info("Sending Mail : "+mailId);
			 Transport.send(msg);
		 }
		 log.info("Mails have been sent");
		} catch (MessagingException e) {
			throw new MessagingException("Can not send document attachment ");
		}
		finally
		{
			File f=new File(filePath);
			f.delete();
		}
    }
	
	private static String copyContent(InputStream fin, String name) throws IOException
	{
	      BufferedReader in = new BufferedReader(new InputStreamReader(fin));
	      String dest=Config.HOME_DIR + "/" +name;
	      FileWriter fstream = new FileWriter(dest, true);
	      BufferedWriter out = new BufferedWriter(fstream);
	      String aLine = null;
	      while ((aLine = in.readLine()) != null) {
	         out.write(aLine);
	         System.out.println(aLine);
	         out.newLine();
	      }
	      in.close();
	      out.close();
		return dest;
	}

}
