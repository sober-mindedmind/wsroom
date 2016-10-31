package com.mindedmind.wsroom.service.impl;

import static com.mindedmind.wsroom.util.EntityUtils.notNull;
import static java.util.Collections.emptySet;

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
import com.mindedmind.wsroom.repository.RoomRepository;
import com.mindedmind.wsroom.repository.UserRepository;
import com.mindedmind.wsroom.service.ChatService;

@Service
public class ChatServiceImpl implements ChatService
{
	private final ConcurrentMap<String, Set<String>> roomsOnActiveUsers = new ConcurrentHashMap<>();
	
	private final RoomRepository roomRepository;
	
	private final MessageRepository messageRepository;
	
	private final UserRepository userRepository;
		
	@Autowired
	public ChatServiceImpl(RoomRepository roomRepository, 
						   MessageRepository messageRepository,
						   UserRepository userRepository)
	{
		this.roomRepository	     = roomRepository;
		this.messageRepository   = messageRepository;
		this.userRepository      = userRepository;
	}

	@Transactional
	@Override public void saveMessage(Message msg, String roomName)
	{
		msg.setRoom(roomRepository.findByName(roomName));
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
		roomRepository.findByName(room).getSubscribedUsers().remove(user);
		deactiveUser(userName, room);
	}

	@Override public void deactiveUser(String user, String room)
	{
		assert roomsOnActiveUsers.containsKey(room);
		roomsOnActiveUsers.getOrDefault(room , emptySet()).remove(user);
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
		Room room = roomRepository.findByName(roomName);
		
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
		
	@Override public void deleteMessage(Long id)
	{
		messageRepository.delete(id);
	}
	
	@Override public void updateMessage(Long id, String txt)
	{
	//	messageRepository.findOne(id).setText(txt);
		messageRepository.updateMessage(id , txt);
	}

	@Override public Message findMessage(Long id)
	{
		return messageRepository.findMessage(id);
	}
	
	@Transactional
	@Override public void banUser(String name, String roomName, boolean ban)
	{
		Room room = roomRepository.findByName(roomName);
		notNull(room , "There is no room with the name like '%s'", roomName);
		User user = userRepository.findUserByName(name);
		notNull(user , "There is no user with the name like '%s'", name);		
		if (ban)
		{						
			room.getBannedUsers().add(user);
		}
		else
		{
			room.getBannedUsers().remove(user);
		}
		room.getSubscribedUsers().remove(user);
		deactiveUser(name, roomName);
	}

	@Override public boolean isActive(String userName, String room)
	{
		return roomsOnActiveUsers.getOrDefault(room , emptySet()).contains(userName);
	}

/*	private Message findMessage(Long id)
	{
		Message msg = messageRepository.findOne(id);
		if (msg == null)
		{
			throw new EntityNotFoundException(String.format("Message with the given id '%s' does not exist", id));
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.getName().equals(msg.getOwner().getName()))
		{
			throw new AccessDeniedException("Can't obtaine access to message");
		}		
		return msg;
	}*/
}
