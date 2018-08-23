package com.dascom.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dascom.entity.PrinterEntity;
import com.dascom.mongodb.PrinterDao;
import com.dascom.service.DeviceService;


@Service
public class DeviceServiceImpl implements DeviceService {

	@Autowired
	private PrinterDao printerDao;
	

	public PrinterEntity getPrinterEntity(String number) {
		PrinterEntity pe = printerDao.findByNumber(number);
		return pe;
	}

//	@Override
//	public List<PrinterEntity> getPrinterEntityList(String number,String[] status) {
//		List<PrinterEntity> list = printerDao.findAllCondition(number);
//		for (PrinterEntity printerEntity : list) {
//			String number2 = printerEntity.getNumber();
//			String hget = redisHandle.hget("status", number2);
//			if (StringUtils.isEmpty(hget)) {
//				list.remove(printerEntity);
//			}else {
//				PrinterStatus ps = JSONObject.parseObject(hget, PrinterStatus.class);
//				boolean mark=true;
//				for (String s : status) {
//					if (ps.getMain().equals(s)) {
//						mark=false;
//					}
//				}
//				if (mark) {
//					list.remove(printerEntity);
//				}
//			}
//		}
//		return list;
//	}

	

//	@Override
//	public ResultVO<Object> judgeDevicePrint(String number) {
//		ResultVO<Object> resultVO;
//		PrinterEntity pe = printerDao.findByNumber(number);
//		PrinterStatus status = pe.getStatus();
//		String main;
//		if (status!=null) {
//			main=status.getMain();
//		}
//		if ("") {
//			
//		}
//		return null;
//	}

}
