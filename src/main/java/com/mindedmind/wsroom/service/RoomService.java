package com.mindedmind.wsroom.service;

import java.util.Collection;
import java.util.Set;

import com.mindedmind.wsroom.domain.Room;

public interface RoomService
{	
	Room findRoom(Long id);
	
	Room findByName(String name, String owner);
		
	Set<Room> findRoomsWhereUserIsOwner(String owner);
	
	void save(Room room);
	
	boolean deleteByName(String name, String owner);
		
	void delete(Room room);
	
	Collection<Room> getAllRooms(String user);
	
	Collection<Room> getSubsribedRooms(String user);
	
	byte[] loadRoomImage(String name);
}
