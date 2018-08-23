//package com.dascom.rabbitmq;
//
//
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.springframework.amqp.rabbit.annotation.RabbitHandler;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//
//import com.alibaba.fastjson.JSONObject;
//import com.dascom.async.AsyncPrintQueueTask;
//import com.dascom.common.utils.UsingComponent;
//import com.dascom.entity.PrinterStatus;
//import com.dascom.redis.RedisHandle;
//
//
//@Component
//@RabbitListener(queues ="printRequest.message")
//public class MsgReceiverPrintRequest {
// 
//	private static final Logger log =LogManager.getLogger(MsgReceiverPrintRequest.class);
//	
//	@Autowired
//	private UsingComponent usingComponent;
//	
//	@Autowired
//	private AsyncPrintQueueTask asyncPrintQueueTask;
//	
//	@Autowired
//	private RedisHandle redisHandle;
// 
//    @RabbitHandler
//    public void process(String number) {
//    	
//    	log.info("printResult:"+number);
//    	boolean andSetUsing = usingComponent.getAndSetUsing(number);
//		if (!andSetUsing) {
//			String ps = redisHandle.get("status"+number);
//			
//			if (!StringUtils.isEmpty(ps)) {
//				PrinterStatus status =JSONObject.parseObject(ps, PrinterStatus.class);
//				if ("ready".equals(status.getMain())||"warn".equals(status.getMain())) {
//					asyncPrintQueueTask.print(number);
//				}
//			}
//		}
//    }
// 
//}
