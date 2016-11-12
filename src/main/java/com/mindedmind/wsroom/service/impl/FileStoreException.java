package com.mindedmind.wsroom.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class FileStoreException extends RuntimeException
{
	public FileStoreException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public FileStoreException(String message)
	{
		super(message);
	}
}
