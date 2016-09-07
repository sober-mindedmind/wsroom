package com.mindedmind.wsroom.wsevent;

import static com.mindedmind.wsroom.util.RoomUtils.extractPath;
import static com.mindedmind.wsroom.util.RoomUtils.extractRoom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import com.mindedmind.wsroom.service.ChatService;
import com.mindedmind.wsroom.web.dto.UserDto;

@Component
public class StompEventListener
{	
	private static Logger LOGGER = LoggerFactory.getLogger(StompEventListener.class);
	
	private static final String CHAT_TOPIC_DEST		    = "/topic/chat/";	
	private static final String USER_JOIN_TOPIC_DEST    = "/topic/join/";	
	private static final String USER_LEAVE_TOPIC_DEST   = "/topic/leave/";
		
	private ChatService chatService;
	
	private SimpMessagingTemplate messagingTemplate;
		
	@Autowired
	public StompEventListener(ChatService chatService, 
							  SimpMessagingTemplate messagingTemplate)
	{		
		this.chatService = chatService;
		this.messagingTemplate = messagingTemplate;
	}
	
	@EventListener
	public void onSubscribe(SessionSubscribeEvent subEvent)		
	{
		StompHeaderAccessor header = StompHeaderAccessor.wrap(subEvent.getMessage());
		if (extractPath(header.getDestination()).equals(CHAT_TOPIC_DEST))
		{		
			String roomName = extractRoom(header.getDestination());			
			chatService.activeUser(header.getUser().getName() , roomName);			
			messagingTemplate.convertAndSend(USER_JOIN_TOPIC_DEST + roomName,
											 new UserDto(header.getUser().getName()));		
		}
		LOGGER.debug("User '{}' has been subscribed", header.getUser().getName());
	}
	
	@EventListener
	public void onUnsubscribe(SessionUnsubscribeEvent subEvent)
	{
		StompHeaderAccessor header = StompHeaderAccessor.wrap(subEvent.getMessage());		
		if (extractPath(header.getSubscriptionId()).equals(CHAT_TOPIC_DEST))
		{
			String roomName = extractRoom(header.getSubscriptionId());			
			chatService.unsubscribe(header.getUser().getName() , roomName);			
			sendLeave(header.getUser().getName(), roomName);
		}
		LOGGER.debug("User '{}' has been unsubscribed", header.getUser().getName());
	}
	
	@EventListener
	public void onDisconnect(SessionDisconnectEvent subEvent)
	{
		StompHeaderAccessor header = StompHeaderAccessor.wrap(subEvent.getMessage());
		String userName = header.getUser().getName();
		chatService.deactiveUser(userName).forEach((room) -> sendLeave(userName, room));
	}
	
	private void sendLeave(String user, String roomName)
	{
		messagingTemplate.convertAndSend(USER_LEAVE_TOPIC_DEST + roomName, new UserDto(user));	
	}
	
}
