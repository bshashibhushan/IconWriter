package com.ikon.dao.bean;

import java.sql.Blob;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name="ikon_SIGN")
public class DigitalSignature implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="USRPFX_ID")
	private String userId = "";
	
	@Column(name="USRPFX_FILE")
	@Lob
	private Blob pfxfile = null;
	
	@Column(name="USRPFX_PASSWORD")
	private String pfxpassword = "";

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Blob getPfxfile() {
		return pfxfile;
	}

	public void setPfxfile(Blob pfxfile) {
		this.pfxfile = pfxfile;
	}

	public String getPfxpassword() {
		return pfxpassword;
	}
	
	public void setPfxpassword(String pfxpassword) {
		this.pfxpassword = pfxpassword;
	} 
	
}
