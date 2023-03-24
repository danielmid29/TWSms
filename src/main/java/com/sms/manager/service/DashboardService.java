package com.sms.manager.service;

import com.sms.manager.model.response.DashboardResponse;

public interface DashboardService  {

	public DashboardResponse getDashBoardDetails(String user, String role);
	
}
