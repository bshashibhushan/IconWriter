/**
 *  openkm, Open Document Management System (http://www.openkm.com)
 *  Copyright (c) 2006-2013  Paco Avila & Josep Llort
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

package com.ikon.frontend.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.ikon.frontend.client.bean.GWTWorkspace;

/**
 * @author jllort
 *
 */
public interface OKMWorkspaceServiceAsync {
	public void getUserWorkspace(AsyncCallback<GWTWorkspace> callback);
	public void getUserDocumentsSize(AsyncCallback<Double> callback);
	public void updateUserWorkspace(GWTWorkspace workspace, AsyncCallback<?> callback);
	public void deleteMailAccount(long id, AsyncCallback<?> callback);
	public void isValidPassword(String password, AsyncCallback<String> callback );
}
