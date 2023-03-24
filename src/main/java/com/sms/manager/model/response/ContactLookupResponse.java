package com.sms.manager.model.response;

import java.util.List;

import com.sms.manager.model.Contact;
import com.sms.manager.model.TransactionResult;

import lombok.Data;

@Data
public class ContactLookupResponse extends TransactionResult {
	
	private long totalCount;
	
	private int totalPage;

	private List<Contact> contactList;
	
}
