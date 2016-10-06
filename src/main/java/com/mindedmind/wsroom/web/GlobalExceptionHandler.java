package com.mindedmind.wsroom.web;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler
{
	@ExceptionHandler(Exception.class)
	@ResponseStatus(BAD_REQUEST)
	public ModelAndView onException(Exception e)
	{
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/error");
		modelAndView.addObject("msg" , e.getMessage());		
		return modelAndView;
	}
}
