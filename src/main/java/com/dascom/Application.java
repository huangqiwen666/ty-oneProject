package com.dascom;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 
 * @author hqw
 * Spring Boot 的启动类
 *
 */
@SpringBootApplication
@EnableAsync
@ComponentScan("com.dascom")
@PropertySource(value = "file:${user.dir}/config/cloudprint_tcp.properties")
@ServletComponentScan
public class Application{
	
	public static void main(String[] args) {
		try {
			File file_log4j2 = new File(System.getProperty("user.dir")+"/config/cloudprint_tcp_log4j2.xml");  
			BufferedInputStream in_log4j2;
			in_log4j2 = new BufferedInputStream(new FileInputStream(file_log4j2));
			final ConfigurationSource source = new ConfigurationSource(in_log4j2);  
			Configurator.initialize(null, source);  
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("加载日志失败");
		}  
		
		
		
		SpringApplication.run(Application.class, args);
	}
	
}