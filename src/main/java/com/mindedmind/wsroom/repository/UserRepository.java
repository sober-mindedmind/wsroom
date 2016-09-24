package com.mindedmind.wsroom.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mindedmind.wsroom.domain.User;

public interface UserRepository extends JpaRepository<User, Long>
{
	@Query("select u from User u where u.name = ?1")
	User findUserByName(String name);
	
	@Query("select u from User u where u.id in ?1")
	Set<User> findUsersById(Long... ids);		
}
