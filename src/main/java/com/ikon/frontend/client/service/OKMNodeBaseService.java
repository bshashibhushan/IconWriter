package com.ikon.frontend.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.ikon.frontend.client.OKMException;

@RemoteServiceRelativePath("Nodebase")
public interface OKMNodeBaseService extends RemoteService {
	public String getOriginalDocumentPath(String DocPath) throws OKMException;
	public String getOriginalFolderPath(String folderPath) throws OKMException;
}
