package com.sms.manager.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.sms.manager.model.Contact;
import com.sms.manager.model.UserDetails;

public interface ContactsRepository extends MongoRepository<Contact, String> {

	@Query("{user:?0 , contactNumber:?1, contactGroup:null}")
	public List<Contact> findByUserNumberAndNullGroup(String user, String number);

	@Query("{user:?0 , contactNumber:?1, contactGroup:?2}")
	public List<Contact> findByUserNumberAndGroup(String user, String number, String group);

	@Query("{user:?0 , contactGroup:?1}")
	public List<Contact> findByUserAndGroup(String user, String group);
	
	@Query("{user:?0 , contactNumber:?1}")
	public List<Contact> findByUserAndNumber(String user, String number);

	@Query(value = "{user:?0 , contactNumber:?1, contactGroup:?2}", delete = true)
	public void deleteByUserNumberAndGroup(String user, String number, String group);

	@Query(value = "{user:?0 , contactNumber:?1}", delete = true)
	public void deleteByUserAndNumber(String user, String number);


	@Query(value = "{user:?0 , contactGroup:?1}", delete = true)
	public void deleteByUserAndGroup(String user, String group);
	
	@Query("{$or : [{user: {$regex: /?0/i }}, {contactGroup: {$regex: /?0/i }}, {contactNumber: {$regex: /?0/i }}, {status: ?0}]}")
	public Page<Contact> findByValue(String value, Pageable pageable);

	@Query(value = "{$or : [{user: {$regex: /?0/i }}, {contactGroup: {$regex: /?0/i }}, {contactNumber: {$regex: /?0/i }}, {status: ?0}]}", count = true)
	public long countByValue(String value);

	@Query("{$or : [{user: {$regex: /?0/i }}, {contactGroup: {$regex: /?0/i }}, {contactNumber: {$regex: /?0/i }}, {status: ?0}], user: ?1}")
	public Page<Contact> findByValueUser(String value, String user, Pageable pageable);

	@Query(value = "{$or : [{user: {$regex: /?0/i }}, {contactGroup: {$regex: /?0/i }}, {contactNumber: {$regex: /?0/i }}, {status: ?0}], user: ?1}", count = true)
	public long countByValueUser(String value, String user);
	
	public List<Contact> findDistinctByContactNumber();
}
