package com.sms.manager.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sms.manager.status.message.StatusMessage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResult {

	private String code;
	
	private String message;
	
	private int httpCode;
	
	@JsonIgnore
	private List<String> messageUuid;	
	
	public TransactionResult(StatusMessage statusMessage) {
		this.code= statusMessage.getCode();
		this.message = statusMessage.getMessage();
		this.httpCode = statusMessage.getHttpStatusCode();		
	}
	
	public void setResult(StatusMessage statusMessage) {
		this.code= statusMessage.getCode();
		this.message = statusMessage.getMessage();
		this.httpCode = statusMessage.getHttpStatusCode();		
	}

}
