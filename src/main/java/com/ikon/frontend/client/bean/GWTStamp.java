package com.ikon.frontend.client.bean;

import com.google.gwt.user.client.rpc.IsSerializable;

public class GWTStamp
  implements IsSerializable
{
  public static final int STAMP_TEXT = 0;
  public static final int STAMP_IMAGE = 1;
  private int type;
  private long id;
  private String name;

  public int getType()
  {
    return this.type;
  }
  public void setType(int type) {
    this.type = type;
  }
  public long getId() {
    return this.id;
  }
  public void setId(long id) {
    this.id = id;
  }
  public String getName() {
    return this.name;
  }
  public void setName(String name) {
    this.name = name;
  }
}