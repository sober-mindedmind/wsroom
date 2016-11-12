package com.mindedmind.wsroom.wsevent;

import static com.mindedmind.wsroom.util.RoomUtils.extractPath;
import static com.mindedmind.wsroom.util.RoomUtils.extractRoom;
import static com.mindedmind.wsroom.wsevent.EventNotifier.CHAT_TOPIC;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import com.mindedmind.wsroom.security.RoomPermissionEvaluator;
import com.mindedmind.wsroom.service.ChatService;

@Component
public class StompEventListener
{	
	private static Logger LOGGER = LoggerFactory.getLogger(StompEventListener.class);
	
	private final ChatService chatService;
	
	private final EventNotifier notifier;
	
	private final RoomPermissionEvaluator roomPermissionEval; 
	
	@Autowired
	public StompEventListener(EventNotifier notifier, 
							  ChatService chatService, 
							  RoomPermissionEvaluator roomPermissionEval)
	{		
		this.notifier = notifier;
		this.chatService = chatService;
		this.roomPermissionEval = roomPermissionEval;
	}
	
	@EventListener
	public void onSubscribe(SessionSubscribeEvent subEvent)		
	{
		StompHeaderAccessor header = StompHeaderAccessor.wrap(subEvent.getMessage());
		if (extractPath(header.getDestination()).equals(CHAT_TOPIC))
		{		
			String roomName = extractRoom(header.getDestination());			
			if (!roomPermissionEval.isBanned(subEvent.getUser().getName(), roomName))
			{					
				chatService.activeUser(header.getUser().getName() , roomName);
				notifier.notifyUserIsActive(roomName , header.getUser().getName());
				LOGGER.info("User '{}' has been subscribed to '{}' destination", header.getUser().getName(), 
						header.getDestination());				
			}
		}
		System.out.println(header.getDestination());
	}
	
	@EventListener
	public void onUnsubscribe(SessionUnsubscribeEvent subEvent)
	{
		StompHeaderAccessor header = StompHeaderAccessor.wrap(subEvent.getMessage());		
		if (extractPath(header.getSubscriptionId()).equals(CHAT_TOPIC))
		{
			String roomName = extractRoom(header.getSubscriptionId());			
			chatService.unsubscribe(header.getUser().getName() , roomName);			
		}
		LOGGER.debug("User '{}' has been unsubscribed", header.getUser().getName());
	}
	
	@EventListener
	public void onDisconnect(SessionDisconnectEvent subEvent)
	{
		StompHeaderAccessor header = StompHeaderAccessor.wrap(subEvent.getMessage());
		String name = header.getUser().getName();
		notifier.notifyUserLeaveRooms(name, chatService.deactiveUser(name));
	}
}
