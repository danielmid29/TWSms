package com.sms.manager.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Document(collection="campaign")
@Data
public class Campaign {

	@Transient
    public static final String SEQUENCE_NAME = "campaign_sequence";
	
	@Id
	private long id;
	
	private String user;
	
	private String name;
	
	private String sender;
	
	private String[] contactGroups;
	
	private int contacts;
	
	private String time;
	
	private List<String> messageUuid;
	
	@Field
	private String status = "QUEUED";
	
	private boolean scheduledMessage; 	
	
	private String message;

	@Field
	private String plivoMessageResponse = "" ;
	
	@Field
	private Date processed_date = new Date(); 
	
}
