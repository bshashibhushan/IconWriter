package com.ikon.frontend.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.ikon.frontend.client.bean.GWTStamp;

import java.util.List;

public abstract interface OKMStampServiceAsync
{
  public abstract void findAll(AsyncCallback<List<GWTStamp>> paramAsyncCallback);

  public abstract void Stamp(long paramLong, int paramInt, String paramString, AsyncCallback<?> paramAsyncCallback);
}

