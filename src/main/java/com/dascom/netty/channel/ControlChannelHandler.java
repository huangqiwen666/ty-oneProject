package com.dascom.netty.channel;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.timeout.IdleStateEvent;

import com.dascom.netty.message.AnalyseMessage;
import com.dascom.netty.message.VerifyMessage;
import com.dascom.netty.service.ControlChannelService;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
@Sharable
@Component
public class ControlChannelHandler extends ChannelInboundHandlerAdapter{

	//日志
	private static final Logger log =LogManager.getLogger(ControlChannelHandler.class);

	@Autowired
	private ControlChannelService controlChannelService;
	
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		log.debug("ControlChannelHandler");
		ByteBuf buf = (ByteBuf) msg;
		byte[] message = new byte[buf.readableBytes()];
		buf.readBytes(message);
		ReferenceCountUtil.release(msg);
		if (message == null || message.length < 20) {
			log.warn("消息头长度小于20，拒绝该连接");
			ctx.close();
			return;
		}
		boolean verify_messagehead = VerifyMessage.verify_messagehead(message);
		if (verify_messagehead) {
			log.debug("消息头正确");
			
			boolean verify_message_serialNumber = VerifyMessage.verify_message_serialNumber(message);
			if (verify_message_serialNumber) {
				controlChannelService.controlInstruct(ctx, message);
				return;
			}
			byte[] type = AnalyseMessage.analyse_messagetype(message);
			byte low_type= type[0];
			byte high_type = type[1];
			
			//控制功能添加位置
			log.debug("消息号,low_type:{},high_type:{}",low_type,high_type);
			if (low_type==1&&high_type==2) {//认证
				controlChannelService.identificationHandle(message,ctx);
			}else if (low_type==5&&high_type==0) {//控制指令 WiFi的回复
				if (message.length<24) {
					return;
				}
				int index=20;//需要解析message的起始位置
				String instruction = AnalyseMessage.analyse_messageInstruction(index, message);//获得控制指令
				/*if ("1008".equals(instruction)) {//查询设备状态  心跳
					controlChannelService.setUserMessage(ctx, message);
				}else */if ("1009".equals(instruction)) {//查询设备状态  心跳
					controlChannelService.saveDeviceStatus(ctx, message);
				}else if ("1011".equals(instruction)) {//重启wifi模块成功
//					log.info("number"+number+"重启wifi模块成功");
					controlChannelService.restartWifiSucceed(ctx);
				}else if ("1016".equals(instruction)) {
					controlChannelService.updatePattern(ctx,message);

				}else if ("1060".equals(instruction)||"1061".equals(instruction)) {
					controlChannelService.sendUpdate(ctx);
				}else if ("1062".equals(instruction)) {
					controlChannelService.exitUpdate(ctx);
				}else if ("1064".equals(instruction)) {
					controlChannelService.comeUserMessage(ctx, message);
				}else if ("1065".equals(instruction)) {
					controlChannelService.getUserMessage(ctx, message);
				}else if ("1066".equals(instruction)) {
					controlChannelService.setUserMessage(ctx, message);
				}else if ("1070".equals(instruction)) {
					controlChannelService.review(ctx, message);
				}else if ("1017".equals(instruction)) {
					controlChannelService.readWifiConfig(ctx, message);
				}else if ("1018".equals(instruction)) {
					controlChannelService.updataWifiConfig(ctx, message);
				}
			}else if (low_type==6&&high_type==2) {
				//打印二维码
				controlChannelService.asyncPrintQRcode(ctx);
			}
		}else {
			log.debug("消息头不正确，拒绝该连接");
			ctx.disconnect();
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		controlChannelService.freeResources(ctx);
		super.channelInactive(ctx);
	}
	
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		StringBuffer sb=new StringBuffer();
		StackTraceElement[] stackTrace = cause.getStackTrace();
		for (StackTraceElement stackTraceElement : stackTrace) {
			sb.append("      "+stackTraceElement.toString()+System.getProperty("line.separator"));
		}
		log.error("控制通道异常是：" + cause.getMessage()+System.getProperty("line.separator")+"详细描述："+System.getProperty("line.separator")+sb.toString());
		ctx.close();
		super.exceptionCaught(ctx, cause);
	}

	
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent e = (IdleStateEvent) evt;
			controlChannelService.heartbeat(ctx,e);
		}else {
			super.userEventTriggered(ctx, evt);
		}
		
	}

	
}
