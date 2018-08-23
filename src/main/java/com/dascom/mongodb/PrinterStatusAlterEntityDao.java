package com.dascom.mongodb;

import com.dascom.entity.PrinterStatusAlterEntity;

/**
 * 操作mongodb db_cloud_device库collection_status_log表的类
 * @author hqw
 *
 */
public interface PrinterStatusAlterEntityDao {

	/**
	 * 根据id (id=number+data) 添加events的值
	 * @param number
	 * @param events
	 */
	void updeta(PrinterStatusAlterEntity printerStatusAlterEntity);
	/**
	 * 根据id查询
	 * @param id
	 * @return PrinterStatusAlter
	 */
	PrinterStatusAlterEntity findOne(String id);
	
	/**
	 * 插入一条数据
	 * @param printerStatusAlter
	 */
	void insert(PrinterStatusAlterEntity printerStatusAlterEntity);
}
