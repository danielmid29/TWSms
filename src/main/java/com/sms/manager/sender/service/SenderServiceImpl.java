package com.sms.manager.sender.service;

import java.util.Date;

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
import com.sms.manager.model.Message;
import com.sms.manager.model.Sender;
import com.sms.manager.model.TransactionResult;
import com.sms.manager.model.response.MessageLookupResponse;
import com.sms.manager.model.response.PlivoNumberCartResponse;
import com.sms.manager.model.response.SenderLookupResponse;
import com.sms.manager.plivo.service.PlivoService;
import com.sms.manager.repository.SenderRepository;
import com.sms.manager.status.message.StatusMessage;

@Service
public class SenderServiceImpl extends AbstractService implements SenderService {

	@Autowired
	private PlivoService plivoService;

	@Autowired
	private SenderRepository senderRepo;

	Logger logger = LoggerFactory.getLogger(SenderServiceImpl.class);

	@Override
	public PlivoNumberCartResponse searchSenderCart(String type) {

		logger.info("searchSenderCart() || parameter type: {}", type);

		PlivoNumberCartResponse numberCartResponse = plivoService.getNumberCart(type);

		logger.info("searchSenderCart() || response: {}", numberCartResponse);

		return numberCartResponse;
	}

	@Override
	public TransactionResult buySender(Sender sender) {
		logger.info("buySender() || parameter number: {}", sender);

		String number = sender.getNumber();

		TransactionResult plivoResponse = plivoService.buyNumber(number);

		if (!StatusMessage.PHONE_NUMBER_BOUGHT.getCode().equals(plivoResponse.getCode())) {
			logger.error("buySender || returning due to error while buying contact : {}", plivoResponse.getMessage());
			return plivoResponse;
		}
		sender.setId(generateSequence(Sender.SEQUENCE_NAME));
		senderRepo.save(sender);

		plivoResponse.setMessage(plivoResponse.getMessage() + sender.getAssignedTo());

		logger.info("buySender() || response: {}", plivoResponse);

		return plivoResponse;
	}

	@Override
	public TransactionResult deleteSender(String sender) {
		logger.info("buySender() || parameter number: {}", sender);

		TransactionResult plivoResponse = plivoService.unrentNumber(sender);

		if (!StatusMessage.PHONE_NUMBER_UNRENT.getCode().equals(plivoResponse.getCode())) {
			logger.error("buySender || returning while unrenting contact due to error  : {}",
					plivoResponse.getMessage());
			return plivoResponse;
		}

		Sender existingRecord = senderRepo.findByNumber(sender);
		String assignedTo = existingRecord.getAssignedTo();

		senderRepo.deleteByNumber(sender);
		
		plivoResponse.setMessage(plivoResponse.getMessage() + assignedTo);

		logger.info("buySender() || response: {}", plivoResponse);

		return plivoResponse;
	}

	@Override
	public TransactionResult updateSender(Sender sender) {
		logger.info("buySender() || parameter number: {}", sender);
		
		String number = sender.getNumber();
		String status = sender.getStatus();
		
		Sender existingRecord = senderRepo.findByNumber(number);
		existingRecord.setStatus(status);
		existingRecord.setLastUpdateDate(new Date());
		senderRepo.save(existingRecord);
		
		return new TransactionResult(StatusMessage.SENDER_UPDATED);
	}
	

	@Override
	public SenderLookupResponse senderLookup(String searchValue, String user, int size, int pageNumber) {

		logger.info("senderLookup || parameter searchValue : {}", searchValue);
		logger.info("senderLookup || parameter user : {}", user);
		logger.info("senderLookup || parameter size : {}", size);
		logger.info("senderLookup || parameter pageNumber : {}", pageNumber);

		Page<Sender> sender = null;

		long count = 0;
		
		if (StringUtils.isEmpty(user)) {
			sender = senderRepo.findByValue(searchValue,
					PageRequest.of(pageNumber, size, Sort.by(Direction.DESC, "lastUpdateDate")));

			count = senderRepo.countByValue(searchValue);
		} else {
			sender = senderRepo.findByValueUser(searchValue, user,
					PageRequest.of(pageNumber, size, Sort.by(Direction.DESC, "lastUpdateDate")));

			count = senderRepo.countByValueUser(searchValue, user);
		}
		SenderLookupResponse response = new SenderLookupResponse();
		response.setTotalPage(sender.getTotalPages());
		response.setTotalCount(count);

		response.setSenderList(sender.getContent());
		response.setResult(StatusMessage.SENDER_FOUND);

		if (count == 0) {
			response.setResult(StatusMessage.SENDER_NOT_FOUND);
		}
		logger.info("senderLookup || response : {}", response);

		return response;
	}

}
