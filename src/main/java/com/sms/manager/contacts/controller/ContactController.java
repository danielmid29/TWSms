package com.sms.manager.contacts.controller;

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

import com.sms.manager.contacts.service.ContactService;
import com.sms.manager.helper.Helper;
import com.sms.manager.model.Contact;
import com.sms.manager.model.ContactGroup;
import com.sms.manager.model.TransactionResult;
import com.sms.manager.model.response.ContactGroupLookupResponse;
import com.sms.manager.model.response.ContactLookupResponse;
import com.sms.manager.model.response.SenderLookupResponse;
import com.sms.manager.user.controller.UserController;

@RestController
public class ContactController {

	@Autowired
	private ContactService contactService;

	@Autowired
	private Helper helper;

	Logger logger = LoggerFactory.getLogger(ContactController.class);

	@PostMapping("/contact/create")
	public ResponseEntity<TransactionResult> addContact(@RequestBody Contact contacts) {
		logger.info("Entering addContact() || request: {}", helper.modelToString(contacts));

		TransactionResult result = contactService.addContact(contacts);

		logger.info("Exiting addContact() || reponse: {}", helper.modelToString(result));
		return new ResponseEntity<TransactionResult>(result, HttpStatusCode.valueOf(result.getHttpCode()));
	}

	@PutMapping("/contact")
	public ResponseEntity<TransactionResult> editContact(@RequestBody Contact contacts) {
		logger.info("Entering editContact() || request: {}", helper.modelToString(contacts));

		TransactionResult result = contactService.updateContact(contacts);

		logger.info("Exiting editContact() || reponse: {}", helper.modelToString(result));
		return new ResponseEntity<TransactionResult>(result, HttpStatusCode.valueOf(result.getHttpCode()));
	}

	@DeleteMapping("/contact")
	public ResponseEntity<TransactionResult> deleteContact(@RequestBody Contact contacts) {
		logger.info("Entering deleteContact() || request: {}", helper.modelToString(contacts));

		TransactionResult result = contactService.deleteContact(contacts);

		logger.info("Exiting deleteContact() || reponse: {}", helper.modelToString(result));
		return new ResponseEntity<TransactionResult>(result, HttpStatusCode.valueOf(result.getHttpCode()));
	}

	@PostMapping("/contact-group/create")
	public ResponseEntity<TransactionResult> addContactGroup(@RequestBody ContactGroup contactList) {
		logger.info("Entering addContactGroup() || request: {}", helper.modelToString(contactList));

		TransactionResult result = contactService.addContactGroup(contactList);

		logger.info("Exiting addContactGroup() || reponse: {}", helper.modelToString(result));
		return new ResponseEntity<TransactionResult>(result, HttpStatusCode.valueOf(result.getHttpCode()));
	}

	@PutMapping("/contact-group")
	public ResponseEntity<TransactionResult> updateContactGroup(@RequestBody ContactGroup contactList) {
		logger.info("Entering updateContactGroup() || request: {}", helper.modelToString(contactList));

		TransactionResult result = contactService.updateContactGroup(contactList);

		logger.info("Exiting updateContactGroup() || reponse: {}", helper.modelToString(result));
		return new ResponseEntity<TransactionResult>(result, HttpStatusCode.valueOf(result.getHttpCode()));
	}

	@DeleteMapping("/contact-group")
	public ResponseEntity<TransactionResult> deleteContactGroup(@RequestBody ContactGroup contactList) {
		logger.info("Entering deleteContactGroup() || request: {}", helper.modelToString(contactList));

		TransactionResult result = contactService.deleteContactGroup(contactList);

		logger.info("Exiting deleteContactGroup() || reponse: {}", helper.modelToString(result));
		return new ResponseEntity<TransactionResult>(result, HttpStatusCode.valueOf(result.getHttpCode()));
	}

	@GetMapping("/contact")
	public ResponseEntity<ContactLookupResponse> contactLookup(@RequestParam String searchValue,
			@RequestParam String user, @RequestParam int size, @RequestParam int pageNumber) {

		logger.info("Entering contactLookup() || request: {}", searchValue);
		logger.info("Entering contactLookup() || request: {}", user);
		logger.info("Entering contactLookup() || request: {}", size);
		logger.info("Entering contactLookup() || request: {}", pageNumber);
		ContactLookupResponse result = contactService.contactLookup(searchValue, user, size, pageNumber);

		logger.info("Exiting contactLookup() || reponse: {}", helper.modelToString(result));
		return new ResponseEntity<ContactLookupResponse>(result, HttpStatusCode.valueOf(result.getHttpCode()));

	}

	@GetMapping("/contact-group")
	public ResponseEntity<ContactGroupLookupResponse> contactGroupLookup(@RequestParam String searchValue,
			@RequestParam String user, @RequestParam int size, @RequestParam int pageNumber) {

		logger.info("Entering contactGroupLookup() || request: {}", searchValue);
		logger.info("Entering contactGroupLookup() || request: {}", user);
		logger.info("Entering contactGroupLookup() || request: {}", size);
		logger.info("Entering contactGroupLookup() || request: {}", pageNumber);
		ContactGroupLookupResponse result = contactService.contactGroupLookup(searchValue, user, size, pageNumber);

		logger.info("Exiting contactGroupLookup() || reponse: {}", helper.modelToString(result));
		return new ResponseEntity<ContactGroupLookupResponse>(result, HttpStatusCode.valueOf(result.getHttpCode()));

	}
}
