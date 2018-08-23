package com.dascom.netty.message;
/**
 * 验证消息头的类
 * @author hqw
 *
 */
public class VerifyMessage {

	/**
	 * 验证设备发送过来的消息头的方法
	 * @return 如果消息头为0X40412F3F返回true,反之false
	 */
	public static boolean verify_messagehead(byte[] message) {
		boolean verify=true;
		verify= (message[0] & 0xFF)==0x40&&(message[1] & 0xFF)==0x41&&(message[2] & 0xFF)==0x2f&&(message[3] & 0xFF)==0x3f;
		return verify;
	}
	/**
	 * 对比设备发送过来的序列号的方法
	 * @return 如果设备应答的序列号与服务器请求的序列号相同则返回true，反之false
	 */
	public static boolean verify_message_serialNumber(byte[] message,byte[] serialNumber) {
		boolean verify=true;
		for (int i = 0; i < serialNumber.length; i++) {
			if (message[4+i]!=serialNumber[i]) {
				return false;
			}
		}
		return verify;
	}
	
	/**
	 * 对比设备发送过来的序列号的方法
	 * @return 如果设备应答的序列号与服务器请求的序列号相同则返回true，反之false
	 */
	public static boolean verify_message_serialNumber(byte[] message) {
		boolean verify=true;
		for (int i = 0; i < 4; i++) {
			if ((message[4+i]&0xff)!=0xff) {
				return false;
			}
		}
		return verify;
	}
}
