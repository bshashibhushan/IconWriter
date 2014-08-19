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

import com.ikon.api.OKMDocument;
import com.ikon.api.OKMFolder;
import com.ikon.automation.AutomationException;
import com.ikon.core.AccessDeniedException;
import com.ikon.core.DatabaseException;
import com.ikon.core.ItemExistsException;
import com.ikon.core.LockException;
import com.ikon.core.PathNotFoundException;
import com.ikon.core.RepositoryException;
import com.ikon.dao.NodeBaseDAO;
import com.ikon.dao.bean.NodeBase;
import com.ikon.extension.core.ExtensionException;
import com.ikon.frontend.client.OKMException;
import com.ikon.frontend.client.service.OKMNodeBaseService;

/**
 * Directory tree service
 */
public class NodeBaseServlet extends OKMRemoteServiceServlet implements OKMNodeBaseService {
	private static final long serialVersionUID = 5746570509074299745L;
	
	@Override
	public String getOriginalDocumentPath(String trashPath) throws OKMException {
		NodeBase doc = null;
		try {
			String uuid  = NodeBaseDAO.getInstance().getUuidFromPath(trashPath);
			doc = NodeBaseDAO.getInstance().findByPk(uuid);
			String destinationPath = doc.getNbsOriginalPath().substring(0,doc.getNbsOriginalPath().lastIndexOf("/")).toString();			
			OKMDocument.getInstance().move(null, trashPath, destinationPath+"/");			
		} catch (PathNotFoundException e) {
			e.printStackTrace();
		} catch (DatabaseException e) {
			e.printStackTrace();
		} catch (ItemExistsException e) {
			e.printStackTrace();
		} catch (AccessDeniedException e) {
			e.printStackTrace();
		} catch (LockException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (ExtensionException e) {
			e.printStackTrace();
		} catch (AutomationException e) {
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public String getOriginalFolderPath(String trashPath) throws OKMException {
		NodeBase doc = null;
		try {
			String uuid  = NodeBaseDAO.getInstance().getUuidFromPath(trashPath);
			doc = NodeBaseDAO.getInstance().findByPk(uuid);
			String destinationPath = doc.getNbsOriginalPath().substring(0,doc.getNbsOriginalPath().lastIndexOf("/")).toString();			
			OKMFolder.getInstance().move(null, trashPath, destinationPath+"/");			
		} catch (PathNotFoundException e) {
			e.printStackTrace();
		} catch (DatabaseException e) {
			e.printStackTrace();
		} catch (ItemExistsException e) {
			e.printStackTrace();
		} catch (AccessDeniedException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		return "";
	}
}
