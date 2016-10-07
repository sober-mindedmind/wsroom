package com.mindedmind.wsroom.web;

import static org.apache.commons.lang3.ArrayUtils.isEmpty;

import java.io.IOException;
import java.security.Principal;
import java.util.Collection;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;

import com.mindedmind.wsroom.domain.Room;
import com.mindedmind.wsroom.domain.User;
import com.mindedmind.wsroom.service.ChatService;
import com.mindedmind.wsroom.service.RoomService;
import com.mindedmind.wsroom.service.UserService;
import com.mindedmind.wsroom.util.ImageUtils;

@Controller
@SessionAttributes({"room", "roomName"})
public class RoomFormController
{	
	private static Logger LOG = LoggerFactory.getLogger(RoomFormController.class);
	
	private final UserService userService;
	
	private final RoomService roomService;
	
	private final ChatService chatService;
	
	public RoomFormController(UserService userService,
						  	  RoomService roomService,
						  	  ChatService chatService)
	{
		this.userService = userService;
		this.roomService = roomService;
		this.chatService = chatService;
	}	
	
	@PostMapping(value = "/rooms", params = "form")
	public String createOrUpdateRoom(@Valid @ModelAttribute("room") Room room,					 				 						 
					 				 BindingResult result,
					 				 @AuthenticationPrincipal(expression  = "user") User currentUser,
									 @RequestParam(name = "users[]", required = false) Long[] allowedUsersId,
									 @RequestParam(name = "image", required = false) MultipartFile photo,	
									 SessionStatus sessionStatus,
									 Model model) throws IOException
	{
		if (result.hasErrors() || !isValidImage(photo))
		{			
			return "/room_constructor";
		}
		saveOrUpdate(room, currentUser, allowedUsersId, photo, model);
		sessionStatus.setComplete();
		LOG.debug("Room '{}' has been created/updated", room.getName());
		return "redirect:/index";
	}
	
	@PostMapping(value = "/admin/rooms", params = "form")
	public String createOrUpdateRoomAdmin(@Valid @ModelAttribute("room") Room room,					 				 
										  BindingResult result,
										  @RequestParam(name = "ownerId", required = false) Long newOwnerId,
										  @RequestParam(name = "users[]", required = false) Long[] allowedUsersId,
										  @RequestParam(name = "image", required = false) MultipartFile photo,	
 										  SessionStatus sessionStatus,
										  Model model) throws IOException
	{
		if (result.hasErrors() || !isValidImage(photo))
		{			
			return "/room_constructor";
		}
		User newOwner = null;
		if (newOwnerId != null)
		{
			newOwner = userService.findUser(newOwnerId);
		}
		saveOrUpdate(room, newOwner, allowedUsersId, photo, model);
		sessionStatus.setComplete();
		LOG.debug("Room '{}' just has been created/updated by Admin", room.getName());
		return "redirect:/admin/admin_page";
	}
			
	private void saveOrUpdate(Room room, 
							  User owner, 
							  Long[] allowedUsersId, 
							  MultipartFile photo,
							  Model model) throws IOException
	{
		if (owner != null)
		{
			room.setOwner(owner);
			if (room.isNew())
			{
				room.getSubscribedUsers().add(owner);
			}
		}		
		setAllowedUsers(room, allowedUsersId);
		setPhoto(room, photo);
		roomService.save(room);
		deactiveUsersInRoom(model);
	}
	
	@GetMapping(value = "/rooms", params = "form")
	public String roomConstructor(Model model, 
								  @RequestParam(value = "name", required = false) String roomName, 
								  Principal principal)
	{		
		Room room;		
		if (roomName == null)
		{
			room = new Room();
			room.setName("Room_" + principal.getName());
			room.setActive(true);
		}
		else
		{
			room = roomService.findByName(roomName);
			model.addAttribute("roomName" , room.getName());
		}
		model.addAttribute("room" , room);
		model.addAttribute("allUsers" , userService.findAll());		
		return "/room_constructor";
	}	

	@GetMapping(value = "/admin/rooms", params = "form")
	public String adminRoomConstructor(Model model, 
									   @RequestParam(value = "name", required = false) String roomName, 
									   Principal principal)
	{
		Collection<User> owners = userService.findAll();
		model.addAttribute("owners", owners);
		return roomConstructor(model, roomName, principal);
	}
	
	private void setAllowedUsers(Room room, Long[] allowedUsersId)
	{
		if (!isEmpty(allowedUsersId))
		{			
			room.setAllowedUsers(userService.findUsers(allowedUsersId));
			room.getAllowedUsers().add(room.getOwner());
		}
	}
	
	private void setPhoto(Room room, MultipartFile photo) throws IOException
	{
		if (photo != null)
		{
			room.setPhoto(photo.getBytes());
		}
	}
	
	/**
	 * If room has been updated then we must deactivate all users in this room, if any.
	 * 
	 * @param model - the model instance in which the 'roomName' session attribute is kept 
	 */
	private void deactiveUsersInRoom(Model model)
	{		
		if (model.containsAttribute("roomName"))
		{
			chatService.deactiveAll((String) model.asMap().get("roomName"));
		}
	}
	
	/**
	 * Checks whether the given photo is valid and size of photo is less then of equals to 100kb. 
	 * 
	 * @param photo - the multipart file 
	 * @return {@code true} if photo is valid
	 */
	private static boolean isValidImage(MultipartFile photo)
	{
		return ImageUtils.isValidImage(photo, 100000);
	}
}
