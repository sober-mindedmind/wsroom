package com.mindedmind.wsroom.web;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.mindedmind.wsroom.domain.User;
import com.mindedmind.wsroom.service.ChatService;
import com.mindedmind.wsroom.service.UserService;

@RunWith(SpringRunner.class)
@WebMvcTest(UserFormController.class)
public class UserControllerTest
{
	@Autowired
    private MockMvc mvc;

	@MockBean
	private UserService userService;
	
	@MockBean
	private ChatService chatService;
	
	@Test 
	public void register_ModelContainsUserAttribute_True() throws Exception
	{
		mvc.perform(get("/users/registration?form=null")).andExpect(model().attributeExists("user"));
	}

	@Test 
	public void register_UserHasCorrectCredentials_True() throws Exception
	{
		User user = new User();
		user.setName("user1");
		user.setPassword("");
		mvc.perform(MockMvcRequestBuilders.post("/users/registration?form=null")
				.sessionAttr("user" , user)				
				.param("name" , "user")
				.param("password" , "password"))
			.andExpect(MockMvcResultMatchers.view().name("redirect:/login"));
		
		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class); 
		verify(userService).save(userCaptor.capture());
		assertEquals(userCaptor.getValue().getName() , "user");
		assertEquals(userCaptor.getValue().getPassword() , "password");
	}	
		
}
