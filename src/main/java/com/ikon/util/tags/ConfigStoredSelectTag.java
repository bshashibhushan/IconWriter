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

package com.ikon.util.tags;

import java.io.IOException;

import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import com.ikon.bean.ConfigStoredOption;
import com.ikon.bean.ConfigStoredSelect;
import com.ikon.core.DatabaseException;
import com.ikon.dao.ConfigDAO;

@SuppressWarnings("serial")
public class ConfigStoredSelectTag extends TagSupport {
	private String key;
	private String value;
	
	@Override
	public int doStartTag() {
		String html = "";
		
		try {
			ConfigStoredSelect stSelect = ConfigDAO.getSelect(key);
			
			if (stSelect != null) {
				html += "<select name=\"cfg_value\">";
				
				for (ConfigStoredOption stOption : stSelect.getOptions()) {
					html += "<option value=\"" + stOption.getValue() + "\" "
							+ (stOption.isSelected() ? "selected=\"selected\"" : "") + ">" + stOption.getName()
							+ "</option>";
				}
				
				html += "</select>";
			}
			
			pageContext.getOut().write(html);
		} catch (DatabaseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return Tag.SKIP_BODY;
	}
	
	@Override
	public void release() {
		super.release();
		key = null;
		value = null;
	}
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
}
