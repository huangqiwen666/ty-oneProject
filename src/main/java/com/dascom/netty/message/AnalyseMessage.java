package com.dascom.netty.message;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dascom.entity.PrinterInfo;
import com.dascom.entity.WifiConfig;


/**
 * 解析数据的类
 * @author hqw
 *
 */
public class AnalyseMessage {

	
	private static final Logger log =LogManager.getLogger(AnalyseMessage.class);
	/**
	 * 取出数据的高低消息号 
	 * @return 如果message的消息号为某阶段的消息号返回true，反之false
	 */
	public static byte[] analyse_messagetype(byte[] message) {
		byte[] type=new byte[2];
		type[0]=message[12];// 拿到低位消息设备号
		type[1]=message[13];// 拿到高位消息设备号
		return type;
	}
	/**
	 * 取出消息体的指令
	 * @param index 需要解析消息的起始位置
	 * @param message 消息体
	 * @return 16进制的字符串
	 */
	public static String analyse_messageInstruction(Integer index,byte[] message) {
		StringBuffer sb=new StringBuffer();
		int v1 = (message[index]&0xff);
		int v2 = (message[index+1]&0xff);
		append16(sb,v1,v2);
		return sb.toString();
	}
	/**
	 * 解析数据通道message获取到设备number
	 * @param message
	 * @return
	 */
	public static String analyse_messageNumber(byte[] message) {
		StringBuffer sb=new StringBuffer();
		int length=message.length;
		for (int i = 20; i < length; i++) {
			int v = message[i] & 0xFF;
			append16(sb,v);
		}
		return sb.toString().toUpperCase();
	}
	
	
	
	private static void append16(StringBuffer sb,int... vs) {
		for (int v : vs) {
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				sb.append(0);
			}
			sb.append(hv);
		}
	}
	
	public static void byteToString16(StringBuffer sb,byte... vs) {
		for (byte b : vs) {
			int v=b&0xff;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				sb.append(0);
			}
			sb.append(hv);
		}
	}
	
	/**
	 * 返回认证阶段WiFi的信息
	 * @param message
	 * @return
	 */
	public static byte[] getIdentificationWifiByte(byte[] message) {
		int index=20;
		try {
			while (message.length>index) {
				int total=message[index+2]&0xff;
				int v1=message[index+4]&0xff;
				int v2=message[index+5]&0xff;
				log.debug("解析：getIdentificationWifiByte;v1={},v2={}",v1,v2);
				if (v1==0&&v2==128) {
					byte[] bs=new byte[total];
					System.arraycopy(message, index+4, bs, 0, total);
					return bs;
				}
				index+=total;
			}
		} catch (Exception e) {
			log.error("捕捉到异常,认证阶段解析错误:"+e.getMessage());
		}
		return null;
	}
	
	/**
	 * 解析WiFi信息，取出服务器所需要的信息info,number
	 * @param message
	 * @return
	 */
	public static Map<String, String> getDeviceLoginMassage(byte[] message) {
		Map<String, String> map=new HashMap<String, String>();
		StringBuffer sb=new StringBuffer();
		int index=0;
		int length = message.length;
		int cache=0;
		while (index<length) {
			int b = message[index]&0xff;
			index++;
			int t = message[index]&0xff;
			index++;
			if (b==0&&t==0x80) {
				//0	所有状态信息
				int num=0;
				num+=message[index]&0xff;
				index++;
				num+=message[index]&0xff<<8;
				index++;
				log.debug("wifi系统状态信息   "+"0	所有状态信息,数目num："+num);
			}else if (b>0&&t==0x01) {//01H	通用信息
				log.debug("01H_wifi通用信息");
				String split = new String(message,index,1);
				index++;
				String[] split2 = new String(message,index,b-3).split(split);
				index+=b-3;
//				for (String string : split2) {
//					log.debug(string);
//				}
				if (split2.length>6) {
					map.put("wifi_model", split2[0]);
					map.put("sn", split2[1]);
					map.put("wifi_protocol_version", split2[2]);
					map.put("wifi_firmware_version", split2[3]);
					map.put("wifi_mainboard_number", split2[4]);
					map.put("wifi_mac", split2[5]);
					map.put("wifi_ssid", split2[6]);
				}
			}else if (b>0&&t==0x02) {	//02H	系统信息
				log.debug("02H_wifi系统信息");
				for (int i = 0; i < 4; i++) {
					cache+=message[index]<<(3-i)*8;
					index++;
				}
				map.put("cache", cache+"");
				log.debug("wifi系统信息，缓存"+cache);
			}else if (b>0&&t==0x03) {//03H	用户信息
				log.debug("03H_wifi用户信息");
				for (int j = 0; j < b-2; j++) {
					int v = message[index] & 0xFF;
					index++;
					for (int i = 0; i < v; i++) {
						int v1 = message[index] & 0xFF;
						index++;
						j++;
						String hexString = Integer.toHexString(v1);
						if (hexString.length()<2) {
							hexString="0"+hexString;
						}
						sb.append(hexString);
					}
					sb.append(";");
				}
				String[] split = sb.toString().split(";");
				for (String string : split) {
					log.debug("用户信息"+string);
				}
				map.put("number", split[0].toUpperCase());
				map.put("device_type", split[1].toUpperCase());
				sb.setLength(0);
			}else {
				log.debug("--------------解析错误------------");
				break;
			}
		}
		return map;
	}
	
	
	/**
	 * 读取jar外  同一路径下config 的配置文件
	 * @param fileAddress
	 * @return
	 * @throws IOException
	 */
	public static Properties getProperties(String fileAddress) throws IOException{
		FileInputStream in = null;
		try {
			File file = new File(System.getProperty("user.dir")+"/config/"+fileAddress);
			in = new FileInputStream(file);
			Properties properties =new Properties();
			properties.load(in);
			in.close();
			return properties;
		}finally {
			if (in!=null) {
				in.close();
			}
		}
	}
	
	
	private static Properties properties;
	private static String Device="Device";
	static{
		try {
			properties=getProperties("device.properties");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 返回设备类型
	 * @param wifiMessage
	 * @return
	 * @throws IOException
	 */
	public static PrinterInfo getPrinterInfo(String number,Map<String, String> wifiMessage) throws IOException {
		PrinterInfo info =new PrinterInfo(); 
		info.setSn(wifiMessage.get("sn"));
		String wifi_firmware_version = wifiMessage.get("wifi_firmware_version");
//		if (wifi_firmware_version!=null&&wifi_firmware_version.endsWith(".")) {
//			wifi_firmware_version= wifi_firmware_version.substring(0, wifi_firmware_version.length()-1);
//		}
		String wifi_protocol_version = wifiMessage.get("wifi_protocol_version");
//		if (wifi_protocol_version!=null&&wifi_protocol_version.endsWith(".")) {
//			wifi_protocol_version= wifi_protocol_version.substring(0, wifi_protocol_version.length()-1);
//		}
		info.setWifi_firmware_version(wifi_firmware_version);
		info.setWifi_protocol_version(wifi_protocol_version);
		info.setWifi_model(wifiMessage.get("wifi_model"));
		info.setWifi_mac(wifiMessage.get("wifi_mac"));
		info.setWifi_ssid(wifiMessage.get("wifi_ssid"));
		info.setWifi_mainboard_number(wifiMessage.get("wifi_mainboard_number"));
		Integer type = Integer.parseInt(wifiMessage.get("device_type"),16);
		String model = properties.getProperty(Device+"."+type);
		if (model==null) {
			return null;
		}
		info.setModel(model);
		Integer dpi = Integer.parseInt(properties.getProperty(Device+"."+model+"."+"dpi"));
//		System.out.println(Device+"."+model+"."+"dpi");
//		System.out.println(dpi);
		info.setDpi(dpi);
		Double paperWidth=Double.parseDouble(properties.getProperty(Device+"."+model+"."+"paperWidth"));
		info.setPaperWidth(paperWidth);
		String simulation = properties.getProperty(Device+"."+model+"."+"simulation");
		info.setSimulation(simulation);
		
		return info;
	}
	/**
	 * 解析查询设备状态的message
	 * @param message
	 * @return 返回设备是否连接上打印机
	 */
	public static boolean heartbeatMessage(byte[] message){
		int index=message.length-6;
		int v=message[index]&0xff;
		if (v==0) {
			log.debug("usb_没有检测到任何设备");
			return false;
		}else if (v==1) {
			log.debug("usb_仅控制端口有设备连接");
			return false;
		}else if (v==2) {
			log.debug("usb_仅数据端口有设备连接");
		}else if (v==3) {
			log.debug("usb_控制端口和数据端口都已有设备连接");
		}
		return true;
	}
	/**
	 * 解析数据通道透传打印返回的message
	 * @param message
	 * @param index
	 * @return 返回设备缓存
	 */
	public static Integer analyseWifiCache(byte[] message,Integer index) {
		Integer cache1=0;
		Integer cache2=0;
		while (index<message.length) {
			int b = message[index]&0xff;
			index++;
			int t = message[index]&0xff;
			index++;
			if (b==0&&t==0x80) {
				//0	所有状态信息
				int num = 0;
				num+=message[index]&0xff;
				index++;
				num+=message[index]&0xff<<8;
				index++;
				log.debug("wifi系统状态信息   "+"0	所有状态信息,数目num："+num);
			}else if (b>0&&t==0x30) {//30H	系统状态
				log.debug("30H_wifi系统状态信息");
				index+=(b-2);
			}else if (b>0&&t==0x31) {	//31H	数据接收状态
				log.debug("31H_wifi数据接收状态");
				for (int i = 0; i < b-2; i++) {
					int v = message[index] & 0xFF;
					index++;
					if (5>i&&i>=1) {
						cache1+=v<<(4-i)*8;
					}
//					if (i==0) {
//						if (v==0) {
//							log.debug("可以接收数据");
//						}else if (v==1) {
//							log.debug("接收缓存已满");
//						}else {
//							log.debug("错误的参数,v="+v);
//						}
//					}else if (5>i&&i>=1) {
//						cache1+=v<<(4-i)*8;
//					}else if (9>i&&i>=5) {
//						cache2+=v<<(4-i)*8;
//					}
				}
				log.debug("接收缓冲中的剩余空间"+cache1);
				log.debug("接收缓冲已收数据的千分比"+cache2);
				return cache1;
			}else {
				log.debug("--------------解析可能出错了----------------");
				break;
			}
		}
		return cache1;
	}
	
	
	
	public static byte[] getWifiConfigtoByte(WifiConfig wifiConfig) {
		byte[] wifiConfigByte=new byte[228+2];
		wifiConfigByte[0]=0x10;
		wifiConfigByte[1]=0x18;
		wifiConfigByte[2]=(byte) ((wifiConfigByte.length-4)&0xff);
		//3
		wifiConfigByte[4]=0x00;
		wifiConfigByte[5]=0x00;
		wifiConfigByte[6]=(byte) 0xfe;
		wifiConfigByte[7]=0x01;
		wifiConfigByte[8]=0x00;
		wifiConfigByte[9]=(byte) 0xd8;
		wifiConfigByte[10]=(byte) 0x06;
		//11
		wifiConfigByte[12]=(byte) 0xff;
		wifiConfigByte[13]=(byte) 0xff;
		int index=14;
		String[] localIpAddress=wifiConfig.getLocalIpAddress().split("\\.");
		for (int i = localIpAddress.length-1; i >= 0; i--) {
			int v = Integer.parseInt(localIpAddress[i]);
			wifiConfigByte[index]=(byte) v;
			index++;
			
		}
		String[] localSubnetMask=wifiConfig.getLocalSubnetMask().split("\\.");
		for (int i = localSubnetMask.length-1; i >= 0; i--) {
			int v = Integer.parseInt(localSubnetMask[i]);
			wifiConfigByte[index]=(byte) v;
			index++;
			
		}
		String[] localGateway=wifiConfig.getLocalGateway().split("\\.");
		for (int i = localGateway.length-1; i >= 0; i--) {
			int v = Integer.parseInt(localGateway[i]);
			wifiConfigByte[index]=(byte) v;
			index++;
		}
		String[] remoteServerAddress=wifiConfig.getRemoteServerAddress().split("\\.");
		for (int i = remoteServerAddress.length-1; i >= 0; i--) {
			int v = Integer.parseInt(remoteServerAddress[i]);
			wifiConfigByte[index]=(byte) v;
			index++;
		}
		String[] mainDNSServerAddress=wifiConfig.getMainDNSServerAddress().split("\\.");
		for (int i = mainDNSServerAddress.length-1; i >= 0; i--) {
			int v = Integer.parseInt(mainDNSServerAddress[i]);
			wifiConfigByte[index]=(byte) v;
			index++;
		}
		String[] slaveDNSServerAddress=wifiConfig.getSlaveDNSServerAddress().split("\\.");
		for (int i = slaveDNSServerAddress.length-1; i >= 0; i--) {
			int v = Integer.parseInt(slaveDNSServerAddress[i]);
			wifiConfigByte[index]=(byte) v;
			index++;
		}
		byte[] udpPort = byte2Length(Integer.toHexString(wifiConfig.getUdpPort()));
		for (byte b : udpPort) {
			wifiConfigByte[index]=b;
			index++;
		}
		byte[] controlPort = byte2Length(Integer.toHexString(wifiConfig.getControlPort()));
		for (byte b : controlPort) {
			wifiConfigByte[index]=b;
			index++;
		}
		byte[] dataPort = byte2Length(Integer.toHexString(wifiConfig.getDataPort()));
		for (byte b : dataPort) {
			wifiConfigByte[index]=b;
			index++;
		}
		byte[] remoteRouteSsid=wifiConfig.getRemoteRouteSsid().getBytes();
		for (int i = 0; i <33; i++) {
			if (i<remoteRouteSsid.length) {
				wifiConfigByte[index]=remoteRouteSsid[i];
			}else {
				wifiConfigByte[index]=0;
			}
			index++;
		}
		byte[] remoteRoutePassword=wifiConfig.getRemoteRoutePassword().getBytes();
		for (int i = 0; i <65; i++) {
			if (i<remoteRoutePassword.length) {
				wifiConfigByte[index]=remoteRoutePassword[i];
			}else {
				wifiConfigByte[index]=0;
			}
			index++;
		}
		wifiConfigByte[index]=wifiConfig.getDhcp();
		index++;
		wifiConfigByte[index]=wifiConfig.getReconnectionInterval();
		index++;
		byte[] apModelSsid=wifiConfig.getApModelSsid().getBytes();
		for (int i = 0; i <22; i++) {
			if (i<apModelSsid.length) {
				wifiConfigByte[index]=apModelSsid[i];
			}else {
				wifiConfigByte[index]=0;
			}
			index++;
		}
		byte[] apModelPassword=wifiConfig.getApModelPassword().getBytes();
		for (int i = 0; i <16; i++) {
			if (i<apModelPassword.length) {
				wifiConfigByte[index]=apModelPassword[i];
			}else {
				wifiConfigByte[index]=0;
			}
			index++;
		}
		wifiConfigByte[index]=wifiConfig.getNetworkMode();
		index++;
		wifiConfigByte[index]=wifiConfig.getDataTransmissionSpeed();
		index++;
		wifiConfigByte[index]=wifiConfig.getNetworkChannel();
		index++;
		wifiConfigByte[index]=wifiConfig.getDns();
		index++;
		wifiConfigByte[index]=wifiConfig.getDeviceModel();
		index++;
		//保留
		index+=8;
		byte[] domainName=wifiConfig.getDomainName().getBytes();
		for (int i = 0; i <32; i++) {
			if (i<domainName.length) {
				wifiConfigByte[index]=domainName[i];
			}else {
				wifiConfigByte[index]=0;
			}
			index++;
		}
		
		
		int crc = 0;
		for (int i =14; i < wifiConfigByte.length; i++) {
			crc^=wifiConfigByte[i];
			for (int j = 0; j < 8; j++) {
				if ((crc&0x80)!=0) {
					crc=(crc<<1)^0x31;
				}else {
					crc<<=1;
				}
			}
		}
		wifiConfigByte[11]=(byte)(crc&0xff);
		
		int sum = 0;
		for(int k=4;k<wifiConfigByte.length;k++){
			sum+=(wifiConfigByte[k]&0xff);
		}
		wifiConfigByte[3]=(byte)(sum&0xff);
		return wifiConfigByte;
		
	}
	
	
	
	
	public static WifiConfig getWifiConfig(byte[] message,int index) {
		
		WifiConfig wifiConfig=new WifiConfig();
//		int index=20+4+8;
		StringBuffer sb =new StringBuffer();
		//获取本机ip
		sb.append((message[index+3]&0xff)+".");
		sb.append((message[index+2]&0xff)+".");
		sb.append((message[index+1]&0xff)+".");
		sb.append((message[index]&0xff));
		wifiConfig.setLocalIpAddress(sb.toString());
		sb.setLength(0);
		index+=4;
		
		
		//获取本机子网掩码
		sb.append((message[index+3]&0xff)+".");
		sb.append((message[index+2]&0xff)+".");
		sb.append((message[index+1]&0xff)+".");
		sb.append((message[index]&0xff));
		wifiConfig.setLocalSubnetMask(sb.toString());
		sb.setLength(0);
		index+=4;
		//获取本机网关
		sb.append((message[index+3]&0xff)+".");
		sb.append((message[index+2]&0xff)+".");
		sb.append((message[index+1]&0xff)+".");
		sb.append((message[index]&0xff));
		wifiConfig.setLocalGateway(sb.toString());
		sb.setLength(0);
		index+=4;
		//远程服务器地址
		sb.append((message[index+3]&0xff)+".");
		sb.append((message[index+2]&0xff)+".");
		sb.append((message[index+1]&0xff)+".");
		sb.append((message[index]&0xff));
		wifiConfig.setRemoteServerAddress(sb.toString());
		sb.setLength(0);
		index+=4;
		//主DNS服务器地址
		sb.append((message[index+3]&0xff)+".");
		sb.append((message[index+2]&0xff)+".");
		sb.append((message[index+1]&0xff)+".");
		sb.append((message[index]&0xff));
		wifiConfig.setMainDNSServerAddress(sb.toString());
		sb.setLength(0);
		index+=4;
		//备用DNS服务器地址
		sb.append((message[index+3]&0xff)+".");
		sb.append((message[index+2]&0xff)+".");
		sb.append((message[index+1]&0xff)+".");
		sb.append((message[index]&0xff));
		wifiConfig.setSlaveDNSServerAddress(sb.toString());
		sb.setLength(0);
		index+=4;
		//UDP服务端口号
		int udpPort=(message[index]&0xff)+((message[index+1]&0xff)<<8);
		wifiConfig.setUdpPort(udpPort);
		index+=2;
		//控制端口号
		int controlPort=(message[index]&0xff)+((message[index+1]&0xff)<<8);
		wifiConfig.setControlPort(controlPort);
		index+=2;
		//控制端口号
		int dataPort=(message[index]&0xff)+((message[index+1]&0xff)<<8);
		wifiConfig.setDataPort(dataPort);
		index+=2;
		//远程路由的ssid 即wifi名称
		String remoteRouteSsid=new String(message, index,33);
		wifiConfig.setRemoteRouteSsid(remoteRouteSsid.trim());
		index+=33;
		//远程路由的password 即wifi密码
		String remoteRoutePassword=new String(message, index,65);
		wifiConfig.setRemoteRoutePassword(remoteRoutePassword.trim());
		index+=65;
		//使能DHCP功能  0x00:无效 , 0x01:有效
		byte dhcp=message[index];
		wifiConfig.setDhcp(dhcp);
		index+=1;
		//重连服务器的间隔时间
		byte reconnectionInterval=message[index];
		wifiConfig.setReconnectionInterval(reconnectionInterval);
		index+=1;
		//本机AP模式网络ssid 即wifi固件名称
		String apModelSsid=new String(message, index,22);
		wifiConfig.setApModelSsid(apModelSsid.trim());
		index+=22;
		//本机AP模式网络密码  
		String apModelPassword=new String(message, index,16);
		wifiConfig.setApModelPassword(apModelPassword.trim());
		index+=16;
		//网络工作模式     0:station, 1:AP
		byte networkMode=message[index];
		wifiConfig.setNetworkMode(networkMode);
		index+=1;
		
		//数据端口数据传输速率    0x02：中速；    0x03：高速；    其他:慢速；
		byte dataTransmissionSpeed=message[index];
		wifiConfig.setDataTransmissionSpeed(dataTransmissionSpeed);
		index+=1;
		//网络通信通道
		byte networkChannel=message[index];
		wifiConfig.setNetworkChannel(networkChannel);
		index+=1;
		//dns功能  0x00:无效,0x01:有效
		byte dns=message[index];
		wifiConfig.setDns(dns);
		index+=1;
		//设备型号
		byte deviceModel=message[index];
		wifiConfig.setDeviceModel(deviceModel);
		index+=1;
		//预留
		index+=8;
		//服务器域名
		String domainName=new String(message, index,32);
		wifiConfig.setDomainName(domainName.trim());
		index+=32;
		return wifiConfig;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
//	该方法解析 获取设备所有状态 
//	public static Map<String, Integer> heartbeat(byte[] message,int index,int total){
//		Map<String, Integer> heartbeatmessage=new HashMap<String, Integer>();
//		Integer cache1=0;
//		Integer cache2=0;
//		while (index<total) {
//			int b = message[index]&0xff;
//			index++;
//			int t = message[index]&0xff;
//			index++;
//			if (b==0&&t==0x80) {
//				//0	所有状态信息
//				int num = 0;
//				num+=message[index]&0xff;
//				index++;
//				num+=message[index]&0xff<<8;
//				index++;
//				log.debug("wifi系统状态信息   "+"0	所有状态信息,数目num："+num);
//			}else if (b>0&&t==0x30) {//30H	系统状态
//				log.debug("30H_wifi系统状态信息");
//				for (int i = 0; i < b-2; i++) {
//					int v = message[index] & 0xFF;
//					index++;
//					if (i==1) {
//						if (v==0) {
//							log.debug("station 运行模式");
//						}else if (v==1) {
//							log.debug("AP运行模式");
//						}else {
//							log.debug("错误的参数");
//						}
//					}else if (i==2) {
//						if (v==0) {
//							log.debug("usb_没有检测到任何设备");
//							heartbeatmessage.put("wifi_usb", 0);
//						}else if (v==1) {
//							log.debug("usb_仅控制端口有设备连接");
//							heartbeatmessage.put("wifi_usb", 1);
//						}else if (v==2) {
//							log.debug("usb_仅数据端口有设备连接");
//							heartbeatmessage.put("wifi_usb", 2);
//						}else if (v==3) {
//							log.debug("usb_控制端口和数据端口都已有设备连接");
//							heartbeatmessage.put("wifi_usb", 3);
//						}
//					}else if (i==3) {
//						if (v==0) {
//							log.debug("错误号v=0,表示无错误");
//						}else {
//							log.debug("错误的参数,未定义");
//						}
//					}else if (i==4) {
//						if (v==0) {
//							log.debug("错误号v=0,表示无错误");
//						}else {
//							log.debug("错误的参数,未定义");
//						}
//					}else if (i==5) {
//						if (v==0) {
//							log.debug("udp端口未打开");
//						}else if (v==1) {
//							log.debug("udp端口正在打开");
//						}else if (v==2){
//							log.debug("udp端口已打开");
//						}else {
//							log.debug("错误的参数,v="+v);
//						}
//					}else if (i==6) {
//						if (v==0) {
//							log.debug("控制端口未打开");
//						}else if (v==1) {
//							log.debug("控制端口正在打开");
//						}else if (v==2){
//							log.debug("控制端口已打开");
//						}else {
//							log.debug("错误的参数,v="+v);
//						}
//					}else if (i==7) {
//						if (v==0) {
//							log.debug("数据端口未打开");
//							heartbeatmessage.put("dataPort", -1);
//						}else if (v==1) {
//							log.debug("数据端口正在打开");
//							heartbeatmessage.put("dataPort", 0);
//						}else if (v==2){
//							log.debug("数据端口已打开");
//							heartbeatmessage.put("dataPort",1);
//						}else {
//							log.debug("错误的参数,v="+v);
//							heartbeatmessage.put("dataPort", -2);
//						}
//					}
//				}
//			}else if (b>0&&t==0x31) {	//31H	数据接收状态
//				log.debug("31H_wifi数据接收状态");
//				
//				for (int i = 0; i < b-2; i++) {
//					int v = message[index] & 0xFF;
//					index++;
//					if (i==0) {
//						if (v==0) {
//							log.debug("可以接收数据");
//						}else if (v==1) {
//							log.debug("接收缓存已满");
//						}else {
//							log.debug("错误的参数,v="+v);
//						}
//					}else if (5>i&&i>=1) {
////						cache1+=v<<(i-1)*8;
//						cache1+=v<<(4-i)*8;
//					}else if (9>i&&i>=5) {
//						cache2+=v<<(4-i)*8;
//					}
//				}
//				
//				heartbeatmessage.put("cache", cache1);
//				log.debug("接收缓冲中的剩余空间"+cache1);
//				log.debug("接收缓冲已收数据的千分比"+cache2);
//			}else {
//				log.debug("--------------解析可能出错了----------------");
//				break;
//			}
//		}
//		return heartbeatmessage;
//	}
	
	
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
