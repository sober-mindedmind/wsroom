package com.mindedmind.wsroom.dto;

public class FileLink extends ChatMessageDto
{
	public enum Status
	{
		EXIST,
		REMOVED;
	}
	
	private String fileName;
	private String hash;
	private Status status = Status.EXIST;
	
	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public String getHash()
	{
		return hash;
	}

	public void setHash(String hash)
	{
		this.hash = hash;
	}

	public Status getStatus()
	{
		return status;
	}

	public void setStatus(Status status)
	{
		this.status = status;
	}
}
