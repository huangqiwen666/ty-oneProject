package com.dascom.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



@Component
public class MsgProducer{
 
	
	@Autowired
    private RabbitTemplate rabbitTemplate;
 
    public void sendMsg(String number) {
        rabbitTemplate.convertAndSend(RabbitConfig.QUEUE_RESULT, number);
    }
}
