package com.dascom.netty.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dascom.entity.PrintStatistics;
import com.dascom.mongodb.PrintStatisticsDao;
import com.dascom.netty.service.PrintStatisticsService;

@Service
public class PrintStatisticsServiceImpl implements PrintStatisticsService {

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	
	@Autowired
	private PrintStatisticsDao printStatisticsDao;
	
	public void printFailureStatistics(String number) {
		String id=number+sdf.format(new Date());
		PrintStatistics ps = printStatisticsDao.findOne(id);
		if (ps==null) {
			ps=new PrintStatistics();
			ps.setNumber(number);
			ps.setSucceed_page(0);
			ps.setFailure_page(1);
			ps.setId(id);
			printStatisticsDao.insert(ps);
		}else {
			Integer failure_page = ps.getFailure_page()+1;
			ps.setFailure_page(failure_page);
			printStatisticsDao.update(ps);
		}
	}

	public void printSucceedStatistics(String number) {
		String id=number+sdf.format(new Date());
		PrintStatistics ps = printStatisticsDao.findOne(id);
		if (ps==null) {
			ps=new PrintStatistics();
			ps.setNumber(number);
			ps.setSucceed_page(1);
			ps.setFailure_page(0);
			ps.setId(id);
			printStatisticsDao.insert(ps);
		}else {
			Integer succeed_page = ps.getSucceed_page()+1;
			ps.setSucceed_page(succeed_page);
			printStatisticsDao.update(ps);
		}
	}
	
}
