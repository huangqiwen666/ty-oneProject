package com.dascom.netty.service.impl;

import java.io.IOException;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dascom.common.RequestEntity;
import com.dascom.netty.message.AnalyseMessage;
import com.dascom.netty.message.GenerateMessageByte;
import com.dascom.netty.service.DataChannelService;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
@Service
public class DataChannelServiceImpl implements DataChannelService {

	private static final Logger log =LogManager.getLogger(DataChannelServiceImpl.class);
	
	private byte[] serialNumber = {0x04,0x04,0x04,0x04};
	
	@Autowired
	private Map<String,Integer> DeviceCacheSizeMap;
	
	@Autowired
	private Map<String, RequestEntity> RequestEntityMap;
	
	@Autowired
	private Map<String,ChannelHandlerContext> DataChannelCtxMap;
	
	@Autowired
	private Map<ChannelHandlerContext,String> ChannelNumberMap;
	
	
	public void checkDeviceNumber(ChannelHandlerContext ctx) throws IOException {
		byte[] messageBody={0x00,0x00};
		byte[] bmbm=GenerateMessageByte.getMessage((byte)0x04,(byte)0x00,serialNumber,messageBody);
 		ByteBuf resp = Unpooled.copiedBuffer(bmbm);
 		ctx.writeAndFlush(resp);
 		log.info("数据通道发送询问设备身份的0x04请求");
	}

	public void freeResources(ChannelHandlerContext ctx) {
		String number = ChannelNumberMap.get(ctx);
		ChannelNumberMap.remove(ctx);
		if (number!=null) {
			DataChannelCtxMap.remove(number);
			RequestEntityMap.remove(number);
		}
	}

	public void setDataChannelNumber(byte[] message, ChannelHandlerContext ctx) throws Exception {
		String number = AnalyseMessage.analyse_messageNumber(message);
		ChannelNumberMap.put(ctx, number);
		DataChannelCtxMap.put(number, ctx);
		RequestEntity requestEntity = RequestEntityMap.get(number);
		int cache = 0;
		if (DeviceCacheSizeMap.containsKey(number)) {
			cache=DeviceCacheSizeMap.get(number);
		}
		sendPrintData(ctx,number,cache,requestEntity);
		
	}

	public void heartbeat(ChannelHandlerContext ctx, IdleStateEvent e) {
		if (e.state() == IdleState.ALL_IDLE) {
			String number = ChannelNumberMap.get(ctx);
			log.info("number:{}释放闲置的数据通道",number);
			ctx.close();
		}
	}

	public void passthroughPrint(ChannelHandlerContext ctx,byte[] message) throws Exception{
		String number = ChannelNumberMap.get(ctx);
		Integer cache=0;
		if (message.length>24) {
			cache=AnalyseMessage.analyseWifiCache(message, 24);
			log.info("number:{},透传打印返回的缓存:{}",number,cache);
		}else {
			log.warn("number:{}透传打印返回的message.length={}",number,message.length);
			return ;
		}
		DeviceCacheSizeMap.put(number, cache);
		RequestEntity requestEntity = RequestEntityMap.get(number);
		if (requestEntity.isEndMark()) {//数据发送完毕
			requestEntity.setMark(true);
			RequestEntityMap.put(number, requestEntity);
		}else {
			sendPrintData(ctx,number,cache,requestEntity);
		}
//		String id = PrintRequestMap.get(number);
//		if (id!=null) {
//			if (PrintDataMap.containsKey(id)) {
//				sendPrintData(ctx,number,id);
//			}else {
//				PrintingMarkMap.put(id, true);
//			}
//		}else {
//			ctx.close();
//		}
	}
	
	
	private void sendPrintData(ChannelHandlerContext ctx,String number,Integer cache,RequestEntity requestEntity ) throws Exception {
//		String number = ChannelNumberMap.get(ctx);

		byte[] data =requestEntity.getData();
		byte[] sendMessage;
		int dataLength=data.length;
		if (dataLength <= cache) {// 只需发送一包
			sendMessage = data;
			requestEntity.setData(null);
			requestEntity.setEndMark(true);
			log.info("number:{}，最后一包发送打印数据length:{}",number,dataLength);
		} else {// 多包发送
			if (cache<=0) {
				Thread.sleep(500);
			}
			log.info("number:{}，发送打印数据length:{}",number,cache);
			sendMessage = new byte[cache];
			byte[] surplusMessage=new byte[data.length - cache];
			System.arraycopy(data, 0, sendMessage, 0, cache);//要发送的数据
			System.arraycopy(data, cache, surplusMessage, 0,data.length - cache);
			requestEntity.setData(surplusMessage);
		}
		byte[] bmbm=GenerateMessageByte.getMessage((byte)0x02,(byte)0x01,serialNumber, sendMessage);//透传打印0x09
		RequestEntityMap.put(number, requestEntity);
		ctx.writeAndFlush(Unpooled.copiedBuffer(bmbm));
	}
	
}
