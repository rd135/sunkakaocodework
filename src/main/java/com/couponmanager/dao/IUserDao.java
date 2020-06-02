package com.couponmanager.dao;

import java.util.HashMap;

import com.couponmanager.model.UserModel;

public interface IUserDao {
	
	int create(HashMap<String, Object> paramMap);
	UserModel select(HashMap<String, Object> paramMap);
}
