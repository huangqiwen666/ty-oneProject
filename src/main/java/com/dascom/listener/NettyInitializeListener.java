package com.dascom.listener;

import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;

import com.alibaba.fastjson.JSONObject;
import com.dascom.entity.PrinterStatus;
import com.dascom.netty.NettyChannelInitiator;
import com.dascom.redis.RedisHandle;


@WebListener
public class NettyInitializeListener implements ServletContextListener{

	
	
	@Autowired
	private NettyChannelInitiator nettyChannelInitiator;
	
	@Autowired
	private RedisHandle redisHandle;
	
	
	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println("--------------contextDestroyed---------------");
	}

	public void contextInitialized(ServletContextEvent arg0) {
//		printerDao.updateAliveAll();
		HashOperations<String, String, String> opsForHash = redisHandle.getOpsForHash();
		Map<String, String> entries = opsForHash.entries("status");
		Set<String> keySet = entries.keySet();
		for (String number : keySet) {
			String string = entries.get(number);
			PrinterStatus ps = JSONObject.parseObject(string, PrinterStatus.class);
			if (!"dead".equals(ps.getMain())) {
				ps.setMain("dead");
				entries.put(number, JSONObject.toJSONString(ps));
			}
		}
		opsForHash.putAll("status", entries);
		System.out.println("-----------netty控制通道启动----------------");
		nettyChannelInitiator.startControlChannel();
		System.out.println("-----------netty数据通道启动----------------");
		nettyChannelInitiator.startDataChannel();
	}

	
	
	
}
