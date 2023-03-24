package com.sms.manager.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sms.manager.helper.Helper;
import com.sms.manager.model.response.DashboardResponse;
import com.sms.manager.service.DashboardService;

@RestController
public class DashboardController {

	@Autowired
	private Helper helper;

	
	@Autowired
	private DashboardService dashboardService;
	
	Logger logger = LoggerFactory.getLogger(MessageController.class);
	
	@GetMapping("/dashboard")
	public ResponseEntity<DashboardResponse> messageLookup(@RequestParam String user, @RequestParam String role) {

		logger.info("Entering messageLookup() || request: {}", user);
		logger.info("Entering messageLookup() || request: {}", role);
		
		DashboardResponse result = dashboardService.getDashBoardDetails(user, role);

		logger.info("Exiting messageLookup() || reponse: {}", helper.modelToString(result));
		return new ResponseEntity<DashboardResponse>(result, HttpStatusCode.valueOf(result.getHttpCode()));
		
	}
}
