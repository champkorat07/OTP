package com.example.otp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class SendEmailService {
	
	@Autowired
	private JavaMailSender javamailsender;

	@Autowired
	private UserDetailsServiceImpl userdetailsserviceimpl;

	public void sendEmailOTP(int otp, String name) {
		String username = name;
		String email = userdetailsserviceimpl.findEmailByUsername(username);
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setTo(email);
		msg.setSubject("TestingOTP from Spring Boot");
		msg.setText("Your email is " + email + "\n Your OTP is : " + otp);
		javamailsender.send(msg);

	}
}
