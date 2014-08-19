package com.ikon.dao.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

@Entity
@Table(name="ikon_RETENTION_POLICY")
public class RetentionPolicy implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="NODE_UUID")
	private String nodeUuid;
	
	@Column(name="SOURCE_PATH")
	private String sourcePath;
	
	@Column(name="DESTINATION_PATH")
	private String destinationPath;
	
	@Column(name="RETENTION_DAYS")
	private int retentionDays;

	@Column(name="EMAIL_LIST")
	private String emailList;
	
	@Column(name="NODE_TYPE")
	private String nodeType;
	
	@Column(name="EXPIRY_DATE")
	private String expiryDate;
	
	@Column(name = "ACTIVE")
	@Type(type = "true_false")
	private boolean active;

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
	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	public String getNodeUuid() {
		return nodeUuid;
	}

	public void setNodeUuid(String nodeUuid) {
		this.nodeUuid = nodeUuid;
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
}
