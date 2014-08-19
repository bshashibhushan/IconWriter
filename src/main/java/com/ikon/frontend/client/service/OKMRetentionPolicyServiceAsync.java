package com.ikon.frontend.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.ikon.frontend.client.bean.GWTRetentionPolicy;

public interface OKMRetentionPolicyServiceAsync {
	
	public void applyRetentionPolicy(GWTRetentionPolicy policy, AsyncCallback<?> callBack);
	public void delete(String uuid, AsyncCallback<?> callBack);

}
