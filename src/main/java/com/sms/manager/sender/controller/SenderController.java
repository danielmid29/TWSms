package com.sms.manager.sender.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sms.manager.helper.Helper;
import com.sms.manager.model.Sender;
import com.sms.manager.model.TransactionResult;
import com.sms.manager.model.response.MessageLookupResponse;
import com.sms.manager.model.response.PlivoNumberCartResponse;
import com.sms.manager.model.response.SenderLookupResponse;
import com.sms.manager.sender.service.SenderService;

@RestController
public class SenderController {

	@Autowired
	private Helper helper;

	@Autowired
	private SenderService senderService;

	Logger logger = LoggerFactory.getLogger(SenderController.class);

	@GetMapping("/sender/search-cart")
	public ResponseEntity<PlivoNumberCartResponse> searchSenderCart(@RequestParam String type) {

		logger.info("Entering searchSenderCart() || request: {}", type);

		PlivoNumberCartResponse result = senderService.searchSenderCart(type);

		logger.info("Exiting searchSenderCart() || reponse: {}", helper.modelToString(result));
		return new ResponseEntity<PlivoNumberCartResponse>(result, HttpStatusCode.valueOf(result.getHttpCode()));

	}

	@PostMapping("/sender/buy")
	public ResponseEntity<TransactionResult> buySender(@RequestBody Sender sender) {

		logger.info("Entering buySender() || request: {}", helper.modelToString(sender));

		TransactionResult result = senderService.buySender(sender);

		logger.info("Exiting buySender() || reponse: {}", helper.modelToString(result));
		return new ResponseEntity<TransactionResult>(result, HttpStatusCode.valueOf(result.getHttpCode()));

	}

	@DeleteMapping("/sender/delete")
	public ResponseEntity<TransactionResult> deleteSender(@RequestParam String number) {

		logger.info("Entering deleteSender() || request: {}", helper.modelToString(number));

		TransactionResult result = senderService.deleteSender(number);

		logger.info("Exiting deleteSender() || reponse: {}", helper.modelToString(result));
		return new ResponseEntity<TransactionResult>(result, HttpStatusCode.valueOf(result.getHttpCode()));

	}

	@PutMapping("/sender")
	public ResponseEntity<TransactionResult> updateStatus(@RequestBody Sender sender) {

		logger.info("Entering updateStatus() || request: {}", helper.modelToString(sender));

		TransactionResult result = senderService.updateSender(sender);

		logger.info("Exiting updateStatus() || reponse: {}", helper.modelToString(result));
		return new ResponseEntity<TransactionResult>(result, HttpStatusCode.valueOf(result.getHttpCode()));

	}

	@GetMapping("/sender")
	public ResponseEntity<SenderLookupResponse> senderLookup(@RequestParam String searchValue,
			@RequestParam String assignedTo, @RequestParam int size, @RequestParam int pageNumber) {

		logger.info("Entering senderLookup() || request: {}", searchValue);
		logger.info("Entering senderLookup() || request: {}", assignedTo);
		logger.info("Entering senderLookup() || request: {}", size);
		logger.info("Entering senderLookup() || request: {}", pageNumber);
		SenderLookupResponse result = senderService.senderLookup(searchValue, assignedTo, size, pageNumber);

		logger.info("Exiting senderLookup() || reponse: {}", helper.modelToString(result));
		return new ResponseEntity<SenderLookupResponse>(result, HttpStatusCode.valueOf(result.getHttpCode()));

	}

}
