package com.mindedmind.wsroom.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.mindedmind.wsroom.domain.Role;
import com.mindedmind.wsroom.domain.User;

public class UserDetailsImpl implements UserDetails
{
	private User user;
	
	private Collection<GrantedAuthority> authorities;
	
	private static class GrantedAuthorityImpl implements GrantedAuthority
	{
		private String name;
		
		GrantedAuthorityImpl(Role role)
		{
			this.name = role.getName();
		}
		
		@Override public String getAuthority()
		{
			return name;
		}		
	}
	
	public UserDetailsImpl() {}
	
	public UserDetailsImpl(User user)
	{		
		Collection<Role> roles = user.getRoles();
		if (roles != null)
		{
			authorities = roles.stream().map(GrantedAuthorityImpl::new).collect(Collectors.toList());
		}
		this.user = user;
	}

	@Override public Collection<GrantedAuthority> getAuthorities()
	{
		return authorities;
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