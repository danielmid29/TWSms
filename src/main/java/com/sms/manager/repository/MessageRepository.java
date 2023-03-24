package com.sms.manager.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.sms.manager.model.Message;

public interface MessageRepository extends MongoRepository<Message, String> {

	@Query("{$or : [{user: {$regex: /?0/i }}, {from: {$regex: /?0/i }}, {to: {$regex: /?0/i }}, {message: {$regex: /?0/i }}, {type: {$regex: /?0/i }}]}")
	public Page<Message> findByValue(String value, Pageable pageable);

	@Query(value = "{$or : [{user: {$regex: /?0/i }}, {from: {$regex: /?0/i }}, {to: {$regex: /?0/i }}, {message: {$regex: /?0/i }}, {type: {$regex: /?0/i }}]}", count = true)
	public long countByValue(String value);

	@Query("{$or : [{user: {$regex: /?0/i }}, {from: {$regex: /?0/i }}, {to: {$regex: /?0/i }}, {message: {$regex: /?0/i }}, {type: {$regex: /?0/i }}], user: ?1}")
	public Page<Message> findByValueUser(String value, String user, Pageable pageable);

	@Query(value = "{$or : [{user: {$regex: /?0/i }}, {from: {$regex: /?0/i }}, {to: {$regex: /?0/i }}, {message: {$regex: /?0/i }}, {type: {$regex: /?0/i }}], user: ?1}", count = true)
	public long countByValueUser(String value, String user);

	@Query(value = "{type: ?0}", count = true)
	public long countByType(String type);

	@Query(value = "{type: ?0, user: ?1}", count = true)
	public long countByTypeAndUser(String type, String user);

	@Query("{processed_date: { $gte: ?0 }, type: ?1}")
	public List<Message> findLastSevenDaysRecords(Date processedDate, String type, Sort sort);

	@Query("{processed_date: { $gte: ?0 }, type: ?1, user: ?2}")
	public List<Message> findLastSevenDaysRecords(Date processedDate, String type, String user, Sort sort);
	
	
	@Query("{status: ?0}")
	public List<Message> findByStatus(String status);

	@Query("{messageUuid: ?0}")
	public Message findByMessageUuid(String messageUuid);
}
