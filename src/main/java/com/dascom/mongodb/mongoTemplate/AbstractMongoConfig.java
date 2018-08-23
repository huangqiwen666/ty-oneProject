//package com.dascom.mongodb.mongoTemplate;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.springframework.data.mongodb.MongoDbFactory;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
//
//import com.mongodb.MongoClient;
//import com.mongodb.MongoCredential;
//import com.mongodb.ServerAddress;
//
//public abstract class AbstractMongoConfig{
//	
//	
//    public MongoDbFactory mongoDbFactory(String host,Integer port,String username,String database,String password){
//        ServerAddress serverAddress = new ServerAddress(host, port);
//        List<MongoCredential> credentialsList = new ArrayList<MongoCredential>();
//        credentialsList.add(MongoCredential.createCredential(username, database, password.toCharArray()));
//        return new SimpleMongoDbFactory(new MongoClient(serverAddress, credentialsList),database);
//    }
////    public abstract MongoTemplate getPrintMongoTemplate();
//    public abstract MongoTemplate getDeviceMongoTemplate();
//    
//}