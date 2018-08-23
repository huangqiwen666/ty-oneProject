//package com.dascom.mongodb.mongoTemplate;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.PropertySource;
//import org.springframework.data.mongodb.MongoDbFactory;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
//
//import com.mongodb.MongoClient;
///**
// * 不需要密码连接mongodb数据库 的类
// * @author hqw
// *
// */
//@Configuration
//@PropertySource("file:${user.dir}/config/mongodb.properties")
//public class PrimaryMongoConfig extends AbstractMongoConfig{
//
//	@Value(value = "${mongo.host}")
//	private String host;
//	
//	@Value(value = "${mongo.port}")
//	private Integer port;
//	
//	@Value(value = "${mongo.device.databaseName}")
//	private String deviceDatabase;
//	
//	@Value(value = "${mongo.print.databaseName}")
//	private String printDatabase;
//    /**
//     *  本地mongodb不需要密码，用这个
//     */
//    public MongoDbFactory mongoDbFactory(String database){
//        return new SimpleMongoDbFactory(new MongoClient(host, port), database);
//    }
//
//    @Bean(name="deviceMongoTemplate")
//    @Override
//    public MongoTemplate getDeviceMongoTemplate() {
//        return new MongoTemplate(mongoDbFactory(deviceDatabase));
//    }
//    /*
//    @Bean(name="printMongoTemplate")
//    @Override
//    public MongoTemplate getPrintMongoTemplate() {
//    	return new MongoTemplate(mongoDbFactory(printDatabase));
//    }
//    */
//    
//    
//}