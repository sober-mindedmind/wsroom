package com.mindedmind.wsroom.web.dto;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.mindedmind.wsroom.domain.Room;

public class RoomDto
{
	@NotNull
	@Size(min = 3)
	private String name;
	
	private transient String password;
		
	private String description;

	private List<String> allowedUserIds = new ArrayList<>();
	
	/** Amount of active users */
	private int usersCount;
	
	private Collection<String> activeUsers;
	
	private boolean active;
	
	private boolean hasPassword;
	
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
		setName(room.getName());
		setDescription(room.getDescription());
		setActive(room.getActive());
		setHasPassword(room.getPassword() != null);			
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

	public List<String> getAllowedUserIds()
	{	
		return allowedUserIds;
	}

	public List<Long> gerAllowedUsersLongIds()
	{
		return allowedUserIds.stream().filter(e -> e != null).map(Long::valueOf).collect(toList());
	}
	
	public boolean isActive()
	{
		return active;
	}

	public void setAllowedUserIds(List<String> allowedUserIds)
	{
		this.allowedUserIds = allowedUserIds;
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

	public Collection<String> getActiveUsers()
	{
		return activeUsers;
	}

	public void setActiveUsers(Collection<String> activeUsers)
	{
		this.activeUsers = activeUsers;
		if (activeUsers != null)
		{
			usersCount = activeUsers.size();
		}
	}
	
	public boolean isContainsAllowedUsers() 
	{
		return getAllowedUserIds() != null && !getAllowedUserIds().isEmpty();
	}
}
