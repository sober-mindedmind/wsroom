package com.mindedmind.wsroom.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
import org.springframework.web.multipart.MultipartFile;

import com.mindedmind.wsroom.domain.User;
import com.mindedmind.wsroom.service.ChatService;
import com.mindedmind.wsroom.service.UserService;
import com.mindedmind.wsroom.service.impl.UserDetailsImpl;
import com.mindedmind.wsroom.util.ImageUtils;

@Controller
@RequestMapping("/users/registration")
@SessionAttributes({"user", "oldName"})
public class UserFormController
{
	private static Logger LOG = LoggerFactory.getLogger(UserFormController.class);
	
	private final UserService userService;
	
	private final ChatService chatService;
	
	@Autowired
	public UserFormController(ChatService chatService, UserService userService)
	{	
		this.userService = userService;
		this.chatService = chatService;
	}	

	@GetMapping(params = "form")
	public String registerUserForm(Model model)
	{
		User user = new User();
		model.addAttribute("user" , user);	
		return "registration";
	}
	
	@GetMapping(value = "/sds", params = "edit")
	public String updateUserForm(Model model, UsernamePasswordAuthenticationToken authToken)
	{
		User currentUser = ((UserDetailsImpl) authToken.getPrincipal()).getUser();
		model.addAttribute("user", currentUser);
		model.addAttribute("oldName", currentUser.getName());
		return "registration";
	}
	
	@PostMapping
	public String createOrUpdateUser(@Valid @ModelAttribute("user") User user,
									 BindingResult result,
									 @RequestParam(name = "image", required = false) MultipartFile image,						  
									 HttpSession httpSession,
									 HttpServletRequest request) throws IOException, ServletException
	{
		if (result.hasErrors() || !ImageUtils.isValidImage(image , 60000))
		{
			return "/registration";
		}
		if (image != null)
		{
			user.setPhoto(image.getBytes());
		}				
		userService.save(user);

		/* if user has been changed then we must deactive this user in all rooms in which he exists */
		String oldName = (String) httpSession.getAttribute("oldName");
		if (oldName != null)
		{
			chatService.deactiveUser(oldName);
		}
		
		request.logout();
		LOG.info("User '{}' has been saved", user.getName());
		return "redirect:/login";
	}
	
}
