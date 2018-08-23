package com.dascom.service;


import com.dascom.entity.PrinterEntity;

/**
 * 出现设备信息的接口
 * @author hqw
 *
 */
public interface DeviceService {
	
	/**
	 * 获取所有在线设备
	 * @return PrinterEntity
	 */
//	List<PrinterEntity> getOnLineDeviceList();

	/**
	 * 获取设备信息
	 * @param number
	 * @return
	 */
	PrinterEntity getPrinterEntity(String number);
	
	/**
	 * 根据条件返回设备集合
	 * @param number
	 * @param status
	 * @return
	 */
//	List<PrinterEntity> getPrinterEntityList(String number,String[] status);

	/**
	 * 判断设备是否可以打印
	 */
//	ResultVO<Object> judgeDevicePrint(String number);
	
	
	
	
}
