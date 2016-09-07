package com.mindedmind.wsroom.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mindedmind.wsroom.domain.Message;
import com.mindedmind.wsroom.domain.Room;
import com.mindedmind.wsroom.domain.User;
import com.mindedmind.wsroom.repository.MessageRepository;
import com.mindedmind.wsroom.repository.UserRepository;
import com.mindedmind.wsroom.service.ChatService;
import com.mindedmind.wsroom.service.RoomService;

@Service
public class ChatServiceImpl implements ChatService
{	
	private ConcurrentHashMap<String, Set<String>> roomsOnActiveUsers = new ConcurrentHashMap<>();
		
	private RoomService roomService;
	
	private MessageRepository messageRepository;
		
	private UserRepository userRepository;
	
	@Autowired
	public ChatServiceImpl(RoomService roomService, 
						   MessageRepository messageRepository,
						   UserRepository userRepository)
	{
		this.roomService	   = roomService;
		this.messageRepository = messageRepository;
		this.userRepository    = userRepository;
	}

	@Transactional
	@Override public void saveMessage(Message msg, String roomName)
	{
		msg.setRoom(roomService.findByName(roomName));
		messageRepository.save(msg);
	}

	@Override public Collection<String> getActiveUsers(String room)
	{
		return roomsOnActiveUsers.get(room);
	}

	@Transactional
	@Override public void unsubscribe(String userName, String room)
	{
		deactiveUser(userName);
		User user = userRepository.findUserByName(userName);	
		roomService.findByName(room).getSubscribedUsers().remove(user);
	}

	@Override public void deactiveUser(String user, String room)
	{
		assert roomsOnActiveUsers.contains(room);
		roomsOnActiveUsers.get(room).remove(user);
	}

	@Override public void activeUser(String user, String room)
	{
		Set<String> users = roomsOnActiveUsers.computeIfAbsent(room , 
				(a) -> Collections.newSetFromMap(new ConcurrentHashMap<>()));
		assert users != null;
		users.add(user);
	}

	@Override public Collection<String> deactiveUser(String user)
	{
		List<String> rooms = new ArrayList<>();
		roomsOnActiveUsers.forEach((key, set) -> {
			if (set.remove(user)) 
				rooms.add(key);
			});
		return rooms;
	}

	@Transactional
	@Override public void subscribe(String userName, String roomName, String password)
	{
		Room room = roomService.findByName(roomName);
		
		/* verifying password */
		if (room.getPassword() != null && !room.getPassword().equals(password))
		{
			throw new SubscriptionException("Can't subscribe to room, password is incorrect");
		}
		
		User user = userRepository.findUserByName(userName);		
		room.getSubscribedUsers().add(user);
	}
}
