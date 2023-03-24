package com.sms.manager.model.response;

import java.util.List;

import com.sms.manager.model.TransactionResult;

import lombok.Data;

@Data
public class PlivoNumberCartResponse extends TransactionResult{

	private List<NumberCartObject> objects;

}
