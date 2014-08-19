package com.ikon.frontend.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.ikon.frontend.client.bean.GWTStapleGroup;
import com.ikon.frontend.client.OKMException;
import java.util.List;

@RemoteServiceRelativePath("../frontend/Stapling")
public abstract interface OKMStaplingService extends RemoteService
{
  public abstract String create(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String groupName)
    throws OKMException;

  public abstract void add(String paramString1, String paramString2, String paramString3)
    throws OKMException;

  public abstract void remove(String paramString)
    throws OKMException;

  public abstract void removeStaple(String paramString)
    throws OKMException;

  public abstract List<GWTStapleGroup> getAll(String paramString)
    throws OKMException;

  public abstract void removeAllStapleByUuid(String paramString)
    throws OKMException;
}