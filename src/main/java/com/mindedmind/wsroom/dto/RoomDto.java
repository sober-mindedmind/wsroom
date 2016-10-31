package com.mindedmind.wsroom.dto;

import java.util.Set;

import com.mindedmind.wsroom.domain.Room;

public class RoomDto
{
	private Long id;
	
	private String name;
	
	private transient String password;
		
	private String description;
	
	/** Amount of active users */
	private int usersCount;
	
	private Set<String> activeUsers;
	
	private Set<ChatMessageDto> preview;
	
	private boolean active;
	
	private boolean hasPassword;
	
	private Long ownerId;
	
	public RoomDto()
	{
	}
	
	/**
	 * Copies room's fields.
	 * 
	 * @param room - the room instance
	 */
	public RoomDto(Room room)
	{
		setId(room.getId());
		setName(room.getName());
		setDescription(room.getDescription());
		setActive(room.getActive());
		setHasPassword(room.getPassword() != null);
		setOwnerId(room.getOwner().getId());
	}
	
	public String getName()
	{
		return name;
	}

	public String getPassword()
	{
		return password;
	}

	public String getDescription()
	{
		return description;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setPassword(String password)
	{
		this.password = password;
		hasPassword = password != null && !password.isEmpty();
	}

	public void setDescription(String description)
	{
		this.description = description;
	}
	
	public boolean isActive()
	{
		return active;
	}

	public void setActive(boolean active)
	{
		this.active = active;
	}

	public boolean isHasPassword()
	{
		return hasPassword;
	}

	public void setHasPassword(boolean hasPassword)
	{
		this.hasPassword = hasPassword;
	}

	public int getUsersCount()
	{
		return usersCount;
	}

	public void setUsersCount(int usersCount)
	{
		this.usersCount = usersCount;
	}

	public Set<String> getActiveUsers()
	{
		return activeUsers;
	}

	public void setActiveUsers(Set<String> activeUsers)
	{
		this.activeUsers = activeUsers;
		if (activeUsers != null)
		{
			usersCount = activeUsers.size();
		}
	}
	
	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public Set<ChatMessageDto> getPreview()
	{
		return preview;
	}

	public void setPreview(Set<ChatMessageDto> preview)
	{
		this.preview = preview;
	}

	public Long getOwnerId()
	{
		return ownerId;
	}

	public void setOwnerId(Long onwerId)
	{
		this.ownerId = onwerId;
	}
}
