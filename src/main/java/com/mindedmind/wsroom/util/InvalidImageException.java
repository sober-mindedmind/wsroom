package com.mindedmind.wsroom.util;

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
