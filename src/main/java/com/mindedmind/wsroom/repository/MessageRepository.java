package com.mindedmind.wsroom.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
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
		
	@EntityGraph(value = "Message.allEager", type = EntityGraphType.LOAD)
	@Query("select m from Message m where m.id = ?1")	
	Message findMessage(Long id);
	
	@Transactional
	@Modifying
	@Query("update Message m set m.text = ?2 where m.id = ?1")
	void updateMessage(Long id, String text);
}
