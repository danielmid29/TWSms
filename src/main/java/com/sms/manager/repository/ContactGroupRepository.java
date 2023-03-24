package com.sms.manager.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.sms.manager.model.ContactGroup;

public interface ContactGroupRepository extends MongoRepository<ContactGroup, String> {

	@Query("{user:?0 , name:?1}")
	public List<ContactGroup> findByUserAndGroup(String user, String name);
	
	@Query(value="{user:?0 , name:?1}", delete= true)
	public void deleteByUserAndGroup(String user, String name);

	
	@Query("{$or : [{user: {$regex: /?0/i }}, {name: {$regex: /?0/i }}, {status: ?0}], user: ?1}")
	public Page<ContactGroup> findByValueUser(String value, String user, Pageable pageable);

	@Query(value = "{$or : [{user: {$regex: /?0/i }}, {name: {$regex: /?0/i }}, {status: ?0}], user: ?1}", count = true)
	public long countByValueUser(String value, String user);
	
	@Query("{$or : [{user: {$regex: /?0/i }}, {name: {$regex: /?0/i }}, {status: ?0}]}")
	public Page<ContactGroup> findByValue(String value, Pageable pageable);

	@Query(value = "{$or : [{user: {$regex: /?0/i }}, {name: {$regex: /?0/i }}, {status: ?0}]}", count = true)
	public long countByValue(String value);
	
}
