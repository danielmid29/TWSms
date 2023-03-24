package com.sms.manager.model.response;

import java.util.List;

import com.sms.manager.model.Sender;
import com.sms.manager.model.TransactionResult;

import lombok.Data;

@Data
public class SenderLookupResponse extends TransactionResult {
	
	private long totalCount;

	private int totalPage;

	private List<Sender> senderList;
	
}
