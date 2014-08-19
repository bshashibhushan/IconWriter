package com.ikon.servlet;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.prefs.Preferences;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ikon.bean.HttpSessionInfo;
import com.ikon.core.Config;
import com.ikon.core.HttpSessionManager;
import com.ikon.core.LicenseException;
import com.websina.license.LicenseManager;

/**
 * <p> This servlet will validate the license by first checking for the license expiry date, then by the system
 * mac id. It will also internally validate for file content modification. If the license contains {@link #SKIP_MACID_CHECK}, the mac
 * ID check will be skipped.</p>
 * @author 
 *
 */
public class ValidateLicenseServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerFactory.getLogger(ValidateLicenseServlet.class);
	private static final String SKIP_MACID_CHECK = "1:1:1:1:1";
	private static final File LICENSE_PATH = new File(Config.HOME_DIR + "/webapps/Infodocs/WEB-INF/classes/lang-profiles/li");

	@Override
	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		logger.info("Validating License");
		LicenseManager licenseManager = null;
		HttpSession httpSession = request.getSession();
		
		/*if(LICENSE_PATH.isFile()){
			if(remainingDays()>0){
				request.setAttribute("DAYS_LEFT", "Trial Days Left : " + remainingDays());
				request.getRequestDispatcher("./login.jsp").forward(request, response);	
			} else {
				LICENSE_PATH.delete();
			}
		}*/			
		
		try {

			if (licenseManager == null) {
				licenseManager = LicenseManager.getInstance();
			}

			if(licenseManager.isValid()){ //checks for unauthorized license modifications	
				if (licenseManager.getFeature("MAC Address").equals(getMacAddress()) || licenseManager.getFeature("MAC Address").equals(SKIP_MACID_CHECK))	{
					if(licenseManager.daysLeft() > 0){
						HttpSessionManager.getInstance().add(request);
						List<HttpSessionInfo> httpSessionInfos = HttpSessionManager.getInstance().getSessions();
						logger.info("Number of users online : " + httpSessionInfos.size());
						if (httpSessionInfos.size() > Integer.parseInt(licenseManager.getFeature("Number of Users"))) {
							logger.info("Max users Logged in");
							httpSession.invalidate();
							throw new LicenseException("Max users have logged in. Please wait.");
						} else {
							httpSession.setAttribute("DAYS_LEFT", "Days Left : " + String.valueOf(licenseManager.daysLeft()));
							request.getRequestDispatcher("./index.jsp").forward(request, response);	
						}
					} else {
						logger.info("License is expired");
						throw new LicenseException("License is expired.");
					}
					
				} else {
					logger.info("Mac Id mismatch");
					throw new LicenseException("MacId of the server seems to be changed.");
				}
				
			} else {
				logger.info("License had been modified");
				throw new LicenseException("License could not be read/expired.");
			}
		} catch(LicenseException exception){
			request.setAttribute("ERROR", exception.getMessage());
			request.setAttribute("SUPPORT", "Please contact support@writercorporation.com");
			request.getRequestDispatcher("./info.jsp").forward(request, response);		
		} catch (RuntimeException exception) {
			request.setAttribute("ERROR", "License could not be found");
			request.setAttribute("SUPPORT", "Please contact support@writercorporation.com");
			request.getRequestDispatcher("./info.jsp").forward(request, response);	
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}

	}

	private int remainingDays() {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("d MMM yyyy");
		DateTime expiryDate = null;
		try {
			expiryDate = formatter.parseDateTime(FileUtils.readFileToString(LICENSE_PATH));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Days.daysBetween(new DateTime(), expiryDate).getDays();
	}

	/**
	 * get MacId of the server.
	 * @return
	 * @throws IOException
	 */
	static String getMacAddress() throws IOException {
		InetAddress inetAddress;
		NetworkInterface networkInterface;
		StringBuilder sb = new StringBuilder();

		try {
			inetAddress = InetAddress.getLocalHost();
			networkInterface = NetworkInterface.getByInetAddress(inetAddress);
			byte[] mac = networkInterface.getHardwareAddress();

			for (int i = 0; i < mac.length; i++) {
				sb.append(String.format("%02X%s", mac[i],
						(i < mac.length - 1) ? "-" : ""));
			}
		} catch (UnknownHostException e) {
			throw new IOException("Unknown Host. Please try again later.");
		} catch (SocketException e) {
			throw new IOException("Could not connect to the socket. Please try again later.");
		}
		return sb.toString();
	}
}
