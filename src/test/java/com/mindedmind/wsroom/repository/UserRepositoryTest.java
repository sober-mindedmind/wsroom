package com.mindedmind.wsroom.repository;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.mindedmind.wsroom.domain.User;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryTest
{
	@Autowired
	private UserRepository repository;

	@Before 
	public void setUp() throws Exception
	{
		
	}

	@Test 
	public void findUserByName_UserExists_True()
	{
		User user = new User();
		user.setName("User");
		user.setPassword("Password");	
		repository.save(user);		
		User existedUser = repository.findUserByName("User");		
		assertEquals(user , existedUser);		
	}

}
