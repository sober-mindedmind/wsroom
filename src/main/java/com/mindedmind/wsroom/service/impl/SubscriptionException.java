package com.mindedmind.wsroom.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SubscriptionException extends RuntimeException
{
	public SubscriptionException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public SubscriptionException(String message)
	{
		super(message);
	}
}
