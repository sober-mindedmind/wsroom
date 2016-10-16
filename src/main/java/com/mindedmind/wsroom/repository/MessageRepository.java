package com.mindedmind.wsroom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.mindedmind.wsroom.domain.Message;
import com.mindedmind.wsroom.domain.Room;
import com.mindedmind.wsroom.domain.User;

public interface MessageRepository extends JpaRepository<Message, Long>
{	
	@Transactional
	@Modifying
	@Query("delete from Message msg where msg.room = ?1")
	void deleteAllMessagesInRoom(Room r);
	
	@Transactional
	@Modifying
	@Query("delete from Message msg where msg.owner = ?1")
	void deleteAllMessagesOfUser(User u);
	
	@Transactional
	@Modifying
	@Query("delete from Message msg where msg.owner.id = ?1 and msg.id = ?2")
	void deleteUserMessage(Long userId, Long msgId);
}
