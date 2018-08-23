package com.dascom.rabbitmq;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


@Component
@RabbitListener(queues ="printResult.messages")
public class MsgReceiver {
 
	private static final Logger log =LogManager.getLogger(MsgReceiver.class);
 
    @RabbitHandler
    public void process(String ss) {
    	log.info("printResult:"+ss);
    }
 
}
