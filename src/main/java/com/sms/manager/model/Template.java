package com.sms.manager.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection="template")
@Data
public class Template {

	@Id
	private int id;
	
	private int userId;

	private String name;
	
	private String access;
	
	private String status;
}
