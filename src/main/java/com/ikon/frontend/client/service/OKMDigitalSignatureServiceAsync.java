package com.ikon.frontend.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface OKMDigitalSignatureServiceAsync {

	public void deletePFX(String userId, AsyncCallback<?> callback);
	public void signDocument(String docPath, AsyncCallback<?> callback);
}
