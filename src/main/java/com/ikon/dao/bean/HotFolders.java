package com.ikon.dao.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

@Entity
@Table(name="ikon_HOTFOLDERS")
public class HotFolders implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="NODE_UUID")
	private String nodeUuid;
	
	@Column(name="SOURCE_PATH")
	private String sourcePath;
	
	@Column(name="DESTINATION_PATH")
	private String destinationPath;
	
	@Column(name = "ACTIVE")
	@Type(type = "true_false")
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

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
