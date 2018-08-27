package com.dascom.common.utils;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dascom.common.RedisKey;
import com.dascom.common.RequestEntity;
import com.dascom.redis.RedisHandle;

@Component
public class UsingComponent {

	
	@Autowired
	private RedisHandle redisHandle;
	
	@Autowired
	private Map<String, RequestEntity> RequestEntityMap;
	
	public synchronized boolean getAndSetUsing(String number) {
		boolean containsKey = RequestEntityMap.containsKey(number);
		return containsKey;
	}
	
	
	public void delUsing(String number) {
		String key=RedisKey.USING+number;
		redisHandle.del(key);
	}
	
	public void resetTimeout(String number,int timeout) {
		String key=RedisKey.USING+number;
		redisHandle.set(key, "true",timeout);
	}
	
	
	public Long getExpire(String number) {
		String key=RedisKey.USING+number;
		Long expire = redisHandle.getExpire(key);
		return expire;
	}
	
	
	
}
