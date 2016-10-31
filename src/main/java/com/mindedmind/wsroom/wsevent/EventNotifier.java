package com.mindedmind.wsroom.wsevent;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.mindedmind.wsroom.dto.ChatMessageDto;
import com.mindedmind.wsroom.dto.TypingDto;
import com.mindedmind.wsroom.dto.UserDto;

@Component
public class EventNotifier
{
	static final String CHAT_TOPIC		       = "/topic/chat/";	
	static final String USER_JOIN_TOPIC        = "/topic/join/";	
	static final String USER_LEAVE_TOPIC   	   = "/topic/leave/";
	static final String DELETE_MESSAGE_TOPIC   = "/topic/delete_msg/";
	static final String UPDATE_MESSAGE_TOPIC   = "/topic/update_msg/";
	
	private final SimpMessagingTemplate messagingTemplate;
	
	@Autowired
	public EventNotifier(SimpMessagingTemplate messagingTemplate)
	{
		this.messagingTemplate = messagingTemplate;
	}

	public void notifyUpdateMessage(String room, ChatMessageDto msg)
	{
		messagingTemplate.convertAndSend(UPDATE_MESSAGE_TOPIC + room, msg);
	}
	
	public void notifyDeleteMessage(String room, ChatMessageDto msg) 
	{
		messagingTemplate.convertAndSend(DELETE_MESSAGE_TOPIC + room, msg);
	}	

	public void notifyUserLeaveRoom(String user, String roomName)
	{
		messagingTemplate.convertAndSend(USER_LEAVE_TOPIC + roomName, new UserDto(user));	
	}
	
	public void notifyUserLeaveRooms(String user, Collection<String> rooms)
	{
		if (rooms != null)
		{
			rooms.forEach(room -> notifyUserLeaveRoom(user, room));
		}
	}
	
	public void notifyUsersLeaveRoom(Collection<String> users, String roomName)
	{
		if (users != null)
		{
			users.forEach(user -> notifyUserLeaveRoom(user, roomName));
		}
	}
	
	public void notifyUserIsActive(String room, String user) 
	{
		messagingTemplate.convertAndSend(USER_JOIN_TOPIC + room, new UserDto(user));		
	}
	
	public void notifySendMessage(ChatMessageDto msg, String room)
	{
		messagingTemplate.convertAndSend("/topic/chat/" + room, msg);
	}
	
	public void notifyTyping(String userName, String room)
	{
		messagingTemplate.convertAndSend("/topic/typing/" + room, new TypingDto(userName , room));
	}
}
