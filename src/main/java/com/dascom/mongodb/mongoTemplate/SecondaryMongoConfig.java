//package com.dascom.mongodb.mongoTemplate;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.context.annotation.PropertySource;
//import org.springframework.data.mongodb.core.MongoTemplate;
///**
// * 要密码连接mongodb数据库 的类
// * @author hqw
// *
// */
//@Configuration
//@PropertySource("file:${user.dir}/config/mongodb.properties")
//public class SecondaryMongoConfig extends AbstractMongoConfig{
//
//	@Value(value = "mongo.host")
//	private String host;
//	
//	@Value(value = "mongo.port")
//	private Integer port;
//	
//	@Value(value = "mongo.device.database")
//	private String deviceDatabase;
//	
//	@Value(value = "mongo.device.username")
//	private String deviceUsername;
//	
//	@Value(value = "mongo.device.passwork")
//	private String devicePasswork;
//	
//	@Value(value = "mongo.print.database")
//	private String printDatabase;
//	
//	@Value(value = "mongo.print.username")
//	private String printUsername;
//	
//	@Value(value = "mongo.print.passwork")
//	private String printPasswork;
//	
//	
//
//	@Primary
//    @Bean(name="deviceMongoTemplate")
//	@Override
//	public MongoTemplate getPrintMongoTemplate() {
//		return new MongoTemplate(mongoDbFactory(host,port,deviceUsername,deviceDatabase,devicePasswork));
//	}
//	@Primary
//    @Bean(name="printMongoTemplate")
//	@Override
//	public MongoTemplate getDeviceMongoTemplate() {
//		return new MongoTemplate(mongoDbFactory(host,port,printUsername,printDatabase,printPasswork));
//	}
//}