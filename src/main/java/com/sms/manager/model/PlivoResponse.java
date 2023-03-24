package com.sms.manager.model;

import lombok.Data;

@Data
public class PlivoResponse {

	private String api_id;
	
	private int error_code;
	
	private String message;
	
	private String error;
	
	private String to_number;
	
	private String from_number;
	
	private String message_state;
	
	private String message_direction;
	
	private String message_uuid;
}
