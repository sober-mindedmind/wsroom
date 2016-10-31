package com.mindedmind.wsroom.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidImageException extends RuntimeException
{
	public InvalidImageException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public InvalidImageException(String message)
	{
		super(message);
	}

	public InvalidImageException(Throwable cause)
	{
		super(cause);
	}

}
