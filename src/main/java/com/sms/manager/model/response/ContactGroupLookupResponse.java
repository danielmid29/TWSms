package com.sms.manager.model.response;

import java.util.List;

import com.sms.manager.model.ContactGroup;
import com.sms.manager.model.TransactionResult;

import lombok.Data;

@Data
public class ContactGroupLookupResponse extends TransactionResult {
	
	private long totalCount;
	
	private int totalPage;

	private List<ContactGroup> contactGroupList;
	
}
