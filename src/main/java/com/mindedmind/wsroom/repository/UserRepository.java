package com.mindedmind.wsroom.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mindedmind.wsroom.domain.User;

public interface UserRepository extends JpaRepository<User, Long>
{
	@EntityGraph(value = "userWithRoles", type = EntityGraphType.LOAD)
	@Query("select u from User u where u.name = ?1")
	User findUserByName(String name);
	
	@Query("select u from User u where u.id in ?1")
	Set<User> findUsersById(Long... ids);
	
	@EntityGraph(value = "userWithRoles", type = EntityGraphType.LOAD)
	@Override List<User> findAll();
}
