package com.mindedmind.wsroom.web;

import java.io.IOException;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.mindedmind.wsroom.util.InvalidImageException;

@ControllerAdvice
public class GlobalExceptionHandler
{
	@ExceptionHandler(InvalidImageException.class)	
	public ModelAndView onInvalidImage()
	{	
		return defaultModelAndView("You have specified a wrong image");
	}
	
	@ExceptionHandler(IOException.class)	
	public ModelAndView onIOException()
	{
		return defaultModelAndView("Can't load an image");
	}	
	
	private static ModelAndView defaultModelAndView(Object errorObject)
	{
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/error");
		modelAndView.addObject("msg" , errorObject);		
		return modelAndView;
	}
}
