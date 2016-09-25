package com.mindedmind.wsroom.web;

import static org.apache.commons.lang3.ArrayUtils.isEmpty;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;

import com.mindedmind.wsroom.domain.Room;
import com.mindedmind.wsroom.domain.User;
import com.mindedmind.wsroom.service.ChatService;
import com.mindedmind.wsroom.service.RoomService;
import com.mindedmind.wsroom.service.UserService;
import com.mindedmind.wsroom.service.impl.UserDetailsImpl;
import com.mindedmind.wsroom.util.ImageUtils;

@Controller
@RequestMapping(value = "/rooms")
@SessionAttributes({"room", "roomName"})
public class RoomFormController
{	
	private static Logger LOG = LoggerFactory.getLogger(RoomFormController.class);
	
	private final UserService userService;
	
	private final RoomService roomService;
	
	private final ChatService chatService;
	
	@Autowired
	public RoomFormController(UserService userService,
						  	  RoomService roomService,
						  	  ChatService chatService)
	{
		this.userService = userService;
		this.roomService = roomService;
		this.chatService = chatService;
	}	

	@PostMapping
	public String createOrUpdateRoom(@Valid @ModelAttribute("room") Room room,
									 BindingResult result,
									 @RequestParam(name = "users[]", required = false) Long[] allowedUsersId,
									 @RequestParam(name = "image", required = false) MultipartFile photo,
									 UsernamePasswordAuthenticationToken authToken,
									 SessionStatus sessionStatus,
									 Model model) throws IOException
	{
		if (result.hasErrors() || !ImageUtils.isValidImage(photo, 100000))
		{
			return "/room_constructor";
		}	
		User owner = ((UserDetailsImpl) authToken.getPrincipal()).getUser();
		room.setOwner(owner);
		if (!isEmpty(allowedUsersId))
		{			
			room.setAllowedUsers(userService.findUsers(allowedUsersId));			
			room.getAllowedUsers().add(owner);
		}
		if (photo != null)
		{
			room.setPhoto(photo.getBytes());
		}
		roomService.save(room);
		
		/* if room has been updated then we must deactivate all users in this room, if any */
		if (model.containsAttribute("roomName"))
		{
			chatService.deactiveAll((String) model.asMap().get("roomName"));
		}
		
		sessionStatus.setComplete();
		LOG.debug("Room '{}' has beed constructed/updated", room.getName());
		return "redirect:/index";
	}
	
	@GetMapping(params = "form")
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
			room = roomService.findByName(roomName , principal.getName());
			model.addAttribute("roomName" , room.getName());
		}				
		model.addAttribute("room" , room);		
		model.addAttribute("allUsers" , userService.findAll());		
		return "/room_constructor";
	}	

}
