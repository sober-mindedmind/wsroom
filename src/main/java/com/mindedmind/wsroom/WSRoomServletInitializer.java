package com.mindedmind.wsroom;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

public class WSRoomServletInitializer extends SpringBootServletInitializer
{
	@Override protected SpringApplicationBuilder configure(SpringApplicationBuilder builder)
	{
		return builder.sources(Application.class);
	}	
}