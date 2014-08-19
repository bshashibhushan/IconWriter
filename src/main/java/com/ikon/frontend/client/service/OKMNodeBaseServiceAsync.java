package com.ikon.frontend.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface OKMNodeBaseServiceAsync {
	public void getOriginalDocumentPath(String docPath, AsyncCallback<String> callback);
	public void getOriginalFolderPath(String fodlerPath, AsyncCallback<String> callback);
}
