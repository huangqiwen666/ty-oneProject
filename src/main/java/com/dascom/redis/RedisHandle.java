package com.dascom.redis;


import org.springframework.data.redis.core.HashOperations;

/**
 * 封装操作redis的接口
 * @author hqw
 *
 */
public interface RedisHandle {

	
	/**
	 * 向redis插入一条key value
	 */
	void set(String key, String value);
	/**
	 * 向redis插入一条key value 并设置过期时间
	 * @param key
	 * @param value
	 * @param timeout
	 */
	void set(String key, String value, int timeout);
	/**
	 * 根据key取到redis的值
	 * @param key
	 * @return value
	 */
	String get(String key);
	/**
	 * 查看redis是否存在该key
	 * @param key
	 * @return Boolean
	 */
	Boolean hasKey(String key);
	/**
	 * 删除redis中的一个key
	 * @param key
	 */
	void del(String key);
	/**
	 * 根据key设置改key的过期时间
	 * @param key
	 * @param seconds
	 * @return
	 */
	
//	Boolean expire(String key, int timeout);
	/**
	 * redis的hash操作，中插入一条key, field, value数据
	 * @param key
	 * @param field
	 * @param value
	 * @return
	 */
	void hset(String key, String field, String value);
	/**
	 * redis的hash操作，根据key，field取出value
	 * @param key
	 * @param field
	 * @return
	 */
	String hget(String key, String field);
	/**
	 * redis的hash操作，根据key,删除多个field
	 * @param key
	 * @param field
	 * @return
	 */
	void hdel(String key, String... field);
	/**
	 * redis的hash操作，查看redis是否存在该 key, field 的value
	 * @param key
	 * @param field
	 * @return
	 */
	Boolean hexists(String key, String field);
	/**
	 * redis的hash操作,返回操作redis hash的HashOperations
	 * @return
	 */
	HashOperations<String, String, String> getOpsForHash();
	/**
	 * 返回List首个value值
	 * @param key
	 * @return
	 */
	String lpop(String key);
	/**
	 * 获取key的过期时间
	 * @param key
	 * @return 
	 */
	Long getExpire(String key);
	
}
