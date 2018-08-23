package com.dascom.netty.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.dascom.common.RequestEntity;
import com.dascom.common.utils.AsyncHttpUtil;
import com.dascom.common.utils.IdentificationResponse;
import com.dascom.entity.PrinterEntity;
import com.dascom.entity.PrinterInfo;
import com.dascom.entity.PrinterStatus;
import com.dascom.mongodb.PrinterDao;
import com.dascom.netty.message.AnalyseMessage;
import com.dascom.netty.message.GenerateMessageByte;
import com.dascom.netty.service.ControlChannelService;
import com.dascom.netty.service.LogEventStatus;
import com.dascom.redis.RedisHandle;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
@Service
public class ControlChannelServiceImpl implements ControlChannelService {
	
	private static final Logger log =LogManager.getLogger(ControlChannelServiceImpl.class);
	
	@Autowired
	private AsyncHttpUtil asyncHttpUtil;
	
	@Value(value = "${tcp.server.userMessageEncode}")
	private String userMessageEncode;
	
	@Autowired
	private IdentificationResponse identificationResponse;
	
	@Autowired
	private Map<String, ChannelHandlerContext> DataChannelCtxMap;
	
	@Autowired
	private Map<String, ChannelHandlerContext> ControlChannelCtxMap;
	
	@Autowired
	private Map<ChannelHandlerContext, String> ChannelNumberMap;
	
	@Autowired
	private Map<String, Integer> DeviceCacheSizeMap;
	
	@Autowired
	private Map<String, RequestEntity> RequestEntityMap;
	
	@Autowired
	private Map<ChannelHandlerContext, Boolean> ControlHeartbeatMap;
	
	@Autowired
	private PrinterDao printerDao;
	
	@Autowired
	private LogEventStatus logEventStatus;
	
	@Autowired
	private RedisHandle redisHandle;
	
	public void freeResources(ChannelHandlerContext ctx) {
		String number = ChannelNumberMap.get(ctx);
		ControlHeartbeatMap.remove(ctx);
		ChannelNumberMap.remove(ctx);
		if (number!=null) {
			log.info("number:{},控制通道关闭,释放资源",number);
			ChannelHandlerContext data_channel =DataChannelCtxMap.get(number);
			if (data_channel!=null) {
				data_channel.close();
			}
			ControlChannelCtxMap.remove(number);
			DeviceCacheSizeMap.remove(number);
			RequestEntity requestEntity = RequestEntityMap.get(number);
			if (requestEntity!=null&&!requestEntity.isMark()) {
				RequestEntityMap.remove(number);
			}
			PrinterStatus printerStatus = new PrinterStatus();
			printerStatus.setMain("dead");
			printerStatus.setNewest(new Date());
			String status = redisHandle.hget("status", number);
			if (!StringUtils.isEmpty(status)) {
				PrinterStatus frontStatus = JSONObject.parseObject(status, PrinterStatus.class);
				if (!"dead".equals(frontStatus.getMain())) {
					logEventStatus.record(number,printerStatus);//
				}
				
			}
			redisHandle.hset("status", number, JSONObject.toJSONString(printerStatus));
		}
	}

	
	/**
	 * 心跳逻辑，简化版
	 */
	public void saveDeviceStatus(ChannelHandlerContext ctx,byte[] message) {
		boolean connect_usb = AnalyseMessage.heartbeatMessage(message);
		ControlHeartbeatMap.put(ctx, true);
		String number = ChannelNumberMap.get(ctx);
		PrinterStatus printerStatus = new PrinterStatus();
		ArrayList<String> subs = null;
		printerStatus.setMain("ready");// 主状态
		if (!connect_usb) {
			printerStatus.setMain("error");
			subs=new ArrayList<String>();
			subs.add("noConnectDevice");
			printerStatus.setSubs(subs);// 子状态
		}
		Date date = new Date();
		printerStatus.setNewest(date);// 当前时间
		
		String status = redisHandle.hget("status", number);
		if (!StringUtils.isEmpty(status)) {
			PrinterStatus frontStatus = JSONObject.parseObject(status, PrinterStatus.class);
			if (!printerStatus.getMain().equals(frontStatus.getMain())) {
				logEventStatus.record(number,printerStatus);//
				if ("ready".equals(printerStatus.getMain())||"warn".equals(printerStatus.getMain())) {
					asyncHttpUtil.sendPrintInform(number);
				}
			}
		}else {
			if ("ready".equals(printerStatus.getMain())||"warn".equals(printerStatus.getMain())) {
				asyncHttpUtil.sendPrintInform(number);
			}
		}
		redisHandle.hset("status", number, JSONObject.toJSONString(printerStatus));
//		PrinterEntity pe = printerDao.updateStatus(number, ps);
//		if (!ps.getMain().equals(pe.getStatus().getMain())) {
//			if ("ready".equals(ps.getMain())) {
//				asyncHttpUtil.sendPrintInform(number);
//			}
//			log.info("number:{},获取到设备状态：{}",number,ps.getMain());
//			logEventStatus.record(number,ps);
//		}
	}
	
	
	public void identificationHandle(byte[] message, ChannelHandlerContext ctx) throws IOException {
		byte[] wifi_message = AnalyseMessage.getIdentificationWifiByte(message);//获取到WiFi的信息
		if (wifi_message!=null) {
			Map<String, String> deviceLoginMassage = AnalyseMessage.getDeviceLoginMassage(wifi_message);//解析成字符串
			String number=deviceLoginMassage.get("number");
			Integer cache=Integer.valueOf(deviceLoginMassage.get("cache"));
			PrinterInfo printerInfo = AnalyseMessage.getPrinterInfo(number,deviceLoginMassage);//拼装设备信息对象 info
			if (number!=null&&printerInfo!=null) {
//				String reqHost = ctx.channel().remoteAddress().toString();
//				boolean identification=identification(number,reqHost,printerInfo);
				PrinterEntity entity = printerDao.findByNumber(number);//查找设备库
//				HashOperations<String, String, String> opsForHash = redisHandle.getOpsForHash();//获取到操作redis hash的对象
//				Map<String, String> entries = opsForHash.entries(number);
				Date date = new Date();
				if (entity==null) {//设备没有登录过
					entity=new PrinterEntity();
					entity.setReg_date(date);// 当前时间
					entity.setNumber(number);
					entity.setOwner("anyone");
					PrinterStatus printerStatus = new PrinterStatus();
					printerStatus.setMain("dead");
					entity.setStatus(printerStatus);
					printerDao.insert(entity);
//					entries=new HashMap<String, String>();
				}else {
					ChannelHandlerContext oldChannel = ControlChannelCtxMap.get(number);
					if (oldChannel!=null) {
						String hget = redisHandle.hget("status", number);
						if (!StringUtils.isEmpty(hget)) {
							PrinterStatus status = JSONObject.parseObject(hget, PrinterStatus.class);
							if (!"dead".equals(status.getMain())) {
								byte[] alreadyLogin = identificationResponse.alreadyLogin();
								log.warn("number"+number+"该设备已经登录了");
								ctx.writeAndFlush(Unpooled.copiedBuffer(alreadyLogin));
								ctx.close();
								return;
							}
						}
						ControlChannelCtxMap.remove(number);
						oldChannel.close();//关闭旧连接
						
					}
				}
//				entity.setReqHost(reqHost);//请求host
				entity.setLogin_date(date);//登录时间
				entity.setInfo(printerInfo);//设备信息
//				entity.setAlive(true);//在线
				
				printerDao.update(number,printerInfo, date);//更新设备库
				log.info("number:{}认证通过,请求modle:{}",number,printerInfo.getModel());
				ChannelNumberMap.put(ctx, number);//在线设备
				ControlHeartbeatMap.put(ctx, true);//心跳标志
				DeviceCacheSizeMap.put(number, cache);//添加缓存
				ControlChannelCtxMap.put(number, ctx);
				byte[] verifySucceed = identificationResponse.verifySucceed();
				ctx.writeAndFlush(Unpooled.copiedBuffer(verifySucceed));//回复认证通过
				
				//认证通过，查询设备状态
				byte type= 0x00;
				byte worknum=0x05;
				byte[] messageBody= {0x10,0x64,0x00,0x00};//读取用户信息
				byte[] serialNumber ={0x10,0x64,0x05,0x00};
				byte[] req=GenerateMessageByte.getMessage(worknum,type,serialNumber,messageBody);
//				RequestEntityMap.put(number, new RequestEntity());
				ctx.writeAndFlush(Unpooled.copiedBuffer(req));
			}else {
				log.warn("number:{}获取设备型号失败",number);
				byte[] messageFailure = identificationResponse.messageFailure();
				ctx.writeAndFlush(Unpooled.copiedBuffer(messageFailure));
				ctx.close();
			}
		}else {
			byte[] messageFailure = identificationResponse.messageFailure();
			log.warn("获取认证信息失败");
			ctx.writeAndFlush(Unpooled.copiedBuffer(messageFailure));
			ctx.close();
		}
	}

	public void heartbeat(ChannelHandlerContext ctx, IdleStateEvent e) throws IOException {
		String number = ChannelNumberMap.get(ctx);
		if (e.state() == IdleState.READER_IDLE) {
			if (ControlHeartbeatMap.containsKey(ctx)) {
				if (ControlHeartbeatMap.get(ctx)) {
					ControlHeartbeatMap.put(ctx, false);
					log.debug("number:{}心跳方法发送0x05,0x00请求给设备--1条",number);
					byte type= 0x00;
					byte worknum=0x05;
					byte[] messageBody= {0x10,0x09,0x01,0x30,0x30};//10  09  01  SS  M0 wifi
					byte[] serialNumber ={0x05,0x05,0x05,0x05}; //RequestByte.getSerialNumber(ctx);
					byte[] req=GenerateMessageByte.getMessage(worknum,type,serialNumber,messageBody);
					ctx.writeAndFlush(Unpooled.copiedBuffer(req));
				}else{
					PrinterStatus printerStatus=new PrinterStatus();
					printerStatus.setMain("dead");
					printerStatus.setNewest(new Date());
					String status = redisHandle.hget("status", number);
					if (!StringUtils.isEmpty(status)) {
						PrinterStatus frontStatus = JSONObject.parseObject(status, PrinterStatus.class);
						if (!"dead".equals(frontStatus.getMain())) {
							log.info("number:{}心跳超时,修改设备状态为离线",number);
							logEventStatus.record(number,printerStatus);//
						}
						
					}
					redisHandle.hset("status", number, JSONObject.toJSONString(printerStatus));
				}
			}else {
				log.info("number:{}的通道心跳标记不存在",number);
				ctx.close();
			}
		}else if(e.state() == IdleState.ALL_IDLE){
			log.info("number:{}闲置超时，关闭连接",number);
			ctx.close();
		}
	}

	public void updatePattern(ChannelHandlerContext ctx, byte[] message) throws IOException {
		int index=24;
		String number=ChannelNumberMap.get(ctx);
		if (message[index]==3) {
			log.info("number"+number+"成功退出固件更新模式，重启wifi模块,指令1011");
			//成功退出出固件更新模式   重启wifi模块
			RequestEntity requestEntity = RequestEntityMap.get(number);
			if (requestEntity.isEndMark()&&requestEntity.getData()==null) {
				requestEntity.setMark(true);
				RequestEntityMap.put(number, requestEntity);
				byte worknum=0x05;
				byte type= 0x00;
				byte[] serialNumber = {0x10, 0x11,0,0};
				byte[] messageBody = {0x10, 0x11, 0x00, 0x00};
				byte[] bmbm=GenerateMessageByte.getMessage(worknum,type,serialNumber,messageBody);
				ctx.writeAndFlush(Unpooled.copiedBuffer(bmbm));
			}
		}else if (message[index]==1) {
			RequestEntity requestEntity = RequestEntityMap.get(number);
			int length=requestEntity.getData().length;
			//进入固件更新模式
			log.info("number"+number+"成功进入固件更新模式，发送更新文件大小:"+length+",指令1060");
			byte worknum=0x05;
			byte type= 0x00;
			byte[] serialNumber = {0x10,0x60,0,0};
			byte[] messageBody={0x10, 0x60, 04, (byte) (((length>>24)&0xff)+((length>>16)&0xff)+((length>>8)&0xff)+(length&0xff)),
					(byte) ((length>>24)&0xff),(byte) ((length>>16)&0xff),(byte) ((length>>8)&0xff),(byte) (length&0xff)};
			byte[] bmbm=GenerateMessageByte.getMessage(worknum,type,serialNumber,messageBody);
			ctx.writeAndFlush(Unpooled.copiedBuffer(bmbm));
		}
	}

	@Override
	public void restartWifiSucceed(ChannelHandlerContext ctx) throws InterruptedException {
		// 重启wifi模块成功
		String number=ChannelNumberMap.get(ctx);
//		RequestEntity requestEntity = RequestEntityMap.get(number);
//		requestEntity.setMark(true);
//		RequestEntityMap.put(number, requestEntity);
		log.info("number"+number+"重启wifi模块成功");
		//如果有更新任务，需返回给   一般都有
		ctx.close();
	}

	@Override
	public void sendUpdate(ChannelHandlerContext ctx) throws IOException {
		String number=ChannelNumberMap.get(ctx);
		RequestEntity requestEntity = RequestEntityMap.get(number);
		if (requestEntity.isEndMark()) {
			log.info("number"+number+"更新数据发送完毕，发送更新完毕指令 1062");
			byte worknum=0x05;
			byte type= 0x00;
			byte[] serialNumber ={0x10,0x62,5,0};
			byte[] messageBody = {0x10, 0x62, 00, 00};
			byte[] bmbm=GenerateMessageByte.getMessage(worknum,type,serialNumber,messageBody);
			ctx.writeAndFlush(Unpooled.copiedBuffer(bmbm));
			return;
		}
		byte[] data = requestEntity.getData();
		int length = data.length;
		byte[] sendPerTime;//每次最大255字节
		if (length>255) {
			sendPerTime=new byte[255+4];
			System.arraycopy(data, 0, sendPerTime,4, 255);
			byte[] data2=new  byte[length-255];
			System.arraycopy(data, 255, data2,0, length-255);
			requestEntity.setData(data2);
			log.debug("number"+number+"多包发送更新数据,指令1061");
		}else {
			log.info("number"+number+"最后一包发送更新数据,指令1061");
			sendPerTime=new byte[length+4];
			System.arraycopy(data, 0, sendPerTime, 4, length);
			requestEntity.setData(null);
			requestEntity.setEndMark(true);
		}
		RequestEntityMap.put(number, requestEntity);
		sendPerTime[0]=0x10;
		sendPerTime[1]=0x61;
		sendPerTime[2]=(byte) ((sendPerTime.length-4)&0xFF);
		int sum=0;
		for(int k=4;k<sendPerTime.length;k++){
			sum+=(sendPerTime[k]&0xff);
		}
		sendPerTime[3]=(byte) (sum&0xff);
		byte worknum=0x05;
		byte type= 0x00;
		byte[] serialNumber = {0,0,5,0};
		byte[] bmbm=GenerateMessageByte.getMessage(worknum,type,serialNumber,sendPerTime);
		ctx.writeAndFlush(Unpooled.copiedBuffer(bmbm));
	}


	@Override
	public void exitUpdate(ChannelHandlerContext ctx) throws IOException {
		String number=ChannelNumberMap.get(ctx);
		log.info("number"+number+"更新数据发送完毕，退出固件更新模式,指令1016");
		byte worknum=0x05;
		byte type= 0x00;
		byte[] serialNumber ={0,0,5,0};
		byte[] messageBody = {0x10, 0x16, 01, 01,01};
		byte[] bmbm=GenerateMessageByte.getMessage(worknum,type,serialNumber,messageBody);
		ctx.writeAndFlush(Unpooled.copiedBuffer(bmbm));
	}

	@Override
	public void comeUserMessage(ChannelHandlerContext ctx, byte[] message) throws IOException {
		String number=ChannelNumberMap.get(ctx);
		RequestEntity requestEntity = RequestEntityMap.get(number);
		if(requestEntity!=null){
			byte[] data = requestEntity.getData();
			if (data!=null) {//设置用户信息
				byte[] sendMessage=new byte[10];
				int length = data.length;
				byte[] serialNumber={0x10,0x66,0x05,0x00};
				byte[] byte2Length = GenerateMessageByte.byte2Length(Integer.toHexString(length));
				log.info("number"+number+"读取用户数据,指令1066");
				sendMessage[0]=0x10;
				sendMessage[1]=0x66;
				sendMessage[2]=(byte) ((sendMessage.length-4)&0xff);
				sendMessage[3]=0x00;
				sendMessage[4]=0x00;
				sendMessage[5]=0x00;
				sendMessage[6]=(byte) ((sendMessage.length-8)&0xff);
				sendMessage[7]=0x00;
				sendMessage[8]=byte2Length[0];
				sendMessage[9]=byte2Length[1];
				int sum=0;
				for(int k=4;k<sendMessage.length;k++){
					sum+=(sendMessage[k]&0xff);
				}
				sendMessage[3]=(byte) (sum&0xff);
				byte[] bmbm=GenerateMessageByte.getMessage((byte)0x05,(byte)0x00,serialNumber,sendMessage);
			
				ctx.writeAndFlush(Unpooled.copiedBuffer(bmbm));
			}
		}else {//读取用户信息
			byte[] sendMessage=new byte[8];
			sendMessage[0]=0x10;
			sendMessage[1]=0x65;
			sendMessage[2]=(byte) ((sendMessage.length-4)&0xff);
			sendMessage[3]=0x00;
			sendMessage[4]=0x00;
			sendMessage[5]=0x00;
			sendMessage[6]=0x02;
			sendMessage[7]=0x00;
			int sum=0;
			for(int k=4;k<sendMessage.length;k++){
				sum+=(sendMessage[k]&0xff);
			}
			sendMessage[3]=(byte) (sum&0xff);
			log.info("number"+number+"读取用户数据,指令1065");
			byte[] serialNumber={0x10,0x65,0x05,0x00};
			byte[] bmbm=GenerateMessageByte.getMessage((byte)0x05,(byte)0x00,serialNumber,sendMessage);
			RequestEntityMap.put(number, new RequestEntity());
			ctx.writeAndFlush(Unpooled.copiedBuffer(bmbm));
		}
	}
	
	
	
	@Override
	public void setUserMessage(ChannelHandlerContext ctx, byte[] message) throws IOException {
		String number=ChannelNumberMap.get(ctx);
		RequestEntity requestEntity = RequestEntityMap.get(number);
		if(requestEntity!=null){
			if (requestEntity.isEndMark()) {
				requestEntity.setMessage(message);
				requestEntity.setMark(true);
				RequestEntityMap.put(number, requestEntity);
				return;
			}
			byte[] data = requestEntity.getData();
			byte[] sendMessage;
			if (data!=null) {
				int length = data.length;
				int index=2;
				if (length>247) {//重后面开始保存
					index+=length-247;
					sendMessage=new byte[8+247];
					System.arraycopy(data,length-247, sendMessage,8, 247);
					byte[] data2=new  byte[length-247];
					System.arraycopy(data, 0, data2,0, length-247);
					requestEntity.setData(data2);
					log.debug("number"+number+"多包发送设置用户数据,指令1066");
				}else {
					log.info("number"+number+"最后一包发送设置用户数据,指令1066");
					sendMessage=new byte[8+length];
					System.arraycopy(data, 0, sendMessage,8, length);
					requestEntity.setData(null);
				}
				byte[] byte2Length = GenerateMessageByte.byte2Length(Integer.toHexString(index));
				sendMessage[0]=0x10;
				sendMessage[1]=0x66;
				sendMessage[2]=(byte) ((sendMessage.length-4)&0xff);
				sendMessage[3]=0x00;
				sendMessage[4]=byte2Length[0];
				sendMessage[5]=byte2Length[1];
				sendMessage[6]=(byte) ((sendMessage.length-8)&0xff);
				sendMessage[7]=0x00;
				int sum=0;
				for(int k=4;k<sendMessage.length;k++){
					sum+=(sendMessage[k]&0xff);
				}
				sendMessage[3]=(byte) (sum&0xff);
			}else {
				sendMessage=new byte[8];
				sendMessage[0]=0x10;
				sendMessage[1]=0x66;
				sendMessage[2]=(byte) ((sendMessage.length-4)&0xff);
				sendMessage[3]=0x00;
				sendMessage[4]=(byte) 0xff;
				sendMessage[5]=(byte) 0xff;
				sendMessage[6]=(byte) 0xff;
				sendMessage[7]=(byte) 0xff;
				int sum=0;
				for(int k=4;k<sendMessage.length;k++){
					sum+=(sendMessage[k]&0xff);
				}
				sendMessage[3]=(byte) (sum&0xff);
				requestEntity.setEndMark(true);
				log.info("number"+number+"保存设置用户数据,指令1066");
			}
			byte[] serialNumber={0x10,0x66,0x05,0x00};
			byte[] bmbm=GenerateMessageByte.getMessage((byte)0x05,(byte)0x00,serialNumber,sendMessage);
			ctx.writeAndFlush(Unpooled.copiedBuffer(bmbm));
		}
	}


	@Override
	public void getUserMessage(ChannelHandlerContext ctx, byte[] message) throws IOException {
		String number=ChannelNumberMap.get(ctx);
		RequestEntity requestEntity = RequestEntityMap.get(number);
		if(requestEntity!=null){
			byte[] beforeMessage = requestEntity.getMessage();
			byte[] laterMessage;
			if (beforeMessage==null) {
				laterMessage=new byte[2];
				if (message.length>24) {
					System.arraycopy(message, 24, laterMessage, 0, message.length-24);
				}else {
					laterMessage[0]=0;
					laterMessage[1]=0;
				}
			}else {
				laterMessage=new byte[beforeMessage.length+message.length-24];
				System.arraycopy(beforeMessage, 0, laterMessage, 0, beforeMessage.length);
				System.arraycopy(message, 24, laterMessage, beforeMessage.length,message.length-24);
				
			}
			requestEntity.setMessage(laterMessage);
			int length=(laterMessage[0]&0xff)+(laterMessage[1]&0xff<<8)+2;
			byte[] sendMessage=new byte[8];
			
			if ((length-laterMessage.length)>255) {
				sendMessage[6]=(byte)0xff;
			}else if ((length-laterMessage.length)==0) {
				String userMessage;
				if (laterMessage.length>2) {
					userMessage=new String(laterMessage,2,laterMessage.length-2,userMessageEncode);
				}else {
					userMessage="{\"userName\":\"anyone\"}";
				}
				redisHandle.set("UserMessage"+number, userMessage);
				log.info("number:{},读取用户数据完成,指令1065,message:{}",number,userMessage);
				RequestEntityMap.remove(number);
				return;
			}else {
				sendMessage[6]=(byte)((length-laterMessage.length)&0xff);
				requestEntity.setEndMark(true);
			}
			byte[] byte2Length = GenerateMessageByte.byte2Length(Integer.toHexString(laterMessage.length));
			sendMessage[0]=0x10;
			sendMessage[1]=0x65;
			sendMessage[2]=(byte) ((sendMessage.length-4)&0xff);
			sendMessage[4]=byte2Length[0];
			sendMessage[5]=byte2Length[1];
			sendMessage[7]=0x00;
			int sum=0;
			for(int k=4;k<sendMessage.length;k++){
				sum+=(sendMessage[k]&0xff);
			}
			sendMessage[3]=(byte) (sum&0xff);
			
			byte[] serialNumber={0x10,0x65,0x05,0x00};
			byte[] bmbm=GenerateMessageByte.getMessage((byte)0x05,(byte)0x00,serialNumber,sendMessage);
			RequestEntityMap.put(number, requestEntity);
			ctx.writeAndFlush(Unpooled.copiedBuffer(bmbm));
			
		}
	}


	@Override
	public void review(ChannelHandlerContext ctx, byte[] message) throws IOException {
		String number=ChannelNumberMap.get(ctx);
		RequestEntity requestEntity = RequestEntityMap.get(number);
		if(requestEntity!=null){
			if (requestEntity.isEndMark()) {//发送完成
				requestEntity.setMessage(message);
				requestEntity.setMark(true);
				RequestEntityMap.put(number, requestEntity);
				return;
			}
			byte[] data = requestEntity.getData();
			int length = data.length;
			byte[] sendPerTime;//每次最大255字节
			if (length>255) {
				sendPerTime=new byte[255+4];
				System.arraycopy(data, 0, sendPerTime,4, 255);
				byte[] data2=new  byte[length-255];
				System.arraycopy(data, 255, data2,0, length-255);
				requestEntity.setData(data2);
				log.debug("number"+number+"多包发送语音数据,指令1070");
			}else {
				log.info("number"+number+"最后一包发送语音数据,指令1070");
				sendPerTime=new byte[length+4];
				System.arraycopy(data, 0, sendPerTime, 4, length);
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
			ctx.writeAndFlush(Unpooled.copiedBuffer(bmbm));
		}
		
	}


	@Override
	public void controlInstruct(ChannelHandlerContext ctx, byte[] message) {
		String number=ChannelNumberMap.get(ctx);
		RequestEntity requestEntity = RequestEntityMap.get(number);
		if (requestEntity!=null) {
			requestEntity.setMessage(message);
			requestEntity.setMark(true);
			RequestEntityMap.put(number, requestEntity);
		}
	}
	@Override
	public void readWifiConfig(ChannelHandlerContext ctx, byte[] message) {
		String number=ChannelNumberMap.get(ctx);
		RequestEntity requestEntity = RequestEntityMap.get(number);
		if (requestEntity!=null) {
			requestEntity.setMessage(message);
			requestEntity.setMark(true);
			RequestEntityMap.put(number, requestEntity);
		}
	}
	
	@Override
	public void updataWifiConfig(ChannelHandlerContext ctx, byte[] message) {
		String number=ChannelNumberMap.get(ctx);
		RequestEntity requestEntity = RequestEntityMap.get(number);
		if (requestEntity!=null) {
			requestEntity.setMessage(message);
			requestEntity.setMark(true);
			RequestEntityMap.put(number, requestEntity);
		}
	}


	@Override
	public void asyncPrintQRcode(ChannelHandlerContext ctx) {
		String number=ChannelNumberMap.get(ctx);
		asyncHttpUtil.printQrcode(number);
	}
}
