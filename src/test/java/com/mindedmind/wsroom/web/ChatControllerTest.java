package com.mindedmind.wsroom.web;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindedmind.wsroom.domain.Room;
import com.mindedmind.wsroom.domain.User;
import com.mindedmind.wsroom.dto.ChatMessageDto;
import com.mindedmind.wsroom.dto.RoomDto;
import com.mindedmind.wsroom.service.ChatService;
import com.mindedmind.wsroom.service.RoomService;
import com.mindedmind.wsroom.service.UserService;
import com.mindedmind.wsroom.service.impl.UserDetailsImpl;
import com.mindedmind.wsroom.wsevent.EventNotifier;

@RunWith(SpringRunner.class)
@WebMvcTest(value = ChatController.class, secure = false)
public class ChatControllerTest
{

	private MessageChannel clientOutboundChannel;

	private MethodHandler annotationMethodHandler;
	
	@Autowired
    private MockMvc mvc;
		
	@MockBean
	private RoomService roomService;
	
	@MockBean
	private ChatService chatService;	
	
	@MockBean
	private UserService userService;
	
	@MockBean
	private EventNotifier notifier;
	
	@Autowired
    private ChatController controller;
	
	
	@Before
	public void setUp() throws Exception
	{

		this.clientOutboundChannel = new MessageChannel();

		this.annotationMethodHandler = new MethodHandler (
				new MessageChannel(), clientOutboundChannel, 
				new SimpMessagingTemplate(new MessageChannel()));
	
		this.annotationMethodHandler.registerHandler(controller);
		this.annotationMethodHandler.setDestinationPrefixes(Arrays.asList("/app"));
		this.annotationMethodHandler.setMessageConverter(new MappingJackson2MessageConverter());
		this.annotationMethodHandler.setApplicationContext(new StaticApplicationContext());
		this.annotationMethodHandler.afterPropertiesSet();
	}

	@Test 
	public void handleMessage_MessageReceived_True() throws JsonProcessingException
	{		
		StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
		headers.setSubscriptionId("0");
		headers.setDestination("/app/chat/Room1");
		headers.setSessionId("0");
		User user = new User();
		user.setName("User");
		Authentication authToken = Mockito.mock(UsernamePasswordAuthenticationToken.class);
		when(authToken.getPrincipal()).thenReturn(new UserDetailsImpl(user));
		
		headers.setUser(authToken);
		headers.setSessionAttributes(new HashMap<String, Object>());
		ChatMessageDto chatMessage = new ChatMessageDto();		
		chatMessage.setText("Hello");		
		byte[] payload = new ObjectMapper().writeValueAsBytes(chatMessage);
		
		Message<byte[]> message = MessageBuilder.withPayload(payload).setHeaders(headers).build();
		when(chatService.isActive(Mockito.any(String.class) , Mockito.any(String.class))).thenReturn(true);
		this.annotationMethodHandler.handleMessage(message);
				
		ArgumentCaptor<com.mindedmind.wsroom.domain.Message> messArgumentCaptor 
			= ArgumentCaptor.forClass(com.mindedmind.wsroom.domain.Message.class);
		
		Mockito.verify(chatService, times(1)).saveMessage(messArgumentCaptor.capture(), Mockito.eq("Room1"));
				
		assertEquals(messArgumentCaptor.getValue().getText(), chatMessage.getText());		
	}
	
	@Test
	public void listSubscribedRooms_UsersRoomsAreFetched_True() throws Exception
	{
		Room r1 = new Room();
		r1.setOwner(new User());
		r1.setName("R1");
		Room r2 = new Room();
		r2.setOwner(new User());
		r2.setName("R2");
		when(roomService.getSubsribedRooms("user")).thenReturn(new HashSet<>(Arrays.asList(r1, r2)));	
		when(chatService.getActiveUsers(Mockito.any(String.class))).thenReturn(null);		
		mvc.perform(MockMvcRequestBuilders.get("/users/rooms/").principal(() -> "user"))
				.andExpect(MockMvcResultMatchers.content()
				.json(new ObjectMapper().writeValueAsString(Arrays.asList(new RoomDto(r1), 
						new RoomDto(r2)))));
	}	

	@Test
	public void listAllRooms_UserObtainsAllRooms_ListNotEmpty() throws Exception
	{
		Room r1 = new Room();
		r1.setOwner(new User());
		r1.setName("R1");
		Room r2 = new Room();
		r2.setName("R2");		
		r2.setOwner(new User());
		when(roomService.getAllRooms("user")).thenReturn(new HashSet<>(Arrays.asList(r1, r2)));
		mvc.perform(MockMvcRequestBuilders.get("/rooms").principal(() -> "user"))
			.andExpect(MockMvcResultMatchers.content()
			.json(new ObjectMapper().writeValueAsString(Arrays.asList(new RoomDto(r1), 
					new RoomDto(r2)))));		
	}
	
	@Test
	public void getUsersInRoom_RoomContainsUsers_True() throws Exception
	{
		when(chatService.getActiveUsers("room1")).thenReturn(new HashSet<>(Arrays.asList("user1", "user2")));
		mvc.perform(MockMvcRequestBuilders.get("/rooms/room1/ausers").principal(() -> "user"))
			.andExpect(MockMvcResultMatchers.content()
			.json(new ObjectMapper().writeValueAsString(Arrays.asList("user1", "user2"))));
	}
	
	@Test
	public void subscribeOnRooms_AllRoomsAreSubscribed_True() throws Exception
	{	
		ObjectMapper mapper = new ObjectMapper();
		RoomDto room1 = new RoomDto();
		room1.setName("room1");
		room1.setPassword("1");
		RoomDto room2 = new RoomDto();
		room2.setName("room2");
		room2.setPassword("2");
		Principal principal = () -> "user";
		mvc.perform(MockMvcRequestBuilders.post("/users/rooms/")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(mapper.writeValueAsString(Arrays.asList(room1, room2)))
				.principal(principal)).andExpect(MockMvcResultMatchers.status().is(201));
		
		verify(chatService , times(1)).subscribe(principal.getName(), room1.getName() , room1.getPassword());
		verify(chatService , times(1)).subscribe(principal.getName(), room2.getName() , room2.getPassword());
	}	
}
