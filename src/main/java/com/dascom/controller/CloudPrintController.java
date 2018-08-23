package com.dascom.controller;


import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.dascom.common.ErrorCode;
import com.dascom.common.ResultVO;
import com.dascom.common.utils.ResultVOUtil;
import com.dascom.common.utils.UsingComponent;
import com.dascom.entity.WifiConfig;
import com.dascom.service.CloudPrintService;


/**
 * 云打印Controller
 * @author hqw
 *
 */

@RestController
@RequestMapping("/v1.0")
public class CloudPrintController {
	
	
	private static final Logger log =LogManager.getLogger(CloudPrintController.class);
	
	@Autowired
	private CloudPrintService cloudPrintService;
	
	@Autowired
	private UsingComponent usingComponent;
	
	@Value(value = "${CloudPrint.print.Timeout}")
	private Integer printTimeout;
	
	@Value(value = "${CloudPrint.update.Timeout}")
	private Integer updateTimeout;
	
	@Value(value = "${CloudPrint.control.Timeout}")
	private Integer controlTimeout;
	
	/**
	 * 打印接口
	 * @param data
	 * @return
	 */
	@RequestMapping(value="/device/print" ,method=RequestMethod.POST,produces="application/json;charset=utf-8")
	public ResultVO<Object> print(@RequestBody JSONObject json,HttpServletResponse response){
		ResultVO<Object> resultVO = null;
		String number = null;
		try {
			number=json.getString("number");
			String id=json.getString("id");
			String data=json.getString("data");
			log.info("/device/print请求,number:{},id:{}",number,id);
//			StringUtils.isEmpty(str)
			if (StringUtils.isEmpty(number)||StringUtils.isEmpty(data)) {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				resultVO=ResultVOUtil.error(ErrorCode.paramError);
				return resultVO;
			}
			if (usingComponent.getAndSetUsing(number)) {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				resultVO=ResultVOUtil.error(ErrorCode.printerUsing);
				return resultVO;
			}
//			byte[] printData = Base64.getDecoder().decode(data);
//			RequestEntity re=new RequestEntity();
//			re.setData(printData);
			log.info("打印请求，number:{},ID:{}",number,id);
//			resultVO = cloudPrintService.print(number,id,printData);
			resultVO = cloudPrintService.print(number,id,data);
			
		} catch (Exception e) {
			StringBuffer sb=new StringBuffer();
			StackTraceElement[] stackTrace = e.getStackTrace();
			for (StackTraceElement stackTraceElement : stackTrace) {
				sb.append("      "+stackTraceElement.toString()+System.getProperty("line.separator"));
			}
			log.error("/device/print请求错误：" + e.getMessage()+System.getProperty("line.separator")+"详细描述："+System.getProperty("line.separator")+sb.toString());
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			resultVO=ResultVOUtil.error(ErrorCode.serviceError);
		}
		return resultVO;
	}
	
	@PostMapping("/device/update")
	public ResultVO<Object> updata(@RequestBody JSONObject json,HttpServletResponse response){
		ResultVO<Object> resultVO = null;
		String number = null;
		try {
			number=json.getString("number");
			String id=json.getString("id");
			String data=json.getString("data");
			log.info("/device/update请求,number:{},id:{}",number,id);
			
			if (StringUtils.isEmpty(number)||StringUtils.isEmpty(data)) {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				resultVO=ResultVOUtil.error(ErrorCode.paramError);
				return resultVO;
			}
			if (usingComponent.getAndSetUsing(number)) {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				resultVO=ResultVOUtil.error(ErrorCode.printerUsing);
				return resultVO;
			}
			
//			byte[] updateData = Base64.getDecoder().decode(data);
//			RequestEntity re=new RequestEntity();
//			re.setData(updateData);
			log.info("打印请求，number:{},ID:{}",number,id);
//			resultVO = cloudPrintService.update(number,id,updateData);
			resultVO = cloudPrintService.update(number,id,data);
		} catch (Exception e) {
			StringBuffer sb=new StringBuffer();
			StackTraceElement[] stackTrace = e.getStackTrace();
			for (StackTraceElement stackTraceElement : stackTrace) {
				sb.append("      "+stackTraceElement.toString()+System.getProperty("line.separator"));
			}
			log.error("/device/update请求错误：" + e.getMessage()+System.getProperty("line.separator")+"详细描述："+System.getProperty("line.separator")+sb.toString());
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			resultVO=ResultVOUtil.error(ErrorCode.serviceError);
		}
		return resultVO;
	}
	
	
	@RequestMapping(value="/device/review" ,method=RequestMethod.POST,produces="application/json;charset=utf-8")
	public ResultVO<Object> review(@RequestBody JSONObject json,HttpServletResponse response) {
		String number = null;
		ResultVO<Object> resultVO = null;
		try {
			number=json.getString("number");
			String id=json.getString("id");
			String data=json.getString("data");
			if (StringUtils.isEmpty(number)||StringUtils.isEmpty(data)) {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				resultVO=ResultVOUtil.error(ErrorCode.paramError);
				return resultVO;
			}
			if (usingComponent.getAndSetUsing(number)) {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				resultVO=ResultVOUtil.error(ErrorCode.printerUsing);
				return resultVO;
			}
			
//			byte[] sendData = data.getBytes("GB2312");
//			RequestEntity re=new RequestEntity();
//			re.setData(sendData);
			log.info("播放语音请求，number:{},ID:{}",number,id);
//			resultVO = cloudPrintService.print(number,id,printData);
			resultVO = cloudPrintService.review(number,id,data);
			
		} catch (Exception e) {
			StringBuffer sb=new StringBuffer();
			StackTraceElement[] stackTrace = e.getStackTrace();
			for (StackTraceElement stackTraceElement : stackTrace) {
				sb.append("      "+stackTraceElement.toString()+System.getProperty("line.separator"));
			}
			log.error("/device/review/请求错误：" + e.getMessage()+System.getProperty("line.separator")+"详细描述："+System.getProperty("line.separator")+sb.toString());
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			resultVO=ResultVOUtil.error(ErrorCode.serviceError);
		}
		return resultVO;
	}
	
	
	@RequestMapping(value="/device/control" ,method=RequestMethod.POST,produces="application/json;charset=utf-8")
	public ResultVO<Object> control(@RequestBody JSONObject json,HttpServletResponse response) {
		String number = null;
		ResultVO<Object> resultVO = null;
		try {
			number=json.getString("number");
			String id=json.getString("id");
			String data=json.getString("data");
			String controlType=json.getString("controlType");
			if (StringUtils.isEmpty(number)||StringUtils.isEmpty(data)||StringUtils.isEmpty(controlType)) {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				resultVO=ResultVOUtil.error(ErrorCode.paramError);
				return resultVO;
			}
			if (usingComponent.getAndSetUsing(number)) {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				resultVO=ResultVOUtil.error(ErrorCode.printerUsing);
				return resultVO;
			}
			
//			byte[] sendData = data.getBytes("GB2312");
//			RequestEntity re=new RequestEntity();
//			re.setData(sendData);
			log.info("控制请求，number:{},ID:{}",number,id);
//			resultVO = cloudPrintService.print(number,id,printData);
			resultVO = cloudPrintService.control(number,id,data,controlType);
			
		} catch (Exception e) {
			StringBuffer sb=new StringBuffer();
			StackTraceElement[] stackTrace = e.getStackTrace();
			for (StackTraceElement stackTraceElement : stackTrace) {
				sb.append("      "+stackTraceElement.toString()+System.getProperty("line.separator"));
			}
			log.error("/device/control 请求错误：" + e.getMessage()+System.getProperty("line.separator")+"详细描述："+System.getProperty("line.separator")+sb.toString());
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			resultVO=ResultVOUtil.error(ErrorCode.serviceError);
		}
		return resultVO;
	}
	
	
	
	@RequestMapping(value="/device/read/wifiConfig" ,method=RequestMethod.POST,produces="application/json;charset=utf-8")
	public ResultVO<Object> readWifiConfig(@RequestBody JSONObject json,HttpServletResponse response) {
		ResultVO<Object> resultVO = null;
		String number = null ;
		try {
			number = json.getString("number");
			String id = json.getString("id");
			if (StringUtils.isEmpty(number)) {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				resultVO=ResultVOUtil.error(ErrorCode.paramError);
				return resultVO;
			} 
			if (usingComponent.getAndSetUsing(number)) {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				resultVO=ResultVOUtil.error(ErrorCode.printerUsing);
				return resultVO;
			}
			resultVO= cloudPrintService.readWifiConfig(number, id);
//			if (resultVO.getCode()==0) {
//				byte[] responseMessage=(byte[]) resultVO.getData();
//				WifiConfig wifiConfig = AnalyseMessage.getWifiConfig(responseMessage,4+8);
//				resultVO.setData(wifiConfig);
//			}
			return resultVO;
		} catch (Exception e) {
			StringBuffer sb=new StringBuffer();
			StackTraceElement[] stackTrace = e.getStackTrace();
			for (StackTraceElement stackTraceElement : stackTrace) {
				sb.append("      "+stackTraceElement.toString()+System.getProperty("line.separator"));
			}
			log.error("/device/read/wifiConfig 请求错误：" + e.getMessage()+System.getProperty("line.separator")+"详细描述："+System.getProperty("line.separator")+sb.toString());
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			resultVO=ResultVOUtil.error(ErrorCode.serviceError);
		}
		return resultVO;
	}
	
	@RequestMapping(value="/device/updata/wifiConfig" ,method=RequestMethod.POST,produces="application/json;charset=utf-8")
	public ResultVO<Object> updataWifiConfig(@RequestBody JSONObject json,HttpServletResponse response) {
		ResultVO<Object> resultVO = null;
		String number = null;
		try {
			number = json.getString("number");
			String id = json.getString("id");
			WifiConfig wifiConfig=json.getObject("data", WifiConfig.class);
			if (StringUtils.isEmpty(number)||wifiConfig==null) {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				resultVO=ResultVOUtil.error(ErrorCode.paramError);
				return resultVO;
			} 
			if (usingComponent.getAndSetUsing(number)) {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				resultVO=ResultVOUtil.error(ErrorCode.printerUsing);
				return resultVO;
			}
			resultVO= cloudPrintService.updataWifiConfig(number, id, wifiConfig);
			return resultVO;
		} catch (Exception e) {
			StringBuffer sb=new StringBuffer();
			StackTraceElement[] stackTrace = e.getStackTrace();
			for (StackTraceElement stackTraceElement : stackTrace) {
				sb.append("      "+stackTraceElement.toString()+System.getProperty("line.separator"));
			}
			log.error("/device/updata/wifiConfig 请求错误：" + e.getMessage()+System.getProperty("line.separator")+"详细描述："+System.getProperty("line.separator")+sb.toString());
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			resultVO=ResultVOUtil.error(ErrorCode.serviceError);
		}
		return resultVO;
	}
	
	
	
	
}
