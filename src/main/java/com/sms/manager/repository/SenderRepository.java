package com.sms.manager.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.sms.manager.model.Message;
import com.sms.manager.model.Sender;

public interface SenderRepository extends MongoRepository<Sender, String> {

	@Query("{number:?0}")
	public Sender findByNumber(String number);

	@Query(value = "{number:?0}", delete = true)
	public Sender deleteByNumber(String number);

	@Query("{$or : [{type: {$regex: /?0/i }}, {number: {$regex: /?0/i }}, {assignedTo: {$regex: /?0/i }}, {status: ?0}]}")
	public Page<Sender> findByValue(String value, Pageable pageable);

	@Query(value = "{$or : [{type: {$regex: /?0/i }}, {number: {$regex: /?0/i }}, {assignedTo: {$regex: /?0/i }}, {status: ?0 }]}", count = true)
	public long countByValue(String value);

	@Query("{$or : [{type: {$regex: /?0/i }}, {number: {$regex: /?0/i }}, {assignedTo: {$regex: /?0/i }}, {status: ?0}], assignedTo: ?1}")
	public Page<Sender> findByValueUser(String value, String user, Pageable pageable);

	@Query(value = "{$or : [{type: {$regex: /?0/i }}, {number: {$regex: /?0/i }}, {assignedTo: {$regex: /?0/i }}, {status: ?0 }], assignedTo: ?1}", count = true)
	public long countByValueUser(String value, String user);
}
