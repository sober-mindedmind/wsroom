package com.mindedmind.wsroom.security;

import static com.mindedmind.wsroom.domain.Role.ROLE_ADMIN;
import static com.mindedmind.wsroom.security.SecurityUtils.hasRole;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.mindedmind.wsroom.repository.RoomRepository;

@Component
public class RoomPermissionEvaluator
{
	private final RoomRepository roomRepository;
	
	public RoomPermissionEvaluator(RoomRepository roomRepository)
	{
		this.roomRepository = roomRepository;
	}

	/**
	 * Checks whether the given authenticated user is the creator of the given room. In case if the current principal is
	 * the owner of the given room then any actions are allowed on the given room.
	 * 
	 * @param roomName - the name of the room
	 * @param auth - the authentication token of the current user
	 * 
	 * @return {@code true} if the given authenticated user is the owner of the given room
	 */
	public boolean isRoomOwner(Authentication auth, String roomName)
	{		
		return hasRole(auth , ROLE_ADMIN) || roomRepository.isRoomOwner(roomName, auth.getName());
	}
	
	public boolean isBanned(String userName, String roomName)
	{
		return roomRepository.isBanned(userName, roomName);
	}
}
