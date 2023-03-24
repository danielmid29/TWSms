package com.sms.manager.model;

import java.util.ArrayList;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Document(collection="contacts_group")
@Data
public class ContactGroup {

	@Transient
    public static final String SEQUENCE_NAME = "contact_group_sequence";
	
	@Id
	private long id;
	
	private String user;
	
	private String name;

	private int contactsCount;
	
	@Field
	private Date createDate = new Date(); 

	@Field
	private Date lastUpdateDate = new Date(); 
	
	@Field
	private String status = "ACTIVE";
	
	@Transient
	private ArrayList<Contact> contactList;
	
}
