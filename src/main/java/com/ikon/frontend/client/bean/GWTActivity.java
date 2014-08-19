package com.ikon.frontend.client.bean;

import com.google.gwt.user.client.rpc.IsSerializable;
import java.util.Date;

public class GWTActivity
  implements IsSerializable
{
  private double id;
  private Date date;
  private String user;
  private String action;
  private String item;
  private String path;
  private String params;

  public double getId()
  {
    return this.id;
  }

  public void setId(double id) {
    this.id = id;
  }

  public String getAction() {
    return this.action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public Date getDate() {
    return this.date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public String getItem() {
    return this.item;
  }

  public void setItem(String item) {
    this.item = item;
  }

  public String getPath() {
	return path;
}

public void setPath(String path) {
	this.path = path;
}

public String getParams() {
    return this.params;
  }

  public void setParams(String params) {
    this.params = params;
  }

  public String getUser() {
    return this.user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("{");
    sb.append("id="); sb.append(this.id);
    sb.append(", date="); sb.append(this.date == null ? null : Long.valueOf(this.date.getTime()));
    sb.append(", user="); sb.append(this.user);
    sb.append(", action="); sb.append(this.action);
    sb.append(", item="); sb.append(this.item);
    sb.append(", params="); sb.append(this.params);
    sb.append("}");
    return sb.toString();
  }
}