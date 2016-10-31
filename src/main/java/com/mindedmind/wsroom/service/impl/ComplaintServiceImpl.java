package com.mindedmind.wsroom.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mindedmind.wsroom.domain.Complaint;
import com.mindedmind.wsroom.domain.Message;
import com.mindedmind.wsroom.repository.ComplaintRepository;
import com.mindedmind.wsroom.repository.MessageRepository;
import com.mindedmind.wsroom.service.ComplaintService;

@Service
@Transactional
public class ComplaintServiceImpl implements ComplaintService 
{
	private final MessageRepository messageRepository;
	
	private final ComplaintRepository complaintRepository;	
	
	public ComplaintServiceImpl(MessageRepository messageRepository, ComplaintRepository complaintRepository)
	{
		this.messageRepository = messageRepository;
		this.complaintRepository = complaintRepository;
	}
	
	@Override public void complainOnMessage(Long id, String reason)
	{
		Message msg = messageRepository.getOne(id);
		Complaint c = new Complaint();
		c.setReason(reason);
		c.setMessage(msg);
		complaintRepository.save(c);
	}

	@Override public void deleteComplaint(Long id)
	{
		complaintRepository.delete(id);
	}

	@Override public List<Complaint> findAll()
	{
		return complaintRepository.findAll();
	}
}
