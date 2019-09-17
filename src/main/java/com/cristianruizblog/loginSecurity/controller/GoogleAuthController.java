package com.cristianruizblog.loginSecurity.controller;

import org.apache.commons.codec.binary.Base32;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.cristianruizblog.loginSecurity.service.TotpService;
import com.cristianruizblog.loginSecurity.service.UserDetailsServiceImpl;

@Controller
public class GoogleAuthController {

	@Autowired
	private UserDetailsServiceImpl userdetailsserviceImpl;

	@Autowired
	private TotpService totpService;

	@GetMapping("/GAGen")
	public String GAGen(Authentication authentication, Model model) {
		String username = authentication.getName();
		String email = userdetailsserviceImpl.findEmailByUsername(username);
		String secrets = username + email;
		String sum = new Base32().encodeToString(secrets.getBytes());
		sum = sum.replace("=", "");
		String secret = "https://zxing.org/w/chart?cht=qr&chs=250x250&chld=M&choe=UTF-8&chl=otpauth://totp/googleAuthtest.com?secret="
				+ sum + "&issuer=googleAuthtest";
		model.addAttribute("secret", secret);
		model.addAttribute("code", sum);
		return "GAGen";
	}

	@GetMapping({ "/GAPreTest" })
	private String GAPreTest() {
		return "GAPreTest";
	}

	@RequestMapping(value = "/GATest", method = RequestMethod.POST)
	public String GATest(@RequestParam("token") String token, Authentication authentication, Model model) {
		String username = authentication.getName();
		String email = userdetailsserviceImpl.findEmailByUsername(username);
		String sum = username + email;
		String msg = "";
		String error = "-1";
		if (totpService.verifyCode(token, sum)) {
			msg = "Entered Otp is valid";
			error = "0";
			model.addAttribute("sumotp", msg);
			model.addAttribute("error", error);
			return "GATest";
		} else {
			msg = "Entered Otp is NOTvalid";
			error = "1";
			model.addAttribute("sumotp", msg);
			model.addAttribute("error", error);
			return "GATest";
		}
	}
}
