package com.ikon.frontend.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.ikon.frontend.client.OKMException;
import com.ikon.frontend.client.bean.GWTStamp;

import java.util.List;

@RemoteServiceRelativePath("../frontend/Stamp")
public abstract interface OKMStampService extends RemoteService
{
  public abstract List<GWTStamp> findAll()
    throws OKMException;

  public abstract void Stamp(long paramLong, int paramInt, String paramString)
    throws OKMException;
}
