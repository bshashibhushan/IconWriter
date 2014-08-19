package com.ikon.frontend.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.ikon.frontend.client.bean.GWTStapleGroup;

import java.util.List;

public abstract interface OKMStaplingServiceAsync
{
  public abstract void create(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String groupName, AsyncCallback<String> paramAsyncCallback);

  public abstract void add(String paramString1, String paramString2, String paramString3, AsyncCallback<?> paramAsyncCallback);

  public abstract void remove(String paramString, AsyncCallback<?> paramAsyncCallback);

  public abstract void removeStaple(String paramString, AsyncCallback<?> paramAsyncCallback);

  public abstract void getAll(String paramString, AsyncCallback<List<GWTStapleGroup>> paramAsyncCallback);

  public abstract void removeAllStapleByUuid(String paramString, AsyncCallback<?> paramAsyncCallback);
}