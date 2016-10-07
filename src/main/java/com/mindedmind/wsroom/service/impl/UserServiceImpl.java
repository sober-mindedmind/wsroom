package com.mindedmind.wsroom.service.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mindedmind.wsroom.domain.Room;
import com.mindedmind.wsroom.domain.User;
import com.mindedmind.wsroom.repository.MessageRepository;
import com.mindedmind.wsroom.repository.RoomRepository;
import com.mindedmind.wsroom.repository.UserRepository;
import com.mindedmind.wsroom.service.UserService;
import com.mindedmind.wsroom.util.ImageUtils;

@Service
@Transactional
public class UserServiceImpl implements UserService   
{	
	private final UserRepository userRepository;
			
	private final MessageRepository messageRepository;
	
	private final RoomRepository roomRepository; 
		
	@Autowired
	public UserServiceImpl(UserRepository userRepository, 
						   MessageRepository messageRepository,
						   RoomRepository roomRepository)
	{
		this.userRepository    = userRepository;
		this.messageRepository = messageRepository;
		this.roomRepository    = roomRepository;
	}

	@Override public void save(User user)
	{
		user.setPhoto(ImageUtils.resize(user.getPhoto(), 25, 25));
		userRepository.save(user);
	}
	
	@Override public User findUser(String name)
	{		
		return userRepository.findUserByName(name);
	}

	@Override public Collection<User> findAll()
	{
		return userRepository.findAll();
	}

	@Override public byte[] loadUserImage(String name)
	{
		return findUser(name).getPhoto();
	}
	
	@Override public Set<User> findUsers(Long... ids)
	{
		Validate.notNull(ids , "Array of identifiers can't be null");
		Set<User> users = userRepository.findUsersById(ids);
		return users == null ? Collections.emptySet() : users;
	}

	@Override public void removeUser(Long id)
	{
		userRepository.delete(id);
	}

	@Override public User findUser(Long id)
	{
		return userRepository.findOne(id);
	}
	
	@Override public void removeUser(String name)
	{
		User user = userRepository.findUserByName(name);
		if (user == null)
		{
			throw new EntityNotFoundException(
					String.format("Can't delete user, because can't find user with the given name '%s'" , name));
		}
		messageRepository.deleteAllMessagesOfUser(user);
		roomRepository.deleteAllRoomsWhereUserIsOwner(user);
		for (Room r : roomRepository.findUserRooms(name))
		{
			r.getSubscribedUsers().remove(user);
			r.getAllowedUsers().remove(user);
		}
		userRepository.delete(user);
	}	
}
