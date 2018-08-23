package com.dascom.mongodb;

import com.dascom.entity.PrintStatistics;

/**
 * 操作mongodb db_print_log库collection_print_statistics表的类
 * @author hqw
 *
 */
public interface PrintStatisticsDao {
	
	/**
	 * 根据id查询
	 * @param id
	 */
	PrintStatistics findOne(String id);
	
	/**
	 * 插入数据
	 * @param PrintStatistics
	 */
	void insert(PrintStatistics ps);
	
	/**
	 * 更新PrintStatistics对象
	 * @param ps
	 */
	void update(PrintStatistics ps);
	/**
	 * 增加打印成功的统计
	 * @param id
	 * @param succeed_page
	 */
//	void addSucceed_page(String id);
	
	/**
	 * 增加打印失败的统计
	 * @param id
	 * @param failure_page
	 */
//	void addFailure_page(String id);
}
