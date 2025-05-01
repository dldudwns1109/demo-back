package com.kh.demo.aop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

import com.kh.demo.configuration.TokenProperties;
import com.kh.demo.service.TokenService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class TokenRenewalInterceptor implements HandlerInterceptor {

	@Autowired
	private TokenService tokenService;
	
	@Autowired
	private TokenProperties tokenProperties;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		try {
			if (request.getMethod().equalsIgnoreCase("options")) return true;
			
			String accessToken = request.getHeader("Authorization");
			if (accessToken == null) return true;
			
			long ms = tokenService.getRemainTime(accessToken);
			if (ms >= tokenProperties.getRenewalLimit() * 60L * 1000L) return true;
			
			long memberNo = tokenService.parseBearerToken(accessToken);
			
			response.setHeader("Access-Control-Expose-Headers", "Access-Token");
			response.setHeader("Access-Token", tokenService.generateAccessToken(memberNo));
			
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			return true;
		}
	}
}
