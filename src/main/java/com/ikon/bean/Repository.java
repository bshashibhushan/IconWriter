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

package com.ikon.bean;

import java.io.Serializable;

/**
 * @author pavila
 * 
 */
public class Repository implements Serializable {
	private static final long serialVersionUID = -6920884124466924375L;

	public static final String OKM = "okm";
	public static final String OKM_URI = "http://www.openkm.org/1.0";
	public static final String ROOT = "Infodocs:root";
	public static final String TRASH = "Infodocs:trash";
	public static final String TEMPLATES = "Infodocs:templates";
	public static final String THESAURUS = "Infodocs:thesaurus";
	public static final String CATEGORIES = "Infodocs:categories";
	public static final String SYS_CONFIG = "Infodocs:config";
	public static final String SYS_CONFIG_TYPE = "Infodocs:sysConfig";
	public static final String SYS_CONFIG_UUID = "Infodocs:uuid";
	public static final String SYS_CONFIG_VERSION = "Infodocs:version";
	public static final String PERSONAL = "Infodocs:personal";
	public static final String MAIL = "Infodocs:mail";
	public static final String USER_CONFIG = "Infodocs:config";
	public static final String USER_CONFIG_TYPE = "Infodocs:userConfig";
	public static final String LOCK_TOKENS = "Infodocs:lockTokens";
	
	private static String uuid;
	private static String updateMsg; 
	private String id;
	private String name;
	private String description;
	
	public static String getUuid() {
		return uuid;
	}
	
	public static void setUuid(String uuid) {
		Repository.uuid = uuid;
	}
	
	public static String getUpdateMsg() {
		return updateMsg;
	}
	
	public static void setUpdateMsg(String updateMsg) {
		Repository.updateMsg = updateMsg;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
