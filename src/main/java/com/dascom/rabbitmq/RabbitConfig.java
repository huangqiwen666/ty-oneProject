package com.dascom.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
	
	@Value("${spring.rabbitmq.host}")
    private String host;
 
    @Value("${spring.rabbitmq.port}")
    private int port;
 
    @Value("${spring.rabbitmq.username}")
    private String username;
 
    @Value("${spring.rabbitmq.password}")
    private String password;

    
//    public static final String EXCHANGE = "print";
 
    public static final String QUEUE_RESULT = "printRequest";
 
//    public static final String ROUTINGKEY_RESULT = "printRequest";

    
//	@Bean
//    public ConnectionFactory connectionFactory() {
//        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
//        connectionFactory.setHost(host);
//        connectionFactory.setPort(port);
//        connectionFactory.setUsername(username);
//        connectionFactory.setPassword(password);
//        connectionFactory.setVirtualHost("/");
//        connectionFactory.setPublisherConfirms(true);//消息确认
//        connectionFactory.setPublisherReturns(true);
//        return connectionFactory;
//    }

	
	@Bean
    public Queue printResultQueue() {
        return new Queue(QUEUE_RESULT);
    }
//
//	@Bean
//    public TopicExchange TopicExchange() {
//        return new TopicExchange(EXCHANGE);
//	}
//	
//    @Bean
//    public Binding binding() {
//        return BindingBuilder.bind(printResultQueue()).to(TopicExchange()).with(ROUTINGKEY_RESULT);
//    }
//    

	
	
}
