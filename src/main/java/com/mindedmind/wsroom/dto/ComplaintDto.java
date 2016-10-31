package com.mindedmind.wsroom.dto;

import com.mindedmind.wsroom.domain.Complaint;

public class ComplaintDto
{
	/** The offensive message */
	private ChatMessageDto offensiveMessage;
	
	/** The reason of complaint */
	private String reason;
	
	private Long id;
	
	public ComplaintDto()
	{}
	
	public ComplaintDto(Complaint c)
	{		
		this.offensiveMessage = new ChatMessageDto(c.getMessage());
		this.reason = c.getReason();
		this.id = c.getId();
	}
	
	public String getReason()
	{
		return reason;
	}

	public void setReason(String reason)
	{
		this.reason = reason;
	}

	public ChatMessageDto getOffensiveMessage()
	{
		return offensiveMessage;
	}

	public void setOffensiveMessage(ChatMessageDto offensiveMessage)
	{
		this.offensiveMessage = offensiveMessage;
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}
}
