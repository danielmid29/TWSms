package com.sms.manager.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Document(collection="message")
@Data
public class Message {

	@Transient
    public static final String SEQUENCE_NAME = "message_sequence";
	
	@Id
	private long id;
	
	private String user;
	
	private String from;
	
	private String to;
	
	private String message;
	
	private String type;
	
	private String campaign;
	
	@Field
	private String status= "QUEUED";

	private String messageUuid ;
	
	@Field
	private String plivoMessageResponse = "" ;

	@Field
	private Date processed_date = new Date(); 
}
