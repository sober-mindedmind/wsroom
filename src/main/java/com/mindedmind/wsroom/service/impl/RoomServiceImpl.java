package com.mindedmind.wsroom.service.impl;

import java.util.Collection;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.mindedmind.wsroom.domain.Room;
import com.mindedmind.wsroom.repository.MessageRepository;
import com.mindedmind.wsroom.repository.RoomRepository;
import com.mindedmind.wsroom.service.RoomService;
import com.mindedmind.wsroom.util.ImageUtils;

@Component
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

	@Transactional
	@Override public Room findByName(String name, String owner)
	{
		return owner == null ? roomRepository.findByName(name) 
							 : roomRepository.findRoomOfOwner(name, owner);
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

	@Transactional
	@Override public boolean deleteByName(String name, String owner)
	{
		Room room = roomRepository.findRoomOfOwner(name, owner);		
		boolean deleted = false;
		if (room != null)
		{
			messageRepository.deleteMessages(room);
			roomRepository.delete(room);
			deleted = true;
		}
		return deleted;
	}

	@Transactional
	@Override public Collection<Room> getAllRooms(String user)
	{
		return roomRepository.allRoomsVisibleForUser(user);
	}

	@Override public void delete(Room room)
	{
		roomRepository.delete(room);
	}

	@Transactional
	@Override public Collection<Room> getSubsribedRooms(String user)
	{
		return roomRepository.findUserRooms(user);
	}

	@Transactional
	@Override public byte[] loadRoomImage(String name)
	{
		return roomRepository.findByName(name).getPhoto();
	}

	@Transactional
	@Override public Set<Room> findRoomsWhereUserIsOwner(String name)
	{
		return roomRepository.findRoomsWhereUserIsOwner(name);
	}

}
