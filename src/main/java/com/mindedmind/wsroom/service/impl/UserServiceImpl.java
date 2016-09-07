package com.mindedmind.wsroom.service.impl;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mindedmind.wsroom.domain.User;
import com.mindedmind.wsroom.repository.UserRepository;
import com.mindedmind.wsroom.service.UserService;
import com.mindedmind.wsroom.util.ImageUtils;

@Service
public class UserServiceImpl implements UserService   
{
	
	private UserRepository userRepository;
			
	@Autowired
	public UserServiceImpl(UserRepository userRepository)
	{	
		this.userRepository = userRepository;
	}

	@Override public void save(User user)
	{
		user.setPhoto(ImageUtils.resize(user.getPhoto(), 25, 25));
		userRepository.save(user);
	}

	@Transactional
	@Override public User findUser(String name)
	{		
		return userRepository.findUserByName(name);
	}

	@Override public Collection<User> findAll()
	{
		return userRepository.findAll();
	}

	@Transactional
	@Override public byte[] loadUserImage(String name)
	{
		return findUser(name).getPhoto();
	}
	
}
