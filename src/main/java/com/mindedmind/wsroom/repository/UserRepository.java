package com.mindedmind.wsroom.repository;

import java.util.List;
import java.util.Set;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.transaction.annotation.Transactional;

import com.mindedmind.wsroom.domain.User;

public interface UserRepository extends JpaRepository<User, Long>
{	
	@Query("select u from User u where u.name = ?1")
	User findUserByName(String name);
	
	@Query("select u from User u where u.id in ?1")
	Set<User> findUsersById(Long... ids);
	
	@Transactional
	@Modifying
	@Query("delete from User u where u.name = ?1")
	int deleteByName(String name);

	@QueryHints(@QueryHint(name = "org.hibernate.annotations.QueryHints.READ_ONLY", value = "true"))
	@Override List<User> findAll();	
}
