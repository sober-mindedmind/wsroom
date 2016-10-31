package com.mindedmind.wsroom.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedSubgraph;
import javax.persistence.Table;

@Entity
@Table(name = "messages")
@NamedEntityGraphs(
		@NamedEntityGraph(
				name = "Message.allEager",
				attributeNodes = {@NamedAttributeNode(value = "owner"), 
								  @NamedAttributeNode(value = "room", subgraph = "room")},
				subgraphs = @NamedSubgraph(name = "room", attributeNodes = @NamedAttributeNode("owner"))))
public class Message extends AbstractEntity
{
	private String text;
	
	private Date time;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private User owner;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Room room;

	public String getText()
	{
		return text;
	}

	public Date getTime()
	{
		return time;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public void setTime(Date time)
	{
		this.time = time;
	}

	public User getOwner()
	{
		return owner;
	}

	public void setOwner(User owner)
	{
		this.owner = owner;
	}

	public Room getRoom()
	{
		return room;
	}

	public void setRoom(Room room)
	{
		this.room = room;
	}

	@Override public String toString()
	{
		return "" + getId();
	}

}
