package com.dascom.netty.service;

import java.io.IOException;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 数据通道服务接口
 * @author hqw
 *
 */
public interface DataChannelService {
	/**
	 * 发送指令查询设备number
	 * @param ctx
	 * @throws IOException 
	 */
	void checkDeviceNumber(ChannelHandlerContext ctx) throws IOException;
	/**
	 * 数据通道释放资源
	 * @param ctx
	 */
	void freeResources(ChannelHandlerContext ctx);
	/**
	 * 获取到设备number，发送打印数据
	 * @param message
	 * @param ctx
	 * @throws Exception 
	 */
	void setDataChannelNumber(byte[] message, ChannelHandlerContext ctx) throws Exception;
	/**
	 * 数据通道长连接的心跳方法
	 * @param ctx
	 * @param e
	 */
	void heartbeat(ChannelHandlerContext ctx, IdleStateEvent e);
	/**
	 * 调用数据通道发送打印数据
	 * @param ctx
	 * @throws Exception
	 */
//	void sendPrintData(ChannelHandlerContext ctx,String number,String id) throws Exception;
	/**
	 * 透传打印处理方法
	 * @param ctx
	 * @throws IOException 
	 */
	public void passthroughPrint(ChannelHandlerContext ctx,byte[] message) throws Exception;
}
