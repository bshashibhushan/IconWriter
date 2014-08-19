package com.ikon.servlet.admin;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ikon.api.OKMRepository;
import com.ikon.core.DatabaseException;
import com.ikon.core.PathNotFoundException;
import com.ikon.core.RepositoryException;
import com.ikon.dao.RetentionPolicyDAO;
import com.ikon.dao.bean.RetentionPolicy;
import com.ikon.servlet.admin.BaseServlet;
import com.ikon.util.UserActivity;
import com.ikon.util.WebUtils;

public class RetentionPolicyServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(RetentionPolicyServlet.class);
	
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
				RetentionPolicy policy = new RetentionPolicy();
				sc.setAttribute("action", action);
				sc.setAttribute("policy", policy);
				sc.getRequestDispatcher("/admin/retention_policy_edit.jsp").forward(request, response);
			} else if (action.equals("edit") || action.equals("delete")) {
				ServletContext sc = getServletContext();
				String policyId = WebUtils.getString(request, "policy_nodeUuid");
				RetentionPolicy policy = RetentionPolicyDAO.findByPk(policyId);
				sc.setAttribute("action", action);
				sc.setAttribute("policy", policy);
				sc.getRequestDispatcher("/admin/retention_policy_edit.jsp").forward(request, response);
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
			RetentionPolicy policy = new RetentionPolicy();
			policy.setSourcePath(WebUtils.getString(request, "policy_sourcePath"));
			policy.setDestinationPath(WebUtils.getString(request, "policy_destinationPath"));
			policy.setRetentionDays(WebUtils.getInt(request, "policy_retentionDays"));
			policy.setExpiryDate(new DateTime().plusDays(policy.getRetentionDays()).toString(DateTimeFormat.forPattern("d MMM yyyy")));
			policy.setEmailList(WebUtils.getString(request, "policy_emailList"));
			policy.setNodeType("openkm:folder");
			policy.setActive(WebUtils.getBoolean(request, "policy_active"));
			
			if (action.equals("create")) {
				if (RetentionPolicyDAO.findByPk(policy.getSourcePath()) == null) {	
					policy.setNodeUuid(OKMRepository.getInstance().getNodeUuid(null, policy.getSourcePath()));
					RetentionPolicyDAO.create(policy);
					
					// Activity log
					UserActivity.log(userId, "ADMIN_RETENTIONPOLICY_CREATE", policy.getNodeUuid(), policy.getSourcePath(), policy.getExpiryDate() );
					list(request, response);					
				} else {
					throw new DatabaseException("Source Folder already mapped.");
				}
			} else if (action.equals("edit")) {
				policy.setNodeUuid(WebUtils.getString(request, "policy_nodeUuid"));
				RetentionPolicyDAO.update(policy);
				
				// Activity log
				UserActivity.log(userId, "ADMIN_RETENTIONPOLICY_EDIT", policy.getNodeUuid(), policy.getSourcePath(), policy.getExpiryDate());
				list(request, response);
			} else if (action.equals("delete")) {
				policy.setNodeUuid(WebUtils.getString(request, "policy_nodeUuid"));
				RetentionPolicyDAO.delete(policy.getNodeUuid());				
				
				// Activity log
				UserActivity.log(userId, "ADMIN_RETENTIONPOLICY_DELETE", policy.getNodeUuid(), policy.getSourcePath(), policy.getExpiryDate());
				list(request, response);
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
	 * List registered retention policies
	 */
	private void list(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, DatabaseException {
		log.debug("list({}, {})", new Object[] { request, response });
		ServletContext sc = getServletContext();
		List<RetentionPolicy> policies = RetentionPolicyDAO.findAll();
		sc.setAttribute("policies", policies);
		sc.getRequestDispatcher("/admin/retention_policy_list.jsp").forward(request, response);
		log.debug("list: void");
	}

}
