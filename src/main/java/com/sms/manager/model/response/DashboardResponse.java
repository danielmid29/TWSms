package com.sms.manager.model.response;


import java.util.List;

import com.sms.manager.model.Message;
import com.sms.manager.model.TransactionResult;

import lombok.Data;

@Data
public class DashboardResponse extends TransactionResult {

	private long userCount;
	
	private long contactCount;
	
	private long smsSent;
	
	private long smsRecieved;
	
	private long campaignsSent;
	
	private List<ChartData> smsSentGraphData; 
	
	private List<ChartData> smsRecievedGraphData; 
	
	private List<ChartData> campaignSentGraphData; 
	
	private long smsSuccessRate;

	private long smsFailureRate;
	
}
