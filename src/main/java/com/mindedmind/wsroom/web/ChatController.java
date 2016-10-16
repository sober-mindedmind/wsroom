package com.mindedmind.wsroom.web;

import static com.mindedmind.wsroom.wsevent.DestinationPath.DELETE_MESSAGE_TOPIC_DEST;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;

import java.security.Principal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.mindedmind.wsroom.domain.Message;
import com.mindedmind.wsroom.domain.Room;
import com.mindedmind.wsroom.domain.User;
import com.mindedmind.wsroom.dto.ChatMessageDto;
import com.mindedmind.wsroom.dto.ErrorDto;
import com.mindedmind.wsroom.dto.RoomDto;
import com.mindedmind.wsroom.dto.TypingDto;
import com.mindedmind.wsroom.dto.UserDto;
import com.mindedmind.wsroom.service.ChatService;
import com.mindedmind.wsroom.service.RoomService;
import com.mindedmind.wsroom.service.UserService;
import com.mindedmind.wsroom.service.impl.EntityNotFoundException;
import com.mindedmind.wsroom.service.impl.SubscriptionException;
import com.mindedmind.wsroom.service.impl.UserDetailsImpl;

@Controller
public class ChatController
{	
	private final ChatService chatService;
		
	private final UserService userService;
	
	private final RoomService roomService;
	
	private final SimpMessagingTemplate messagingTemplate;
	
	public ChatController(ChatService chatService, 
						  UserService userService,
						  RoomService roomService,
						  SimpMessagingTemplate messagingTemplate)
	{
		this.chatService = chatService;
		this.userService = userService;
		this.roomService = roomService;
		this.messagingTemplate = messagingTemplate;
	}
		
	@MessageMapping("/chat/{room}")
	public ChatMessageDto handleMessage(@Payload ChatMessageDto messageDto, 
									 	@DestinationVariable("room") String roomName,
									 	Authentication auth)
	{		
		User currentUser = ((UserDetailsImpl)auth.getPrincipal()).getUser();
		Date currentDate = new Date();				
		Message msg = new Message();
		msg.setText(messageDto.getText());
		msg.setTime(currentDate);
		msg.setOwner(currentUser);
		chatService.saveMessage(msg, roomName);
				
		messageDto.setId(msg.getId());		
		messageDto.setServerTime(currentDate.toString());		
		messageDto.setOwner(currentUser.getName());
		messageDto.setOwnerId(currentUser.getId());
		messageDto.setRoomId(msg.getRoom().getId());
		
		/* send to /topic/chat/{room} */
		return messageDto;
	}
		
	@MessageMapping("/typing/{room}")
	public TypingDto handleTyping(Principal principal, 
								  @DestinationVariable("room") String roomName)
	{
		return new TypingDto(principal.getName(), roomName);
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
	
	@ResponseStatus(HttpStatus.OK)
	@DeleteMapping("/rooms/{name}")
	public void removeRoom(@PathVariable("name") String name)
	{			
		Room room = roomService.findByName(name);
		roomService.delete(room);
		chatService.deactiveAll(name);
	}
		
	@GetMapping("/rooms/{name}/image")
	@ResponseBody
	public byte[] loadRoomImage(@PathVariable("name") String name)
	{
		return roomService.loadRoomImage(name);
	}	
	
	/**
	 * Returns all rooms in the application, call of this method is only acceptable for administrator.
	 * 
	 * @return all rooms in the application
	 */
	@GetMapping(value = "/rooms", params="all")
	@ResponseBody
	public Set<RoomDto> getAllRooms()
	{
		return roomService.getAllRooms().stream().map(RoomDto::new).collect(toSet());		
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
	
	@GetMapping("/users")
	@ResponseBody
	public Set<UserDto> getAllUsers()
	{
		return userService.findAll().stream().map(UserDto::new).collect(toSet());
	}
	
	@DeleteMapping("/users/{name}")
	@ResponseStatus(HttpStatus.OK)
	public void removeUser(@PathVariable("name") String name)
	{
		userService.removeUser(name);
		chatService.deactiveUser(name);
	}
	
	@DeleteMapping("/users/{userId}/rooms/{roomId}/messages/{msgId}")
	@ResponseStatus(HttpStatus.OK)
	public void removeMessage(@PathVariable("userId") Long userId,
							  @PathVariable("roomId") String roomId,
							  @PathVariable("msgId") Long msgId)
	{
		chatService.removeMessage(userId , msgId);
		messagingTemplate.convertAndSend(DELETE_MESSAGE_TOPIC_DEST + roomId, msgId);
	}
	
	@GetMapping("/users/principal")
	@ResponseBody
	public UserDto getPrincipal(@AuthenticationPrincipal(expression = "user") User user)
	{
		return new UserDto(user);
	}
		
	@GetMapping("/rooms/myrooms")
	@ResponseBody
	public Set<String> listRoomsWherePrincipalIsOwner(Principal principal)  
	{
		return roomService.findRoomsWhereUserIsOwner(principal.getName())
				.stream()
				.map(r -> r.getName())
				.collect(toSet());
	}

	@ExceptionHandler({SubscriptionException.class, EntityNotFoundException.class})
	@ResponseStatus(BAD_REQUEST)
	@ResponseBody
	public ErrorDto onException(Exception e)
	{		
		return new ErrorDto(e.getMessage());
	}
}
