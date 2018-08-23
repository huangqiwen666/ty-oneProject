package com.dascom.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 存储每天打印统计的实体
 * @author hqw
 */
@Document(collection = "collection_print_statistics")
public class PrintStatistics {

	@Id
	private String _id;
	
	@Indexed(unique = true)
	private String id;//number +date   例:number2018-04-19
	
	private String number;//number
	
	private Integer succeed_page;//成功次数
	
	private Integer failure_page;//失败次数
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public Integer getSucceed_page() {
		return succeed_page;
	}
	public void setSucceed_page(Integer succeed_page) {
		this.succeed_page = succeed_page;
	}
	public Integer getFailure_page() {
		return failure_page;
	}
	public void setFailure_page(Integer failure_page) {
		this.failure_page = failure_page;
	}
	
}
