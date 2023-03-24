package com.sms.manager.email.service;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;


@Service
@EnableAsync
public class EmailService {
 
    @Value("${spring.mail.host}") private String smtpHost;
 
    @Value("${spring.mail.port}") private String smtpPort;
 
    @Value("${spring.mail.username}") private String smtpSender;
 
    @Value("${spring.mail.password}") private String smtpPassword;

	Logger logger = LoggerFactory.getLogger(EmailService.class);
	
	@Autowired
	JavaMailSender javaMailSender ;
	
    @Async
    public void sendSimpleMail(String to, String message)
    {
        // Try block to check for exceptions
        try {
 
            // Creating a simple mail message
            SimpleMailMessage mailMessage
                = new SimpleMailMessage();
 
            // Setting up necessary details
            mailMessage.setFrom(smtpSender);
            mailMessage.setTo(to);
            mailMessage.setText(message);
            mailMessage.setSubject("New {SMS} account created");
 
            // Sending the mail
            javaMailSender.send(mailMessage);
            logger.info("Mail Sent Successfully");
        }
 
        // Catch block to handle the exceptions
        catch (Exception e) {
        	 logger.error("Error while Sending Mail {}" ,  e.getMessage());
        }
    }
}
