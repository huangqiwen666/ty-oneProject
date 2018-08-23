package com.dascom.entity;

import java.util.Date;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "collection_printers")
public class PrinterEntity {
	@Indexed(unique = true)
	private String number;//设备编号
	
	private Date reg_date;//设备注册时间，即编号创建时间
	private Date login_date;//设备登录时间

	private String alias;//别名
	
	private String owner;
    private PrinterAlert alert;//报警手段
	private PrinterStatus status;//打印机的状态信息
	private PrinterInfo info;//包含打印机的各种信息
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public Date getReg_date() {
		return reg_date;
	}
	public void setReg_date(Date reg_date) {
		this.reg_date = reg_date;
	}
	public Date getLogin_date() {
		return login_date;
	}
	public void setLogin_date(Date login_date) {
		this.login_date = login_date;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public PrinterAlert getAlert() {
		return alert;
	}
	public void setAlert(PrinterAlert alert) {
		this.alert = alert;
	}
	public PrinterStatus getStatus() {
		return status;
	}
	public void setStatus(PrinterStatus status) {
		this.status = status;
	}
	public PrinterInfo getInfo() {
		return info;
	}
	public void setInfo(PrinterInfo info) {
		this.info = info;
	}
		
}


