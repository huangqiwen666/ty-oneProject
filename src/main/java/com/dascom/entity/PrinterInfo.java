package com.dascom.entity;

public class PrinterInfo {
	
	private String sn;// sn
	private String vendor;// 厂商
	private String model;// 型号
	private Integer dpi;// 宽度
//	private Integer YDPI;// 高度
	private Double paperWidth;// 可打印宽度
	private String simulation;//仿真
	
	
	private String wifi_model;
	private String wifi_protocol_version;
	private String wifi_firmware_version;
	private String wifi_mainboard_number;
	private String wifi_mac;
	private String wifi_ssid;
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public String getVendor() {
		return vendor;
	}
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public Integer getDpi() {
		return dpi;
	}
	public void setDpi(Integer dpi) {
		this.dpi = dpi;
	}
	
	
	public Double getPaperWidth() {
		return paperWidth;
	}
	public void setPaperWidth(Double paperWidth) {
		this.paperWidth = paperWidth;
	}
	public String getSimulation() {
		return simulation;
	}
	public void setSimulation(String simulation) {
		this.simulation = simulation;
	}
	public String getWifi_model() {
		return wifi_model;
	}
	public void setWifi_model(String wifi_model) {
		this.wifi_model = wifi_model;
	}
	public String getWifi_protocol_version() {
		return wifi_protocol_version;
	}
	public void setWifi_protocol_version(String wifi_protocol_version) {
		this.wifi_protocol_version = wifi_protocol_version;
	}
	public String getWifi_firmware_version() {
		return wifi_firmware_version;
	}
	public void setWifi_firmware_version(String wifi_firmware_version) {
		this.wifi_firmware_version = wifi_firmware_version;
	}
	public String getWifi_mainboard_number() {
		return wifi_mainboard_number;
	}
	public void setWifi_mainboard_number(String wifi_mainboard_number) {
		this.wifi_mainboard_number = wifi_mainboard_number;
	}
	public String getWifi_mac() {
		return wifi_mac;
	}
	public void setWifi_mac(String wifi_mac) {
		this.wifi_mac = wifi_mac;
	}
	public String getWifi_ssid() {
		return wifi_ssid;
	}
	public void setWifi_ssid(String wifi_ssid) {
		this.wifi_ssid = wifi_ssid;
	}
	

}
