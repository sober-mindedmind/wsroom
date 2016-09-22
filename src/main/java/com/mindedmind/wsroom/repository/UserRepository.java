package com.mindedmind.wsroom.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mindedmind.wsroom.domain.User;

public interface UserRepository extends JpaRepository<User, Long>
{
	@Query("select u from User u where u.name = :name")
	User findUserByName(@Param("name") String name);
	
	@Query("select u from User u where u.id in :ids")
	Set<User> findUsersById(@Param("ids") Long... ids);		
}
