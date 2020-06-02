package com.couponmanager.service.impl;


import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.couponmanager.dao.IUserDao;
import com.couponmanager.model.ApiModel;
import com.couponmanager.model.UserModel;
import com.couponmanager.service.IUserService;
import com.couponmanager.util.CryptoHelper;
import com.couponmanager.util.ErrorCodeType;

@Service
public class UserServiceImpl implements IUserService {
	
	@Autowired
	private IUserDao userDao;
	
	@Override
	public UserModel login(String userId, String password) throws Exception {
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		map.put("USER_ID", userId);
		map.put("PASSWORD", CryptoHelper.AESCTR_Encode_Default(password , "UTF-8"));
		
		UserModel user = userDao.select(map);
		return user;
	}

	@Override
	public ApiModel create(String userId, String password, ApiModel apiModel) throws Exception {
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		map.put("USER_ID", userId);
		UserModel user = userDao.select(map);
		
		if(user == null) {
			String test = CryptoHelper.AESCTR_Encode_Default(password , "UTF-8");
			System.out.println(CryptoHelper.AESCTR_Decode_Default(test));
			
			map.put("PASSWORD", CryptoHelper.AESCTR_Encode_Default(password , "UTF-8"));
			int count = userDao.create(map);
			
			if(count != 0) {
				apiModel.setResult(ErrorCodeType.Success);
				apiModel.setMessage("Create Success!");
			} else {
				apiModel.setResult(ErrorCodeType.DBError);
				apiModel.setMessage("Create Fail!");
			}
		} else {
			apiModel.setResult(ErrorCodeType.Duplicate);
			apiModel.setMessage("UserId Duplicate!");
		}
		
		return apiModel;
	}
	
}
