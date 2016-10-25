package com.mindedmind.wsroom.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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
	private final ConcurrentMap<String, Set<String>> roomsOnActiveUsers = new ConcurrentHashMap<>();
	
	private final RoomService roomService;
	
	private final MessageRepository messageRepository;
	
	private final UserRepository userRepository;
	
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

	@Override public Set<String> getActiveUsers(String room)
	{
		return roomsOnActiveUsers.get(room);
	}

	@Transactional
	@Override public void unsubscribe(String userName, String room)
	{		
		User user = userRepository.findUserByName(userName);	
		roomService.findByName(room).getSubscribedUsers().remove(user);
		deactiveUser(userName);
	}

	@Override public void deactiveUser(String user, String room)
	{
		assert roomsOnActiveUsers.containsKey(room);
		Set<String> users = roomsOnActiveUsers.get(room);
		if (users != null)
		{
			users.remove(user);
		}		
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
		roomsOnActiveUsers.forEach((room, users) -> {
			if (users.remove(user)) 
				rooms.add(room);
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

	@Override public Set<String> deactiveAll(String roomName)
	{
		return roomsOnActiveUsers.remove(roomName);		
	}
	
	@Override public void removeMessage(String user, Long msgId)
	{
		messageRepository.delete(msgId);	
	}

	@Transactional
	@Override public void updateMessage(String user, Long msgId, String txt)
	{
		Message msg = messageRepository.findOne(msgId);
		if (msg == null)
		{
			throw new EntityNotFoundException(String.format("Message with the given id '%s' does not exist", msgId));
		}
		msg.setText(txt);
	}
}
