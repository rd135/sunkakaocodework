package com.couponmanager.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.couponmanager.service.IJwtService;

@Component
public class JwtInterceptor implements HandlerInterceptor{
	
	private static final String HEADER_AUTH = "Authorization";
	
	@Autowired
	private IJwtService jwtService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		final String token = request.getHeader(HEADER_AUTH);

		if(token != null && jwtService.isUsable(token)){
			return true;
		}else{
			System.out.println(request.getContextPath());
			response.sendError(401,"Authorized Error!!");
			return false;
		}
	}
}
