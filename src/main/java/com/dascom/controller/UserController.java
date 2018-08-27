package com.dascom.controller;


import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.dascom.common.ErrorCode;
import com.dascom.common.RedisKey;
import com.dascom.common.RequestEntity;
import com.dascom.common.ResultVO;
import com.dascom.common.utils.ResultVOUtil;
import com.dascom.common.utils.UsingComponent;
import com.dascom.redis.RedisHandle;
import com.dascom.service.UserService;

@RestController
@RequestMapping("/v1.0")
public class UserController {
	
	private static final Logger log =LogManager.getLogger(UserController.class);
	@Autowired
	private UserService userService;
	
	@Autowired
	private RedisHandle redisHandle;
	
	@Autowired
	private UsingComponent usingComponent;
	
	@Value(value = "${tcp.server.userMessageEncode}")
	private String userMessageEncode;
	
	@Value(value = "${CloudPrint.writeUser.Timeout}")
	private Integer writeUserTimeout;
	
	@RequestMapping(value="/device/writeUser" ,method=RequestMethod.POST,produces="application/json;charset=utf-8")
	public ResultVO<Object> writeUserMessage(@RequestBody JSONObject json,HttpServletResponse response) {
		ResultVO<Object> resultVO = null;
		String number=null;
		String id=null;
		String data=null;
		try {
			number = json.getString("number");
			id=json.getString("id");
			data = json.getString("data");
			
			if(StringUtils.isEmpty(number)||StringUtils.isEmpty(data)){
				resultVO=ResultVOUtil.error(ErrorCode.paramError);
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				return resultVO;
			}
//			byte[] userMessage=new byte[3583];
			byte[] bytes = data.getBytes(userMessageEncode);
			if (bytes.length>3583) {
				resultVO=ResultVOUtil.error(ErrorCode.userMessageOutSize);
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				return resultVO;
			}
			if (usingComponent.getAndSetUsing(number)) {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				resultVO=ResultVOUtil.error(ErrorCode.printerUsing);
				return resultVO;
			}
			
			RequestEntity re=new RequestEntity();
			re.setData(bytes);
			resultVO = userService.writeUserMessage(number, id, re);
			if (resultVO.getCode()==0) {
				redisHandle.set(RedisKey.RESTRICTPRINT+number, data);
			}
		}catch (Exception e) {
			StringBuffer sb=new StringBuffer();
			StackTraceElement[] stackTrace = e.getStackTrace();
			for (StackTraceElement stackTraceElement : stackTrace) {
				sb.append("      "+stackTraceElement.toString()+System.getProperty("line.separator"));
			}
			log.error("/device/writeUser请求错误：" + e.getMessage()+System.getProperty("line.separator")+"详细描述："+System.getProperty("line.separator")+sb.toString());
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			resultVO=ResultVOUtil.error(ErrorCode.serviceError);
		}
		return resultVO;
	}
	
	
}
