package com.dascom.service.impl;

import java.util.Base64;
import java.util.Date;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.dascom.common.ErrorCode;
import com.dascom.common.RedisKey;
import com.dascom.common.RequestEntity;
import com.dascom.common.ResultVO;
import com.dascom.common.utils.ResultVOUtil;
import com.dascom.common.utils.SendMessageUtils;
import com.dascom.common.utils.UsingComponent;
import com.dascom.entity.PrinterStatus;
import com.dascom.entity.WifiConfig;
import com.dascom.netty.message.AnalyseMessage;
import com.dascom.netty.message.GenerateMessageByte;
import com.dascom.netty.service.PrintStatisticsService;
import com.dascom.redis.RedisHandle;
import com.dascom.service.CloudPrintService;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

@Service
public class CloudPrintServiceImpl implements CloudPrintService {

	private static final Logger log =LogManager.getLogger(CloudPrintServiceImpl.class);
	
	@Value(value = "${CloudPrint.print.Timeout}")
	private Integer printTimeout;
	
	@Value(value = "${CloudPrint.update.Timeout}")
	private Integer updateTimeout;
	
	@Value(value = "${CloudPrint.control.Timeout}")
	private Integer controlTimeout;
	
	@Autowired
	private UsingComponent usingComponent;
	
	@Autowired
	private Map<String, ChannelHandlerContext> ControlChannelCtxMap;
	
	@Autowired
	private Map<String, ChannelHandlerContext> DataChannelCtxMap;
	
	@Autowired
	private PrintStatisticsService PrintStatisticsService;
	
	@Autowired
	private Map<String, RequestEntity> RequestEntityMap;
	
	@Autowired
	private RedisHandle redisHandle;

	@Override
	public ResultVO<Object> print(String number,String id,String data) throws Exception {
		ResultVO<Object> resultVO;
		ChannelHandlerContext datactx =null;
		ChannelHandlerContext controlctx = null ;
		try {
			byte[] printData = Base64.getDecoder().decode(data);
			RequestEntity re=new RequestEntity();
			re.setData(printData);
			RequestEntityMap.put(number, re);
			datactx = DataChannelCtxMap.get(number);
			if (datactx==null) {
				controlctx = ControlChannelCtxMap.get(number);
				if (controlctx==null) {//设备离线了
					PrinterStatus ps=new PrinterStatus();
					ps.setMain("dead");
					ps.setNewest(new Date());
					redisHandle.hset(RedisKey.STATUS, number, JSONObject.toJSONString(ps));
					PrintStatisticsService.printFailureStatistics(number);
					resultVO=ResultVOUtil.error(ErrorCode.printerOffline,id,ErrorCode.printerOfflineDescribe);
					log.info("number:{}通道都不存在了，设备离线，返回code:{}",number,resultVO.getCode());
					return resultVO;
				}else {
					//发送开启数据通道指令
					log.info("number:{}开启数据通道",number);
					byte[] req = SendMessageUtils.startDataChannel();
					controlctx.writeAndFlush(Unpooled.copiedBuffer(req));
				}
			}else {//数据通道存在，直接发送数据
				log.info("number:{}数据通道存在,直接发送打印数据",number);
				byte[] req = SendMessageUtils.findDeviceCache();
				datactx.writeAndFlush(Unpooled.copiedBuffer(req));
			}
			Integer statistics=0;
			while(statistics<(printTimeout*2)){
				Thread.sleep(500);
				RequestEntity requestEntity = RequestEntityMap.get(number);
				if (requestEntity==null) {
					resultVO=ResultVOUtil.error(ErrorCode.handleError,id);
					log.info("number:{}打印异常，返回code:{}",number,resultVO.getCode(),ErrorCode.handleErrorDescribe);
					PrintStatisticsService.printFailureStatistics(number);
					return resultVO;
				}
				if (requestEntity.isMark()) {
					resultVO=ResultVOUtil.success(id);
					log.info("number:{}打印成功，返回code:{}",number,0);
					PrintStatisticsService.printSucceedStatistics(number);
					return resultVO;
				}
				statistics++;
			}
			resultVO=ResultVOUtil.error(ErrorCode.handleTimeout,id,ErrorCode.handleTimeoutDescribe);
			PrintStatisticsService.printFailureStatistics(number);
			datactx.close();//数据通道打印超时，关闭通道
			log.info("number:{}打印超时，返回code:{}",number,resultVO.getCode());
			return resultVO;
		}finally {
			RequestEntityMap.remove(number);
			log.debug("number:{},CloudPrintService释放资源",number);
		}
	}

	@Override
	public ResultVO<Object> update(String number,String id,String data) throws Exception {
		ResultVO<Object> resultVO = null;
		ChannelHandlerContext controlctx = null ;
		try {
			byte[] updateData = Base64.getDecoder().decode(data);
			RequestEntity re=new RequestEntity();
			re.setData(updateData);
			controlctx = ControlChannelCtxMap.get(number);
			if (controlctx==null) {//设备离线了
				PrinterStatus ps=new PrinterStatus();
				ps.setMain("dead");
				ps.setNewest(new Date());
				redisHandle.hset(RedisKey.STATUS, number, JSONObject.toJSONString(ps));
				resultVO=ResultVOUtil.error(ErrorCode.printerOffline,id);
				log.info("number:{}通道都不存在了，设备离线，返回code:{}",number,resultVO.getCode());
				return resultVO;
			}else {
				RequestEntityMap.put(number, re);
				//进入更新模式指令
				byte worknum=0x05;
				byte type= 0x00;
				byte[] serialNumber ={0,0,5,0};
				byte[] messageBody= {0x10, 0x16, 0x00, 0x00};
				byte[] bmbm=GenerateMessageByte.getMessage(worknum,type,serialNumber,messageBody);
				//发送指令
				controlctx.writeAndFlush(Unpooled.copiedBuffer(bmbm));
			}
			Integer statistics=0;
			usingComponent.resetTimeout(number,updateTimeout+1);
			while(statistics<(updateTimeout)){
				Thread.sleep(1000);
				RequestEntity requestEntity = RequestEntityMap.get(number);
				if (requestEntity==null) {
					resultVO=ResultVOUtil.error(ErrorCode.handleError,id);
					log.info("number:{}更新异常，返回code:{}",number,resultVO.getCode());
					return resultVO;
				}
				if (requestEntity.isMark()) {
					resultVO=ResultVOUtil.success(id);
					log.info("number:{}更新成功，返回code:{}",number,0);
					return resultVO;
				}
				statistics++;
			}
			
			byte[] exitUpdata = SendMessageUtils.exitUpdata();
			
			controlctx.writeAndFlush(Unpooled.copiedBuffer(exitUpdata));
//			controlctx.close();
			resultVO=ResultVOUtil.error(ErrorCode.handleTimeout,id);
			log.info("number:{}更新超时，返回code:{}",number,resultVO.getCode());
			return resultVO;
		} finally {
			RequestEntityMap.remove(number);
			log.debug("number:{},CloudPrintService释放资源",number);
		}
	}

	@Override
	public ResultVO<Object> review(String number, String id,String data) throws Exception {
		ResultVO<Object> resultVO = null;
		ChannelHandlerContext controlctx = null ;
		try {
			byte[] sendData = data.getBytes("GB2312");
			controlctx = ControlChannelCtxMap.get(number);
			if (controlctx==null) {//设备离线了
				PrinterStatus ps=new PrinterStatus();
				ps.setMain("dead");
				ps.setNewest(new Date());
				redisHandle.hset(RedisKey.STATUS, number, JSONObject.toJSONString(ps));
				resultVO=ResultVOUtil.error(ErrorCode.printerOffline,id);
				log.info("number:{}通道都不存在了，设备离线，返回code:{}",number,resultVO.getCode());
				return resultVO;
			}else {
				RequestEntity requestEntity=new RequestEntity();
				byte[] sendPerTime;//每次最大255字节
				int length = sendData.length;
				if (length>255) {
					sendPerTime=new byte[255+4];
					System.arraycopy(sendData, 0, sendPerTime,4, 255);
					byte[] data2=new  byte[length-255];
					System.arraycopy(sendData, 255, data2,0, length-255);
					requestEntity.setData(data2);
					log.debug("number"+number+"多包发送语音数据,指令1070");
				}else {
					log.info("number"+number+"最后一包发送语音数据,指令1070");
					sendPerTime=new byte[length+4];
					System.arraycopy(sendData, 0, sendPerTime, 4, length);
					requestEntity.setData(null);
					requestEntity.setEndMark(true);
				}
				RequestEntityMap.put(number, requestEntity);
				sendPerTime[0]=0x10;
				sendPerTime[1]=0x70;
				sendPerTime[2]=(byte) ((sendPerTime.length-4)&0xFF);
				int sum=0;
				for(int k=4;k<sendPerTime.length;k++){
					sum+=(sendPerTime[k]&0xff);
				}
				sendPerTime[3]=(byte) (sum&0xff);
				byte worknum=0x05;
				byte type= 0x00;
				byte[] serialNumber = {0x10,0x70,5,0};
				byte[] bmbm=GenerateMessageByte.getMessage(worknum,type,serialNumber,sendPerTime);
				controlctx.writeAndFlush(Unpooled.copiedBuffer(bmbm));
				
//				RequestEntityMap.put(number, re);
//				//进入更新模式指令
//				byte worknum=0x05;
//				byte type= 0x00;
//				byte[] serialNumber ={0x10, 0x70,5,0};
//				byte[] messageBody= {0x10, 0x70, 0x00, 0x00};
//				byte[] bmbm=GenerateMessageByte.getMessage(worknum,type,serialNumber,messageBody);
//				//发送指令
//				controlctx.writeAndFlush(Unpooled.copiedBuffer(bmbm));
			}
			Integer statistics=0;
			usingComponent.resetTimeout(number,controlTimeout+1);
			while(statistics<(controlTimeout)){
				Thread.sleep(500);
				RequestEntity requestEntity = RequestEntityMap.get(number);
				if (requestEntity==null) {
					resultVO=ResultVOUtil.error(ErrorCode.handleError,id);
					log.info("number:{}播放语音异常，返回code:{}",number,resultVO.getCode());
					return resultVO;
				}
				if (requestEntity.isMark()) {
					byte[] message = requestEntity.getMessage();
					if (message.length<24) {
						resultVO=ResultVOUtil.error(ErrorCode.reviewSendFailure,id);
						log.info("number:{}播放语音失败，返回code:{}",number,resultVO.getCode());
						return resultVO;
					}
					if (message[22]==0&&message[23]!=0) {
						resultVO=ResultVOUtil.error(ErrorCode.reviewSendFailure,id);
						log.info("number:{}设置用户信息失败，返回code:{},失败错误码SS:{}",number,resultVO.getCode(),message[23]);
						return resultVO;
					}
					resultVO=ResultVOUtil.success(id);
					log.info("number:{}播放语音完毕，返回code:{}",number,0);
					return resultVO;
				}
				statistics++;
			}
			resultVO=ResultVOUtil.error(ErrorCode.handleTimeout,id);
			log.info("number:{}播放语音超时，返回code:{}",number,resultVO.getCode());
			return resultVO;
		} finally {
			RequestEntityMap.remove(number);
			log.debug("number:{},CloudPrintService释放资源",number);
		}
	}

	@Override
	public ResultVO<Object> control(String number, String id, String data,String controlType) throws Exception {
		ResultVO<Object> resultVO = null;
		ChannelHandlerContext controlctx = null ;
		try {
			byte[] controlData = Base64.getDecoder().decode(data);
			controlctx = ControlChannelCtxMap.get(number);
			if (controlctx==null) {//设备离线了
				PrinterStatus ps=new PrinterStatus();
				ps.setMain("dead");
				ps.setNewest(new Date());
				redisHandle.hset(RedisKey.STATUS, number, JSONObject.toJSONString(ps));
				resultVO=ResultVOUtil.error(ErrorCode.printerOffline,id);
				log.info("number:{}通道都不存在了，设备离线，返回code:{}",number,resultVO.getCode());
				return resultVO;
			}else {
				RequestEntity requestEntity=new RequestEntity();
				requestEntity.setEndMark(true);
				RequestEntityMap.put(number, requestEntity);
				byte worknum=0x05;
				byte type= 0x00;
				if ("printer".equals(controlType)) {
					type=0x01;
				}
				byte[] serialNumber = {(byte) 0xff,(byte) 0xff,(byte) 0xff,(byte) 0xff};
				byte[] bmbm=GenerateMessageByte.getMessage(worknum,type,serialNumber,controlData);
				controlctx.writeAndFlush(Unpooled.copiedBuffer(bmbm));
			}
			
			Integer statistics=0;
			usingComponent.resetTimeout(number,controlTimeout+1);
			while(statistics<(controlTimeout)){
				Thread.sleep(500);
				RequestEntity requestEntity = RequestEntityMap.get(number);
				if (requestEntity==null) {
					resultVO=ResultVOUtil.error(ErrorCode.handleError,id);
					log.info("number:{}控制异常，返回code:{}",number,resultVO.getCode());
					return resultVO;
				}
				if (requestEntity.isMark()) {
					byte[] message = requestEntity.getMessage();
					StringBuffer sb=null;
					if (message.length<20) {
						resultVO=ResultVOUtil.error(ErrorCode.controlFailure,id);
						log.info("number:{}控制失败，返回code:{}",number,resultVO.getCode());
						return resultVO;
					}else {
						sb=new StringBuffer();
						if (message.length>20) {
							for (int i =20; i < message.length; i++) {
								int v=message[i]&0xff;
								String hexString = Integer.toHexString(v);
								if (hexString.length()<2) {
									hexString="0"+hexString;
								}
								sb.append(hexString);
								sb.append(",");
							}
						}
					}
					resultVO=ResultVOUtil.success(id, sb.toString());
					log.info("number:{}控制完毕，返回code:{},message:{}",number,0,sb.toString());
					return resultVO;
				}
				statistics++;
			}
			resultVO=ResultVOUtil.error(ErrorCode.handleTimeout,id);
			log.info("number:{}控制超时，返回code:{}",number,resultVO.getCode());
			return resultVO;
		} finally {
			RequestEntityMap.remove(number);
			log.debug("number:{},CloudPrintService释放资源",number);
		}
	}

	@Override
	public ResultVO<Object> readWifiConfig(String number, String id) throws Exception {
		ResultVO<Object> resultVO = null;
		ChannelHandlerContext controlctx = null ;
		try {
			byte[] controlData ={0x10,0x17,0,0};
			controlctx = ControlChannelCtxMap.get(number);
			if (controlctx==null) {//设备离线了
				PrinterStatus ps=new PrinterStatus();
				ps.setMain("dead");
				ps.setNewest(new Date());
				redisHandle.hset(RedisKey.STATUS, number, JSONObject.toJSONString(ps));
				resultVO=ResultVOUtil.error(ErrorCode.printerOffline,id);
				log.info("number:{}通道都不存在了，设备离线，返回code:{}",number,resultVO.getCode());
				return resultVO;
			}else {
				RequestEntity requestEntity=new RequestEntity();
				requestEntity.setEndMark(true);
				RequestEntityMap.put(number, requestEntity);
				byte worknum=0x05;
				byte type= 0x00;
				byte[] serialNumber ={0x10,0x17,0x05,0};
				byte[] bmbm=GenerateMessageByte.getMessage(worknum,type,serialNumber,controlData);
				controlctx.writeAndFlush(Unpooled.copiedBuffer(bmbm));
			}
			
			Integer statistics=0;
			usingComponent.resetTimeout(number,controlTimeout+1);
			while(statistics<(controlTimeout*2)){
				Thread.sleep(500);
				RequestEntity requestEntity = RequestEntityMap.get(number);
				if (requestEntity==null) {
					resultVO=ResultVOUtil.error(ErrorCode.handleError,id);
					log.info("number:{}读取wifi配置异常，返回code:{}",number,resultVO.getCode());
					return resultVO;
				}
				if (requestEntity.isMark()) {
					byte[] message = requestEntity.getMessage();
					if (message.length<=20+4+8) {
						resultVO=ResultVOUtil.error(ErrorCode.readWifiConfigFailure,id);
						log.info("number:{}读取wifi配置失败，返回code:{}",number,resultVO.getCode());
					}else {
						WifiConfig wifiConfig = AnalyseMessage.getWifiConfig(message, 20+4+8);
						resultVO=ResultVOUtil.success(id,wifiConfig);
						log.info("number:{}读取wifi配置完毕，返回code:{},message:{}",number,0);
					}
					return resultVO;
				}
				statistics++;
			}
			resultVO=ResultVOUtil.error(ErrorCode.handleTimeout,id);
			log.info("number:{}读取wifi配置超时，返回code:{}",number,resultVO.getCode());
			return resultVO;
		} finally {
			RequestEntityMap.remove(number);
			log.debug("number:{},CloudPrintService释放资源",number);
		}
	}

	@Override
	public ResultVO<Object> updataWifiConfig(String number, String id, WifiConfig wifiConfig) throws Exception {
		
		ResultVO<Object> resultVO = null;
		ChannelHandlerContext controlctx = null ;
		try {
			controlctx = ControlChannelCtxMap.get(number);
			if (controlctx==null) {//设备离线了
				PrinterStatus ps=new PrinterStatus();
				ps.setMain("dead");
				ps.setNewest(new Date());
				redisHandle.hset(RedisKey.STATUS, number, JSONObject.toJSONString(ps));
				resultVO=ResultVOUtil.error(ErrorCode.printerOffline,id);
				log.info("number:{}通道都不存在了，设备离线，返回code:{}",number,resultVO.getCode());
				return resultVO;
			}else {
				byte[] wifiConfigtoByte = AnalyseMessage.getWifiConfigtoByte(wifiConfig);
				RequestEntity requestEntity=new RequestEntity();
				requestEntity.setEndMark(true);
				RequestEntityMap.put(number, requestEntity);
				byte worknum=0x05;
				byte type= 0x00;
				byte[] serialNumber = {0x10,0x18,0x05,0x00};
				byte[] bmbm=GenerateMessageByte.getMessage(worknum,type,serialNumber,wifiConfigtoByte);
				controlctx.writeAndFlush(Unpooled.copiedBuffer(bmbm));
			}
			Integer statistics=0;
			usingComponent.resetTimeout(number,controlTimeout+1);
			while(statistics<(controlTimeout*2)){
				Thread.sleep(500);
				RequestEntity requestEntity = RequestEntityMap.get(number);
				if (requestEntity==null) {
					resultVO=ResultVOUtil.error(ErrorCode.handleError,id);
					log.info("number:{}控制更新wifi配置，返回code:{}",number,resultVO.getCode());
					return resultVO;
				}
				if (requestEntity.isMark()) {
					byte[] message = requestEntity.getMessage();
					if (message.length<24) {
						resultVO=ResultVOUtil.error(ErrorCode.updataWifiConfigFailure,id);
						log.info("number:{}控制更新wifi配置失败，返回code:{}",number,resultVO.getCode());
						return resultVO;
					}
					if (message[22]==0&&message[23]!=0) {
						resultVO=ResultVOUtil.error(ErrorCode.updataWifiConfigFailure,id);
						log.info("number:{}控制更新wifi配置失败，返回code:{}，错误码SS:{}",number,resultVO.getCode(),message[23]&0xff);
						return resultVO;
					}
					byte[] restartWifi = SendMessageUtils.restartWifi();
					controlctx.writeAndFlush(Unpooled.copiedBuffer(restartWifi));
					resultVO=ResultVOUtil.success(id);
					log.info("number:{}控制更新wifi配置完毕，返回code:{}",number,0);
					return resultVO;
				}
				statistics++;
			}
			resultVO=ResultVOUtil.error(ErrorCode.handleTimeout,id);
			log.info("number:{}控制超时，返回code:{}",number,resultVO.getCode());
			return resultVO;
		} finally {
			RequestEntityMap.remove(number);
			log.debug("number:{},CloudPrintService释放资源",number);
		}
	}
	

}
