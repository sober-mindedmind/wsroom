package com.mindedmind.wsroom.domain;

public enum Role
{
	ROLE_USER("ROLE_USER"),
	ROLE_ADMIN("ROLE_ADMIN");
	
	private String name;
	
	Role(String name)
	{
		this.name = name;
	}
		
	public String getName()
	{
		return name;
	}
	
	public static Role getRole(String name)
	{
		for (Role r : values())
		{
			if (r.getName().equals(name))
			{
				return r;
			}
		}
		throw new IllegalArgumentException("Wrong role name " + name);
	}
}
