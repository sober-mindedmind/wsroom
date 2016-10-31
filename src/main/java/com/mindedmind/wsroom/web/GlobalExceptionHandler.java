package com.mindedmind.wsroom.web;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
	@ExceptionHandler(Exception.class)
	@ResponseStatus(BAD_REQUEST)
	public ModelAndView onException(Exception e)
	{
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/error");
		modelAndView.addObject("msg" , e.getMessage());
		LOGGER.debug("Global exception has been occured: ", e);
		return modelAndView;
	}
}
