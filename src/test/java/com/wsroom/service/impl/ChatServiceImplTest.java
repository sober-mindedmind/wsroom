package com.wsroom.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.mindedmind.wsroom.domain.Room;
import com.mindedmind.wsroom.repository.MessageRepository;
import com.mindedmind.wsroom.repository.UserRepository;
import com.mindedmind.wsroom.service.RoomService;
import com.mindedmind.wsroom.service.impl.ChatServiceImpl;
import com.mindedmind.wsroom.service.impl.SubscriptionException;

@RunWith(MockitoJUnitRunner.class)
public class ChatServiceImplTest
{
	@InjectMocks 
	private ChatServiceImpl chatServiceImpl;

	@Mock
	private RoomService roomService;

	@Mock
	private MessageRepository messageRepository;

	@Mock
	private UserRepository userRepository;
		
	@Mock
	private SimpMessagingTemplate stompTemplate;
	
	@Test
	public void activeUser_IsUserActiveted_True()
	{		
		chatServiceImpl.activeUser("user", "room");
		assertEquals(new ArrayList<>(chatServiceImpl.getActiveUsers("room")), Arrays.asList("user"));
	}
	
	@Test
	public void unsubscribe_UserIsUnsubscribed_True()
	{		
		when(roomService.findByName("room1")).thenReturn(new Room());		
		chatServiceImpl.activeUser("user" , "room1");		
		chatServiceImpl.unsubscribe("user" , "room1");
		assertTrue(chatServiceImpl.getActiveUsers("room1").isEmpty());
	}
	
	@Test
	public void deactiveUser_UserIsDeactivated_True()
	{
		chatServiceImpl.activeUser("user" , "room1");
		chatServiceImpl.deactiveUser("user", "room1");
		assertTrue(chatServiceImpl.getActiveUsers("room1").isEmpty());
	}
	
	@Test
	public void deactiveUser_UserIsDeactivedInAllRooms()
	{
		chatServiceImpl.activeUser("user" , "room1");
		chatServiceImpl.activeUser("user" , "room2");
		chatServiceImpl.deactiveUser("user");
		assertTrue(chatServiceImpl.getActiveUsers("room1").isEmpty());
		assertTrue(chatServiceImpl.getActiveUsers("room2").isEmpty());
	}
	
	@Test(expected = SubscriptionException.class)
	public void subscribe_WrongPassword_ExceptionThrown()
	{
		Room room = new Room();
		room.setName("room1");
		room.setPassword("1");		
		when(roomService.findByName("room1")).thenReturn(room);		
		chatServiceImpl.subscribe("user1", "room1", "2");
	}
}
