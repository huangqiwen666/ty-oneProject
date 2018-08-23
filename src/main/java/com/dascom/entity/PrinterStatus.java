package com.dascom.entity;

import java.util.ArrayList;
import java.util.Date;

public class PrinterStatus {

	private String main;//主状态
	private ArrayList<String> sub;//子状态
	private Date newest;//操作时间
	public String getMain() {
		return main;
	}
	public void setMain(String main) {
		this.main = main;
	}
	
	public ArrayList<String> getSubs() {
		return sub;
	}
	public void setSubs(ArrayList<String> subs) {
		this.sub = subs;
	}
	public Date getNewest() {
		return newest;
	}
	public void setNewest(Date newest) {
		this.newest = newest;
	}


}
