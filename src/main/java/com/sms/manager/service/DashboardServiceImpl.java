package com.sms.manager.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.mongodb.client.DistinctIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.sms.manager.helper.AbstractService;
import com.sms.manager.model.Campaign;
import com.sms.manager.model.Message;
import com.sms.manager.model.response.ChartData;
import com.sms.manager.model.response.DashboardResponse;
import com.sms.manager.repository.CampaignRepository;
import com.sms.manager.repository.ContactGroupRepository;
import com.sms.manager.repository.ContactsRepository;
import com.sms.manager.repository.MessageRepository;
import com.sms.manager.repository.UserDetailsRepository;
import com.sms.manager.status.message.StatusMessage;

@Service
public class DashboardServiceImpl extends AbstractService implements DashboardService {

	@Autowired
	private UserDetailsRepository userRepo;

	@Autowired
	private ContactGroupRepository contactGrpRepo;

	@Autowired
	private ContactsRepository contactRepo;

	@Autowired
	private MessageRepository messageRepo;

	@Autowired
	private CampaignRepository campaignRepo;

	@Autowired
	MongoTemplate mongoTemplate;

	@Override
	public DashboardResponse getDashBoardDetails(String user, String role) {

		DashboardResponse response = new DashboardResponse();

		if (StringUtils.equals(role, "admin"))
			response.setUserCount(userRepo.count());

		response.setContactCount(getContactsCount(user));

		if (StringUtils.isEmpty(user))
			response.setSmsSent(messageRepo.countByType("outbound"));
		else
			response.setSmsSent(messageRepo.countByTypeAndUser("outbound", user));

		if (StringUtils.isEmpty(user))
			response.setSmsRecieved(messageRepo.countByType("inbound"));
		else
			response.setSmsRecieved(messageRepo.countByTypeAndUser("inbound", user));

		if (StringUtils.isEmpty(user))
			response.setCampaignsSent(campaignRepo.count());
		else
			response.setCampaignsSent(campaignRepo.countByUser(user));

		response.setSmsSentGraphData(getSmsChartData("outbound", user));
		response.setSmsRecievedGraphData(getSmsChartData("inbound", user));
		response.setCampaignSentGraphData(getCampaignChartData(user));
		response.setResult(StatusMessage.SUCCESS);

		return response;
	}

	private List<ChartData> getSmsChartData(String type, String user) {

		Date yourDate = DateUtils.addDays(new Date(), -7);
		List<Message> messageData = null;
		if (StringUtils.isEmpty(user))
			messageData = messageRepo.findLastSevenDaysRecords(yourDate, type,
					Sort.by(Sort.Direction.DESC, "processed_date"));
		else
			messageData = messageRepo.findLastSevenDaysRecords(yourDate, type, user,
					Sort.by(Sort.Direction.DESC, "processed_date"));

		List<ChartData> smsGraphData = new ArrayList<>();
		String tempDate = "";
		long tempCount = 0;
		long messageSize = messageData.size();
		for (int i = 0; i < messageSize; i++) {
			Date date = messageData.get(i).getProcessed_date();
			Instant dd = date.toInstant();
			String msgDate = dd.toString().substring(0, 10);
			if ((tempCount != 0 && !StringUtils.equals(tempDate, msgDate))) {
				ChartData chartData = new ChartData();
				chartData.setDate(tempDate);
				chartData.setCount(tempCount);
				tempCount = 0;
				smsGraphData.add(chartData);
			}
			tempDate = dd.toString().substring(0, 10);
			tempCount++;
			if (i == messageSize - 1) {
				ChartData cd = new ChartData();
				cd.setCount(1);
				cd.setDate(tempDate);
				smsGraphData.add(cd);
			}
		}

		return smsGraphData;
	}

	

	private List<ChartData> getCampaignChartData(String user) {

		Date yourDate = DateUtils.addDays(new Date(), -7);
		List<Campaign> campaignData = null;
		if (StringUtils.isEmpty(user))
			campaignData = campaignRepo.findLastSevenDaysRecords(yourDate,
					Sort.by(Sort.Direction.DESC, "processed_date"));
		else
			campaignData = campaignRepo.findLastSevenDaysRecords(yourDate, user,
					Sort.by(Sort.Direction.DESC, "processed_date"));

		List<ChartData> campaignGraphData = new ArrayList<>();
		String tempDate = "";
		long tempCount = 0;
		long messageSize = campaignData.size();
		for (int i = 0; i < messageSize; i++) {
			Date date = campaignData.get(i).getProcessed_date();
			Instant dd = date.toInstant();
			String msgDate = dd.toString().substring(0, 10);
			if ((tempCount != 0 && !StringUtils.equals(tempDate, msgDate))) {
				ChartData chartData = new ChartData();
				chartData.setDate(tempDate);
				chartData.setCount(tempCount);
				tempCount = 0;
				campaignGraphData.add(chartData);
			}
			tempDate = dd.toString().substring(0, 10);
			tempCount++;
			if (i == messageSize - 1) {
				ChartData cd = new ChartData();
				cd.setCount(1);
				cd.setDate(tempDate);
				campaignGraphData.add(cd);
			}
		}

		return campaignGraphData;
	}

	private long getContactsCount(String user) {
		List<String> numberList = new ArrayList<>();

		DistinctIterable<String> coll = null;

		if (!StringUtils.isEmpty(user))
			coll = mongoTemplate.getCollection("contacts").distinct("contactNumber", Filters.eq("user", user),
					String.class);
		else
			coll = mongoTemplate.getCollection("contacts").distinct("contactNumber", String.class);

		MongoCursor<String> cursor = coll.iterator();
		while (cursor.hasNext()) {
			String number = cursor.next();
			numberList.add(number);
		}

		long contactCount = numberList.size();

		return contactCount;
	}

}
