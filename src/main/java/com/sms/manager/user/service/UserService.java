package com.sms.manager.user.service;

import com.sms.manager.model.ResetPasswordRequest;
import com.sms.manager.model.TransactionResult;
import com.sms.manager.model.UserDetails;
import com.sms.manager.model.response.UserDetailsResponse;

public interface UserService {

	public UserDetailsResponse userLookup(String searchValue, int size, int pageNumber);
	
	public TransactionResult createUser(UserDetails userDetails); 
	
	public TransactionResult updateUser(UserDetails userDetails); 
	
	public TransactionResult resetPassword(ResetPasswordRequest resetPWRequest); 
	
	public TransactionResult deleteUser(UserDetails userDetails);
	
}
