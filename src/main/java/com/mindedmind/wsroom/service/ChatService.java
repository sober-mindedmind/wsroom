package com.mindedmind.wsroom.service;

import java.util.Collection;
import java.util.Set;

import org.springframework.security.access.prepost.PreAuthorize;

import com.mindedmind.wsroom.domain.Message;

public interface ChatService
{	
	Message findMessage(Long id);
	
	void saveMessage(Message msg, String roomName);
	
	@PreAuthorize("@messagePermissionEvaluator.canModify(#id, authentication)")
	void deleteMessage(Long id);

	@PreAuthorize("@messagePermissionEvaluator.canModify(#id, authentication)")
	void updateMessage(Long id, String txt);
	
	Set<String> getActiveUsers(String room);
	
	boolean isActive(String userName, String room);
	
	void deactiveUser(String user, String room);
	
	/**
	 * Makes user inactive in all rooms.
	 * 
	 * @param user - the name of user
	 * @return collection of rooms at which the given user was deactivated 
	 */
	Collection<String> deactiveUser(String user);
	
	void activeUser(String user, String room);
		
	void subscribe(String user, String roomName, String password);
	
	void unsubscribe(String user, String room);
	
	/**
	 * Deactive all users in the given room;  
	 * 
	 * @param roomName - the room name
	 * @return the set of users which were in this room
	 */
	Set<String> deactiveAll(String roomName);
	
	@PreAuthorize("@roomPermissionEvaluator.isRoomOwner(authentication, #roomName)")
	void banUser(String name, String roomName, boolean ban);
	
}
