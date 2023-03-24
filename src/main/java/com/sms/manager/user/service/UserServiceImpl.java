package com.sms.manager.user.service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.sms.manager.email.service.EmailService;
import com.sms.manager.helper.AbstractService;
import com.sms.manager.model.PasswordVault;
import com.sms.manager.model.ResetPasswordRequest;
import com.sms.manager.model.TransactionResult;
import com.sms.manager.model.UserDetails;
import com.sms.manager.model.response.UserDetailsResponse;
import com.sms.manager.plivo.service.PlivoService;
import com.sms.manager.repository.PasswordVaultRepository;
import com.sms.manager.repository.UserDetailsRepository;
import com.sms.manager.status.message.StatusMessage;

@Service
public class UserServiceImpl extends AbstractService implements UserService {

	@Autowired
	private UserDetailsRepository userRepo;

	@Autowired
	private PasswordVaultRepository pwRepo;

	@Autowired
	private PlivoService plivoService;

	@Autowired
	private EmailService emailService;
	

	Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Override
	public TransactionResult createUser(UserDetails userDetails) {

		logger.info("createUser || Parameter UserDetails : userDetails");

		TransactionResult transactionResult = new TransactionResult();
		String userId = userDetails.getUserId();
		String contact = userDetails.getContact();
		String email = userDetails.getEmail();

		transactionResult = plivoService.validatePhoneNumber(contact);
		if (!StringUtils.equals(StatusMessage.PHONE_NUMBER_VALID.getCode(), transactionResult.getCode())) {
			logger.error("createUser || returning due to number lookup error : {}", transactionResult.getMessage());
			return transactionResult;
		}

		List<UserDetails> repoUserDetails = userRepo.findByUserOrdContact(userId, contact);

		for (UserDetails rud : repoUserDetails) {
			if (StringUtils.equals(contact, rud.getContact()) && StringUtils.equals(userId, rud.getUserId())) {
				transactionResult.setResult(StatusMessage.CONTACT_AND_USERID_EXIST);
				logger.error("createUser || {}", transactionResult.getMessage());
				return transactionResult;
			}

			if (StringUtils.equals(userId, rud.getUserId())) {
				transactionResult.setResult(StatusMessage.USER_ID_EXIST);
				logger.error("createUser || {}", transactionResult.getMessage());
				return transactionResult;
			}

			if (StringUtils.equals(contact, rud.getContact())) {
				transactionResult.setResult(StatusMessage.USER_EXIST_CONTACT);
				logger.error("createUser || {}", transactionResult.getMessage());
				return transactionResult;
			}
		}

		long id = generateSequence(UserDetails.SEQUENCE_NAME);
	    ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
		userDetails.setUid(id);
		userDetails.setCreateDate(new Date());
		userDetails.setLastUpdateDate(new Date());
		userRepo.save(userDetails);

		String password = generateRandomPassword();

		PasswordVault pwVault = new PasswordVault(generateSequence(PasswordVault.SEQUENCE_NAME), id,
				BCrypt.hashpw(password, BCrypt.gensalt()));

		pwRepo.save(pwVault);

		String message = StatusMessage.NEW_USER_MESSAGE.getMessage();
		message = message.replace("{user-id}", userId);
		message = message.replace("{password}", password);

		plivoService.sendMessage(contact, message);
		emailService.sendSimpleMail(email, message);

		logger.info("createUser || User creation successfull");

		return new TransactionResult(StatusMessage.NEW_USER_CREATED);
	}

	private String generateRandomPassword() {
		int leftLimit = 48; // numeral '0'
		int rightLimit = 122; // letter 'z'
		int targetStringLength = 10;
		Random random = new Random();

		String generatedString = random.ints(leftLimit, rightLimit + 1)
				.filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97)).limit(targetStringLength)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();

		return generatedString;
	}

	@Override
	public TransactionResult updateUser(UserDetails userDetails) {
		logger.info("updateUser || Parameter UserDetails : userDetails");

		TransactionResult transactionResult = new TransactionResult();
		String userId = userDetails.getUserId();

		if (StringUtils.isEmpty(userId)) {
			transactionResult.setResult(StatusMessage.MANDATORY_FIELD_USERID_MISSING);
			logger.error("updateUser || Mandatory field user id missing");
			return transactionResult;
		}

		String role = userDetails.getRole();
		String contact = userDetails.getContact();
		String email = userDetails.getEmail();
		Integer totalSmsSent = userDetails.getTotalSmsSent();
		Integer totalContact = userDetails.getTotalContacts();
		String status = userDetails.getStatus();
		String profilePicPath = userDetails.getProfilePicName();

		List<UserDetails> updateUDS = userRepo.findByUserId(userId);

		for (UserDetails ud : updateUDS) {
			if (!StringUtils.isEmpty(role))
				ud.setRole(role);
			if (!StringUtils.isEmpty(contact))
				ud.setContact(contact);
			if (!StringUtils.isEmpty(email))
				ud.setEmail(email);
			if (!StringUtils.isEmpty(status))
				ud.setStatus(status);
			if (!StringUtils.isEmpty(profilePicPath))
				ud.setProfilePicName(profilePicPath);
			if (totalSmsSent != null && totalSmsSent != 0)
				ud.setTotalSmsSent(totalSmsSent);
			if (totalContact != null && totalContact != 0)
				ud.setTotalContacts(totalContact);

			ud.setLastUpdateDate(new Date());
			logger.info("updateUser || updating user details");
			userRepo.save(ud);
		}

		logger.info("updateUser || User update successfull");
		transactionResult.setResult(StatusMessage.USER_UPDATE_SUCCESS);
		return transactionResult;
	}

	@Override
	public TransactionResult deleteUser(UserDetails userDetails) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TransactionResult resetPassword(ResetPasswordRequest resetPWRequest) {

		logger.info("resetPassword || Entering");
		
		TransactionResult transactionResult = new TransactionResult();
		
		String userId = resetPWRequest.getUserId();
		String currentPassword = resetPWRequest.getCurrentPassword();
		String newPassword = resetPWRequest.getNewPassword();
		
		List<UserDetails> userDetails = userRepo.findByUserId(userId);
		
		if(userDetails.isEmpty()) {
			logger.error("resetPassword || Cannot find details for this user");
			transactionResult.setResult(StatusMessage.USER_NOT_FOUND);
			return transactionResult;
		}
		
		for(UserDetails ud : userDetails) {
			long prtyFk = ud.getUid();
			PasswordVault pwModel = pwRepo.findByPrtyFK(prtyFk);
			
			if(!BCrypt.checkpw(currentPassword, pwModel.getPassword())) {
				logger.error("resetPassword || Password not matching with existing password");
				transactionResult.setResult(StatusMessage.PASSWORD_DOES_NOT_MATCH);
				return transactionResult;
			}
			
			pwModel.setLastUpdateDate(new Date());
			pwModel.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
			pwRepo.save(pwModel);
		}
		
		logger.info("resetPassword || Password reset successful");
		transactionResult.setResult(StatusMessage.RESET_PASSWORD_SUCCESSFUL);
		return transactionResult;
	}

	@Override
	public UserDetailsResponse userLookup(String searchValue, int size, int pageNumber) {

		logger.info("userLookup || parameter searchValue : {}", searchValue );
		logger.info("userLookup || parameter size : {}", size );
		logger.info("userLookup || parameter pageNumber : {}", pageNumber );
		
		Page<UserDetails> userDetails = userRepo.findByValue(searchValue, PageRequest.of(pageNumber, size, Sort.by(Direction.DESC, "lastUpdateDate")));

		long count = userRepo.countByValue(searchValue);
		
		UserDetailsResponse response = new UserDetailsResponse();
		response.setTotalPage(userDetails.getTotalPages());
		response.setTotalCount(count);

		response.setUserDetails(userDetails.getContent());
		response.setResult(StatusMessage.USER_DETAILS_FOUND);

		if(count ==0) {
			response.setResult(StatusMessage.USER_NOT_FOUND);
		}
		
		logger.info("userLookup || response : {}", response);
		
		return response;
	}

}
