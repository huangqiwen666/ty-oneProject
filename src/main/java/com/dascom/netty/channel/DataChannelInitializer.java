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
 * 数据通道初始化
 * @author hqw
 *
 */
@Component
@PropertySource("file:${user.dir}/config/netty.properties")
public class DataChannelInitializer extends ChannelInitializer<SocketChannel>{

	
	@Value("${netty.DataChannelMark}")
	private boolean DataChannelMark;
	
	@Value("${netty.DataChannelReadWriteTime}")
	private Integer DataChannelReadWriteTime;
	
	@Autowired
	private DataChannelHandler dataChannelHandler;
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ch.pipeline().addLast("IdleStateHandler",new IdleStateHandler(0,0,DataChannelReadWriteTime,TimeUnit.SECONDS));
		ch.pipeline().addLast("DataChannelHandler",dataChannelHandler);// 数据通道
//		if (DataChannelMark) {
//			//定时心跳
//			ch.pipeline().addLast("IdleStateHandler",new IdleStateHandler(0,0,DataChannelReadWriteTime,TimeUnit.SECONDS));
//			ch.pipeline().addLast("DataChannelHandler",dataChannelHandler);// 数据通道
//		}else {
//			ch.pipeline().addLast("DataChannelHandler",dataChannelHandler);// 数据通道短连接
//		}
		
//		ch.writeAndFlush(msg);
	}

	/*public static byte[] queryDeviceNumber() throws IOException {
		byte[] serialNumber = GenerateMessageByte.getSerialNumber();
		byte[] messageBody={0x00,0x00};
		byte[] bmbm=GenerateMessageByte.getMessage((byte)0x04,(byte)0x00,serialNumber,messageBody);
// 		ByteBuf resp = Unpooled.copiedBuffer(bmbm);
 		return bmbm;
	}*/
}
