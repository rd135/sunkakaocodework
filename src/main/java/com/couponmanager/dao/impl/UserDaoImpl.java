package com.couponmanager.dao.impl;

import java.util.HashMap;

import org.springframework.stereotype.Repository;

import com.couponmanager.dao.IUserDao;
import com.couponmanager.model.UserModel;
import com.couponmanager.util.BaseDao;

@Repository
public class UserDaoImpl extends BaseDao implements IUserDao {

	@Override
	public int create(HashMap<String, Object> paramMap) {
		return sqlSession.insert("User.insert", paramMap);
	}

	@Override
	public UserModel select(HashMap<String, Object> paramMap) {
		return sqlSession.selectOne("User.getUser", paramMap);
	}


}
