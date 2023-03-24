package com.sms.manager.campaign.service;

import com.sms.manager.model.Campaign;
import com.sms.manager.model.TransactionResult;
import com.sms.manager.model.response.CampaignLookupResponse;

public interface CampaignService {

	public TransactionResult sendCampaign(Campaign campaign);

	CampaignLookupResponse campaignLookup(String searchValue, String user, int size, int pageNumber);
	
}
