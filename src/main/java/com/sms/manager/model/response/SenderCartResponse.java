package com.sms.manager.model.response;

import java.util.List;

import com.sms.manager.model.TransactionResult;

import lombok.Data;

@Data
public class SenderCartResponse {

	private List<SenderItemOnCart> cartItems;
	
	private TransactionResult transactionResult;
	
}
