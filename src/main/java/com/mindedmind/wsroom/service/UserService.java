package com.mindedmind.wsroom.service;

import java.util.Collection;
import java.util.Set;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

import com.mindedmind.wsroom.domain.User;

public interface UserService
{	
	void save(User user);
	
	@PreAuthorize("#name == principal.username or hasRole('ROLE_ADMIN')")
	User findUser(String name);
	
	@PostAuthorize("returnObject.name == principal.username or hasRole('ROLE_ADMIN')")
	User findUser(Long id);
	
	Set<User> findUsers(Long... ids);
	
	Collection<User> findAll();
			
	byte[] loadUserImage(String name);
	
	void removeUser(Long id);
	
	@PreAuthorize("#name == principal.username or hasRole('ROLE_ADMIN')")
	void removeUser(String name);
}
