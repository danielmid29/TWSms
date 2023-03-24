package com.sms.manager.campaign.service;

import java.util.ArrayList;
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

import com.sms.manager.contacts.service.ContactServiceImpl;
import com.sms.manager.helper.AbstractService;
import com.sms.manager.model.Campaign;
import com.sms.manager.model.Contact;
import com.sms.manager.model.ContactGroup;
import com.sms.manager.model.Message;
import com.sms.manager.model.TransactionResult;
import com.sms.manager.model.response.CampaignLookupResponse;
import com.sms.manager.model.response.ContactGroupLookupResponse;
import com.sms.manager.plivo.service.PlivoService;
import com.sms.manager.repository.CampaignRepository;
import com.sms.manager.repository.ContactsRepository;
import com.sms.manager.repository.MessageRepository;
import com.sms.manager.status.message.StatusMessage;

@Service
public class CampaignServiceImpl extends AbstractService implements CampaignService {

	@Autowired
	private ContactsRepository contactRepo;

	@Autowired
	private CampaignRepository campaignRepo;

	@Autowired
	private PlivoService messageService;

	@Autowired
	private MessageRepository messageRepo;
	
	Logger logger = LoggerFactory.getLogger(CampaignServiceImpl.class);

	@Override
	public TransactionResult sendCampaign(Campaign campaign) {

		logger.info("sendCampaign() || parameter: {}", campaign);

		TransactionResult result = new TransactionResult();
		String name = campaign.getName();
		String user = campaign.getUser();
		String sender = campaign.getSender();
		String[] contactGroups = campaign.getContactGroups();
		String message = campaign.getMessage();

		List<Campaign> campaignExist = campaignRepo.findByUserAndName(user, name);

		if (!campaignExist.isEmpty()) {
			logger.error("sendCampaign || Campaign already exist with this name");
			result.setResult(StatusMessage.CAMPAIGN_EXIST);
			return result;
		}

		List<String> numberGroup = new ArrayList<>();

		int totalCounts = 0;
		for (int i = 0; i < contactGroups.length; i++) {
			String contactGroup = contactGroups[i];
			List<Contact> contacts = contactRepo.findByUserAndGroup(user, contactGroup);
			for (Contact contact : contacts) {
				if (!numberGroup.contains(contact.getContactNumber())) {
					numberGroup.add(contact.getContactNumber());
					totalCounts++;
				}
			}
		}

		result = messageService.sendBulkMessage(sender, numberGroup, message);

		if (!StringUtils.equals(StatusMessage.CAMPAIGN_SUCCESS.getCode(), result.getCode())) {
			logger.error("sendMessage || returning due to error : {}", result.getMessage());
			result.setMessage(result.getMessage().replace("{campaign-name}", name));
			return result;
		}

		List<String> messageUuidList = result.getMessageUuid();
		campaign.setId(generateSequence(Campaign.SEQUENCE_NAME));
		campaign.setMessageUuid(messageUuidList);
		campaign.setContacts(totalCounts);
		campaignRepo.save(campaign);
		
		List<Message> messageList = new ArrayList<>();
		for(String messageUuid : messageUuidList) {
			Message messageDoc = new Message();
			messageDoc.setId(generateSequence(Message.SEQUENCE_NAME));
			messageDoc.setCampaign(name);
			messageDoc.setFrom(sender);
			messageDoc.setMessage(message);
			messageDoc.setMessageUuid(messageUuid);
			messageDoc.setType("outbound");
			messageDoc.setUser(user);
			messageDoc.setTo("Updating Shortly");
			messageList.add(messageDoc);
		}
		messageRepo.saveAll(messageList);
		
		String failureString = " for " + messageUuidList.size() + " out of" + totalCounts
				+ ". Possible due to invalid number for failed desitations";
		failureString = (totalCounts - messageUuidList.size() > 0) ? failureString : "";

		result.setResult(StatusMessage.CAMPAIGN_SUCCESS);
		result.setMessage(result.getMessage().replace("{campaign-name}", name) + failureString);
		logger.info("sendCampaign() || response: {}", result);
		return result;
	}

	@Override
	public CampaignLookupResponse campaignLookup(String searchValue, String user, int size, int pageNumber) {

		logger.info("campaignLookup || parameter searchValue : {}", searchValue);
		logger.info("campaignLookup || parameter user : {}", user);
		logger.info("campaignLookup || parameter size : {}", size);
		logger.info("campaignLookup || parameter pageNumber : {}", pageNumber);
		Page<Campaign> campaigns = null;
		long count = 0;

		if (StringUtils.isEmpty(user)) {
			campaigns = campaignRepo.findByValue(searchValue,
					PageRequest.of(pageNumber, size, Sort.by(Direction.DESC, "processed_date")));

			count = campaignRepo.countByValue(searchValue);
		} else {
			campaigns = campaignRepo.findByValueUser(searchValue, user,
					PageRequest.of(pageNumber, size, Sort.by(Direction.DESC, "processed_date")));

			count = campaignRepo.countByValueUser(searchValue, user);
		}

		CampaignLookupResponse response = new CampaignLookupResponse();
		response.setTotalPage(campaigns.getTotalPages());
		response.setTotalCount(count);

		response.setCampaignList(campaigns.getContent());
		response.setResult(StatusMessage.CAMPAIGN_FOUND);

		if (count == 0) {
			response.setResult(StatusMessage.CAMPAIGN_NOT_FOUND);
		}
		logger.info("campaignLookup || response : {}", response);

		return response;
	}
}
