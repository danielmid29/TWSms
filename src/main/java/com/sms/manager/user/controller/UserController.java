package com.sms.manager.user.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sms.manager.helper.Helper;
import com.sms.manager.model.ResetPasswordRequest;
import com.sms.manager.model.TransactionResult;
import com.sms.manager.model.UserDetails;
import com.sms.manager.model.response.UserDetailsResponse;
import com.sms.manager.user.service.UserService;

@RestController
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private Helper helper;

	Logger logger = LoggerFactory.getLogger(UserController.class);

	@GetMapping("/user/lookup")
	public ResponseEntity<UserDetailsResponse> userLookup(@RequestParam String searchValue, @RequestParam int size, @RequestParam int pageNumber) {
		logger.info("Entering userLookup() || request: {}", searchValue);
		logger.info("Entering userLookup() || request: {}", size);
		logger.info("Entering userLookup() || request: {}", pageNumber);
		UserDetailsResponse result = userService.userLookup(searchValue, size,pageNumber);

		logger.info("Exiting userLookup() || reponse: {}", helper.modelToString(result));
		return new ResponseEntity<UserDetailsResponse>(result, HttpStatusCode.valueOf(result.getHttpCode()));
	}
	
	
	@PostMapping("/user/create")
	public ResponseEntity<TransactionResult> createUser(@RequestBody UserDetails userdetails) {
		logger.info("Entering createUser() || request: {}", helper.modelToString(userdetails));

		TransactionResult result = userService.createUser(userdetails);

		logger.info("Exiting createUser() || reponse: {}", helper.modelToString(result));
		return new ResponseEntity<TransactionResult>(result, HttpStatusCode.valueOf(result.getHttpCode()));
	}
	
	@PutMapping("/user")
	public ResponseEntity<TransactionResult> updateUser(@RequestBody UserDetails userdetails) {
		logger.info("Entering updateUser() || request: {}", helper.modelToString(userdetails));

		TransactionResult result = userService.updateUser(userdetails);

		logger.info("Exiting updateUser() || reponse: {}", helper.modelToString(result));
		return new ResponseEntity<TransactionResult>(result, HttpStatusCode.valueOf(result.getHttpCode()));
	}
	
	@PutMapping("/reset-password")
	public ResponseEntity<TransactionResult> resetPassword(@RequestBody ResetPasswordRequest resetPWRequest) {
		logger.info("Entering resetPassword()");

		TransactionResult result = userService.resetPassword(resetPWRequest);

		logger.info("Exiting resetPassword() || reponse: {}", helper.modelToString(result));
		return new ResponseEntity<TransactionResult>(result, HttpStatusCode.valueOf(result.getHttpCode()));
	}

}
