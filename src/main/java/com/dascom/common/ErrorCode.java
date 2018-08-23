package com.dascom.common;

public class ErrorCode {

	/**
	 * 服务错误
	 */
	public static Integer serviceError=1000;
	/**
	 * 参数错误
	 */
	public static Integer paramError=1001;
	/**
	 * 
	 */
	public static Integer userMessageOutSize=1002;
	/**
	 * 打印机离线
	 */
	public static Integer printerOffline=1010;
	/**
	 * 打印机占用
	 */
	public static Integer printerUsing=1011;
	/**
	 * 操作异常
	 */
	public static Integer handleError=1012;
	/**
	 * 打印超时
	 */
	public static Integer handleTimeout = 1013;
	/**
	 * 语音发送失败
	 */
	public static Integer reviewSendFailure=1014;
	/**
	 * 控制指令失败
	 */
	public static Integer controlFailure=1015;
	/**
	 * 读取wifi配置失败
	 */
	public static Integer readWifiConfigFailure=1016;
	/**
	 * 更新wifi配置失败
	 */
	public static Integer updataWifiConfigFailure=1017;
	
}
