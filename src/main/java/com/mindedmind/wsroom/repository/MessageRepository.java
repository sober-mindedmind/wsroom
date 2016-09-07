package com.mindedmind.wsroom.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mindedmind.wsroom.domain.Message;

public interface MessageRepository extends JpaRepository<Message, Long>
{	
}
