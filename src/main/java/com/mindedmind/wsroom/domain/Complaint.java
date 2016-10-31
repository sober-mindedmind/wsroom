package com.mindedmind.wsroom.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedSubgraph;

@Entity
@NamedEntityGraphs(
		@NamedEntityGraph(
				name = "Complaint.withOffender",
				attributeNodes = {@NamedAttributeNode(value = "message", subgraph = "message")},
				subgraphs = @NamedSubgraph(name = "message", attributeNodes = {@NamedAttributeNode("owner"), 
						@NamedAttributeNode("room")})))
public class Complaint extends AbstractEntity
{
	private String reason;
	
	@ManyToOne(fetch = FetchType.EAGER)
	private Message message;

	public String getReason()
	{
		return reason;
	}

	public void setReason(String reason)
	{
		this.reason = reason;
	}

	public Message getMessage()
	{
		return message;
	}

	public void setMessage(Message message)
	{
		this.message = message;
	}
}