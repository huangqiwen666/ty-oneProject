//package com.dascom.controller;
//
//import java.util.List;
//
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.dascom.common.ResultVO;
//import com.dascom.common.utils.ResultVOUtil;
//import com.dascom.entity.PrinterEntity;
//import com.dascom.service.DeviceService;
///**
// * 显示打印页面
// * @author hqw
// *
// */
//@RestController
//public class DeviceController {
//
//	private static final Logger log =LogManager.getLogger(DeviceController.class);
//	
//	@Autowired
//	private DeviceService deviceService;
//	
////	@GetMapping("/device/find/print")
////    public ResultVO<Object> getPrinterEntityList(String number) {
////		log.debug("获取可打印的设备");
////		ResultVO<Object> resultVO;
////		try {	
////			deviceService.getPrinterEntity(number);
////			
////		}catch (Exception e) {
////			
////		}
////		
////    	return list;
////    }
//	
////	@GetMapping("/device/find/condition")
////	public ResultVO<Object> showPrintPage(String number,String[] status) {
////		log.info("/device/find/condition,data:{},{}",number,status);
////		ResultVO<Object> resultVO;
////		try {
////			List<PrinterEntity> ps = deviceService.getPrinterEntityList(number,status);
////			resultVO=ResultVOUtil.success(ps);
////		} catch (Exception e) {
////			StringBuffer sb=new StringBuffer();
////			StackTraceElement[] stackTrace = e.getStackTrace();
////			for (StackTraceElement stackTraceElement : stackTrace) {
////				sb.append("      "+stackTraceElement.toString()+System.getProperty("line.separator"));
////			}
////			log.error("/device/find/condition请求错误：" + e.getMessage()+System.getProperty("line.separator")+"详细描述："+System.getProperty("line.separator")+sb.toString());
////			resultVO=ResultVOUtil.error(3000);
////		}
////		return resultVO;
////	}
//}
