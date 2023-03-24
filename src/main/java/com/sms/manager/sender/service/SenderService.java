package com.sms.manager.sender.service;

import com.sms.manager.model.Sender;
import com.sms.manager.model.TransactionResult;
import com.sms.manager.model.response.MessageLookupResponse;
import com.sms.manager.model.response.PlivoNumberCartResponse;
import com.sms.manager.model.response.SenderLookupResponse;

public interface SenderService {

	public PlivoNumberCartResponse searchSenderCart(String type);
	
	public TransactionResult buySender(Sender sender);

	public TransactionResult deleteSender(String sender);
	
	public TransactionResult updateSender(Sender sender);

	public SenderLookupResponse senderLookup(String searchValue, String user, int size, int pageNumber);
	
}
