package com.mindedmind.wsroom.security;

import static com.mindedmind.wsroom.domain.Role.ROLE_ADMIN;
import static com.mindedmind.wsroom.security.SecurityUtils.hasRole;
import static com.mindedmind.wsroom.util.EntityUtils.notNull;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.mindedmind.wsroom.domain.Message;
import com.mindedmind.wsroom.repository.MessageRepository;

@Component
public class MessagePermissionEvaluator
{
	private final MessageRepository messageRepository;
	
	
	public MessagePermissionEvaluator(MessageRepository messageRepository)
	{	
		this.messageRepository = messageRepository;
	}

	@Transactional
	public boolean canModify(Long id, Authentication auth)
	{
		Message msg = messageRepository.findMessage((Long) id);
		notNull(msg , "There is no message with such id '%s'" , id);			
		
		/*
		 * Allow to delete or update this message in case if owner of this message is current principal or the
		 * room to which this message belongs was created by the current principal
		 */
		return hasRole(auth , ROLE_ADMIN) 
				|| isPrincipalHasName(auth, msg.getOwner().getName())
				|| isPrincipalHasName(auth, msg.getRoom().getOwner().getName());
	}

	private static boolean isPrincipalHasName(Authentication authentication, String name) 
	{
		return authentication.getName().equals(name);
	}
	
}
