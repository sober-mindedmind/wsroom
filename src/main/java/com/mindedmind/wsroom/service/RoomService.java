package com.mindedmind.wsroom.service;

import java.util.Collection;

import com.mindedmind.wsroom.domain.Room;

public interface RoomService
{	
	Room findRoom(Long id);
	
	Room findByName(String name);
		
	void save(Room room);
	
	void deleteByName(String name);
		
	void delete(Room room);
	
	Collection<Room> getAllRooms(String user);
	
	Collection<Room> getSubsribedRooms(String user);
	
	byte[] loadRoomImage(String name);
}
