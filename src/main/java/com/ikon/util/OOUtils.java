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

package com.ikon.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import net.sf.jooreports.templates.DocumentTemplate;
import net.sf.jooreports.templates.DocumentTemplateException;
import net.sf.jooreports.templates.DocumentTemplateFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * http://jodreports.sourceforge.net/
 * 
 * @author pavila
 */
public class OOUtils {
	private static Logger log = LoggerFactory.getLogger(OOUtils.class);
	
	/**
	 * Fill ODT template
	 */
	public static void fillTemplate(InputStream input, Map<String, Object> model, 
			OutputStream output) throws FileNotFoundException, DocumentTemplateException, IOException {
		log.info("fillTemplate({}, {}, {})", new Object[] { input, model, output });
		DocumentTemplateFactory dtf = new DocumentTemplateFactory();
		DocumentTemplate tpl = dtf.getTemplate(input);
		tpl.createDocument(model, output);
		log.info("fillTemplate: void");
	}
}
