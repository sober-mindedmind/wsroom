package com.mindedmind.wsroom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.mindedmind.wsroom.config.JdbcProperties;

@SpringBootApplication
@EnableConfigurationProperties(JdbcProperties.class)
public class Application
{	
	public static void main(String[] args)
	{			
		SpringApplication.run(Application.class, args);
	}	
}
