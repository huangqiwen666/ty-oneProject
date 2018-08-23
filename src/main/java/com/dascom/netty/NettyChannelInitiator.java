package com.dascom.netty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;

@Component
@PropertySource("file:${user.dir}/config/netty.properties")
public class NettyChannelInitiator {

	@Value("${netty.ControlChannelPort}")
	private Integer ControlChannelPort;
	
	@Value("${netty.DataChannelPort}")
	private Integer DataChannelPort;
	
	@Autowired
	private ServerBootstrap controlServerBootstrap;
	@Autowired
	private ServerBootstrap dataServerBootstrap;

	
	
	@Async
	public void startControlChannel() {
		System.out.println(ControlChannelPort);
		ChannelFuture f;
		try {
			f = controlServerBootstrap.bind(ControlChannelPort).sync();
			f.channel().closeFuture().sync();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Async
	public void startDataChannel() {
		System.out.println(DataChannelPort);
		ChannelFuture f;
		try {
			f = dataServerBootstrap.bind(DataChannelPort).sync();
			f.channel().closeFuture().sync();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
