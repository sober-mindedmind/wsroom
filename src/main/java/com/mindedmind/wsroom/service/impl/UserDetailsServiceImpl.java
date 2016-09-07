package com.mindedmind.wsroom.service.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import com.mindedmind.wsroom.domain.User;
import com.mindedmind.wsroom.repository.UserRepository;

public class UserDetailsServiceImpl implements UserDetailsService
{
	private UserRepository userRepository;

	public UserDetailsServiceImpl(UserRepository userRepository)
	{	
		this.userRepository = userRepository;
	}

	@Transactional
	@Override public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
	{
		User user = userRepository.findUserByName(username);		
		if (user == null)
		{
			throw new UsernameNotFoundException(String.format("User '%s' was not found"));
		}
		return new UserDetailsImpl(user);
	}

}
