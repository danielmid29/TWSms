package com.sms.manager.model;

import lombok.Data;

@Data
public class ResetPasswordRequest {

	private String userId;
	
	private long userFk;
	
	private String currentPassword;
	
	private String newPassword;
	
}
