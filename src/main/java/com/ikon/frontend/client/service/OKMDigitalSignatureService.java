package com.ikon.frontend.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("sign")
public interface OKMDigitalSignatureService extends RemoteService {
	
	public void deletePFX(String userId);
	public void signDocument(String docPath);
}
