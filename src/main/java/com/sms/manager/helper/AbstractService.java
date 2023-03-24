package com.sms.manager.helper;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoOperations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sms.manager.model.DatabaseSequence;
import com.sms.manager.model.PlivoResponse;
import com.sms.manager.model.TransactionResult;
import com.sms.manager.status.message.StatusMessage;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class AbstractService {

	@Autowired
	private MongoOperations mongoOperations;

	@Value("${plivo.auth.id}")
	private String plivoAuthId;

	@Value("${plivo.auth.token}")
	private String plivoAuthToken;

	private RestTemplate restTemplate = new RestTemplate();

	Logger logger = LoggerFactory.getLogger(AbstractService.class);

	public long generateSequence(String seqName) {
		DatabaseSequence counter = mongoOperations.findAndModify(query(where("_id").is(seqName)),
				new Update().inc("seq", 1), options().returnNew(true).upsert(true), DatabaseSequence.class);
		return !Objects.isNull(counter) ? counter.getSeq() : 1;
	}

}
