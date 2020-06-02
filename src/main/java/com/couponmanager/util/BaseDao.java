package com.couponmanager.util;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseDao {
	@Autowired
	protected SqlSession sqlSession;
	
	//protected static final Logger logger=LoggerFactory.getLogger(BaseDao.class);
}
