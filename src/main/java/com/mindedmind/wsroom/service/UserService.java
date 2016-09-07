package com.mindedmind.wsroom.service;

import java.util.Collection;

import com.mindedmind.wsroom.domain.User;

public interface UserService
{
	void save(User user);
	
	User findUser(String name);
	
	Collection<User> findAll();
			
	byte[] loadUserImage(String name);
}
