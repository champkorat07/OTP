package com.cristianruizblog.loginSecurity.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.cristianruizblog.loginSecurity.service.OtpService;
import com.cristianruizblog.loginSecurity.service.SendEmailService;

@Controller
public class OtpController {

	@Autowired
	private OtpService otpService;

	@Autowired
	private SendEmailService sendemailservice;

	@GetMapping({ "/OTPsend" })
	private String index() {
		return "OTPsend";
	}

	@GetMapping({ "/OTPresult" })
	private String OTPresult(Model model) {
		String check = "Unknows";
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName();
		if (otpService.getValidateCache(name) == true) {
			check = "OTP is validate";
			model.addAttribute("check", check);
		} else {
			check = "OTP is NOTvalidate";
			model.addAttribute("check", check);
		}
		return "OTPresult";
	}

	@RequestMapping(value = "/OTPwain", method = RequestMethod.POST)
	private String generateOtp(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName();
		int otp = otpService.generateOTP(name);
		model.addAttribute("name", name);
		sendemailservice.sendEmailOTP(otp, name);
		return "OTPwain";
	}

	@RequestMapping(value = "/OTPsum", method = RequestMethod.POST)
	private String validateOtp(@RequestParam("otpnum") int otpnum, Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName();
		String msg = "";
		String error = "0";
		int count = -1;
		if (otpnum >= 0) {
			int serverOtp = otpService.getOtp(name);
			if (serverOtp > 0) {
				if (otpnum == serverOtp) {
					otpService.clearOTP(name);
					otpService.validateDose(name);
					msg = "Entered Otp is valid";
					model.addAttribute("sumotp", msg);
					model.addAttribute("error", error);
					return "OTPsum";
				} else {
					count = otpService.MisplacedOTP(name);
					msg = "Entered Otp is NOT valid You have\n You have " + Integer.toString(4 - count)
							+ " more chances.";
					model.addAttribute("sumotp", msg);
					error = "1";
					model.addAttribute("error", error);
					return "OTPsum";
				}
			} else {
				msg = "OTP is not generate";
				model.addAttribute("sumotp", msg);
				error = "2";
				model.addAttribute("error", error);
				return "OTPsum";
			}
		} else {
			msg = "Entered Otp is NOT SET";
			model.addAttribute("sumotp", msg);
			error = "2";
			model.addAttribute("error", error);
			model.addAttribute("name", name);
			return "OTPsum";

		}
	}

}
