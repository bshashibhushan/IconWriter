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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ikon.api.OKMDocument;
import com.ikon.api.OKMFolder;
import com.ikon.api.OKMMail;
import com.ikon.api.OKMRepository;
import com.ikon.automation.AutomationException;
import com.ikon.bean.Document;
import com.ikon.core.AccessDeniedException;
import com.ikon.core.Config;
import com.ikon.core.DatabaseException;
import com.ikon.core.ItemExistsException;
import com.ikon.core.LockException;
import com.ikon.core.PathNotFoundException;
import com.ikon.core.RepositoryException;
import com.ikon.core.UserQuotaExceededException;
import com.ikon.extension.core.ExtensionException;
import com.ikon.frontend.client.OKMException;
import com.ikon.frontend.client.constants.service.ErrorCode;
import com.ikon.frontend.client.service.OKMMassiveService;

/**
 * Massive service
 */
public class MassiveServlet extends OKMRemoteServiceServlet implements OKMMassiveService {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(MassiveServlet.class);
	
	@Override
	public void copy(List<String> paths, String fldPath) throws OKMException {
		log.debug("copy({}, {})", paths, fldPath);
		updateSessionManager();
		String error = "";
		String pathErrors = "";
		
		for (String path : paths) {
			try {
				if (OKMDocument.getInstance().isValid(null, path)) {
					OKMDocument.getInstance().copy(null, path, fldPath);
				} else if (OKMFolder.getInstance().isValid(null, path)) {
					OKMFolder.getInstance().copy(null, path, fldPath);
				} else if (OKMMail.getInstance().isValid(null, path)) {
					OKMMail.getInstance().copy(null, path, fldPath);
				}
			} catch (PathNotFoundException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (AccessDeniedException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (RepositoryException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (DatabaseException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (ItemExistsException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (IOException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (UserQuotaExceededException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (ExtensionException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (AutomationException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			}
		}
		
		if (!error.equals("")) {
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMassiveService, ErrorCode.CAUSE_General),
					pathErrors + "\n\n" + error);
		}
	}
	
	@Override
	public void move(List<String> paths, String fldPath) throws OKMException {
		log.debug("move({}, {})", paths, fldPath);
		updateSessionManager();
		String error = "";
		String pathErrors = "";
		
		for (String path : paths) {
			try {
				if (OKMDocument.getInstance().isValid(null, path)) {
					OKMDocument.getInstance().move(null, path, fldPath);
				} else if (OKMFolder.getInstance().isValid(null, path)) {
					OKMFolder.getInstance().move(null, path, fldPath);
				} else if (OKMMail.getInstance().isValid(null, path)) {
					OKMMail.getInstance().move(null, path, fldPath);
				}
			} catch (PathNotFoundException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (AccessDeniedException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (RepositoryException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (DatabaseException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (ItemExistsException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (LockException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (ExtensionException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (AutomationException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			}
		}
		
		if (!error.equals("")) {
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMassiveService, ErrorCode.CAUSE_General),
					pathErrors + "\n\n" + error);
		}
	}
	
	@Override
	public void delete(List<String> paths) throws OKMException {
		log.debug("delete({})", paths);
		updateSessionManager();
		String error = "";
		String pathErrors = "";
		
		for (String path : paths) {
			try {
				if (OKMDocument.getInstance().isValid(null, path)) {
					OKMDocument.getInstance().delete(null, path);
				} else if (OKMFolder.getInstance().isValid(null, path)) {
					OKMFolder.getInstance().delete(null, path);
				} else if (OKMMail.getInstance().isValid(null, path)) {
					OKMMail.getInstance().delete(null, path);
				}
			} catch (PathNotFoundException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (AccessDeniedException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (RepositoryException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (DatabaseException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (LockException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (ExtensionException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			}
		}
		
		if (!error.equals("")) {
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMassiveService, ErrorCode.CAUSE_General),
					pathErrors + "\n\n" + error);
		}
	}
	
	@Override
	public List<String> checkout(List<String> paths) throws OKMException {
		log.debug("checkout({})", paths);
		updateSessionManager();
		List<String> docUUIDs = new ArrayList<String>();
		String error = "";
		String pathErrors = "";
		
		// set all as checked out
		for (String path : paths) {
			try {
				if (OKMDocument.getInstance().isValid(null, path)) {
					OKMDocument.getInstance().checkout(null, path);
					docUUIDs.add(OKMRepository.getInstance().getNodeUuid(null, path));
				}
			} catch (LockException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (PathNotFoundException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (AccessDeniedException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (RepositoryException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (DatabaseException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			}
		}
		
		if (!error.equals("")) {
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMassiveService, ErrorCode.CAUSE_General), pathErrors
					+ "\n\n" + error);
		}
		
		return docUUIDs;
	}
	
	@Override
	public void cancelCheckout(List<String> paths) throws OKMException {
		log.debug("cancelCheckout({})", paths);
		updateSessionManager();
		String error = "";
		String pathErrors = "";
		boolean hasAdminRole = getThreadLocalRequest().isUserInRole(Config.DEFAULT_ADMIN_ROLE);
		
		// set all as checked out
		for (String path : paths) {
			try {
				if (OKMDocument.getInstance().isValid(null, path) && OKMDocument.getInstance().isCheckedOut(null, path)) {
					if (!hasAdminRole) {
						OKMDocument.getInstance().cancelCheckout(null, path);
					} else {
						Document doc = OKMDocument.getInstance().getProperties(null, path);
						if (doc.getLockInfo().getOwner().equals(getThreadLocalRequest().getRemoteUser())) {
							OKMDocument.getInstance().cancelCheckout(null, path);
						} else {
							OKMDocument.getInstance().forceCancelCheckout(null, path);
						}
					}
				}
			} catch (LockException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (PathNotFoundException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (AccessDeniedException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (RepositoryException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (DatabaseException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			}
		}
		
		if (!error.equals("")) {
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMassiveService, ErrorCode.CAUSE_General), pathErrors
					+ "\n\n" + error);
		}
	}
	
	@Override
	public void purge(List<String> paths) throws OKMException {
		String error = "";
		String pathErrors = "";
		
		for (String path : paths) {
			try {	
				if (OKMDocument.getInstance().isValid(null, path)) {
					OKMDocument.getInstance().purge(null, path);
				} else if (OKMFolder.getInstance().isValid(null, path)) {
					OKMFolder.getInstance().purge(null, path);
				} else if (OKMMail.getInstance().isValid(null, path)) {
					OKMMail.getInstance().purge(null, path);
				}
			} catch (PathNotFoundException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (AccessDeniedException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (RepositoryException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (DatabaseException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (LockException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			} catch (ExtensionException e) {
				log.error(e.getMessage(), e);
				error += "\n" + e.getMessage();
				pathErrors += "\n" + path;
			}
		}
		
		if (!error.equals("")) {
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMassiveService, ErrorCode.CAUSE_General),
					pathErrors + "\n\n" + error);
		}
		
	}
}
