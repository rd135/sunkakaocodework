package com.couponmanager.dao;

import java.util.HashMap;
import java.util.List;

import com.couponmanager.model.CouponModel;

public interface ICouponDao {
	
	int create(HashMap<String, Object> paramMap);
	int update(HashMap<String, Object> paramMap);
	List<CouponModel> getCoupon(HashMap<String, Object> paramMap);
}
