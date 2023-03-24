package com.sms.manager.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Document(collection="sender")
@Data
public class Sender {

	@Transient
    public static final String SEQUENCE_NAME = "sender_sequence";
	
	@Id
	private long id;
	
	private String type;
	
	private String number;
	
	private String assignedTo; 

	@Field
	private String status = "ACTIVE";

	@Field
	private Date createDate = new Date(); 

	@Field
	private Date lastUpdateDate = new Date(); 
}
