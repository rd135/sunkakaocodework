package com.couponmanager.service;

import java.util.Map;

public interface IJwtService {

	<T> String createToken(String key, T data, String subject);
	boolean isUsable(String jwt);
	Map<String, Object> get(String key);
}
