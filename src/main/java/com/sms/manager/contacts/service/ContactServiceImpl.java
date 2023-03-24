package com.sms.manager.contacts.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.sms.manager.helper.AbstractService;
import com.sms.manager.model.Contact;
import com.sms.manager.model.ContactGroup;
import com.sms.manager.model.Sender;
import com.sms.manager.model.TransactionResult;
import com.sms.manager.model.response.ContactGroupLookupResponse;
import com.sms.manager.model.response.ContactLookupResponse;
import com.sms.manager.model.response.SenderLookupResponse;
import com.sms.manager.plivo.service.PlivoService;
import com.sms.manager.repository.ContactGroupRepository;
import com.sms.manager.repository.ContactsRepository;
import com.sms.manager.status.message.StatusMessage;

@Service
public class ContactServiceImpl extends AbstractService implements ContactService {

	@Autowired
	private ContactsRepository contactRepo;

	@Autowired
	private ContactGroupRepository contactGrpRepo;

	@Autowired
	private PlivoService plivoService;

	Logger logger = LoggerFactory.getLogger(ContactServiceImpl.class);

	@Override
	public TransactionResult addContact(Contact contact) {

		logger.info("addContact() || parameter: {}", contact);

		TransactionResult transactionResult = new TransactionResult();

		String user = contact.getUser();
		String number = contact.getContactNumber();
		String group = contact.getContactGroup();

		// Removing all character other than number
		number = number.replaceAll("[^\\d.]", "");

		contact.setContactNumber(number);

		transactionResult = plivoService.validatePhoneNumber(number);
		if (!StringUtils.equals(StatusMessage.PHONE_NUMBER_VALID.getCode(), transactionResult.getCode())) {
			logger.error("addContact || returning due to number lookup error : {}", transactionResult.getMessage());
			return transactionResult;
		}
		List<Contact> existingContact = new ArrayList<>();
		if (StringUtils.isEmpty(group)) {
			existingContact = contactRepo.findByUserNumberAndNullGroup(user, number);
			if (!existingContact.isEmpty()) {
				transactionResult.setResult(StatusMessage.PHONE_NUMBER_EXIST);
				logger.error("addContact || returning since contact already exist");
				return transactionResult;
			}
		} else {
			existingContact = contactRepo.findByUserNumberAndGroup(user, number, group);
			if (!existingContact.isEmpty()) {
				transactionResult.setResult(StatusMessage.PHONE_NUMBER_GROUP_EXIST);
				logger.error("addContact || returning since contact already exist with this group name");
				return transactionResult;
			}
			List<ContactGroup> contactGroupList = contactGrpRepo.findByUserAndGroup(user, group);

			for (ContactGroup cg : contactGroupList) {
				cg.setContactsCount(cg.getContactsCount() + 1);
				cg.setLastUpdateDate(new Date());
				contactGrpRepo.save(cg);
			}
		}

		contact.setId(generateSequence(Contact.SEQUENCE_NAME));
		contactRepo.save(contact);
		transactionResult.setResult(StatusMessage.PHONE_NUMBER_ADDED);
		logger.info("addContact || Contact saved successfull");
		return transactionResult;
	}

	@Override
	public TransactionResult updateContact(Contact contact) {
		logger.info("updateContact() || parameter: {}", contact);

		TransactionResult transactionResult = new TransactionResult();

		String user = contact.getUser();
		String number = contact.getContactNumber();
		String group = contact.getContactGroup();
		String status = contact.getStatus();

		List<Contact> existingContacts = new ArrayList<>();
		if (StringUtils.isEmpty(group)) {
			existingContacts = contactRepo.findByUserAndNumber(user, number);
		} else {
			existingContacts = contactRepo.findByUserNumberAndGroup(user, number, group);
		}
		if (!existingContacts.isEmpty()) {
			for (Contact ec : existingContacts) {
				if (!StringUtils.isEmpty(status))
					ec.setStatus(status);

				ec.setLastUpdateDate(new Date());
				contactRepo.save(ec);
			}
		}

		transactionResult.setResult(StatusMessage.PHONE_NUMBER_UPDATED);
		logger.info("updateContact() || successful: {}", transactionResult);
		return transactionResult;
	}

	@Override
	public TransactionResult deleteContact(Contact contact) {
		logger.info("deleteContact() || parameter: {}", contact);

		TransactionResult transactionResult = new TransactionResult();

		String user = contact.getUser();
		String number = contact.getContactNumber();
		String group = contact.getContactGroup();

		if (StringUtils.isEmpty(group)) {
			contactRepo.deleteByUserAndNumber(user, number);
		} else {
			contactRepo.deleteByUserNumberAndGroup(user, number, group);

			List<ContactGroup> contactGroupList = contactGrpRepo.findByUserAndGroup(user, group);

			for (ContactGroup cg : contactGroupList) {
				cg.setContactsCount(cg.getContactsCount() - 1);
				cg.setLastUpdateDate(new Date());
				contactGrpRepo.save(cg);
			}
		}

		transactionResult.setResult(StatusMessage.PHONE_NUMBER_DELETED);
		logger.info("deleteContact() || successful: {}", transactionResult);
		return transactionResult;
	}

	@Override
	public TransactionResult addContactGroup(ContactGroup contactList) {

		logger.info("addContactGroup() || parameter: {}", contactList);
		String name = contactList.getName();
		String user = contactList.getUser();
		List<Contact> contacts = contactList.getContactList();
		TransactionResult transactionResult = new TransactionResult();

		List<Contact> existingContacts = contactRepo.findByUserAndGroup(user, name);

		if (!existingContacts.isEmpty()) {
			transactionResult.setResult(StatusMessage.CONTACT_GROUP_EXIST);
			logger.error("addContactGroup || returning since contact group already exist");
			return transactionResult;
		}

		for (Contact contact : contacts) {
			contact.setId(generateSequence(Contact.SEQUENCE_NAME));
			contact.setUser(user);
			contact.setContactGroup(name);
		}

		contactList.setId(generateSequence(ContactGroup.SEQUENCE_NAME));
		contactList.setContactsCount(contacts.size());
		contactGrpRepo.save(contactList);
		contactRepo.saveAll(contacts);

		transactionResult = new TransactionResult(StatusMessage.CONTACT_GROUP_ADDED);
		logger.info("addContactGroup() || Contact group added");
		return transactionResult;
	}

	@Override
	public TransactionResult updateContactGroup(ContactGroup contactList) {

		logger.info("updateContactGroup() || parameter: {}", contactList);
		String name = contactList.getName();
		String user = contactList.getUser();
		String status = contactList.getStatus();
		TransactionResult transactionResult = new TransactionResult();

		List<Contact> existingContacts = contactRepo.findByUserAndGroup(user, name);

		for (Contact contact : existingContacts) {
			contact.setStatus(status);
			contact.setLastUpdateDate(new Date());
		}
		List<ContactGroup> existingContactGroup = contactGrpRepo.findByUserAndGroup(user, name);
		for (ContactGroup cg : existingContactGroup) {
			cg.setStatus(status);
			cg.setLastUpdateDate(new Date());
			contactGrpRepo.save(cg);
		}
		contactRepo.saveAll(existingContacts);

		transactionResult = new TransactionResult(StatusMessage.CONTACT_GROUP_UPDATED);
		logger.info("updateContactGroup() || Contact group updated");
		return transactionResult;

	}

	@Override
	public TransactionResult deleteContactGroup(ContactGroup contactList) {

		logger.info("deleteContactGroup() || parameter: {}", contactList);
		String name = contactList.getName();
		String user = contactList.getUser();
		TransactionResult transactionResult = new TransactionResult();

		contactRepo.deleteByUserAndGroup(user, name);

		contactGrpRepo.deleteByUserAndGroup(user, name);

		transactionResult = new TransactionResult(StatusMessage.CONTACT_GROUP_DELETED);
		logger.info("deleteContactGroup() || Contact group deleted");
		return transactionResult;

	}

	@Override
	public ContactLookupResponse contactLookup(String searchValue, String user, int size, int pageNumber) {

		logger.info("contactLookup || parameter searchValue : {}", searchValue);
		logger.info("contactLookup || parameter size : {}", size);
		logger.info("contactLookup || parameter pageNumber : {}", pageNumber);

		Page<Contact> contacts = null;
		long count = 0;

		if (StringUtils.isEmpty(user)) {
			contacts = contactRepo.findByValue(searchValue,
					PageRequest.of(pageNumber, size, Sort.by(Direction.DESC, "lastUpdateDate")));

			count = contactRepo.countByValue(searchValue);
		} else {
			contacts = contactRepo.findByValueUser(searchValue, user,
					PageRequest.of(pageNumber, size, Sort.by(Direction.DESC, "lastUpdateDate")));

			count = contactRepo.countByValueUser(searchValue, user);
		}
		ContactLookupResponse response = new ContactLookupResponse();
		response.setTotalPage(contacts.getTotalPages());
		response.setTotalCount(count);

		response.setContactList(contacts.getContent());
		response.setResult(StatusMessage.CONTACT_FOUND);

		if (count == 0) {
			response.setResult(StatusMessage.CONTACT_NOT_FOUND);
		}
		logger.info("contactLookup || response : {}", response);

		return response;
	}

	@Override
	public ContactGroupLookupResponse contactGroupLookup(String searchValue, String user, int size, int pageNumber) {

		logger.info("contactGroupLookup || parameter searchValue : {}", searchValue);
		logger.info("contactGroupLookup || parameter user : {}", user);
		logger.info("contactGroupLookup || parameter size : {}", size);
		logger.info("contactGroupLookup || parameter pageNumber : {}", pageNumber);
		Page<ContactGroup> contacts = null;
		long count = 0;

		if (StringUtils.isEmpty(user)) {
			contacts = contactGrpRepo.findByValue(searchValue,
					PageRequest.of(pageNumber, size, Sort.by(Direction.DESC, "lastUpdateDate")));

			count = contactGrpRepo.countByValue(searchValue);
		} else {
			contacts = contactGrpRepo.findByValueUser(searchValue, user,
					PageRequest.of(pageNumber, size, Sort.by(Direction.DESC, "lastUpdateDate")));

			count = contactGrpRepo.countByValueUser(searchValue, user);
		}

		ContactGroupLookupResponse response = new ContactGroupLookupResponse();
		response.setTotalPage(contacts.getTotalPages());
		response.setTotalCount(count);

		response.setContactGroupList(contacts.getContent());
		response.setResult(StatusMessage.CONTACT_GROUP_FOUND);

		if (count == 0) {
			response.setResult(StatusMessage.CONTACT_GROUP_NOT_FOUND);
		}
		logger.info("contactGroupLookup || response : {}", response);

		return response;
	}
}
