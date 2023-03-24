package com.sms.manager.campaign.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sms.manager.campaign.service.CampaignService;
import com.sms.manager.helper.Helper;
import com.sms.manager.model.Campaign;
import com.sms.manager.model.TransactionResult;
import com.sms.manager.model.response.CampaignLookupResponse;
import com.sms.manager.model.response.ContactLookupResponse;

@RestController
public class CampaignController {

	@Autowired
	CampaignService campaignService;
	
	@Autowired
	private Helper helper;

	Logger logger = LoggerFactory.getLogger(CampaignController.class);
	
	@PostMapping("/campaign/send")
	public ResponseEntity<TransactionResult> sendCampaign(@RequestBody Campaign campaign) {
		logger.info("Entering sendCampaign() || request: {}", helper.modelToString(campaign));

		TransactionResult result = campaignService.sendCampaign(campaign);

		logger.info("Exiting sendCampaign() || reponse: {}", helper.modelToString(result));
		return new ResponseEntity<TransactionResult>(result, HttpStatusCode.valueOf(result.getHttpCode()));
	}
	

	@GetMapping("/campaign")
	public ResponseEntity<CampaignLookupResponse> campaignLookup(@RequestParam String searchValue,
			@RequestParam String user, @RequestParam int size, @RequestParam int pageNumber) {

		logger.info("Entering campaignLookup() || request: {}", searchValue);
		logger.info("Entering campaignLookup() || request: {}", user);
		logger.info("Entering campaignLookup() || request: {}", size);
		logger.info("Entering campaignLookup() || request: {}", pageNumber);
		CampaignLookupResponse result = campaignService.campaignLookup(searchValue, user, size, pageNumber);

		logger.info("Exiting campaignLookup() || reponse: {}", helper.modelToString(result));
		return new ResponseEntity<CampaignLookupResponse>(result, HttpStatusCode.valueOf(result.getHttpCode()));

	}

	
}
