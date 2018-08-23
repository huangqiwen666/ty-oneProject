package com.dascom.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;

import com.dascom.rabbitmq.MsgProducer;
import com.dascom.redis.RedisHandle;


/**
 * 异步调用接口服务打印绑定二维码
 * @author hqw
 *
 */
@Configuration
public class AsyncHttpUtil{

	private static final Logger log = LogManager.getLogger(AsyncHttpUtil.class);
	
	@Value(value ="${CloudPrint.printQRCode.url}")
	private String requestUrl;
	
	@Value(value ="${CloudPrint.informPrint.url}")
	private String informPrint;
	
	@Autowired
	private RedisHandle redisHandle;
	
	private String printQueue="printQueue";
	
	private String using="using";
	
	@Autowired
	private MsgProducer msgProducer;
	
	
	public void sendPrintInform(String number){
		Boolean hasKey = redisHandle.hasKey(printQueue+number);
		if (hasKey) {
			if (!redisHandle.hasKey(using+number)) {
//				log.info("number"+number+"，队列里面有打印任务，通知api接口大印");
//				sendInformQequest(number);
				msgProducer.sendMsg(number);
			}
		}
	}
	
	
	@Async
	public void sendInformQequest(String number){
		try {
			//请求的webservice的url
			log.info("number:{},通知api服务打印:{}",number,informPrint);
	        URL url= new URL(informPrint);
	        //创建http链接
	        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
	        //设置请求的方法类型
	        httpURLConnection.setRequestMethod("POST");
	        //设置请求的内容类型
//	        httpURLConnection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
	        //设置发送数据
	        httpURLConnection.setDoOutput(true);
	        //设置接受数据
	        httpURLConnection.setDoInput(true);
	        //发送数据,使用输出流
	        OutputStream outputStream = httpURLConnection.getOutputStream();
	        //发送的soap协议的数据
	        String content = "number="+number;
	        //发送数据
	        outputStream.write(content.getBytes());
	        
	        int responseCode = httpURLConnection.getResponseCode();
	        InputStream inputStream;
	        if (responseCode==200) {
	        	 //接收数据
	        	inputStream = httpURLConnection.getInputStream();
			}else {
				inputStream= httpURLConnection.getErrorStream();
			}
	        //定义字节数组
	        byte[] b = new byte[1024];
	        //定义一个输出流存储接收到的数据
	        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	        //开始接收数据
	        int len = 0;
	        while (true) {
	            len = inputStream.read(b);
	            if (len == -1) {
	                //数据读完
	                break;
	            }
	            byteArrayOutputStream.write(b, 0, len);
	        }
	        //从输出流中获取读取到数据(服务端返回的)
	        String response = byteArrayOutputStream.toString();
	        log.info("number:{}通知api服务打印返回:{}",number,response);
		} catch (Exception e) {
			StringBuffer sb=new StringBuffer();
			StackTraceElement[] stackTrace = e.getStackTrace();
			for (StackTraceElement stackTraceElement : stackTrace) {
				sb.append("     "+stackTraceElement.toString()+System.getProperty("line.separator"));
			}
			log.error("请求通知api服务打印错误：" + e.getMessage()+System.getProperty("line.separator")+"详细描述："+System.getProperty("line.separator")+sb.toString());
		}
	}
	
	
	
	@Async
	public void printQrcode(String number){
		try {
			 //请求的webservice的url
			log.info("number:{},请求打印二维码路径:{}",number,requestUrl);
	        URL url= new URL(requestUrl);
	        //创建http链接
	        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
	        //设置请求的方法类型
	        httpURLConnection.setRequestMethod("POST");
	        //设置请求的内容类型
//	        httpURLConnection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
	        //设置发送数据
	        httpURLConnection.setDoOutput(true);
	        //设置接受数据
	        httpURLConnection.setDoInput(true);
	        //发送数据,使用输出流
	        OutputStream outputStream = httpURLConnection.getOutputStream();
	        //发送的soap协议的数据
	        String content = "number="+number;
	        //发送数据
	        outputStream.write(content.getBytes());
	        
	        int responseCode = httpURLConnection.getResponseCode();
	        InputStream inputStream;
	        if (responseCode==200) {
	        	 //接收数据
	        	inputStream = httpURLConnection.getInputStream();
			}else {
				inputStream= httpURLConnection.getErrorStream();
			}
	        //定义字节数组
	        byte[] b = new byte[1024];
	        //定义一个输出流存储接收到的数据
	        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	        //开始接收数据
	        int len = 0;
	        while (true) {
	            len = inputStream.read(b);
	            if (len == -1) {
	                //数据读完
	                break;
	            }
	            byteArrayOutputStream.write(b, 0, len);
	        }
	        //从输出流中获取读取到数据(服务端返回的)
	        String response = byteArrayOutputStream.toString();
	        log.info("number:{}请求打印二维码返回:{}",number,response);
		} catch (Exception e) {
			StringBuffer sb=new StringBuffer();
			StackTraceElement[] stackTrace = e.getStackTrace();
			for (StackTraceElement stackTraceElement : stackTrace) {
				sb.append("     "+stackTraceElement.toString()+System.getProperty("line.separator"));
			}
			log.error("请求打印二维码错误：" + e.getMessage()+System.getProperty("line.separator")+"详细描述："+System.getProperty("line.separator")+sb.toString());
		}
		
	}

	
	
}
