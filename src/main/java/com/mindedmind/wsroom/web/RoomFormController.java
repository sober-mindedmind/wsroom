package com.mindedmind.wsroom.web;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;

import javax.validation.Valid;

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
import com.mindedmind.wsroom.service.RoomService;
import com.mindedmind.wsroom.service.UserService;
import com.mindedmind.wsroom.service.impl.UserDetailsImpl;
import com.mindedmind.wsroom.util.ImageUtils;

@Controller
@RequestMapping(value = "/rooms")
@SessionAttributes("room")
public class RoomFormController
{	
	private static Logger LOG = LoggerFactory.getLogger(RoomFormController.class);
	
	private final UserService userService;
	
	private final RoomService roomService;
		
	@Autowired
	public RoomFormController(UserService userService,
						  	  RoomService roomService)
	{
		this.userService = userService;
		this.roomService = roomService;
	}	
	
	@PostMapping
	public String createRoom(@Valid @ModelAttribute("room") Room room,
							 BindingResult result,
							 @RequestParam(name = "image", required = false) MultipartFile photo,
							 UsernamePasswordAuthenticationToken authToken,
							 SessionStatus sessionStatus) throws IOException
	{
		if (result.hasErrors() || !ImageUtils.isValidImage(photo, 100000))
		{
			return "/room_constructor";
		}
		User owner = ((UserDetailsImpl) authToken.getPrincipal()).getUser();
		room.setOwner(owner);
		if (room.getAllowedUsers() != null)
		{
			room.getAllowedUsers().removeIf(e -> e == null);
			room.getAllowedUsers().add(owner);
		}
		if (photo != null)
		{
			room.setPhoto(photo.getBytes());
		}
		roomService.save(room);
		sessionStatus.setComplete();
		LOG.debug("Room '{}' has beed constructed", room.getName());
		return "redirect:/index";
	}
		
	@GetMapping(params = "form")
	public String roomConstructor(Model model, Principal principal)
	{		
		Room room = new Room();
		room.setAllowedUsers(new ArrayList<User>());
		room.setName("Room_" + principal.getName());
		room.setActive(true);		
		model.addAttribute("room" , room);
		model.addAttribute("allUsers" , userService.findAll());
		return "/room_constructor";
	}	
	
}
