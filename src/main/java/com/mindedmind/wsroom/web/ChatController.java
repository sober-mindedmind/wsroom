package com.mindedmind.wsroom.web;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;

import java.security.Principal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.mindedmind.wsroom.domain.Message;
import com.mindedmind.wsroom.domain.User;
import com.mindedmind.wsroom.service.ChatService;
import com.mindedmind.wsroom.service.RoomService;
import com.mindedmind.wsroom.service.UserService;
import com.mindedmind.wsroom.service.impl.SubscriptionException;
import com.mindedmind.wsroom.service.impl.UserDetailsImpl;
import com.mindedmind.wsroom.web.dto.ChatMessageDto;
import com.mindedmind.wsroom.web.dto.RoomDto;
import com.mindedmind.wsroom.web.dto.UserDto;

@Controller
public class ChatController
{	
	private final ChatService chatService;
		
	private final UserService userService;
	
	private final RoomService roomService;
	
	@Autowired
	public ChatController(ChatService chatService, 
						  UserService userService,
						  RoomService roomService)
	{
		this.chatService = chatService;
		this.userService = userService;
		this.roomService = roomService;
	}
		
	@MessageMapping("/chat/{room}")
	public ChatMessageDto handleMessage(@Payload ChatMessageDto messageDto, 
									 	@DestinationVariable("room") String roomName,
									 	UsernamePasswordAuthenticationToken authToken)
	{		
		
		Date currentDate = new Date();
		User owner = ((UserDetailsImpl)(authToken.getPrincipal())).getUser();
		messageDto.setServerTime(currentDate.toString());		
		messageDto.setOwner(owner.getName());
		
		Message msg = new Message();
		msg.setText(messageDto.getText());
		msg.setTime(currentDate);
		msg.setOwner(owner);
		chatService.saveMessage(msg, roomName);
				
		/* send to /topic/chat/{room} */
		return messageDto;
	}
		
	@MessageMapping("/typing/{room}")
	public UserDto handleTyping(Principal principal)
	{
		return new UserDto(principal.getName());
	}	
	
	/**
	 * Returns list of active users in the given room i.e. returns users which are online at the moment of this method 
	 * invocation. 
	 * 
	 * @param room - the name of the room
	 * @return list of active users in the given room 
	 */
	@GetMapping("/rooms/{room}/ausers")
	@ResponseBody
	public Collection<String> getUsersInRoom(@PathVariable("room") String room)
	{
		return chatService.getActiveUsers(room);
	}
	
	/**
	 * Returns list of all rooms that have been registered in the chat application and visible for requested user.  
	 * 
	 * @param principal - the current principal instance
	 * @return list of all rooms
	 */
	@GetMapping("/rooms")
	@ResponseBody
	public Collection<RoomDto> listAllRooms(Principal principal)
	{		
		List<RoomDto> dtos = roomService.getAllRooms(principal.getName()).stream().map(room -> {
			RoomDto dto = new RoomDto(room);		
			Collection<String> users = chatService.getActiveUsers(room.getName());			
			dto.setUsersCount(users == null ? 0 : users.size());			
			return dto;
		}).collect(toList());	
		return dtos;
	}
		
	@GetMapping("/rooms/{name}/image")
	@ResponseBody
	public byte[] loadRoomImage(@PathVariable("name") String name)
	{
		return roomService.loadRoomImage(name);
	}	
	
	/**
	 * Returns list of room on which the current user has been subscribed. The path does not include user path variable
	 * because users must not see rooms of other user.
	 * 
	 * @param principal - the current principal instance
	 * @return list of rooms on which the current user has been subscribed
	 */
	@GetMapping("/users/rooms")
	@ResponseBody
	public Collection<RoomDto> listSubscribedRooms(Principal principal)
	{
		Collection<RoomDto> dtos = roomService.getSubsribedRooms(principal.getName()).stream().map(room -> {			
			RoomDto dto = new RoomDto(room);
			dto.setActiveUsers(chatService.getActiveUsers(room.getName()));
			return dto;
		}).collect(toList());
		return dtos;
	}
	
	@PostMapping("/users/rooms")	
	@ResponseStatus(CREATED)
	public void subscribeOnRooms(@RequestBody Collection<RoomDto> subscribedRooms, Principal principal)
	{
		subscribedRooms.forEach((dto) -> chatService.subscribe(principal.getName() , dto.getName(), dto.getPassword()));
	}		
	
	@GetMapping("/users/{name}/image")
	@ResponseBody
	public byte[] loadUserProfileImage(@PathVariable("name") String name)
	{	
		return userService.loadUserImage(name);
	}	

	@ExceptionHandler(SubscriptionException.class)
	@ResponseStatus(BAD_REQUEST)
	@ResponseBody
	public String onException(SubscriptionException e)
	{
		/* TODO add exception message dto  */
		return e.getMessage();
	}
	
}
