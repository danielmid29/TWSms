package com.sms.manager.model.response;

import java.util.List;

import com.sms.manager.model.Message;
import com.sms.manager.model.TransactionResult;

import lombok.Data;

@Data
public class MessageLookupResponse extends TransactionResult {

	private long totalCount;
	
	private int totalPage;

	private List<Message> messages;
	
}
