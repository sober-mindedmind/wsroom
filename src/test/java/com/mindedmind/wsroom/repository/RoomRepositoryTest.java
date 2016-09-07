package com.mindedmind.wsroom.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.mindedmind.wsroom.domain.Room;
import com.mindedmind.wsroom.domain.User;

@RunWith(SpringRunner.class)
@DataJpaTest
public class RoomRepositoryTest
{
	@Autowired
	private RoomRepository roomRepository;

	@Autowired
	private UserRepository userRepository;
			
	@Before 
	public void setUp()
	{		
		User privateUser = new User();
		privateUser.setName("PrivateUser1");
		privateUser.setPassword("1111111111");
		
		Room privateRoom = new Room();
		privateRoom.setName("PrivateRoom1");
		privateRoom.setAllowedUsers(Collections.singleton(privateUser));
		
		User user = new User();
		user.setName("User1");		
		user.setPassword("111111");
		
		Room room = new Room(); 
		room.setName("Room1");
		room.setSubscribedUsers(Collections.singleton(user));
		

		userRepository.save(privateUser);
		roomRepository.save(privateRoom);
		userRepository.save(user);
		roomRepository.save(room);			
		
	}
	
	@Test 
	public void findUserRooms_UserSubscribedOnRooms_True()
	{		
		Collection<Room> rooms = roomRepository.findUserRooms("User1");
		assertEquals(rooms.size() , 1);
		assertTrue(rooms.contains(roomRepository.findByName("Room1")));		
	}
	
	@Test
	public void allRoomsVisibleForUser_UserSeesOnlyAllowedRooms_True()
	{		
		Room room2 = new Room(); 
		room2.setName("Room2");
		roomRepository.save(room2);
		
		Collection<Room> rooms = roomRepository.allRoomsVisibleForUser("User1");
		assertEquals(rooms.size() , 1);
		assertTrue(rooms.contains(room2));
	}
}
