package com.example.otp.config;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.otp.service.UserDetailsServiceImpl;

@Configuration
@EntityScan("com.cristianruizblog.loginSecurity")
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {



	
	String[] resources = new String[] { "/include/**", "/css/**", "/icons/**", "/img/**", "/js/**", "/layer/**" };

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.authorizeRequests().antMatchers(resources).permitAll()
				.antMatchers("/login", "/index","/register.html","/scripts.js", "/styles.css").permitAll()
				.antMatchers("/admin*").access("hasRole('ADMIN')")
				.antMatchers("/user*").access("hasRole('USER') or hasRole('ADMIN')")
				.antMatchers("/OTPsend*").access("hasRole('USER') or hasRole('ADMIN')")
				.antMatchers("/OTPwain*").access("hasRole('USER') or hasRole('ADMIN')")
				.antMatchers("/OTPsum*").access("hasRole('USER') or hasRole('ADMIN')")
				.antMatchers("/OTPresult*").access("hasRole('USER') or hasRole('ADMIN')")
				.antMatchers("/GAGen*").access("hasRole('USER') or hasRole('ADMIN')")
				.antMatchers("/GATest*").access("hasRole('USER') or hasRole('ADMIN')")
				.antMatchers("/GAPreTest*").access("hasRole('USER') or hasRole('ADMIN')")
				.antMatchers("/").access("hasRole('USER') or hasRole('ADMIN')")
					.anyRequest().authenticated()
					.and()
				.formLogin()
				.loginPage("/login")
					.permitAll()
					.defaultSuccessUrl("/menu")
					.failureUrl("/login?error=true")
					.usernameParameter("username")
					.passwordParameter("password")
					.and()
				.rememberMe().rememberMeParameter("uniqueAndSecret")
					.and()
				.logout()
					.permitAll()
					.deleteCookies("JSESSIONID")
					.logoutSuccessUrl("/login?logout");
				
			       

	}


	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		bCryptPasswordEncoder = new BCryptPasswordEncoder(4);
		return bCryptPasswordEncoder;
	}

	@Autowired
	UserDetailsServiceImpl userDetailsService;

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {

		// Setting Service to find User in the database.
		// And Setting PassswordEncoder
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}
}