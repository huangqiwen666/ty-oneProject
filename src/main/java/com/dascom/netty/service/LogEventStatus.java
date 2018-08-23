package com.dascom.netty.service;



import com.dascom.entity.PrinterStatus;

public interface LogEventStatus {

	
	void record(String number,PrinterStatus status);
	
}
