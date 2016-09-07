package com.mindedmind.wsroom.service.impl;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.mindedmind.wsroom.domain.User;

public class UserDetailsImpl implements UserDetails
{
	private User user;
	
	public UserDetailsImpl(User user)
	{		
		this.user = user;
	}

	@Override public Collection<GrantedAuthority> getAuthorities()
	{
		return null;
	}

	@Override public String getPassword()
	{
		return user.getPassword();
	}

	@Override public String getUsername()
	{
		return user.getName();
	}

	@Override public boolean isAccountNonExpired()
	{
		return user.isActive();
	}

	@Override public boolean isAccountNonLocked()
	{
		return user.isActive();
	}

	@Override public boolean isCredentialsNonExpired()
	{
		return user.isActive();
	}

	@Override public boolean isEnabled()
	{
		return user.isActive();
	}

	public User getUser()
	{
		return user;
	}
	
}