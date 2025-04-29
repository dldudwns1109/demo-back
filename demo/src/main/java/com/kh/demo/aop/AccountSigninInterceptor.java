package com.kh.demo.aop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import com.kh.demo.service.TokenService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AccountSigninInterceptor implements HandlerInterceptor {

	@Autowired
	private TokenService tokenService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if (request.getMethod().equalsIgnoreCase("options")) return true;
		
		try {
			String authorization = request.getHeader("Authorization");
			if (authorization == null) throw new Exception();
			if (!authorization.startsWith("Bearer"))
				throw new Exception();
			
			String token = authorization.substring(7);
			long memberNo = tokenService.parse(token);
			return true;
		} catch(Exception e) {
			response.sendError(401);
			return false;
		}
	}
}
