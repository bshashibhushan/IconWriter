package com.ikon.servlet.admin;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ikon.api.OKMRepository;
import com.ikon.core.DatabaseException;
import com.ikon.core.PathNotFoundException;
import com.ikon.core.RepositoryException;
import com.ikon.dao.HotFoldersDAO;
import com.ikon.dao.bean.HotFolders;
import com.ikon.util.UserActivity;
import com.ikon.util.WebUtils;

public class HotFoldersServlet extends BaseServlet {

	private static final long serialVersionUID = 1L;	
	private static Logger log = LoggerFactory.getLogger(HotFoldersServlet.class);
	
	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		String method = request.getMethod();
		
		if (isAdmin(request)) {
			if (method.equals(METHOD_GET)) {
				doGet(request, response);
			} else if (method.equals(METHOD_POST)) {
				doPost(request, response);
			}
		}
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		log.debug("doGet({}, {})", request, response);
		request.setCharacterEncoding("UTF-8");
		String action = WebUtils.getString(request, "action");
		updateSessionManager(request);
		
		try {
			
			if (action.equals("create")) {
				ServletContext sc = getServletContext();
				HotFolders folders = new HotFolders();
				sc.setAttribute("action", action);
				sc.setAttribute("policy", folders);
				sc.getRequestDispatcher("/admin/hotfolders_edit.jsp").forward(request, response);
			} else if (action.equals("edit") || action.equals("delete")) {
				ServletContext sc = getServletContext();
				String foldersId = WebUtils.getString(request, "policy_nodeUuid");
				HotFolders folders = HotFoldersDAO.findByPk(foldersId);
				sc.setAttribute("action", action);
				sc.setAttribute("policy", folders);
				sc.getRequestDispatcher("/admin/hotfolders_edit.jsp").forward(request, response);
			} else {
				list(request, response);
			}
			
		} catch (DatabaseException e) {
			e.printStackTrace();
		} 
	}
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		request.setCharacterEncoding("UTF-8");
		String action = "";
		String userId = request.getRemoteUser();
		updateSessionManager(request);
		
		try {
		
			action = WebUtils.getString(request, "action") ;
			HotFolders folders = new HotFolders();
			folders.setSourcePath(WebUtils.getString(request, "policy_sourcePath"));
			folders.setDestinationPath(WebUtils.getString(request, "policy_destinationPath"));
			folders.setActive(WebUtils.getBoolean(request, "policy_active"));
			
			if (action.equals("create")) {
				if(!new File(folders.getSourcePath()).exists()) {
					request.setAttribute("ErrorMessage", "The source path is not valid, Give proper source path.");
					request.getRequestDispatcher("/admin/hotfolders_edit.jsp").forward(request, response);
				} else { 
					if (HotFoldersDAO.findByPk(folders.getDestinationPath()) == null) {	
						folders.setNodeUuid(OKMRepository.getInstance().getNodeUuid(null, folders.getDestinationPath()));
						HotFoldersDAO.create(folders);
						
						// Activity log
						UserActivity.log(userId, "ADMIN_HOTFOLDER_CREATE", folders.getNodeUuid(), folders.getSourcePath(), null);
						list(request, response);
					} else {
						throw new DatabaseException("Source Folder already mapped.");
					}
				}
			} else if (action.equals("edit")) {
				if(!new File(folders.getSourcePath()).exists()) {
					request.setAttribute("ErrorMessage", "The source path is not valid, Give proper source path.");
					request.getRequestDispatcher("/admin/hotfolders_edit.jsp").forward(request, response);
				} else { 
					folders.setNodeUuid(WebUtils.getString(request, "policy_nodeUuid"));
					HotFoldersDAO.update(folders);
					
					// Activity log
					UserActivity.log(userId, "ADMIN_HOTFOLDER_EDIT", folders.getNodeUuid(), folders.getSourcePath(), null);
					list(request, response);
				}
			} else if (action.equals("delete")) {
				if(!new File(folders.getSourcePath()).exists()) {
					request.setAttribute("ErrorMessage", "The source path is not valid, Give proper source path.");
					request.getRequestDispatcher("/admin/hotfolders_edit.jsp").forward(request, response);
				} else { 
					folders.setNodeUuid(WebUtils.getString(request, "policy_nodeUuid"));
					HotFoldersDAO.delete(folders.getNodeUuid());				
					
					// Activity log
					UserActivity.log(userId, "ADMIN_HOTFOLDER_DELETE", folders.getNodeUuid(), folders.getSourcePath(), null);
					list(request, response);
				}
			}		
		} catch (DatabaseException e) {
			e.printStackTrace();
		} catch (PathNotFoundException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * List registered Hot Folders
	 */
	private void list(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, DatabaseException {
		log.debug("list({}, {})", new Object[] { request, response });
		ServletContext sc = getServletContext();
		List<HotFolders> policies = HotFoldersDAO.findAll();
		sc.setAttribute("policies", policies);
		sc.getRequestDispatcher("/admin/hotfolders_list.jsp").forward(request, response);
		log.debug("list: void");
	}


}
