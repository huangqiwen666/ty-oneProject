package com.dascom.netty.message;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;


/**
 * 生成请求设备信息的类
 * @author hqw
 *
 */
public class GenerateMessageByte {
	
	private GenerateMessageByte() {
	}

	private static final GenerateMessageByte requestByte=new GenerateMessageByte();
	// 静态工厂方法
	public static GenerateMessageByte getSingletonContext() {
		return requestByte;
	}

	
	private static final byte[] versionNumber ={0x01,0};
	private static final byte[] messageMark ={0x40,0x41,0x2F,0x3F};//0X40412F3F
	private static final byte[] errorMessage ={0x00,0};
	private static final byte[] retainMessage ={0x00,0};
	
	
	/**
	 * 生成序列号数组
	 * @return
	 */
	public static byte[] getSerialNumber(){
		int nextInt = new Random().nextInt(2147483600);
		String hexString =Integer.toHexString(nextInt);
		while(hexString.length()<8){
			hexString ="0"+hexString;
		}
		int length = hexString.length() / 2;  
		char[] hexChars = hexString.toCharArray(); 
		byte[] d = new byte[length];
		
		for (int i = 0; i < length; i++) {   
			int pos = i * 2; 
			d[length-1-i] =(byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1])); 
		 }
		return d;
	}
	/**
	 * 生成message
	 * @param workNum 低位消息号   业务类型
	 * @param type 高位消息号   0x00：wifi  0x01： 设备
	 * @param serialNumber 随机number号
	 * @param messageBody  消息体
	 * @return
	 * @throws IOException
	 */
	public static byte[] getMessage(byte workNum,byte type,byte[] serialNumber,byte[] messageBody) throws IOException {
		ByteArrayOutputStream byteStream = null;
		DataOutputStream out = null ;
		try {
			byte[] messageBodyLength =byte4Length(Integer.toHexString(messageBody.length));//消息体长度
			byteStream = new ByteArrayOutputStream();
			out= new DataOutputStream(byteStream);
			out.write(messageMark);
			out.write(serialNumber);
			out.write(messageBodyLength);
			out.write(workNum);
			out.write(type);
			out.write(versionNumber);
			out.write(errorMessage);
			out.write(retainMessage);
			out.write(messageBody);
			out.flush();
			byte[] message = byteStream.toByteArray();  
			return message;
			
		} finally {
			if (byteStream!=null) byteStream.close();
			if (out!=null) out.close();
		}
	}

	/**
	 * 16进制的字符串 最终转化为4字节的数组,遵循小端排序
	 * @param str
	 * @return
	 */
	public static  byte[] byte4Length(String str){
	String hexString = str.toUpperCase(); 
	while(hexString.length()<8){
		hexString ="0"+hexString;
		}
		int length = hexString.length() / 2;  
		char[] hexChars = hexString.toCharArray(); 
		byte[] d = new byte[length];
		
		for (int i = 0; i < length; i++) {   
			int pos = i * 2;   
			d[length-1-i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1])); 
		 }  
		return d;
	}
		
	/**
	 * 转换
	 * @param c
	 * @return
	 */
	private static byte charToByte(char c) {   
		return (byte) "0123456789ABCDEF".indexOf(c);   
	}
	
	/**
	 * 16进制的字符串 最终转化为2字节的数组,遵循小端排序
	 * @param str
	 * @return
	 */
	public static  byte[] byte2Length(String str){
		String hexString = str.toUpperCase();
		while(hexString.length()<4){
			hexString ="0"+hexString;
		}
		int length = hexString.length() / 2;  
		char[] hexChars = hexString.toCharArray(); 
		byte[] d = new byte[length];   
		for (int i = 0; i < length; i++) {   
			int pos = i * 2;   
			d[length-1-i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1])); 
		 }  
		return d;
	}
}
