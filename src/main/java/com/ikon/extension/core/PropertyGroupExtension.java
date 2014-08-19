/**
 *  openkm, Open Document Management System (http://www.openkm.com)
 *  Copyright (c) 2006-2011  Paco Avila & Josep Llort
 *
 *  No bytes were intentionally harmed during the development of this application.
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.ikon.extension.core;

import java.io.IOException;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.Session;

import com.ikon.bean.form.FormElement;
import com.ikon.core.AccessDeniedException;
import com.ikon.core.DatabaseException;
import com.ikon.core.LockException;
import com.ikon.core.NoSuchGroupException;
import com.ikon.core.NoSuchPropertyException;
import com.ikon.core.ParseException;
import com.ikon.core.PathNotFoundException;
import com.ikon.core.Ref;
import com.ikon.core.RepositoryException;

public interface PropertyGroupExtension extends Extension {
	/**
	 * Executed BEFORE addGroup
	 */
	public void preAddGroup(Session session, Ref<Node> node, String grpName) throws NoSuchGroupException,
			LockException, PathNotFoundException, AccessDeniedException, RepositoryException,
			DatabaseException, ExtensionException;
	
	/**
	 * Executed AFTER addGroup
	 */
	public void postAddGroup(Session session, Ref<Node> node, String grpName) throws NoSuchGroupException,
			LockException, PathNotFoundException, AccessDeniedException, RepositoryException,
			DatabaseException, ExtensionException;
	
	/**
	 * Executed BEFORE removeGroup
	 */
	public void preRemoveGroup(Session session, Ref<Node> node, String grpName) throws AccessDeniedException, 
			NoSuchGroupException, LockException, PathNotFoundException, RepositoryException, 
			DatabaseException, ExtensionException;
	
	/**
	 * Executed AFTER removeGroup
	 */
	public void postRemoveGroup(Session session, Ref<Node> node, String grpName) throws AccessDeniedException, 
			NoSuchGroupException, LockException, PathNotFoundException, RepositoryException, 
			DatabaseException, ExtensionException;

	/**
	 * Executed BEFORE setProperties
	 */
	public void preSetProperties(Session session, Ref<Node> node, String grpName, List<FormElement> properties)
			throws IOException, ParseException, NoSuchPropertyException, NoSuchGroupException, LockException,
			PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException, ExtensionException;
	
	/**
	 * Executed AFTER setProperties
	 */
	public void postSetProperties(Session session, Ref<Node> node, String grpName, List<FormElement> properties)
			throws IOException, ParseException, NoSuchPropertyException, NoSuchGroupException, LockException,
			PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException, ExtensionException;
}
