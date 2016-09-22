package com.mindedmind.wsroom.service;

import java.util.Collection;
import java.util.Set;

import com.mindedmind.wsroom.domain.User;

public interface UserService
{
	void save(User user);
	
	User findUser(String name);
	
	Set<User> findUsers(Long... ids);
	
	Collection<User> findAll();
			
	byte[] loadUserImage(String name);
		
}
