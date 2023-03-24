package com.sms.manager.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.sms.manager.model.UserDetails;

public interface UserDetailsRepository extends MongoRepository<UserDetails, String> {

	@Query("{$or : [{userId: ?0}, {contact: ?1}]}")
	public List<UserDetails> findByUserOrdContact(String userId, String contact);

	@Query("{userId: ?0}")
	public List<UserDetails> findByUserId(String userId);

	@Query("{$or : [{firstName: {$regex: /?0/i }}, {lastName: {$regex: /?0/i }}, {userId: {$regex: /?0/i }}, {role: {$regex: /?0/i }}, {contact: {$regex: /?0/i }}, {email: {$regex: /?0/i }}, {status: ?0}]}")
	public Page<UserDetails> findByValue(String value, Pageable pageable);

	@Query(value = "{$or : [{firstName: {$regex: /?0/i }}, {lastName: {$regex: /?0/i }}, {userId: {$regex: /?0/i }}, {role: {$regex: /?0/i }}, {contact: {$regex: /?0/i }}, {email: {$regex: /?0/i }}, {status: ?0}]}", count = true)
	public long countByValue(String value);
}
