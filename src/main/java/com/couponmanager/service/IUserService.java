package com.couponmanager.service;

import com.couponmanager.model.ApiModel;
import com.couponmanager.model.UserModel;

public interface IUserService {
	public UserModel login(String userId , String password) throws Exception;
	public ApiModel create(String userId , String password, ApiModel apiModel) throws Exception;
}
