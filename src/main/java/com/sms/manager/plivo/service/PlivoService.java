package com.sms.manager.plivo.service;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plivo.api.Plivo;
import com.plivo.api.exceptions.PlivoRestException;
import com.plivo.api.models.message.Message;
import com.plivo.api.models.message.MessageCreateResponse;
import com.sms.manager.model.PlivoResponse;
import com.sms.manager.model.TransactionResult;
import com.sms.manager.model.response.PlivoNumberCartResponse;
import com.sms.manager.status.message.StatusMessage;

@Service
public class PlivoService {

	@Value("${plivo.auth.id}")
	private String plivoAuthId;

	@Value("${plivo.auth.token}")
	private String plivoAuthToken;

	@Value("${plivo.number.validation}")
	private String numberValidationUrl;

	@Value("${plivo.number.cart.url}")
	private String numberCartUrl;

	@Value("${plivo.number.buy.url}")
	private String buyNumberUrl;

	@Value("${plivo.number.unrent.url}")
	private String unrentNumberUrl;

	@Value("${plivo.message.status.check.url}")
	private String checkMessageStatusUrl;

	@Value("${plivo.auth.sender.id}")
	private String senderId;

	private RestTemplate restTemplate = new RestTemplate();

	Logger logger = LoggerFactory.getLogger(PlivoService.class);

	public TransactionResult sendMessage(String to, String message) {
		return sendMessage(senderId, to, message);
	}

	public TransactionResult sendMessage(String from, String to, String message) {

		logger.info("sendMessage || Paramete from: {}", from);
		logger.info("sendMessage || Paramete to: {}", to);

		TransactionResult transactionResult = new TransactionResult(StatusMessage.SMS_DELIVERY_SUCCESS);

		Plivo.init(plivoAuthId, plivoAuthToken);
		MessageCreateResponse response = new MessageCreateResponse();
		try {
			response = Message.creator(from, to, message).create();
			logger.info("sendMessage || response : {}", response);

		}

		catch (PlivoRestException | IOException e) {
			PlivoResponse error = new PlivoResponse();
			ObjectMapper objectMapper = new ObjectMapper();
			transactionResult.setResult(StatusMessage.SMS_DELIVERY_FAILED);
			try {
				objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				error = objectMapper.readValue(e.getMessage(), PlivoResponse.class);
				logger.error("sendMessage || Error : {}", error);
				transactionResult.setMessage(transactionResult.getMessage() + error);
			} catch (JsonProcessingException e1) {
				logger.error("sendMessage() || Error in sending message: {}", e.getMessage());
				transactionResult.setMessage(transactionResult.getMessage() + e.getMessage());
			}
		}
		List<String> messageUuids = response.getMessageUuid();
		transactionResult.setMessageUuid(messageUuids);
		transactionResult.setMessage(response.getMessage());
		logger.info("sendMessage || SMS delivered");
		return transactionResult;
	}

	public TransactionResult sendBulkMessage(String from, List<String> to, String message) {

		logger.info("sendBulkMessage || Paramete from: {}", from);
		logger.info("sendBulkMessage || Paramete to: {}", to);

		TransactionResult transactionResult = new TransactionResult(StatusMessage.CAMPAIGN_SUCCESS);

		Plivo.init(plivoAuthId, plivoAuthToken);
		MessageCreateResponse response = new MessageCreateResponse();
		try {
			response = Message.creator(from, to, message).create();
			logger.info("sendBulkMessage || response : {}", response);

		}

		catch (PlivoRestException | IOException e) {
			PlivoResponse error = new PlivoResponse();
			ObjectMapper objectMapper = new ObjectMapper();
			transactionResult.setResult(StatusMessage.CAMPAIGN_FAILED);
			try {
				objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				error = objectMapper.readValue(e.getMessage(), PlivoResponse.class);
				logger.error("sendBulkMessage || Error : {}", error);
				transactionResult.setMessage(transactionResult.getMessage() + error);
			} catch (JsonProcessingException e1) {
				logger.error("sendBulkMessage() || Error in sending message: {}", e.getMessage());
				transactionResult.setMessage(transactionResult.getMessage().replace("{mesage}", e.getMessage()));
			}
		}

		List<String> messageUuids = response.getMessageUuid();
		transactionResult.setMessageUuid(messageUuids);
		transactionResult.setMessage(response.getMessage());
		logger.info("sendBulkMessage || SMS delivered");
		return transactionResult;
	}

	@SuppressWarnings("finally")
	public TransactionResult validatePhoneNumber(String contact) {
		TransactionResult result = new TransactionResult();
		String message = "";
		PlivoResponse error = new PlivoResponse();
		ObjectMapper objectMapper = new ObjectMapper();
		HttpEntity<?> httpEntity = new HttpEntity<>(getPlivoHeader());
		String urlTemplate = UriComponentsBuilder.fromHttpUrl(numberValidationUrl + contact)
				.queryParam("type", "{type}").encode().toUriString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("type", "carrier");
		ResponseEntity<String> response = null;
		try {
			response = restTemplate.exchange(urlTemplate, HttpMethod.GET, httpEntity, String.class, params);
		} catch (HttpClientErrorException e) {
			message = e.getResponseBodyAsString();
		} catch (Exception e) {
			message = e.getMessage();
		} finally {
			if (!message.isEmpty()) {
				try {
					objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
					error = objectMapper.readValue(message, PlivoResponse.class);
					logger.error("validatePhoneNumber() || Error in phone number lookup: {}", error);
					if (error.getError_code() == 404)
						result.setResult(StatusMessage.PHONE_NUMBER_INVALID);
					else {
						message = "Error in phone number lookup: " + error;
						result.setResult(StatusMessage.PHONE_NUMBER_VALIDATION_FAILED);
						result.setMessage(result.getMessage() + error);
					}
				} catch (Exception e1) {
					logger.error("validatePhoneNumber() || Error in phone number lookup: {}", message);
					result.setResult(StatusMessage.PHONE_NUMBER_VALIDATION_FAILED);
					result.setMessage(result.getMessage() + message);
				}
				return result;
			} else {
				logger.info("validatePhoneNumber() || Valid Number");
				return new TransactionResult(StatusMessage.PHONE_NUMBER_VALID);
			}
		}
	}

	@SuppressWarnings("finally")
	public PlivoNumberCartResponse getNumberCart(String type) {
		TransactionResult result = new TransactionResult();
		String message = "";
		PlivoResponse error = new PlivoResponse();
		ObjectMapper objectMapper = new ObjectMapper();
		HttpEntity<?> httpEntity = new HttpEntity<>(getPlivoHeader());
		Map<String, String> params = new HashMap<String, String>();
		params.put("type", "carrier");
		ResponseEntity<String> response = null;
		PlivoNumberCartResponse responseBody = new PlivoNumberCartResponse();
		try {
			response = restTemplate.exchange(numberCartUrl + type, HttpMethod.GET, httpEntity, String.class, params);
		} catch (HttpClientErrorException e) {
			message = e.getResponseBodyAsString();
		} catch (Exception e) {
			message = e.getMessage();
		} finally {
			if (!message.isEmpty()) {
				try {
					objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
					error = objectMapper.readValue(message, PlivoResponse.class);
					logger.error("getNumberCart() || Error in phone number cart lookup: {}", error);

					message = "Error in phone number lookup: " + error.getMessage();
					result.setResult(StatusMessage.PHONE_NUMBER_CART_SEARCH_FAILED);
					result.setMessage(result.getMessage() + error);

				} catch (Exception e1) {
					logger.error("getNumberCart() || Error in phone number cart lookup: {}", message);
					result.setResult(StatusMessage.PHONE_NUMBER_CART_SEARCH_FAILED);
					result.setMessage(result.getMessage() + message);
				}
				responseBody.setResult(StatusMessage.PHONE_NUMBER_CART_SEARCH_FAILED);
				responseBody.setMessage(result.getMessage());
				return responseBody;
			} else {
				objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				try {
					responseBody = objectMapper.readValue(response.getBody(), PlivoNumberCartResponse.class);
				} catch (Exception e) {
				}
				logger.info("getNumberCart() || Retrieved numbers");
				responseBody.setResult(StatusMessage.PHONE_NUMBER_CART_SEARCH_SUCCESS);
				return responseBody;
			}
		}
	}

	@SuppressWarnings("finally")
	public TransactionResult buyNumber(String number) {
		TransactionResult result = new TransactionResult();
		String message = "";
		PlivoResponse error = new PlivoResponse();
		ObjectMapper objectMapper = new ObjectMapper();
		HttpEntity<?> httpEntity = new HttpEntity<>(getPlivoHeader());
		Map<String, String> params = new HashMap<String, String>();
		params.put("type", "carrier");
		ResponseEntity<String> response = null;
		try {
			response = restTemplate.exchange(buyNumberUrl + number + "/", HttpMethod.POST, httpEntity, String.class,
					params);
		} catch (HttpClientErrorException e) {
			message = e.getResponseBodyAsString();
		} catch (Exception e) {
			message = e.getMessage();
		} finally {
			if (!message.isEmpty()) {
				try {
					objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
					error = objectMapper.readValue(message, PlivoResponse.class);
					logger.error("buyNumber() || Error in buying phone number : {}", error);

					result.setResult(StatusMessage.PHONE_NUMBER_BUY_ERROR);
					result.setMessage(result.getMessage() + error);

				} catch (Exception e1) {
					logger.error("buyNumber() || Error in buying phone number : {}", message);
					result.setResult(StatusMessage.PHONE_NUMBER_BUY_ERROR);
					result.setMessage(result.getMessage() + message);
				}
				return result;
			} else {
				logger.info("buyNumber() || Number bought");
				result.setResult(StatusMessage.PHONE_NUMBER_BOUGHT);
				return result;
			}
		}
	}

	@SuppressWarnings("finally")
	public TransactionResult unrentNumber(String number) {
		TransactionResult result = new TransactionResult();
		String message = "";
		PlivoResponse error = new PlivoResponse();
		ObjectMapper objectMapper = new ObjectMapper();
		HttpEntity<?> httpEntity = new HttpEntity<>(getPlivoHeader());
		Map<String, String> params = new HashMap<String, String>();
		params.put("type", "carrier");
		ResponseEntity<String> response = null;
		try {
			response = restTemplate.exchange(unrentNumberUrl + number + "/", HttpMethod.DELETE, httpEntity,
					String.class, params);
		} catch (HttpClientErrorException e) {
			message = e.getResponseBodyAsString();
		} catch (Exception e) {
			message = e.getMessage();
		} finally {
			if (!message.isEmpty()) {
				try {
					error = objectMapper.readValue(message, PlivoResponse.class);
					logger.error("unrentNumber() || Error in unrenting phone number : {}", error);

					result.setResult(StatusMessage.PHONE_NUMBER_UNRENT_ERROR);
					result.setMessage(result.getMessage() + error);

				} catch (Exception e1) {
					logger.error("unrentNumber() || Error in unrenting phone number : {}", message);
					result.setResult(StatusMessage.PHONE_NUMBER_UNRENT_ERROR);
					result.setMessage(result.getMessage() + message);
				}
				return result;
			} else {
				logger.info("unrentNumber() || Number deleted");
				result.setResult(StatusMessage.PHONE_NUMBER_UNRENT);
				return result;
			}
		}
	}
	

	@SuppressWarnings("finally")
	public String checkMesssageStatus(String messageUuid) {
		TransactionResult result = new TransactionResult();
		String message = "";
		HttpEntity<?> httpEntity = new HttpEntity<>(getPlivoHeader());
		Map<String, String> params = new HashMap<String, String>();
		params.put("type", "carrier");
		ResponseEntity<String> response = null;
		try {
			response = restTemplate.exchange(checkMessageStatusUrl + messageUuid + "/", HttpMethod.GET, httpEntity,
					String.class, params);
		} catch (HttpClientErrorException e) {
			message = e.getResponseBodyAsString();
		} catch (Exception e) {
			message = e.getMessage();
		} finally {
			if (!message.isEmpty()) {

				logger.info("checkMesssageStatus() || error :{}", message);
				return "Error :"+message;
			} else {
				logger.info("checkMesssageStatus() || response: {}", response.getBody().toString());
				return response.getBody();
			}
		}
	}

	@SuppressWarnings("serial")
	private HttpHeaders getPlivoHeader() {
		return new HttpHeaders() {
			{
				String auth = plivoAuthId + ":" + plivoAuthToken;
				byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
				String authHeader = "Basic " + new String(encodedAuth);
				set("Authorization", authHeader);
			}
		};
	}
}
