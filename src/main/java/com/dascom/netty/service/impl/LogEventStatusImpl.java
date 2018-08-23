package com.dascom.netty.service.impl;

import java.text.SimpleDateFormat;
import java.util.LinkedList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dascom.entity.PrinterStatus;
import com.dascom.entity.PrinterStatusAlterEntity;
import com.dascom.mongodb.PrinterStatusAlterEntityDao;
import com.dascom.netty.service.LogEventStatus;

@Service
public class LogEventStatusImpl implements LogEventStatus {

	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	
	@Autowired
	private PrinterStatusAlterEntityDao printerStatusAlterEntityDao;
	
	/**
	 * 设备状态变更信息存储方法
	 * @param number
	 * @param alive 设备此时的状态
	 */
	public void record(String number,PrinterStatus status){
		String id=number+sdf.format(status.getNewest());
		PrinterStatusAlterEntity psae = printerStatusAlterEntityDao.findOne(id);
		if (psae==null) {
			psae=new PrinterStatusAlterEntity();
			psae.setId(id);
			LinkedList<PrinterStatus> linkedList = new LinkedList<PrinterStatus>();
			linkedList.addFirst(status);
			psae.setEvents(linkedList);
			printerStatusAlterEntityDao.insert(psae);
		}else {
			LinkedList<PrinterStatus> events = psae.getEvents();
			events.addFirst(status);
			psae.setEvents(events);
			printerStatusAlterEntityDao.updeta(psae);
		}
	}

}
