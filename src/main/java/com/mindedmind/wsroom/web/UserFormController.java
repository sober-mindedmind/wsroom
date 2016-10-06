package com.mindedmind.wsroom.web;

import static com.mindedmind.wsroom.domain.Role.ROLE_USER;
import static java.util.stream.Collectors.toSet;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;

import com.mindedmind.wsroom.domain.Role;
import com.mindedmind.wsroom.domain.User;
import com.mindedmind.wsroom.service.ChatService;
import com.mindedmind.wsroom.service.UserService;
import com.mindedmind.wsroom.service.impl.UserDetailsImpl;
import com.mindedmind.wsroom.util.ImageUtils;

@Controller
@RequestMapping
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

	@GetMapping(value = "/users/registration", params = "form")
	public String registrationForm(Model model,
								   @AuthenticationPrincipal UserDetailsImpl auth)
	{		
		User currentUser;		
		if (auth == null)
		{
			currentUser = new User();
		}	
		else
		{
			currentUser = auth.getUser();
			model.addAttribute("oldName", currentUser.getName());
		}
		model.addAttribute("user",  currentUser);
		return "registration";
	}
	
	@GetMapping(value = "/admin/users/registration", params = "form")
	public String registrationFormAdmin(Model model, @RequestParam(value = "id", required = false) Long id)
	{				
		User user;
		if (id == null)
		{
			user = new User();
		}
		else
		{
			user = userService.findUser(id);
			model.addAttribute("oldName", user.getName());
		}	
		model.addAttribute("user", user);		
		model.addAttribute("allRoles", Role.values());
		return "registration";
	}
		
	@PostMapping(value = "/users/registration", params = "form")
	public String createOrUpdateUser(@Valid @ModelAttribute("user") User user,
									 BindingResult result,
									 @RequestParam(name = "image", required = false) MultipartFile image,						  
									 HttpServletRequest request,			
									 Model model) throws IOException, ServletException
	{
		if (result.hasErrors() || !isValidUserImage(image))
		{
			return "/registration";
		}
		saveOrUpdate(user, image, model);
		request.logout();
		LOG.info("User '{}' has been updated", user.getName());
		return "redirect:/login";
	}

	@PostMapping(value = "/admin/users/registration", params = "form")
	public String createOrUpdateUserAdmin(@Valid @ModelAttribute("user") User user,
										  BindingResult result,
										  @RequestParam(name = "image", required = false) MultipartFile image,
										  Model model,
										  @RequestParam(name = "user_roles[]", required = false) String[] roles,
										  SessionStatus sessionStatus) throws IOException
	{		
		if (result.hasErrors() || !isValidUserImage(image))
		{
			model.addAttribute("allRoles", Role.values());
			return "/registration";
		}	
		user.setRoles(Arrays.stream(roles).map(Role::getRole).collect(toSet()));
		saveOrUpdate(user, image, model);
		sessionStatus.setComplete();
		return "redirect:/admin/admin_page";
	}
	
	private void saveOrUpdate(User user, MultipartFile image, Model model) throws IOException
	{
		if (user.isNew())
		{
			user.getRoles().add(ROLE_USER);
		}				
		setPhoto(user, image);
		userService.save(user);
		deactiveUser(model);
	}
	
	private void setPhoto(User user, MultipartFile image) throws IOException
	{
		if (image != null)
		{
			user.setPhoto(image.getBytes());
		}
	}
	
	/**
	 * If user has been changed then we must deactive this user in all rooms in which he exists.
	 * 
	 * @param model - the model where the session 'oldName' attribute is kept, 'oldName' keeps the old name of the
	 * user
	 */
	private void deactiveUser(Model model)
	{
		if (model != null && model.containsAttribute("oldName"))
		{
			chatService.deactiveUser((String) model.asMap().get("oldName"));
		}
	}
	
	/**
	 * Checks whether this image has correct extension and appropriate size (less then of equals to 60kb).
	 * 
	 * @param image - the multipart file image
	 * @return {@code true} if this image has correct extension and appropriate size
	 */
	private static boolean isValidUserImage(MultipartFile image)
	{
		return ImageUtils.isValidImage(image , 60000);
	}

}
