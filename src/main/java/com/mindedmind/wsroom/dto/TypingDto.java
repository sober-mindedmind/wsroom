package com.mindedmind.wsroom.dto;

public class TypingDto
{
	private String userName;
	private String roomName;
	
	public TypingDto(String userName, String roomName)
	{	
		this.userName = userName;
		this.roomName = roomName;
	}

	public String getUserName()
	{
		return userName;
	}

	public String getRoomName()
	{
		return roomName;
	}
	
}
