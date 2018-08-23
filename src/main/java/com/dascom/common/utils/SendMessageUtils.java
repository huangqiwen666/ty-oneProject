package com.dascom.common.utils;


import java.io.IOException;

import com.dascom.netty.message.GenerateMessageByte;

public class SendMessageUtils {

	
	/**
	 * 开启数据通道
	 * @return
	 * @throws IOException
	 */
	public static byte[] startDataChannel() throws IOException {
		byte[] messageBody=new byte[0];
		byte[] serialNumber = {0,0,3,0};
		byte[] req=GenerateMessageByte.getMessage((byte)0x03,(byte)0x00,serialNumber,messageBody);
		return req;
	}
	/**
	 * 数据通道查询设备缓存
	 * @return
	 * @throws IOException
	 */
	public static byte[] findDeviceCache() throws IOException {
		byte[] sendMessage=new byte[0];
		byte[] serialNumber = {0,0,2,1};
		byte[] req=GenerateMessageByte.getMessage((byte)0x02,(byte)0x01,serialNumber, sendMessage);//透传打印0x09
		return req;
	}
	/**
	 * 进入读写用户信息指令
	 * @return
	 * @throws IOException
	 */
	public static byte[] readWriteUserMessage() throws IOException {
		byte[] sendMessage=new byte[4];
		sendMessage[0]=0x10;
		sendMessage[1]=0x64;
		sendMessage[2]=0;
		sendMessage[3]=0;
		byte[] req=GenerateMessageByte.getMessage((byte)0x05,(byte)0x00,sendMessage, sendMessage);//透传打印0x09
		return req;
	}
	
	/**
	 * 重启wifi指令
	 * @return
	 * @throws IOException
	 */
	public static byte[] restartWifi() throws IOException {
		byte[] sendMessage=new byte[4];
		sendMessage[0]=0x10;
		sendMessage[1]=0x11;
		sendMessage[2]=0;
		sendMessage[3]=0;
		byte[] req=GenerateMessageByte.getMessage((byte)0x05,(byte)0x00,sendMessage, sendMessage);//透传打印0x09
		return req;
	}
	/**
	 * 退出更新模式指令
	 * @return
	 * @throws IOException
	 */
	public static byte[] exitUpdata() throws IOException {
		byte worknum=0x05;
		byte type= 0x00;
		byte[] serialNumber ={0,0,5,0};
		byte[] messageBody = {0x10, 0x16, 01, 01,01};
		byte[] req=GenerateMessageByte.getMessage(worknum,type,serialNumber,messageBody);
		return req;
	}
	
	
}
