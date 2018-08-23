package com.dascom.entity;


/**
 * 描述:
 *
 * @author pwt
 * @create 2018-08-08 11:16
 */
public class WifiConfig {
	//本机IP地址
    private String localIpAddress;
    //本机子网掩码
    private String localSubnetMask;
    //本机网关
    private String localGateway;
    //远程服务器地址
    private String remoteServerAddress;
    //主DNS服务器地址
    private String mainDNSServerAddress;
    //备用DNS服务器地址
    private String slaveDNSServerAddress;
    //UDP服务端口号
    private Integer udpPort;
    //控制端口
    private Integer controlPort;
    //数据端口
    private Integer dataPort;
    //远程路由的ssid 即wifi名称
    private String remoteRouteSsid;
    //远程路由的password 即wifi密码
    private String remoteRoutePassword;
    //使能DHCP功能  0x00:无效 , 0x01:有效
    private byte dhcp;
    //重连服务器的间隔时间
    private byte reconnectionInterval;
    //本机AP模式网络ssid 即wifi固件名称
    private String apModelSsid;
    //本机AP模式网络密码  
    private String apModelPassword;
    //网络工作模式     0:station, 1:AP
    private byte networkMode;
    //数据端口数据传输速率    0x02：中速；    0x03：高速；    其他:慢速；
    private byte dataTransmissionSpeed;
    //网络通信通道
    private byte networkChannel;
    //dns功能  0x00:无效,0x01:有效
    private byte dns;
    //设备型号
    private byte deviceModel;
    //服务器域名
    private String domainName;
    
    
    
    
	public String getLocalIpAddress() {
		return localIpAddress;
	}
	public void setLocalIpAddress(String localIpAddress) {
		this.localIpAddress = localIpAddress;
	}
	public String getLocalSubnetMask() {
		return localSubnetMask;
	}
	public void setLocalSubnetMask(String localSubnetMask) {
		this.localSubnetMask = localSubnetMask;
	}
	public String getLocalGateway() {
		return localGateway;
	}
	public void setLocalGateway(String localGateway) {
		this.localGateway = localGateway;
	}
	public String getRemoteServerAddress() {
		return remoteServerAddress;
	}
	public void setRemoteServerAddress(String remoteServerAddress) {
		this.remoteServerAddress = remoteServerAddress;
	}
	public String getMainDNSServerAddress() {
		return mainDNSServerAddress;
	}
	public void setMainDNSServerAddress(String mainDNSServerAddress) {
		this.mainDNSServerAddress = mainDNSServerAddress;
	}
	public String getSlaveDNSServerAddress() {
		return slaveDNSServerAddress;
	}
	public void setSlaveDNSServerAddress(String slaveDNSServerAddress) {
		this.slaveDNSServerAddress = slaveDNSServerAddress;
	}
	public Integer getUdpPort() {
		return udpPort;
	}
	public void setUdpPort(Integer udpPort) {
		this.udpPort = udpPort;
	}
	public Integer getControlPort() {
		return controlPort;
	}
	public void setControlPort(Integer controlPort) {
		this.controlPort = controlPort;
	}
	public Integer getDataPort() {
		return dataPort;
	}
	public void setDataPort(Integer dataPort) {
		this.dataPort = dataPort;
	}
	public String getRemoteRouteSsid() {
		return remoteRouteSsid;
	}
	public void setRemoteRouteSsid(String remoteRouteSsid) {
		this.remoteRouteSsid = remoteRouteSsid;
	}
	public String getRemoteRoutePassword() {
		return remoteRoutePassword;
	}
	public void setRemoteRoutePassword(String remoteRoutePassword) {
		this.remoteRoutePassword = remoteRoutePassword;
	}
	
	
	public byte getDhcp() {
		return dhcp;
	}
	public void setDhcp(byte dhcp) {
		this.dhcp = dhcp;
	}
	public byte getReconnectionInterval() {
		return reconnectionInterval;
	}
	public void setReconnectionInterval(byte reconnectionInterval) {
		this.reconnectionInterval = reconnectionInterval;
	}
	public String getApModelSsid() {
		return apModelSsid;
	}
	public void setApModelSsid(String apModelSsid) {
		this.apModelSsid = apModelSsid;
	}
	public String getApModelPassword() {
		return apModelPassword;
	}
	public void setApModelPassword(String apModelPassword) {
		this.apModelPassword = apModelPassword;
	}
	public byte getNetworkMode() {
		return networkMode;
	}
	public void setNetworkMode(byte networkMode) {
		this.networkMode = networkMode;
	}
	public byte getDataTransmissionSpeed() {
		return dataTransmissionSpeed;
	}
	public void setDataTransmissionSpeed(byte dataTransmissionSpeed) {
		this.dataTransmissionSpeed = dataTransmissionSpeed;
	}
	public byte getNetworkChannel() {
		return networkChannel;
	}
	public void setNetworkChannel(byte networkChannel) {
		this.networkChannel = networkChannel;
	}
	public byte getDns() {
		return dns;
	}
	public void setDns(byte dns) {
		this.dns = dns;
	}
	public byte getDeviceModel() {
		return deviceModel;
	}
	public void setDeviceModel(byte deviceModel) {
		this.deviceModel = deviceModel;
	}
	public String getDomainName() {
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	@Override
	public String toString() {
		return "WifiConfig [localIpAddress=" + localIpAddress + ", localSubnetMask=" + localSubnetMask
				+ ", localGateway=" + localGateway + ", remoteServerAddress=" + remoteServerAddress
				+ ", mainDNSServerAddress=" + mainDNSServerAddress + ", slaveDNSServerAddress=" + slaveDNSServerAddress
				+ ", udpPort=" + udpPort + ", controlPort=" + controlPort + ", dataPort=" + dataPort
				+ ", remoteRouteSsid=" + remoteRouteSsid + ", remoteRoutePassword=" + remoteRoutePassword + ", dhcp="
				+ dhcp + ", reconnectionInterval=" + reconnectionInterval + ", apModelSsid=" + apModelSsid
				+ ", apModelPassword=" + apModelPassword + ", networkMode=" + networkMode + ", dataTransmissionSpeed="
				+ dataTransmissionSpeed + ", networkChannel=" + networkChannel + ", dns=" + dns + ", deviceModel="
				+ deviceModel + ", domainName=" + domainName + "]";
	}
    


}
