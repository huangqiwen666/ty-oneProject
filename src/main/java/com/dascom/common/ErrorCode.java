package com.dascom.common;

public class ErrorCode {

	/**
	 * 服务错误1000
	 */
	public static Integer serviceError=1000;
	/**
	 * 参数错误1001
	 */
	public static Integer paramError=1001;
	/**
	 * 要写入wifi的用户信息过大1002
	 */
	public static Integer userMessageOutSize=1002;
	/**
	 * 打印机离线1010
	 */
	public static Integer printerOffline=1010;
	public static String printerOfflineDescribe="打印机离线";
	/**
	 * 打印机占用1011
	 */
	public static Integer printerUsing=1011;
	/**
	 * 操作异常1012
	 */
	public static Integer handleError=1012;
	public static String handleErrorDescribe="打印机离线";//暂时定义
	/**
	 * 操作超时1013
	 */
	public static Integer handleTimeout = 1013;
	public static String handleTimeoutDescribe="打印超时";
	/**
	 * 语音发送失败1014
	 */
	public static Integer reviewSendFailure=1014;
	/**
	 * 控制指令失败1015
	 */
	public static Integer controlFailure=1015;
	/**
	 * 读取wifi配置失败1016
	 */
	public static Integer readWifiConfigFailure=1016;
	/**
	 * 更新wifi配置失败1017
	 */
	public static Integer updataWifiConfigFailure=1017;
	
}
