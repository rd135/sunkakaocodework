package com.couponmanager.dao.impl;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.couponmanager.dao.ICouponDao;
import com.couponmanager.model.CouponModel;
import com.couponmanager.util.BaseDao;

@Repository
public class CouponDaoImpl extends BaseDao implements ICouponDao {

	@Override
	public int create(HashMap<String, Object> paramMap) {
		return sqlSession.insert("Coupon.insert", paramMap);
	}

	@Override
	public List<CouponModel> getCoupon(HashMap<String, Object> paramMap) {
		return sqlSession.selectList("Coupon.getCoupon", paramMap);
	}

	@Override
	public int update(HashMap<String, Object> paramMap) {
		return sqlSession.update("Coupon.update", paramMap);
	}

}
