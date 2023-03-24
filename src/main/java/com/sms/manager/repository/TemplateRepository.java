package com.sms.manager.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.sms.manager.model.Template;

public interface TemplateRepository extends MongoRepository<Template, String>  {

}
