package com.ikon.servlet.frontend;

import java.lang.reflect.InvocationTargetException;
import org.apache.commons.beanutils.BeanUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import com.ikon.core.DatabaseException;
import com.ikon.dao.RetentionPolicyDAO;
import com.ikon.dao.bean.RetentionPolicy;
import com.ikon.frontend.client.bean.GWTRetentionPolicy;
import com.ikon.frontend.client.service.OKMRetentionPolicyService;
import com.ikon.servlet.frontend.OKMRemoteServiceServlet;
import com.ikon.util.UserActivity;

public class RetentionPolicyDocumentServlet extends OKMRemoteServiceServlet implements
		OKMRetentionPolicyService{

	private static final long serialVersionUID = 1L;

	@Override
	public void applyRetentionPolicy(GWTRetentionPolicy gwtPolicy) {
		
		RetentionPolicy policy = new RetentionPolicy();
		
		try {
			BeanUtils.copyProperties(policy, gwtPolicy);
			policy.setExpiryDate(new DateTime().plusDays(gwtPolicy.getRetentionDays()).toString(DateTimeFormat.forPattern("d MMM yyyy")));
			RetentionPolicyDAO.create(policy);
			
			UserActivity.log(getThreadLocalRequest().getRemoteUser(), "APPLY_RETENTION_POLICY", gwtPolicy.getNodeUuid(), gwtPolicy.getSourcePath(), policy.getExpiryDate());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void delete(String uuid) {
		try {
			RetentionPolicyDAO.delete(uuid);
			
			UserActivity.log(getThreadLocalRequest().getRemoteUser(), "DELETE_RETENTION_POLICY", uuid, null, null);
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
