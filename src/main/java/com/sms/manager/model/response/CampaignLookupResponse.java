package com.sms.manager.model.response;

import java.util.List;

import com.sms.manager.model.Campaign;
import com.sms.manager.model.ContactGroup;
import com.sms.manager.model.TransactionResult;

import lombok.Data;

@Data
public class CampaignLookupResponse extends TransactionResult{

	private long totalCount;
	
	private int totalPage;

	private List<Campaign> campaignList;
	
}
