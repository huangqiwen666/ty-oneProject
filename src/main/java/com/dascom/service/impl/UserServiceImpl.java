package com.dascom.service.impl;

import java.util.Date;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.dascom.common.RequestEntity;
import com.dascom.common.ResultVO;
import com.dascom.common.utils.ResultVOUtil;
import com.dascom.common.utils.SendMessageUtils;
import com.dascom.common.utils.UsingComponent;
import com.dascom.entity.PrinterStatus;
import com.dascom.redis.RedisHandle;
import com.dascom.service.UserService;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
@Service
public class UserServiceImpl implements UserService {
	
	private static final Logger log =LogManager.getLogger(UserServiceImpl.class);
	
	@Value(value = "${CloudPrint.writeUser.Timeout}")
	private Integer writeUserTimeout;
	
	@Autowired
	private Map<String, ChannelHandlerContext> ControlChannelCtxMap;

	@Autowired
	private Map<String, RequestEntity> RequestEntityMap;

	@Autowired
	private RedisHandle redisHandle;
	
	@Autowired
	private UsingComponent usingComponent;

	@Override
	public ResultVO<Object> writeUserMessage(String number,String id, RequestEntity requestEntity) throws Exception {
		ResultVO<Object> resultVO;
		ChannelHandlerContext controlctx = null ;
		try {
			controlctx=ControlChannelCtxMap.get(number);
			if (controlctx==null) {
				PrinterStatus ps=new PrinterStatus();
				ps.setMain("dead");
				ps.setNewest(new Date());
				redisHandle.hset("status", number, JSONObject.toJSONString(ps));
				resultVO=ResultVOUtil.error(1011,id);
				log.info("number:{}通道都不存在了，设备离线，返回code:{}",number,resultVO.getCode());
				return resultVO;
			}
			byte[] writeUserMessage = SendMessageUtils.readWriteUserMessage();
			RequestEntityMap.put(number, requestEntity);
			controlctx.writeAndFlush(Unpooled.copiedBuffer(writeUserMessage));
			Integer statistics=0;
			usingComponent.resetTimeout(number, writeUserTimeout+1);
			while(statistics<(writeUserTimeout*2)){
				Thread.sleep(500);
				requestEntity = RequestEntityMap.get(number);
				if (requestEntity==null) {
					resultVO=ResultVOUtil.error(1012,id);
					log.info("number:{}设置用户信息异常，返回code:{}",number,resultVO.getCode());
					return resultVO;
				}
				if (requestEntity.isMark()) {
					byte[] message = requestEntity.getMessage();
					if (message.length<24) {
						resultVO=ResultVOUtil.error(1021,id);
						log.info("number:{}设置用户信息失败，返回code:{}",number,resultVO.getCode());
						return resultVO;
					}
					if (message[22]==0&&message[23]!=0) {
						resultVO=ResultVOUtil.error(1021,id);
						log.info("number:{}设置用户信息失败，返回code:{},失败错误码SS:{}",number,resultVO.getCode(),message[23]);
						return resultVO;
					}
					resultVO=ResultVOUtil.success(id);
					log.info("number:{}设置用户信息成功，返回code:{}",number,0);
					return resultVO;
				}
				statistics++;
			}
			resultVO=ResultVOUtil.error(1013,id);
			log.info("number:{}设置用户信息超时，返回code:{}",number,resultVO.getCode());
		} finally {
			RequestEntityMap.remove(number);
		}
		return resultVO;
	}

//	@Override
//	public ResultVO<Object> readUserMessage(String number,String id) throws Exception {
//		ResultVO<Object> resultVO = null;
//		ChannelHandlerContext controlctx = null ;
//		try {
//			controlctx=ControlChannelCtxMap.get(number);
//			if (controlctx==null) {
//				PrinterStatus ps=new PrinterStatus();
//				ps.setMain("dead");
//				ps.setNewest(new Date());
//				printerDao.updateStatus(number, ps);
//				resultVO=ResultVOUtil.error(3101,id);
//				log.info("number:{}通道都不存在了，设备离线，返回code:{}",number,3101);
//				return resultVO;
//			}
//			byte[] writeUserMessage = SendMessageUtils.writeUserMessage();
//			RequestEntityMap.put(number, new RequestEntity());
//			controlctx.writeAndFlush(Unpooled.copiedBuffer(writeUserMessage));
//			Integer statistics=0;
//			RequestEntity requestEntity;
//			while(statistics<(writeUserTimeout*2)){
//				Thread.sleep(500);
//				statistics++;
//				requestEntity = RequestEntityMap.get(number);
//				if (requestEntity==null) {
//					continue;
//				}
//				if (requestEntity.isMark()) {
//					byte[] message = requestEntity.getMessage();
//					int length = message.length;
//					if (length<=2) {
//						resultVO=ResultVOUtil.error(3111,id);
//						log.info("number:{}读取用户信息异常，返回code:{}",number,3111);
//						return resultVO;
//					}
//					String userMessage=new String(message, 24, message.length-24);
//					resultVO=ResultVOUtil.success(id,userMessage.trim());
//					log.info("number:{}读取用户信息成功，返回code:{},message:{}",number,0,userMessage);
//					return resultVO;
//				}
//			}
//			resultVO=ResultVOUtil.error(3109,id);
//			
//		} finally {
//			RequestEntityMap.remove(number);
//		}
//		
//		
//		return resultVO;
//	}

}
