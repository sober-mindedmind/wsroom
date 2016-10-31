package com.mindedmind.wsroom.dto;

import com.mindedmind.wsroom.domain.Message;

public class ChatMessageDto
{
	private Long id;
	
	private String text;
	
	private String serverTime;
	
	private String owner;
	
	private Long ownerId;
	
	private String room;
	
	/*private Long roomId;*/

	public ChatMessageDto()
	{}
	
	public ChatMessageDto(Message message)
	{
		setId(message.getId());
		setText(message.getText());
		setServerTime(message.getTime().toString());
		setOwner(message.getOwner().getName());
		setOwnerId(message.getOwner().getId());
		setRoom(message.getRoom().getName());
		/*setRoomId(message.getRoom().getId());*/
	}
		
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

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public Long getOwnerId()
	{
		return ownerId;
	}

	/*public Long getRoomId()
	{
		return roomId;
	}
*/
	public void setOwnerId(Long ownerId)
	{
		this.ownerId = ownerId;
	}

	/*public void setRoomId(Long roomId)
	{
		this.roomId = roomId;
	}*/

	@Override public String toString()
	{
		return "ChatMessageDto [id=" + id + ", text=" + text + ", serverTime=" + serverTime + ", owner=" + owner + "]";
	}

	public String getRoom()
	{
		return null;
	}

	public void setRoom(String room)
	{
		this.room = room;
	}		
}
