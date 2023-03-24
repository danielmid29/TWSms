package com.sms.manager.model.response;

import java.util.List;

import com.sms.manager.model.TransactionResult;
import com.sms.manager.model.UserDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsResponse extends TransactionResult {
	
	private long totalCount;
	
	private int totalPage;

	private List<UserDetails> userDetails;
	
}
