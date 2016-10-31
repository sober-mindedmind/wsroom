package com.mindedmind.wsroom.service;

import java.util.List;

import com.mindedmind.wsroom.domain.Complaint;

public interface ComplaintService
{
	void complainOnMessage(Long id, String reason);
	
	void deleteComplaint(Long id);

	List<Complaint> findAll();	
}
