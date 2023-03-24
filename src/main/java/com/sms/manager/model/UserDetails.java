package com.sms.manager.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;


@Document(collection="user_details")
@Data
public class UserDetails {

	@Transient
    public static final String SEQUENCE_NAME = "user_details_sequence";
	
	@Id
	private long uid;
	
	private String firstName;
	
	private String lastName;
	
	private String userId;
	
	private String role;
	
	private String contact;
	
	private String email;
	
	private Integer totalContacts;
	
	private Integer totalSmsSent;
	
	private String profilePicName; 
	
	private Date createDate; 
	
	private Date lastUpdateDate; 
	
	@Field
	private String status = "ACTIVE";
}
