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


package com.ikon.extension.frontend.client;

import com.google.gwt.user.client.Window;
import com.ikon.frontend.client.extension.event.HasWorkspaceEvent.WorkspaceEventConstant;
import com.ikon.frontend.client.extension.event.handler.WorkspaceHandlerExtension;

/**
 * HandlersTest
 * 
 * @author jllort
 *
 */
//public class HandlersTest implements NavigatorHandlerExtension {
//
//	@Override
//	public void onChange(NavigatorEventConstant event) {
//		Window.alert("navigator event fired");
//	}
//}

/**
 * HandlersTest
 * 
 * @author jllort
 *
 */
public class HandlersTest implements WorkspaceHandlerExtension {

	@Override
	public void onChange(WorkspaceEventConstant event) {
		Window.alert("workspace event fired");
	}
}
