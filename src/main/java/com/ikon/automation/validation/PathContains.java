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

package com.ikon.automation.validation;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ikon.api.OKMRepository;
import com.ikon.automation.AutomationUtils;
import com.ikon.automation.Validation;

/**
 * Check if the current parent path contains a designed one. The only
 * parameter is a path and will test if this one is included in the
 * actual parent.
 * 
 * @author pavila
 */
public class PathContains implements Validation {
	private static Logger log = LoggerFactory.getLogger(PathContains.class);
	
	@Override
	public boolean isValid(HashMap<String, Object> env, Object... params) {
		String uuid = AutomationUtils.getString(0, params);
		String parentPath = AutomationUtils.getParentPath(env);
		
		try {
			String path = OKMRepository.getInstance().getNodePath(null, uuid);
			
			if (parentPath.startsWith(path)) {
				return true;
			} else {
			 return false;
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		
		return false;
	}
}
