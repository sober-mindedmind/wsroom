package com.mindedmind.wsroom.domain;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Represents place were one user can chat with another. Each room has its owner i.e. such user that created this
 * room. Owner can specify password for this room therefore making this room private. If password was not specified then
 * room is public, hence any user can enter such room. Also each room can have set of allowed users, if set is non empty
 * then only the users which were specified in this set allow to access this room, otherwise, if list is empty
 * (or {@code null}) and no password was specified then room is considered as public. 
 */
@Entity
@Table(name = "rooms")
@NamedEntityGraphs(
		@NamedEntityGraph(
				name = "Room.roomWithOwner",
				attributeNodes = {@NamedAttributeNode("owner")}))
public class Room extends AbstractEntity
{
	@NotNull
	@Size(min = 3)
	@Column(unique = true)
	private String name;
	
	private String password;
	
	private String description;
			
	@ManyToOne(fetch = FetchType.LAZY)
	private User owner;
	
	/**
	 * The list of users which are allowed to enter {@code this} room. If list is empty or {@code null} then any user is
	 * allowed to access this room
	 */
	@ManyToMany
	@JoinTable(name = "private_users_rooms")	
	private Set<User> allowedUsers 	 = new HashSet<>();
	
	@ManyToMany
	private Collection<User> subscribedUsers = new HashSet<>();
	
	private Boolean active = Boolean.TRUE; 
	
	@Lob
	private byte[] photo;
	
	public String getPassword()
	{
		return password;
	}
	
	public String getDescription()
	{
		return description;
	}
		
	public User getOwner()
	{
		return owner;
	}
	
	public void setPassword(String password)
	{
		this.password = password;
	}
	
	public void setDescription(String description)
	{
		this.description = description;
	}
		
	public void setOwner(User owner)
	{
		this.owner = owner;
	}

	public Boolean getActive()
	{
		return active;
	}

	public void setActive(Boolean active)
	{
		this.active = active;
	}

	public byte[] getPhoto()
	{
		return photo;
	}

	public void setPhoto(byte[] photo)
	{
		this.photo = photo;
	}

	public Set<User> getAllowedUsers()
	{
		return allowedUsers;
	}

	public void setAllowedUsers(Set<User> allowedUsers)
	{
		this.allowedUsers = allowedUsers;
	}

	public Collection<User> getSubscribedUsers()
	{
		return subscribedUsers;
	}

	public void setSubscribedUsers(Set<User> subscribedUsers)
	{
		this.subscribedUsers = subscribedUsers;
	}
 
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	@Override public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getActive() == null) ? 0 : getActive().hashCode());
		result = prime * result + ((getAllowedUsers() == null) ? 0 : getAllowedUsers().hashCode());
		result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
		result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
		result = prime * result + ((getOwner() == null) ? 0 : getOwner().hashCode());
		result = prime * result + ((getPassword() == null) ? 0 : getPassword().hashCode());
		result = prime * result + Arrays.hashCode(getPhoto());
		result = prime * result + ((getSubscribedUsers() == null) ? 0 : getSubscribedUsers().hashCode());
		return result;
	}

	@Override public boolean equals(Object obj)
	{
		if (obj == this)
		{
			return true;
		}
	
		if (!(obj instanceof Room))
		{
			return false;
		}
		
		Room other = (Room) obj;		
		
		return Objects.equals(getName(), other.getName())
				&& Objects.equals(getPassword(), other.getPassword())
				&& Objects.equals(getDescription(), other.getDescription())
				&& Objects.equals(getActive(), other.getActive())
				&& Objects.equals(getOwner(), other.getOwner())
				&& Objects.equals(getSubscribedUsers(), other.getSubscribedUsers())
				&& Objects.equals(getAllowedUsers(), other.getAllowedUsers())
				&& Objects.equals(getPhoto(), other.getPhoto());
	}
}
