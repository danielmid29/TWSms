package com.sms.manager.service;

import com.sms.manager.model.Message;
import com.sms.manager.model.TransactionResult;
import com.sms.manager.model.response.MessageLookupResponse;
import com.sms.manager.model.response.UserDetailsResponse;

public interface MessageService {

	public TransactionResult sendMessage(Message message);

	MessageLookupResponse messageLookup(String searchValue, String user, int size, int pageNumber);

	void updateStatus();

	void updateCampaignStatus(); 
	
}
