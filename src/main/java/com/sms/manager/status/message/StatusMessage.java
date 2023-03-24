package com.sms.manager.status.message;

public enum StatusMessage {
	
	MANDATORY_FIELD_USERID_MISSING("E001","UserId is mandatory",422),
	
	
	NEW_USER_MESSAGE("U001","Hello, Account has been create been created on {SMS} for you. Provided below will be you user details, \n \n \tuser-id : {user-id} \n \tpassword: {password} \n \nPlease reset your password after your first login.",200),
	DEACTIVATE_USER("U002","Hello, your {SMS} account has been deactivated by the admin. Please contact admin team for any queries.",200),
	ACTIVATE_USER("U003","Hello, your {SMS} account has been activated. Please contact admin team for any queries.",200),
	RESET_PASSWORD_MESSAGE("U004","Your password has been regenerated for your {SMS} account. \n Password: {password}.\n \n Please reset your password after your next login.",200),
	NEW_USER_CREATED("U005","User Creation Success.",201),
	USER_UPDATE_SUCCESS("U006","User Detail update success",200),
	USER_DETAILS_FOUND("U007","User details found",200),
	
	NEW_USER_FAILURE("UF001","User Creation Failed.",422),
	USER_ID_EXIST("UF002","User Id is already used",422),
	USER_NOT_FOUND("UF003","User not found",404),
	USER_EXIST_CONTACT("UF004","User exists for this contact",422),
	CONTACT_AND_USERID_EXIST("UF005","User exists for this user id and contact",422),

	RESET_PASSWORD_SUCCESSFUL("P001","Password reset successful",200),
	PASSWORD_DOES_NOT_MATCH("PF001","Password not matching with existing password",422),
	
	NEW_SENDER_ASSIGNED("S001","You have been provided with a new Sender-Id",200),
	NEW_PHONE_ASSIGNED("S002","You have been provided with a new phone number for sending messages",200),
	TEMPLATE_SHARED("S003","You have been shared with a new message template by {user-id}",200),
	CONTACT_GROUP_SHARED("S004","You have been shared with a new contact group by {user-id}",200),
	
	
	PHONE_NUMBER_ACCESS_REVOKE("R001","{sender} sender access has been revoked by admin",200),
	TEMPLATE_ACCESS_REVOKE("R002","{sender} sender access has been revoked by admin",200),
	

	SCHEDULED_CAMPAIGN_SUCCESS("C001","Scheduled campaign has delivered successful",200),
	CAMPAIGN_SUCCESS("C002","Campaign({campaign-name}) has been queued for delivery",200),
	CAMPAIGN_FAILED("C003","Campaign({campaign-name}) delivery unsuccessful due to error: ",422),
	CAMPAIGN_EXIST("CF001","Campaign already exist with this name",422),
	CAMPAIGN_FOUND("P005","Campaign found",200),
	CAMPAIGN_NOT_FOUND("PF005","Campaign not found",404),
	

	SMS_DELIVERY_SUCCESS("S001","SMS delivery successful",200),
	SMS_DELIVERY_FAILED("S002","SMS delivery unsuccessful due to error: ",422),
	MESSAGE_FOUND("S003","Message found",200),
	MESSAGE_NOT_FOUND("SF003","Message not found",404),

	PHONE_NUMBER_VALID("P001","Validalid Phone Number",200),
	PHONE_NUMBER_ADDED("P002","Phone number added",200),
	PHONE_NUMBER_UPDATED("P003","Phone number updated",200),
	PHONE_NUMBER_DELETED("P004","Phone number deleted",200),
	CONTACT_GROUP_ADDED("P005","Contact Group added",200),
	CONTACT_GROUP_FOUND("P005","Contact Group found",200),
	CONTACT_GROUP_UPDATED("P005","Contact Group updated",200),
	CONTACT_GROUP_DELETED("P006","Contact Group deleted",200),
	CONTACT_GROUP_NOT_FOUND("PF005","Contact Group not found",404),
	PHONE_NUMBER_INVALID("PF001","Invalid Phone Number",422),
	PHONE_NUMBER_EXIST("PF002","Phone number already exist",422),
	PHONE_NUMBER_GROUP_EXIST("PF003","Phone number already Exist in this group",422),
	PHONE_NUMBER_VALIDATION_FAILED("PF004","Error in phone validation :",500),
	CONTACT_GROUP_EXIST("PF006","Contact Group already exist with this name",422),
	
	

	PHONE_NUMBER_CART_SEARCH_SUCCESS("PC001","Number cart details retrieved",200),
	PHONE_NUMBER_BOUGHT("PB001","Phone number has been bought and assigned to the user ",200),
	PHONE_NUMBER_UNRENT("PB002","Phone number has been deleted revoked access to this contact for user ",200),
	PHONE_NUMBER_ASSIGNED("PB003","Phone number assigned",200),
	SENDER_UPDATED("PB004","Phone number status has been updated",200),
	PHONE_NUMBER_CART_SEARCH_FAILED("PCF001","Error in phone number cart lookup :",500),
	PHONE_NUMBER_BUY_ERROR("PBF001","Error in buing phone number :",500),
	PHONE_NUMBER_UNRENT_ERROR("PBF001","Error in unrenting phone number :",500),
	CONTACT_FOUND("C001","Contact Found",200),
	CONTACT_NOT_FOUND("CF001","Contact Not Found",404),
	SENDER_FOUND("S001","Sender Found",200),
	SENDER_NOT_FOUND("SF001","Sender Not Found",404),
	
	

	SUCCESS("DS001","Success",200),
	;
	

	StatusMessage(String code, String message, int httpStatusCode) {
		this.code = code;
		this.message = message;
		this.httpStatusCode = httpStatusCode;
	}
	
	private final String code;
	private final String message;
	private final int httpStatusCode;
	
	public String getCode() {
		return code;
	}
	public String getMessage() {
		return message;
	}
	public int getHttpStatusCode() {
		return httpStatusCode;
	}
	
}
