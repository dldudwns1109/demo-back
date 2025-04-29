package com.kh.demo.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.kh.demo.aop.AccountSigninInterceptor;
import com.kh.demo.aop.TokenRenewalInterceptor;

public class InterceptorConfiguration implements WebMvcConfigurer {
	
	@Autowired
	private TokenRenewalInterceptor tokenRenewalInterceptor;
	
	@Autowired
	private AccountSigninInterceptor accountSigninInterceptor;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(tokenRenewalInterceptor)
								.addPathPatterns("/**");
		// 나중에 설정 예정
//		registry.addInterceptor(accountSigninInterceptor)
//								.addPathPatterns("");
	}
}
