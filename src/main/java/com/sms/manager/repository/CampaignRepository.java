package com.sms.manager.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.sms.manager.model.Campaign;
import com.sms.manager.model.Message;

public interface CampaignRepository extends MongoRepository<Campaign, String> {

	@Query("{user:?0, name:?1 }")
	List<Campaign> findByUserAndName(String user, String name);

	@Query("{$or : [{user: {$regex: /?0/i }}, {name: {$regex: /?0/i }}, {sender: {$regex: /?0/i }}, {contacts: {$regex: /?0/i }}, {message: {$regex: /?0/i }}, {status: ?0}], user: ?1}")
	public Page<Campaign> findByValueUser(String value, String user, Pageable pageable);

	@Query(value = "{$or : [{user: {$regex: /?0/i }}, {name: {$regex: /?0/i }}, {sender: {$regex: /?0/i }}, {contacts: {$regex: /?0/i }}, {message: {$regex: /?0/i }}, {status: ?0}], user: ?1}", count = true)
	public long countByValueUser(String value, String user);

	@Query("{$or : [{user: {$regex: /?0/i }}, {name: {$regex: /?0/i }}, {sender: {$regex: /?0/i }}, {contacts: {$regex: /?0/i }}, {message: {$regex: /?0/i }}, {status: ?0}]}")
	public Page<Campaign> findByValue(String value, Pageable pageable);

	@Query(value = "{$or : [{user: {$regex: /?0/i }}, {name: {$regex: /?0/i }}, {sender: {$regex: /?0/i }}, {contacts: {$regex: /?0/i }}, {message: {$regex: /?0/i }}, {status: ?0}]}", count = true)
	public long countByValue(String value);
	

	@Query(value = "{user: ?0}", count = true)
	public long countByUser(String user);

	@Query("{processed_date: { $gte: ?0 }}")
	public List<Campaign> findLastSevenDaysRecords(Date processedDate, Sort sort);

	@Query("{processed_date: { $gte: ?0 }, user: ?1}")
	public List<Campaign> findLastSevenDaysRecords(Date processedDate, String user, Sort sort);
	
	
	@Query("{status: ?0}")
	public List<Campaign> findByStatus(String status);
}
