package com.couponmanager.controllers;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.couponmanager.model.ApiModel;
import com.couponmanager.model.UserModel;
import com.couponmanager.service.IJwtService;
import com.couponmanager.service.IUserService;
import com.couponmanager.util.ErrorCodeType;

@Controller
@RequestMapping(value = "/api/Users")
public class UserController {
	
	@Autowired 
	private IJwtService jwtService;
	
	@Autowired
	private IUserService userService;
	
	@RequestMapping(value="/create" , method = RequestMethod.POST)
	public @ResponseBody ApiModel create (
			@RequestParam(required=true) String userId ,
			@RequestParam(required=true) String password) throws Throwable {
		ApiModel apiModel = new ApiModel();
		apiModel = userService.create(userId, password, apiModel);
		
		return apiModel;
	}
	
	
	@RequestMapping(value="/login" ,method = RequestMethod.POST)
	public @ResponseBody ApiModel Login (
			@RequestParam(required=true) String userId ,
			@RequestParam(required=true) String password,
			HttpServletResponse response) throws Throwable {
		ApiModel apiModel = new ApiModel();
		String token = null;
		
		//로그인 후 토큰 굽기 까지
		UserModel resultUser = userService.login(userId, password);
		if(resultUser != null) {
			token = jwtService.createToken("member", resultUser, "user");
			response.setHeader("Authorization", token);
			apiModel.setResult(ErrorCodeType.Success);
			apiModel.setMessage("Authorization :" + token);
		} else {
			apiModel.setResult(ErrorCodeType.DBError);
			apiModel.setMessage("Success fail");
		}
		
		return apiModel;
	}
}
