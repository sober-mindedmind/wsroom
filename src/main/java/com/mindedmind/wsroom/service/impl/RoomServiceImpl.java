package com.mindedmind.wsroom.service.impl;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.mindedmind.wsroom.domain.Room;
import com.mindedmind.wsroom.repository.MessageRepository;
import com.mindedmind.wsroom.repository.RoomRepository;
import com.mindedmind.wsroom.service.RoomService;
import com.mindedmind.wsroom.util.ImageUtils;

@Component
@Transactional
public class RoomServiceImpl implements RoomService
{

	private RoomRepository roomRepository;
		
	private MessageRepository messageRepository;
	
	@Autowired
	public RoomServiceImpl(RoomRepository roomRepository, MessageRepository messageRepository)
	{
		this.roomRepository = roomRepository;	
		this.messageRepository = messageRepository;
	}
	
	@Override public Room findRoom(Long id)
	{		
		return roomRepository.findOne(id);
	}
	
	@Override public Room findByName(String name)
	{
		return roomRepository.findByName(name);
	}

	@Override public void save(Room room)
	{
		room.setPhoto(ImageUtils.resize(room.getPhoto() , 30 , 30));
		
		/* Forbid empty passwords */
		if ("".equals(room.getPassword()))
		{
			room.setPassword(null);
		}
		
		roomRepository.save(room);
	}

	@Override public void deleteByName(String name)
	{
		Room room = findByName(name);
		if (room == null)
		{
			throw new EntityNotFoundException(
					String.format("Can't delete room, because can't find the room with the given name '%s'" , name));		
		}		
		delete(room);
	}
		
	@Override public Set<Room> getAllRooms(String user)
	{
		return roomRepository.allRoomsVisibleForUser(user);
	}
	
	@Override public void delete(Room room)
	{
		messageRepository.deleteAllMessagesInRoom(room);
		roomRepository.delete(room);
	}

	@Override public Set<Room> getSubsribedRooms(String user)
	{
		return roomRepository.findUserRooms(user);
	}

	@Override public byte[] loadRoomImage(String name)
	{
		return roomRepository.findByName(name).getPhoto();
	}

	@Override public Set<Room> findRoomsWhereUserIsOwner(String name)
	{
		return roomRepository.findRoomsWhereUserIsOwner(name);
	}

	@Override public Set<Room> getAllRooms()
	{
		return new HashSet<>(roomRepository.findAll());
	}
}
