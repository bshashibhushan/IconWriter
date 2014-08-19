package com.ikon.frontend.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.ikon.frontend.client.bean.GWTRetentionPolicy;

@RemoteServiceRelativePath("../frontend/RetentionPolicy")
public interface OKMRetentionPolicyService extends RemoteService{
	
	/**
	 * Applies the retentionpolicy to a document. It uses Joda time to convert from days to the date.
	 * The entire policy is copied using bean utils and a policy is updated.
	 */
	public void applyRetentionPolicy(GWTRetentionPolicy policy);
	
	/**
	 * Deletes the policy
	 * @param uuid
	 */
	public void delete(String uuid);
	
}
