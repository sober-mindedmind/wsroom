package com.mindedmind.wsroom.web;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.mindedmind.wsroom.domain.Room;
import com.mindedmind.wsroom.domain.User;
import com.mindedmind.wsroom.service.ChatService;
import com.mindedmind.wsroom.service.RoomService;
import com.mindedmind.wsroom.service.UserService;
import com.mindedmind.wsroom.service.impl.UserDetailsImpl;

@RunWith(SpringRunner.class)
@WebMvcTest(RoomFormController.class)
public class RoomControllerTest
{
	@Autowired
    private MockMvc mvc;
	
	@MockBean
	private RoomService roomService;
		
	@MockBean
	private UserService userService;
	
	@MockBean
	private ChatService chatService;
	
	@Test
	public void createRoom_IsRoomSuccessfullySaved_True() throws Exception
	{
		User owner = new User();
		owner.setId(0L);
		owner.setName("user");
		owner.setPassword("1");
		
		User user1 = new User();
		user1.setId(1L);
		user1.setName("user1");
		User user2 = new User();
		user2.setId(2L);
		user2.setName("user2");
		
		when(userService.findUsers(new Long[] {1L, 2L})).thenReturn(new HashSet<>(Arrays.asList(user1, user2)));
		
		Room sessionRoom = new Room();
		sessionRoom.setAllowedUsers(new HashSet<User>());
		
		UsernamePasswordAuthenticationToken authToken = Mockito.mock(UsernamePasswordAuthenticationToken.class);
		when(authToken.getPrincipal()).thenReturn(new UserDetailsImpl(owner));
		
		mvc.perform(MockMvcRequestBuilders.post("/rooms")
				.param("name" , "room1")
				.param("password" , "password")
				.param("description" , "the room")
				.param("users[]" , "1", "2")				
				.principal(authToken)
				.sessionAttr("room" , sessionRoom)
				).andExpect(MockMvcResultMatchers.view().name("redirect:/index"));
		
		ArgumentCaptor<Room> roomCaptor = ArgumentCaptor.forClass(Room.class); 
		verify(roomService).save(roomCaptor.capture());
		Room room = roomCaptor.getValue();
		
		assertEquals(room.getName() , "room1");
		assertEquals(room.getPassword() , "password");
		assertEquals(room.getDescription() , "the room");
		assertEquals(room.getOwner(), owner);
		assertEquals(room.getAllowedUsers().size(), 3);
		room.getAllowedUsers().removeIf(e -> e.getId() == 1	|| e.getId() == 2);
		assertEquals(room.getAllowedUsers().size(), 1);
	}
	
}
