/**
 * openkm, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2013 Paco Avila & Josep Llort
 * 
 * No bytes were intentionally harmed during the development of this application.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.ikon.servlet.frontend;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jcr.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ikon.core.Config;
import com.ikon.core.DatabaseException;
import com.ikon.core.PathNotFoundException;
import com.ikon.dao.UserConfigDAO;
import com.ikon.dao.bean.MailAccount;
import com.ikon.dao.bean.Profile;
import com.ikon.dao.bean.UserConfig;
import com.ikon.frontend.client.OKMException;
import com.ikon.frontend.client.bean.GWTConverterStatus;
import com.ikon.frontend.client.bean.GWTFileUploadingStatus;
import com.ikon.frontend.client.bean.GWTTestMail;
import com.ikon.frontend.client.constants.service.ErrorCode;
import com.ikon.frontend.client.service.OKMGeneralService;
import com.ikon.module.jcr.stuff.JCRUtils;
import com.ikon.servlet.frontend.util.WorkflowUtil;
import com.ikon.util.MailUtils;

/**
 * GeneralServlet
 * 
 * @author jllort
 */
public class GeneralServlet extends OKMRemoteServiceServlet implements OKMGeneralService {
	private static Logger log = LoggerFactory.getLogger(GeneralServlet.class);
	private static final long serialVersionUID = -879908904295685769L;
	
	@Override
	public GWTFileUploadingStatus getFileUploadStatus() {
		log.debug("getFileUploadStatus()");
		GWTFileUploadingStatus fus = new GWTFileUploadingStatus();
		updateSessionManager();
		
		if (getThreadLocalRequest().getSession().getAttribute(FileUploadServlet.FILE_UPLOAD_STATUS) != null) {
			FileUploadListener listener = (FileUploadListener) getThreadLocalRequest().getSession().getAttribute(
					FileUploadServlet.FILE_UPLOAD_STATUS);
			fus.setStarted(true);
			fus.setBytesRead(listener.getBytesRead());
			fus.setContentLength(listener.getContentLength());
			fus.setUploadFinish(listener.isUploadFinish());
			
			if (listener.getBytesRead() == listener.getContentLength() || listener.isUploadFinish()) {
				getThreadLocalRequest().getSession().removeAttribute(FileUploadServlet.FILE_UPLOAD_STATUS);
			}
		}
		
		log.debug("getFileUploadStatus: {}", fus);
		return fus;
	}
	
	@Override
	public GWTConverterStatus getConversionStatus() {
		log.debug("getConversionStatus()");
		GWTConverterStatus cos = new GWTConverterStatus();
		updateSessionManager();
		
		if (getThreadLocalRequest().getSession().getAttribute(ConverterServlet.FILE_CONVERTER_STATUS) != null) {
			ConverterListener listener = (ConverterListener) getThreadLocalRequest().getSession().getAttribute(
					ConverterServlet.FILE_CONVERTER_STATUS);
			cos.setStatus(listener.getStatus());
			cos.setConversionFinish(listener.isConversionFinish());
			cos.setError(listener.getError());
			if (listener.getError()!=null) {
				cos.setConversionFinish(true);
			}
			if (listener.isConversionFinish()) {
				getThreadLocalRequest().getSession().removeAttribute(ConverterServlet.FILE_CONVERTER_STATUS);
			}
		}
		
		log.debug("getConversionStatus: {}", cos);
		return cos;
	}
	
	@Override
	public GWTTestMail testMailConnection(String host, String user, String password, String mailFolder, String protocol) {
		log.debug("testImapConnection({}, {}, {}, {})", new Object[] { host, user, password, mailFolder });
		GWTTestMail test = new GWTTestMail();
		updateSessionManager();
		
		try {
			test.setError(false);
			MailAccount ma = new MailAccount();
			ma.setMailProtocol(protocol);
			ma.setMailHost(host);
			ma.setMailUser(user);
			ma.setMailPassword(password);
			ma.setMailFolder(mailFolder);
			ma.setMailMarkSeen(true);
			MailUtils.testConnection(ma);
		} catch (IOException e) {
			test.setError(true);
			test.setErrorMsg(e.getMessage());
			e.printStackTrace();
		}
		
		log.debug("testImapConnection: {}", test);
		return test;
	}
	
	@Override
	public List<String> getEnabledExtensions() throws OKMException {
		log.debug("getEnabledExtensions()");
		updateSessionManager();
		List<String> extensions = new ArrayList<String>();
		Set<String> set = new HashSet<String>();
		Session session = null;
		
		try {
			Profile up = new Profile();
			UserConfig uc = UserConfigDAO.findByPk(getThreadLocalRequest().getRemoteUser());
			up = uc.getProfile();
			extensions = new ArrayList<String>(up.getPrfMisc().getExtensions());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMGeneralService, ErrorCode.CAUSE_PathNotFound),
					e.getMessage());
		} catch (DatabaseException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMGeneralService, ErrorCode.CAUSE_Database),
					e.getMessage());
		} finally {
			JCRUtils.logout(session);
		}
		
		return extensions;
	}
}
