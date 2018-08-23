package com.dascom.netty.service;

import java.io.IOException;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
/**
 * 控制通道服务接口
 * @author hqw
 *
 */
public interface ControlChannelService {

	/**
	 * 通道关闭时释放资源
	 * @param ctx
	 */
	void freeResources(ChannelHandlerContext ctx);
	/**
	 * 控制通道收到查询WiFi状态治疗了1009执行的方法
	 * @param ctx
	 * @param message
	 */
	void saveDeviceStatus(ChannelHandlerContext ctx, byte[] message);
	/**
	 * 控制通道设备认证方法
	 * @param message
	 * @param ctx
	 * @throws IOException
	 */
	void identificationHandle(byte[] message, ChannelHandlerContext ctx) throws IOException;
	/**
	 * netty 心跳方法触发执行的方法
	 * @param ctx
	 * @param e 
	 * @throws IOException 
	 */
	void heartbeat(ChannelHandlerContext ctx, IdleStateEvent e) throws IOException;
	/**
	 * wifi模块更新模式返回
	 * @param ctx
	 * @param message
	 * @throws IOException 
	 */
	void updatePattern(ChannelHandlerContext ctx, byte[] message) throws IOException;
	
	/**
	 * 重启WiFi模块成功
	 * @param ctx
	 * @throws InterruptedException 
	 */
	void restartWifiSucceed(ChannelHandlerContext ctx) throws InterruptedException;
	
	/**
	 * 发送更新数据
	 * @param ctx
	 * @throws IOException 
	 */
	void sendUpdate(ChannelHandlerContext ctx) throws IOException;
	
	/**
	 * 更新数据发送完毕，退出固件更新模式
	 * @param ctx
	 * @throws IOException 
	 */
	void exitUpdate(ChannelHandlerContext ctx) throws IOException;
	/**
	 * 进入读取用户信息状态
	 * @param ctx
	 * @param message
	 * @throws IOException
	 */
	public void comeUserMessage(ChannelHandlerContext ctx, byte[] message) throws IOException;
	/**
	 * 设置用户信息并回复前端
	 * @param ctx
	 * @param message
	 * @throws IOException 
	 */
	void setUserMessage(ChannelHandlerContext ctx, byte[] message) throws IOException;
	
	/**
	 * 读取用户信息
	 * @param ctx
	 * @param message
	 * @throws IOException
	 */
	void getUserMessage(ChannelHandlerContext ctx, byte[] message)throws IOException;
	/**
	 * 播放语音
	 * @param ctx
	 * @param message
	 * @throws IOException
	 */
	void review(ChannelHandlerContext ctx, byte[] message)throws IOException;
	/**
	 * 控制指令返回
	 * @param ctx
	 * @param message
	 */
	void controlInstruct(ChannelHandlerContext ctx, byte[] message);

	/**
	 * 读取wifi固件的配置
	 * @param ctx
	 * @param message
	 */
	void readWifiConfig(ChannelHandlerContext ctx, byte[] message);
	/**
	 * 更新wifi固件配置的返回
	 * @param ctx
	 * @param message
	 */
	void updataWifiConfig(ChannelHandlerContext ctx, byte[] message);
	/**
	 * 异步打印二维码
	 * @param ctx
	 */
	void asyncPrintQRcode(ChannelHandlerContext ctx);
	
}
