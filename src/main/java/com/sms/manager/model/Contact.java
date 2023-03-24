package com.sms.manager.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection="contacts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contact {

	@Transient
    public static final String SEQUENCE_NAME = "contacts_sequence";
	
	@Id
	private long id;
	
	private String user;
	
	private String contactGroup;
	
	private String contactNumber;

	@Field
	private Date createDate = new Date(); 

	@Field
	private Date lastUpdateDate = new Date(); 
	
	@Field
	private String status = "ACTIVE";

	public Contact(String user, String contactNumber) {	
		this.user = user;
		this.contactNumber = contactNumber;
	}
		
}
