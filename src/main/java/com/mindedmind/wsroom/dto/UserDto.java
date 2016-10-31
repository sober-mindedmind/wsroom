package com.mindedmind.wsroom.dto;

import com.mindedmind.wsroom.domain.User;

public class UserDto
{
	private Long id;
	
	private String name;
	
	public UserDto()
	{}
	
	public UserDto(User user)
	{
		this(user.getId(), user.getName());		
	}
	
	public UserDto(String name)
	{		
		this(null, name);
	}
	
	public UserDto(Long id, String name)
	{	
		this.id = id;
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
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
