package com.dascom.redis.impl;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.dascom.redis.RedisHandle;
@Repository
public class RedisHandleImpl implements RedisHandle {

	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	public void set(String key, String value) {
		redisTemplate.opsForValue().set(key, value);
	}

	public void set(String key, String value, int timeout) {
		redisTemplate.opsForValue().set(key, value,timeout,TimeUnit.SECONDS);
	}

	public String get(String key) {
		return redisTemplate.opsForValue().get(key);
	}

	public Boolean hasKey(String key) {
		return redisTemplate.hasKey(key);
	}

	public void del(String key) {
		redisTemplate.delete(key);
	}

//	public Boolean expire(String key, int timeout) {
//		Boolean expire = redisTemplate.expire(key, timeout,TimeUnit.SECONDS);
//		return expire;
//	}

	public void hset(String key, String field, String value) {
		redisTemplate.opsForHash().put(key, field, value);
	}

	public String hget(String key, String field) {
		HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
		return opsForHash.get(key, field);
	}

	public void hdel(String key, String... field) {
		HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
		for (String hashkey : field) {
			opsForHash.delete(key, hashkey);
		}
	}

	public Boolean hexists(String key, String field) {
		HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
		return opsForHash.hasKey(key, field);
	}
	
	public HashOperations<String, String, String> getOpsForHash() {
		return redisTemplate.opsForHash();
	}
	
	public void convertAndSend(String key,Object value) {
		redisTemplate.convertAndSend(key, value);
	}

//	private String printTask = "printTask";
	public String lpop(String key) {
		ListOperations<String, String> opsForList = redisTemplate.opsForList();
		return opsForList.leftPop(key);
	}

	@Override
	public Long getExpire(String key) {
		return redisTemplate.getExpire(key);
	}
	
	
	
}
