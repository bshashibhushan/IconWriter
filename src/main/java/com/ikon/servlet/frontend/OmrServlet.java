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

import net.sourceforge.jiu.codecs.InvalidFileStructureException;
import net.sourceforge.jiu.codecs.InvalidImageIndexException;
import net.sourceforge.jiu.codecs.UnsupportedTypeException;
import net.sourceforge.jiu.ops.MissingParameterException;
import net.sourceforge.jiu.ops.WrongParameterException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ikon.automation.AutomationException;
import com.ikon.core.AccessDeniedException;
import com.ikon.core.DatabaseException;
import com.ikon.core.LockException;
import com.ikon.core.NoSuchGroupException;
import com.ikon.core.NoSuchPropertyException;
import com.ikon.core.ParseException;
import com.ikon.core.PathNotFoundException;
import com.ikon.core.RepositoryException;
import com.ikon.dao.OmrDAO;
import com.ikon.dao.bean.Omr;
import com.ikon.extension.core.ExtensionException;
import com.ikon.frontend.client.OKMException;
import com.ikon.frontend.client.bean.GWTOmr;
import com.ikon.frontend.client.constants.service.ErrorCode;
import com.ikon.frontend.client.service.OKMOmrService;
import com.ikon.omr.OMRHelper;
import com.ikon.util.GWTUtil;
import com.ikon.util.OMRException;

/**
 * OMR service
 */
public class OmrServlet extends OKMRemoteServiceServlet implements OKMOmrService {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(OmrServlet.class);

	@Override
	public List<GWTOmr> getAllOmr() throws OKMException {
		List<GWTOmr> omrList = new ArrayList<GWTOmr>();
		try {
			for (Omr omr : OmrDAO.getInstance().findAllActive()) {
				omrList.add(GWTUtil.copy(omr));
			}
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMOmrService, ErrorCode.CAUSE_Database),e.getMessage());
		}
		return omrList;
	}

	@Override
	public void process(long omId, String uuid) throws OKMException {
		try {
			OMRHelper.processAndStoreMetadata(omId, uuid); 
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMOmrService, ErrorCode.CAUSE_IO),e.getMessage());
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMOmrService, ErrorCode.CAUSE_PathNotFound),e.getMessage());
		} catch (AccessDeniedException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMOmrService, ErrorCode.CAUSE_AccessDenied),e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMOmrService, ErrorCode.CAUSE_Repository),e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMOmrService, ErrorCode.CAUSE_Database),e.getMessage());
		} catch (OMRException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMOmrService, ErrorCode.CAUSE_Omr),e.getMessage());
		} catch (NoSuchGroupException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMOmrService, ErrorCode.CAUSE_NoSuchGroup),e.getMessage());
		} catch (LockException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMOmrService, ErrorCode.CAUSE_Lock),e.getMessage());
		} catch (ExtensionException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMOmrService, ErrorCode.CAUSE_Extension),e.getMessage());
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMOmrService, ErrorCode.CAUSE_Parse),e.getMessage());
		} catch (NoSuchPropertyException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMOmrService, ErrorCode.CAUSE_NoSuchProperty),e.getMessage());
		} catch (AutomationException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMOmrService, ErrorCode.CAUSE_Automation),e.getMessage());
		} catch (InvalidFileStructureException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMOmrService, ErrorCode.CAUSE_Omr),e.getMessage());
		} catch (InvalidImageIndexException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMOmrService, ErrorCode.CAUSE_Omr),e.getMessage());
		} catch (UnsupportedTypeException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMOmrService, ErrorCode.CAUSE_Omr),e.getMessage());
		} catch (MissingParameterException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMOmrService, ErrorCode.CAUSE_Omr),e.getMessage());
		} catch (WrongParameterException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMOmrService, ErrorCode.CAUSE_Omr),e.getMessage());
		} 
	}
}
