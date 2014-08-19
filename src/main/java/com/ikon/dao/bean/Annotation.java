package com.ikon.dao.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.Type;


@Entity
@Table(name="ikon_ANNOTATIONS")

public class Annotation {
	
	@Id
	@Column(name="NODE_UUID")
	private String uuid = "";
	@Column(name="ANNOTATION_TEXT")
	@Lob @Type(type = "org.hibernate.type.TextType")
	private String text = "";
	
	
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	

}
