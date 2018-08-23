package com.dascom.netty;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.dascom.netty.channel.ControlChannelInitializer;
import com.dascom.netty.channel.DataChannelInitializer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 控制通道启动器
 * @author hqw
 *
 */
@Configuration
@PropertySource("file:${user.dir}/config/netty.properties")
public class NettyServerConfig{

	@Value("${netty.SO_BACKLOG}")
	private Integer SO_BACKLOG;
	
	@Value("${netty.SO_KEEPALIVE}")
	private boolean SO_KEEPALIVE;
	
	
	@Autowired
	private ControlChannelInitializer controlChannelInitializer;
	
	@Autowired
	private DataChannelInitializer dataChannelInitializer;
	
	
//	private static final EventLoopGroup bossGroup = new NioEventLoopGroup();
	
//	private static final EventLoopGroup workerGroup = new NioEventLoopGroup();
	/**
	 * netty 控制通道启动方法
	 */
	@Bean(name = "controlServerBootstrap")
	public ServerBootstrap controlServerBootstrap() {
		System.out.println("------------------controlServerBootstrap---------------------------------");
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		ServerBootstrap b = new ServerBootstrap();
		b = b.group(bossGroup, workerGroup);
		b = b.channel(NioServerSocketChannel.class);
		b = b.childHandler(controlChannelInitializer);
		b = b.option(ChannelOption.SO_BACKLOG,SO_BACKLOG);
		b = b.childOption(ChannelOption.SO_KEEPALIVE,SO_KEEPALIVE);
//		ChannelFuture f = b.bind(ControlChannelPort).sync();
//		f.channel().closeFuture().sync();
		return b;
	}
	
	@Bean(name = "dataServerBootstrap")
	public ServerBootstrap dataServerBootstrap() {
		System.out.println("------------------dataServerBootstrap---------------------------------");
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		ServerBootstrap b = new ServerBootstrap();
		b = b.group(bossGroup, workerGroup);
		b = b.channel(NioServerSocketChannel.class);
		b = b.childHandler(dataChannelInitializer);
		b = b.option(ChannelOption.SO_BACKLOG,SO_BACKLOG);
		b = b.childOption(ChannelOption.SO_KEEPALIVE,SO_KEEPALIVE);
//		ChannelFuture f = b.bind(ControlChannelPort).sync();
//		f.channel().closeFuture().sync();
		return b;
	}
	
	
	/*@Bean(name = "bossGroup")
	public EventLoopGroup bossGroup() {
		return bossGroup;
	}

	@Bean(name = "workerGroup")
	public EventLoopGroup workerGroup() {
		return workerGroup;
	}
	*/
/*	
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}*/
	
}
