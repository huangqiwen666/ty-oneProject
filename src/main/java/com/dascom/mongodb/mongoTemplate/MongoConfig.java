package com.dascom.mongodb.mongoTemplate;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.util.StringUtils;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

@Configuration
@PropertySource("file:${user.dir}/config/mongodb.properties")
public class MongoConfig {

	@Value(value = "${mongo.host}")
	private String host;
	
	@Value(value = "${mongo.port}")
	private Integer port;
	//db_cloud_device 库
	@Value(value = "${mongo.device.databaseName}")
	private String deviceDatabase;
	@Value(value = "${mongo.device.username}")
	private String deviceUsername;
	@Value(value = "${mongo.device.password}")
	private String devicePassword;
	
	//db_print_log 库
	@Value(value = "${mongo.print.databaseName}")
	private String printDatabase;
	@Value(value = "${mongo.print.username}")
	private String printUsername;
	@Value(value = "${mongo.print.password}")
	private String printPassword;

    @Primary
    @Bean(name ="deviceMongoTemplate")
    public MongoTemplate primaryMongoTemplate() throws Exception {
    	MongoDbFactory deviceFactory = deviceFactory(host,port,deviceUsername,devicePassword,deviceDatabase);
    	MappingMongoConverter mappingMongoConverter = mappingMongoConverter(deviceFactory);
        return new MongoTemplate(deviceFactory,mappingMongoConverter);
    }

    @Bean
    @Qualifier("printMongoTemplate")
    public MongoTemplate secondaryMongoTemplate() throws Exception {
    	MongoDbFactory printFactory = printFactory(host,port,printUsername,printPassword,printDatabase);
    	MappingMongoConverter mappingMongoConverter = mappingMongoConverter(printFactory);
        return new MongoTemplate(printFactory,mappingMongoConverter);
    }
    @Bean
    @Primary
    public MongoDbFactory deviceFactory(String host,Integer port,String username,String password,String database) throws Exception {
    	if (StringUtils.isEmpty(username)||StringUtils.isEmpty(password)) {
    		return new SimpleMongoDbFactory(new MongoClient(host,port),database);
		}
    	ServerAddress serverAddress = new ServerAddress(host, port);
    	List<MongoCredential> credentialsList = new ArrayList<MongoCredential>();
    	credentialsList.add(MongoCredential.createScramSha1Credential(username, database, password.toCharArray()));
    	return new SimpleMongoDbFactory(new MongoClient(serverAddress, credentialsList),database);
    }

    @Bean
    public MongoDbFactory printFactory(String host,Integer port,String username,String password,String database) throws Exception {
    	if (StringUtils.isEmpty(username)||StringUtils.isEmpty(password)) {
    		return new SimpleMongoDbFactory(new MongoClient(host,port),database);
		}
    	ServerAddress serverAddress = new ServerAddress(host, port);
    	List<MongoCredential> credentialsList = new ArrayList<MongoCredential>();
    	credentialsList.add(MongoCredential.createScramSha1Credential(username, database, password.toCharArray()));
    	return new SimpleMongoDbFactory(new MongoClient(serverAddress, credentialsList),database);
    }
    
    @Bean  
    public MappingMongoConverter mappingMongoConverter(MongoDbFactory factory) {  
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);  
        MappingMongoConverter mappingConverter = new MappingMongoConverter(dbRefResolver, new MongoMappingContext());  
        mappingConverter.setTypeMapper(new DefaultMongoTypeMapper(null));  
        return mappingConverter;  
    }  
	    
}
