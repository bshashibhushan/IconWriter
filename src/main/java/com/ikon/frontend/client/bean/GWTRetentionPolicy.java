package com.ikon.frontend.client.bean;

import com.google.gwt.user.client.rpc.IsSerializable;

public class GWTRetentionPolicy implements IsSerializable{
	
	private String nodeUuid;
	private String sourcePath;
	private String destinationPath;
	private int retentionDays;
	private String emailList;
	private String nodeType;
	private String expiryDate;
	private boolean active;
	
	public String getNodeUuid() {
		return nodeUuid;
	}
	public void setNodeUuid(String nodeUuid) {
		this.nodeUuid = nodeUuid;
	}
	public String getSourcePath() {
		return sourcePath;
	}
	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}
	public String getDestinationPath() {
		return destinationPath;
	}
	public void setDestinationPath(String destinationPath) {
		this.destinationPath = destinationPath;
	}
	public int getRetentionDays() {
		return retentionDays;
	}
	public void setRetentionDays(int retentionDays) {
		this.retentionDays = retentionDays;
	}
	public String getEmailList() {
		return emailList;
	}
	public void setEmailList(String emailList) {
		this.emailList = emailList;
	}
	public String getNodeType() {
		return nodeType;
	}
	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}
	public String getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
}
