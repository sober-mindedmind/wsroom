package com.mindedmind.wsroom.web;

import static java.util.stream.Collectors.toSet;

import java.util.Collection;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mindedmind.wsroom.dto.ChatMessageDto;
import com.mindedmind.wsroom.dto.ComplaintDto;
import com.mindedmind.wsroom.service.ChatService;
import com.mindedmind.wsroom.service.ComplaintService;
import com.mindedmind.wsroom.wsevent.EventNotifier;

@RestController
public class MessageController
{
	private final ComplaintService complaintService; 
	
	private final ChatService chatService;
	
	private final EventNotifier notifier;	

	public MessageController(ComplaintService complaintService, ChatService chatService, EventNotifier notifier)
	{
		this.complaintService = complaintService;
		this.notifier = notifier;
		this.chatService = chatService;
	}
	
	@DeleteMapping(value = "/messages/{msgId}", params = "room")
	@ResponseStatus(HttpStatus.OK)
	public void removeMessage(@PathVariable("msgId") Long msgId,
							  @RequestParam("room") String room)
	{
	 	chatService.deleteMessage(msgId);
	 	ChatMessageDto msgDto = new ChatMessageDto();
	 	msgDto.setId(msgId);
	 	msgDto.setRoom(room);
		notifier.notifyDeleteMessage(room , msgDto);
	}
	
	@PutMapping("/messages/{msgId}")
	@ResponseStatus(HttpStatus.OK)
	public void updateMessage(@RequestBody ChatMessageDto msgDto, @PathVariable("msgId") Long  msgId)
	{
		chatService.updateMessage(msgId , msgDto.getText());
		notifier.notifyUpdateMessage(msgDto.getRoom() , msgDto);
	}
	
	@PostMapping("/messages/{id}/complaints")
	@ResponseStatus(HttpStatus.OK)
	public void createComplaint(@PathVariable("id") Long id,
								@RequestBody String reason)
	{
		complaintService.complainOnMessage(id , reason);
	}
	
	@GetMapping("/complaints")
	public Collection<ComplaintDto> allComplaints()
	{
		return complaintService.findAll().stream().map(ComplaintDto::new).collect(toSet());
	}
	
	@DeleteMapping("/complaints/{id}")
	@ResponseStatus(HttpStatus.OK)
	public void deleteComplaints(@PathVariable("id") Long id)
	{
		complaintService.deleteComplaint(id);
	}
}
