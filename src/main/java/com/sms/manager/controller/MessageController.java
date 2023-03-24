package com.sms.manager.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sms.manager.helper.Helper;
import com.sms.manager.model.Message;
import com.sms.manager.model.TransactionResult;
import com.sms.manager.model.response.MessageLookupResponse;
import com.sms.manager.model.response.UserDetailsResponse;
import com.sms.manager.service.MessageService;

@RestController
public class MessageController {

	@Autowired
	private Helper helper;

	@Autowired
	private MessageService messageService;
	
	Logger logger = LoggerFactory.getLogger(MessageController.class);
	
	@GetMapping("/message")
	public ResponseEntity<MessageLookupResponse> messageLookup(@RequestParam String searchValue, @RequestParam String user, @RequestParam int size, @RequestParam int pageNumber) {

		logger.info("Entering messageLookup() || request: {}", searchValue);
		logger.info("Entering messageLookup() || request: {}", user);
		logger.info("Entering messageLookup() || request: {}", size);
		logger.info("Entering messageLookup() || request: {}", pageNumber);
		MessageLookupResponse result = messageService.messageLookup(searchValue, user, size,pageNumber);

		logger.info("Exiting messageLookup() || reponse: {}", helper.modelToString(result));
		return new ResponseEntity<MessageLookupResponse>(result, HttpStatusCode.valueOf(result.getHttpCode()));
		
	}
	
	@PostMapping("/message/send")
	public ResponseEntity<TransactionResult> sendMessage(@RequestBody Message message) {
		
		logger.info("Entering sendMessage() || request: {}", helper.modelToString(message));

		TransactionResult result = messageService.sendMessage(message);

		logger.info("Exiting sendMessage() || reponse: {}", helper.modelToString(result));
		return new ResponseEntity<TransactionResult>(result, HttpStatusCode.valueOf(result.getHttpCode()));
		
	}
	

	@GetMapping("/initializeCheckMessageStatus")
//	@Scheduled(fixedDelay = 1000 * 3)
	public void initializeCheckMessageStatus() {

		logger.info("Entering initializeCheckMessageStatus() || Enter");
		
		messageService.updateStatus();

		logger.info("Exiting initializeCheckMessageStatus() || Exit");
	}
	
	@GetMapping("/initializeCheckCampaignStatus")
//	@Scheduled(fixedDelay = 1000 * 60 * 5)
	public void initializeCheckCampaignStatus() {

		logger.info("Entering initializeCheckCampaignStatus() || Enter");
		
		messageService.updateCampaignStatus();

		logger.info("Exiting initializeCheckCampaignStatus() || Exit");
	}
	
	@PostMapping("/inputMessage")
//	@Scheduled(fixedDelay = 1000 * 60 * 5)
	public void checkInboundMessages(@RequestBody Object object) {

		logger.info("Entering initializeCheckCampaignStatus() || Enter :{}", object.toString());
		logger.info("Exiting initializeCheckCampaignStatus() || Exit");
	}
	
	
	
	
	
}
