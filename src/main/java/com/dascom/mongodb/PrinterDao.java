package com.dascom.mongodb;

import java.util.Date;
import java.util.List;

import com.dascom.entity.PrinterEntity;
import com.dascom.entity.PrinterInfo;

/**
 * 操作mongodb db_cloud_device库collection_printers表的类
 * @author hqw
 *
 */
public interface PrinterDao {

	/**
	 * 认证通过，更新设备信息
	 */
	public void update(String number,PrinterInfo info,Date date);
	
	/**
	 *  插入打印机信息
	 */
	void insert(PrinterEntity printer);
	
	/**
	 * 根据number查找设备表
	 * @retrun PrinterEntity
	 */
	PrinterEntity findByNumber(String number);
	
	/**
	 * @param number
	 * @param alive
	 * 根据number更新设备的状态
	 */
	void updateAlive(String number, boolean alive);
	
	/**
	 * 查询所有设备
	 * @return PrinterEntity的List集合
	 */
	List<PrinterEntity> findAllPrinterEntity();
	/**
	 * 查询owner所有的设备
	 * @param owner
	 * @return PrinterEntity的List集合
	 */
	List<PrinterEntity> findAllDevice(String owner);
	
	/**
	 * 查询所有在线设备
	 * @return
	 */
	List<PrinterEntity> findAllByAlive(Boolean alive);
	
	
	/**
	 * 根据条件返回设备集合
	 * @param number
	 * @param status
	 * @return
	 */
	public List<PrinterEntity> findAllCondition(String number);

	
	/**
	 * 根据number更新设备状态，并返回改对象
	 * @param number
	 * @param printerStatus
	 * @return
	 */
//	public PrinterEntity updateStatus(String number, PrinterStatus printerStatus);
	/**
	 *修改全部设备状态为离线
	 */
	public void updateAliveAll();
}
