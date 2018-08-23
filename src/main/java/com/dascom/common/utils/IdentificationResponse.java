package com.dascom.common.utils;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.dascom.netty.message.GenerateMessageByte;
/**
 * 设备认证Response的类
 * @author DS020
 *
 */
@Component
@PropertySource("file:${user.dir}/config/netty.properties")
public class IdentificationResponse {

	@Value("${netty.ControlChannelReadTime}")
	private Integer ControlChannelReadTime;
	
	
	private byte[] serialNumber ={0,0,0,0};
	/**
	 * 传入超时时间，生成回复device认证成功的byte[]
	 * @param heartbeat 
	 * @throws IOException
	 */
	public byte[] verifySucceed() throws IOException {
		byte[] byte2Length = GenerateMessageByte.byte2Length(Integer.toHexString(ControlChannelReadTime));
		byte[] messageBody={0x00,0x00,byte2Length[0],byte2Length[1]};
		byte[] message = GenerateMessageByte.getMessage((byte) 1, (byte) 2, serialNumber, messageBody);
		return message;
	}
	/**
	 * 设备未注册
	 * @throws IOException
	 */
	public byte[] unregistered() throws IOException {
//		byte[] serialNumber ={0,0,0,1};
		byte[] messageBody={0x01,0x00,0,0};
		byte[] message = GenerateMessageByte.getMessage((byte) 1, (byte) 2, serialNumber, messageBody);
		return message;
	}
	/**
	 *  设备信息获取失败
	 * @throws IOException
	 */
	public byte[] messageFailure() throws IOException {
//		byte[] serialNumber ={0,0,0,1};
		byte[] messageBody={0x02,0x00,0,0};
		byte[] messaage = GenerateMessageByte.getMessage((byte) 1, (byte) 2, serialNumber, messageBody);
		return messaage;
	}
	/**
	 * 设备已经登录
	 * @return
	 * @throws IOException
	 */
	public byte[] alreadyLogin() throws IOException {
//		byte[] serialNumber ={0,0,0,1};
		byte[] messageBody={0x03,0x00,0,0};
		byte[] messaage = GenerateMessageByte.getMessage((byte) 1, (byte) 2, serialNumber, messageBody);
		return messaage;
	}
	/**
	 * 其他错误
	 * @return
	 * @throws IOException
	 */
	public byte[] elseError() throws IOException {
//		byte[] serialNumber ={0,0,0,1};
		byte[] messageBody={0x04,0x00,0,0};
		byte[] messaage = GenerateMessageByte.getMessage((byte) 1, (byte) 2, serialNumber, messageBody);
		return messaage;
	}

	
}
