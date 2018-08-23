package com.dascom.netty.channel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dascom.netty.message.AnalyseMessage;
import com.dascom.netty.message.VerifyMessage;
import com.dascom.netty.service.DataChannelService;

import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

@Sharable
@Component
public class DataChannelHandler extends ChannelInboundHandlerAdapter{

	//日志
	private static final Logger log =LogManager.getLogger(DataChannelHandler.class);

	@Autowired
	private DataChannelService dataChannelService;
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		dataChannelService.checkDeviceNumber(ctx);
		super.channelActive(ctx);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		dataChannelService.freeResources(ctx);
		super.channelInactive(ctx);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		log.debug("----------DataChannelHandler----------");
		ByteBuf buf = (ByteBuf) msg;
		byte[] message = new byte[buf.readableBytes()];
		buf.readBytes(message);
		ReferenceCountUtil.release(msg);
		if (message == null || message.length < 20) {
			log.warn("消息头长度小于20，断开连接");
			ctx.close();
			return;
		}
		boolean verify_messagehead = VerifyMessage.verify_messagehead(message);
		if (verify_messagehead) {
			log.debug("消息头正确");
			
			byte[] type = AnalyseMessage.analyse_messagetype(message);
			byte low_type= type[0];
			byte high_type = type[1];
			log.debug("数据通道接收到的message解析出,low_type:{},high_type:{}",low_type,high_type);
			if (low_type==4/*&&high_type==0*/) {//查询到设备number
				dataChannelService.setDataChannelNumber(message,ctx);
				
			}else if (low_type==2/*&&high_type==1*/) {//透传打印返回
				dataChannelService.passthroughPrint(ctx,message);
			}
		}else {
			log.debug("消息头不正确，拒绝该连接");
			ctx.disconnect();
		}
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent e = (IdleStateEvent) evt;
			dataChannelService.heartbeat(ctx,e);
		}else {
			super.userEventTriggered(ctx, evt);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		StringBuffer sb=new StringBuffer();
		StackTraceElement[] stackTrace = cause.getStackTrace();
		for (StackTraceElement stackTraceElement : stackTrace) {
			sb.append("      "+stackTraceElement.toString()+System.getProperty("line.separator"));
		}
		log.error("数据通道异常是：" + cause.getMessage()+System.getProperty("line.separator")+"详细描述："+System.getProperty("line.separator")+sb.toString());
		cause.printStackTrace();
		ctx.close();
		super.exceptionCaught(ctx, cause);
	}

	
	
}
