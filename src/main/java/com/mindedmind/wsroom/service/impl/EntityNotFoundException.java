package com.mindedmind.wsroom.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EntityNotFoundException extends RuntimeException
{
	public EntityNotFoundException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public EntityNotFoundException(String message)
	{
		super(message);
	}

	public EntityNotFoundException(Throwable cause)
	{
		super(cause);
	}	
}
