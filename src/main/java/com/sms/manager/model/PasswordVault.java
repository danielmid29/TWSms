package com.sms.manager.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection="password_vault")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordVault {

	@Transient
    public static final String SEQUENCE_NAME = "password_vault_sequence";
	
	
	@Id
	private long id;
	
	private long prtyFk;
	
	private String password;

	@Field
	private Date createDate = new Date(); 

	@Field
	private Date lastUpdateDate = new Date(); 
	
	@Field
	private String status = "ACTIVE";

	public PasswordVault(long id, long prtyFk, String password) {
		this.id = id;
		this.prtyFk = prtyFk;
		this.password = password;
	}
	
}
