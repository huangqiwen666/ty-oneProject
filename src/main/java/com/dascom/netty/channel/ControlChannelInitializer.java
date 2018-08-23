package com.dascom.netty.channel;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
/**
 * 控制通道初始化
 * @author hqw
 */
@Component
@PropertySource("file:${user.dir}/config/netty.properties")
public class ControlChannelInitializer extends ChannelInitializer<SocketChannel>{
	
	@Value("${netty.ControlChannelReadTime}")
	private Integer ControlChannelReadTime;
	
	@Value("${netty.ControlChannelReadWriteTime}")
	private Integer ControlChannelReadWriteTime;
	
	@Autowired
	private ControlChannelHandler controlChannelHandler;
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		//解码器 解决分包、粘包问题
//		ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(ByteOrder.LITTLE_ENDIAN, Integer.MAX_VALUE, 8,4,8,0,true));
		//定时心跳
		ch.pipeline().addLast("IdleStateHandler",new IdleStateHandler(ControlChannelReadTime, 0,ControlChannelReadWriteTime,TimeUnit.SECONDS));
//		ch.pipeline().addLast("verifyChannelHandler",new VerifyChannelHandler());
//		ch.pipeline().addLast("AnalyseChannelHandle",new AnalyseChannelHandle());
		ch.pipeline().addLast("ControlChannelHandler",controlChannelHandler);
	}

	
	
}
