package com.ikon.dao.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="ikon_SMTP_DETAILS")
public class SMTPConfig implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="SMTP_HOST")
	private String smtphost = "";
	@Column(name="SMTP_PORT")
	private String smtpport = "";
	@Column(name="USERNAME")
	private String username = "";
	@Column(name="PASSWORD")
	private String password = "";
	@Column(name="SECURE")
	private boolean isSSL=false;	
	
	public String getSmtphost() {
		return smtphost; 
	}
	public void setSmtphost(String smtphost) {
		this.smtphost = smtphost;
	}
	public String getSmtpport() {
		return smtpport;
	}
	public void setSmtpport(String smtpport) {
		this.smtpport = smtpport;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public boolean isSSL() {
		return isSSL;
	}
	public void setSSL(boolean isSSL) {
		this.isSSL = isSSL;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("username="); sb.append(username);
		sb.append(", password="); sb.append(password);
		sb.append(", smtphost="); sb.append(smtphost);
		sb.append(", smtport="); sb.append(smtpport);
		sb.append(", isSSL="); sb.append(isSSL);
		sb.append("}");
		return sb.toString();
	}
	
	
}