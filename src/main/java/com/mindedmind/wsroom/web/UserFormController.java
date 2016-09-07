package com.mindedmind.wsroom.web;

import java.io.IOException;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.mindedmind.wsroom.domain.User;
import com.mindedmind.wsroom.service.UserService;
import com.mindedmind.wsroom.util.ImageUtils;

@Controller
public class UserFormController
{
	private static Logger LOG = LoggerFactory.getLogger(UserFormController.class);
	
	private final UserService userService;
	
	@Autowired
	public UserFormController(UserService userService)
	{	
		this.userService = userService;
	}	

	@GetMapping("/registration")
	public String register(@ModelAttribute("user") User user)
	{
		return "registration";
	}	
		
	@PostMapping("/registration")
	public String register(@Valid User user,
						   BindingResult result,
						   @RequestParam(name = "image", required = false) MultipartFile image)
								   throws IOException
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
		LOG.info("User '{}' has been saved", user.getName());
		return "redirect:/login";
	}
	
}
