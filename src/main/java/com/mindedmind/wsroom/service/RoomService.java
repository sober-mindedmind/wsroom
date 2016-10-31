package com.mindedmind.wsroom.service;

import java.util.Set;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

import com.mindedmind.wsroom.domain.Room;

public interface RoomService
{	
	@PostAuthorize("returnObject.owner.name == principal.username or hasRole('ROLE_ADMIN')")
	Room findRoom(Long id);
	
	//@PostAuthorize("returnObject.owner.name == principal.username or hasRole('ROLE_ADMIN')")
	Room findByName(String name);
		
	Set<Room> findRoomsWhereUserIsOwner(String owner);
	
	@PreAuthorize("#room.owner.name == principal.username or hasRole('ROLE_ADMIN')")
	void save(Room room);
	
	void deleteByName(String name);
	
	/**
	 * Deletes the given room and all messages in this room.
	 * 
	 * @param room - the room instance
	 */
	@PreAuthorize("#room.owner.name == principal.username or hasRole('ROLE_ADMIN')")
	void delete(Room room);
		
	/** 
	 * @param user - the user name 
	 * @return list of rooms visible to the given user	
	 */
	Set<Room> getAllRooms(String user);
	
	@Secured("ROLE_ADMIN")
	Set<Room> getAllRooms();
	
	Set<Room> getSubsribedRooms(String user);
	
	byte[] loadRoomImage(String name);	
}
