package com.sms.manager.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.sms.manager.model.PasswordVault;

public interface PasswordVaultRepository extends MongoRepository<PasswordVault, String> {

	@Query("{prtyFk: ?0}")
	PasswordVault findByPrtyFK(long prtyFk);
	
}
