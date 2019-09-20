package com.example.otp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.otp.service.OtpService;
import com.example.otp.service.UserDetailsServiceImpl;

@Controller
public class LoginController {

	@Autowired
	private UserDetailsServiceImpl userdetailsserviceImpl;

	@Autowired
	private OtpService otpService;

	@GetMapping({ "/login" })
	public String index() {
		return "index";
	}

	@GetMapping({ "/", "/menu" })
	public String menu(Authentication authentication) {
		String username = authentication.getName();
		otpService.validateDosefinished(username);
		return "menu";
	}

	@GetMapping("/user")
	public String user(Authentication authentication, Model model) {
		String username = authentication.getName();
		String email = userdetailsserviceImpl.findEmailByUsername(username);
		model.addAttribute("email", email);
		return "user";
	}

	@GetMapping("/admin")
	public String admin(Authentication authentication, Model model) {
		String username = authentication.getName();
		String email = userdetailsserviceImpl.findEmailByUsername(username);
		model.addAttribute("email", email);
		return "admin";
	}
}
