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

package com.ikon.ws.endpoint;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ikon.core.AccessDeniedException;
import com.ikon.core.DatabaseException;
import com.ikon.core.PathNotFoundException;
import com.ikon.core.RepositoryException;
import com.ikon.core.SMTPException;
import com.ikon.module.ModuleManager;
import com.ikon.module.NotificationModule;
import com.ikon.principal.PrincipalAdapterException;

@WebService(name = "eArchiDocNotification", serviceName = "eArchiDocNotification", targetNamespace = "http://ws.earchidoc.com")
public class NotificationService {
	private static Logger log = LoggerFactory.getLogger(NotificationService.class);
	
	@WebMethod
	public void subscribe(@WebParam(name = "token") String token, @WebParam(name = "nodePath") String nodePath)
			throws PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException, SMTPException {
		log.debug("subscribe({}, {})", token, nodePath);
		NotificationModule nm = ModuleManager.getNotificationModule();
		nm.subscribe(token, nodePath);
		log.debug("subscribe: void");
	}
	
	@WebMethod
	public void unsubscribe(@WebParam(name = "token") String token, @WebParam(name = "nodePath") String nodePath)
			throws PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("unsubscribe({}, {})", token, nodePath);
		NotificationModule nm = ModuleManager.getNotificationModule();
		nm.unsubscribe(token, nodePath);
		log.debug("unsubscribe: void");
	}
	
	@WebMethod
	public String[] getSubscriptors(@WebParam(name = "token") String token, @WebParam(name = "nodePath") String nodePath)
			throws PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("getSubscriptors({}, {})", token, nodePath);
		NotificationModule nm = ModuleManager.getNotificationModule();
		Set<String> col = nm.getSubscriptors(token, nodePath);
		String[] result = (String[]) col.toArray(new String[col.size()]);
		log.debug("getSubscriptors: {}", result);
		return result;
	}
	
	@WebMethod
	public void notify(@WebParam(name = "token") String token, @WebParam(name = "nodePath") String nodePath,
			@WebParam(name = "users") String[] users, @WebParam(name = "message") String message,
			@WebParam(name = "attachment") boolean attachment) throws PathNotFoundException, AccessDeniedException,
			PrincipalAdapterException, RepositoryException, DatabaseException, IOException, SMTPException {
		log.debug("notify({}, {}, {}, {}, {})", new Object[] { token, nodePath, users, message, attachment });
		NotificationModule nm = ModuleManager.getNotificationModule();
		nm.notify(token, nodePath, Arrays.asList(users), message, attachment);
		log.debug("notify: void");
	}
}
