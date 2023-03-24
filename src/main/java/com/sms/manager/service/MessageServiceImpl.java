package com.sms.manager.service;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sms.manager.contacts.service.ContactService;
import com.sms.manager.helper.AbstractService;
import com.sms.manager.model.Campaign;
import com.sms.manager.model.Contact;
import com.sms.manager.model.Message;
import com.sms.manager.model.PlivoResponse;
import com.sms.manager.model.TransactionResult;
import com.sms.manager.model.UserDetails;
import com.sms.manager.model.response.MessageLookupResponse;
import com.sms.manager.model.response.UserDetailsResponse;
import com.sms.manager.plivo.service.PlivoService;
import com.sms.manager.repository.CampaignRepository;
import com.sms.manager.repository.MessageRepository;
import com.sms.manager.status.message.StatusMessage;

@Service
public class MessageServiceImpl extends AbstractService implements MessageService {

	@Autowired
	private ContactService contactService;

	@Autowired
	private PlivoService plivoService;

	@Autowired
	private MessageRepository messageRepo;

	@Autowired
	private CampaignRepository campaignRepo;

	Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

	@Override
	public TransactionResult sendMessage(Message message) {

		logger.info("sendMessage() || parameter message: {}", message);

		TransactionResult result = new TransactionResult();

		String from = message.getFrom();
		String to = message.getTo();
		String type = message.getType();
		String user = message.getUser();
		String messageString = message.getMessage();

		// Removing all character other than number
		to = to.replaceAll("[^\\d.]", "");
		message.setTo(to);

		if (StringUtils.equals(type, "new")) {
			Contact newContact = new Contact(user, to);
			result = contactService.addContact(newContact);
			if (!StringUtils.equals(StatusMessage.PHONE_NUMBER_ADDED.getCode(), result.getCode())) {
				logger.error("sendMessage || returning due to error while adding contact : {}", result.getMessage());
				return result;
			}
		}

		result = plivoService.sendMessage(from, to, messageString);

		if (!StringUtils.equals(StatusMessage.SMS_DELIVERY_SUCCESS.getCode(), result.getCode())) {
			logger.error("sendMessage || returning due to error : {}", result.getMessage());
			return result;
		}

		message.setId(generateSequence(Message.SEQUENCE_NAME));
		message.setCampaign("N/A");
		message.setType("outbound");
		message.setMessageUuid(result.getMessageUuid().get(0));
		messageRepo.save(message);

		result.setResult(StatusMessage.SMS_DELIVERY_SUCCESS);

		logger.info("sendMessage() || response: {}", result);

		return result;

	}

	@Override
	public MessageLookupResponse messageLookup(String searchValue, String user, int size, int pageNumber) {

		logger.info("messageLookup || parameter searchValue : {}", searchValue);
		logger.info("messageLookup || parameter size : {}", size);
		logger.info("messageLookup || parameter pageNumber : {}", pageNumber);

		Page<Message> message = messageRepo.findByValue(searchValue,
				PageRequest.of(pageNumber, size, Sort.by(Direction.DESC, "processed_date")));

		long count = messageRepo.countByValue(searchValue);

		if (StringUtils.isEmpty(user)) {
			message = messageRepo.findByValue(searchValue,
					PageRequest.of(pageNumber, size, Sort.by(Direction.DESC, "lastUpdateDate")));

			count = messageRepo.countByValue(searchValue);
		} else {
			message = messageRepo.findByValueUser(searchValue, user,
					PageRequest.of(pageNumber, size, Sort.by(Direction.DESC, "lastUpdateDate")));

			count = messageRepo.countByValueUser(searchValue, user);
		}
		MessageLookupResponse response = new MessageLookupResponse();
		response.setTotalPage(message.getTotalPages());
		response.setTotalCount(count);

		response.setMessages(message.getContent());
		response.setResult(StatusMessage.MESSAGE_FOUND);

		if (count == 0) {
			response.setResult(StatusMessage.MESSAGE_NOT_FOUND);
		}
		logger.info("messageLookup || response : {}", response);

		return response;
	}

	@Override
	public void updateStatus() {

		logger.info("updateStatus || inside update message status ");

		List<Message> messageList = messageRepo.findByStatus("QUEUED");

		PlivoResponse plivoResponse = new PlivoResponse();
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		for (Message message : messageList) {
			String messageUuid = message.getMessageUuid();

			String plivoResponseString = plivoService.checkMesssageStatus(messageUuid);

			if (StringUtils.contains(plivoResponseString, "Error"))
				continue;

			try {
				plivoResponse = objectMapper.readValue(plivoResponseString, PlivoResponse.class);
			} catch (JsonProcessingException e) {
			}

			String status = "";
			String plivoStatus = plivoResponse.getMessage_state();

			if (plivoStatus.equals("sent")) {
				message.setPlivoMessageResponse("Message Sent");
				status = "SENT";
			} else if (plivoStatus.equals("delivered")) {
				message.setPlivoMessageResponse("Delivery success");
				status = "DELIVERED";
			} else if (plivoStatus.equals("failed") || plivoStatus.equals("undelivered")) {
				if (!StringUtils.isEmpty(plivoResponse.getError()))
					message.setPlivoMessageResponse(plivoResponse.getError());
				else if (!StringUtils.isEmpty(plivoResponse.getMessage()))
					message.setPlivoMessageResponse(plivoResponse.getError());
				else
					message.setPlivoMessageResponse("Unknown Error");
				status = "FAILED";
			} else
				continue;

			if (StringUtils.equals(message.getTo(), "Updating Shortly"))
				message.setTo(plivoResponse.getTo_number());

			message.setStatus(status.toUpperCase());
			messageRepo.save(message);
		}

	}

	@Override
	public void updateCampaignStatus() {

		List<Campaign> campaignList = campaignRepo.findByStatus("QUEUED");

		for (Campaign campaign : campaignList) {
			List<String> messageUuidList = campaign.getMessageUuid();

			long success = 0;
			long failed = 0;
			long sent = 0;
			for (String messageUuid : messageUuidList) {

				Message responseMessage = messageRepo.findByMessageUuid(messageUuid);

				String status = responseMessage.getStatus();

				if (status.equals("SENT")) {
					sent++;
				} else if (status.equals("DELIVERED")) {
					success++;
				} else if (status.equals("FAILED")) {
					failed++;
				} else
					continue;

			}

			if (success + failed + sent == messageUuidList.size()) {
				campaign.setPlivoMessageResponse(
						String.format("Delivered: %d , Sent: %d, Failed: %d", success, sent, failed));
				campaign.setStatus("COMPLETED");
			} else {
				campaign.setPlivoMessageResponse(String.format("Delivered: %d , Failed: %d, Sent: %d, Queued: %d", success,
						failed, sent, messageUuidList.size() - (success + failed + sent)));
				campaign.setStatus("PROCESSING");
			}
			campaignRepo.save(campaign);
		}

	}

}
