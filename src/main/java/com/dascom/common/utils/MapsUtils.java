package com.dascom.common.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.dascom.common.RequestEntity;

import io.netty.channel.ChannelHandlerContext;
/**
 * 静态map存放类
 * @author hqw
 *
 */
@Configuration
public class MapsUtils {

	private static final Map<String, ChannelHandlerContext> ControlChannelCtxMap = new ConcurrentHashMap<String, ChannelHandlerContext>();
	
	private static final Map<String, ChannelHandlerContext> DataChannelCtxMap = new ConcurrentHashMap<String, ChannelHandlerContext>();
	
	private static final Map<ChannelHandlerContext, String> ChannelNumberMap = new ConcurrentHashMap<ChannelHandlerContext, String>();
	
	private static final Map<ChannelHandlerContext, Boolean> ControlHeartbeatMap = new ConcurrentHashMap<ChannelHandlerContext, Boolean>();
	
	private static final Map<String, Integer> DeviceCacheSizeMap = new ConcurrentHashMap<String, Integer>();
	
//	private static final Map<String, Boolean> PrintingMarkMap = new ConcurrentHashMap<String, Boolean>();
//	
//	private static final Map<String, byte[]> PrintDataMap = new ConcurrentHashMap<String, byte[]>();
//	
//	private static final Map<String, String> PrintRequestMap = new ConcurrentHashMap<String, String>();
//	
//	private static final Map<String, byte[]> UpdateWifiDataMap = new ConcurrentHashMap<String, byte[]>();
//
//	private static final Map<String, String> UpdateRequestMap = new ConcurrentHashMap<String, String>();
//	
//	private static final Map<String, Boolean> UpdateMarkMap = new ConcurrentHashMap<String, Boolean>();
	
	private static final Map<String, RequestEntity> RequestEntityMap = new ConcurrentHashMap<String, RequestEntity>();
	
	
	
	
	
	@Bean(name = "ControlChannelCtxMap")
	public Map<String, ChannelHandlerContext> ControlChannelCtxMap(){
		return ControlChannelCtxMap;
	}
	@Bean(name = "DataChannelCtxMap")
	public Map<String, ChannelHandlerContext> DataChannelCtxMap(){
		return DataChannelCtxMap;
	}
	@Bean(name = "ChannelNumberMap")
	public Map<ChannelHandlerContext, String> ChannelNumberMap(){
		return ChannelNumberMap;
	}
	@Bean(name = "ControlHeartbeatMap")
	public Map<ChannelHandlerContext, Boolean> ControlHeartbeatMap(){
		return ControlHeartbeatMap;
	}
	@Bean(name = "DeviceCacheSizeMap")
	public Map<String, Integer> DeviceCacheSizeMap(){
		return DeviceCacheSizeMap;
	}
	
	//---打印
//	@Bean(name = "PrintingMarkMap")
//	public Map<String, Boolean> PrintingMarkMap(){
//		return PrintingMarkMap;
//	}
//	
//	@Bean(name = "PrintDataMap")
//	public Map<String, byte[]> PrintDataMap(){
//		return PrintDataMap;
//	}
//	@Bean(name = "PrintRequestMap")
//	public Map<String, String> PrintRequestMap(){
//		return PrintRequestMap;
//	}
//	
//	
//	//更新
//	@Bean(name = "UpdateMarkMap")
//	public Map<String, Boolean> UpdateMarkMap(){
//		return UpdateMarkMap;
//	}
//	@Bean(name = "UpdateRequestMap")
//	public Map<String, String> UpdateRequestMap(){
//		return UpdateRequestMap;
//	}
//	@Bean(name = "UpdateWifiDataMap")
//	public Map<String, byte[]> UpdateWifiDataMap(){
//		return UpdateWifiDataMap;
//	}
	
	@Bean(name = "RequestEntityMap")
	public Map<String, RequestEntity> RequestEntityMap(){
		return RequestEntityMap;
	}
	
	
}
