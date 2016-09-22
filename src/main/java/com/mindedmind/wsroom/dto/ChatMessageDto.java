package com.mindedmind.wsroom.dto;

public class ChatMessageDto
{
	private String text;
	
	private String serverTime;
	
	private String owner;

	public String getServerTime()
	{
		return serverTime;
	}

	public void setServerTime(String serverTime)
	{
		this.serverTime = serverTime;
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public String getOwner()
	{
		return owner;
	}

	public void setOwner(String owner)
	{
		this.owner = owner;
	}
}
