package com.mindedmind.wsroom.domain;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Represents an authenticated user and a client of a chat.  
 */
@Entity
@Table(name = "users")
@NamedEntityGraphs(
		@NamedEntityGraph(
				name = "User.userWithRoles",
				attributeNodes = {@NamedAttributeNode("roles")}))
public class User extends AbstractEntity
{	
	@NotNull
	@Size(min = 1) 
	@Column(unique = true)
	private String name;
	
	@Lob
	@Basic(fetch = FetchType.LAZY)
	private byte[] photo;
	
	@Size(min = 6)	
	private String password;
	
	@Column(nullable = true)
	private Boolean active = true;
	
	private String email;
	
	private String birthday;
	
	@Enumerated(EnumType.STRING)
	@ElementCollection(fetch = FetchType.EAGER)
	private Set<Role> roles = new HashSet<>(10);
	
	public byte[] getPhoto()
	{
		return photo;
	}

	public void setPhoto(byte[] photo)
	{
		this.photo = photo;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public Boolean isActive()
	{
		return active;
	}

	public String getEmail()
	{
		return email;
	}

	public String getBirthday()
	{
		return birthday;
	}

	public void setActive(Boolean active)
	{
		this.active = active;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public void setBirthday(String birthday)
	{
		this.birthday = birthday;
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
		result = prime * result + ((isActive() == null) ? 0 : isActive().hashCode());
		result = prime * result + ((getBirthday() == null) ? 0 : getBirthday().hashCode());
		result = prime * result + ((getEmail() == null) ? 0 : getEmail().hashCode());
		result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
		result = prime * result + ((getPassword() == null) ? 0 : getPassword().hashCode());
		result = prime * result + Arrays.hashCode(getPhoto());
		return result;
	}

	@Override public boolean equals(Object obj)
	{
		if (obj == this)
		{
			return true;
		}
		
		if (!(obj instanceof User))
		{
			return false;
		}
		User user = (User) obj;
		return user.isActive() == isActive() 
				&& Objects.equals(user.getPassword() , getPassword())
				&& Objects.equals(user.getEmail(), getEmail())
				&& Objects.equals(user.getBirthday(), getBirthday())
				&& Objects.equals(user.getName() , getName())				
				&& Arrays.equals(user.getPhoto() , getPhoto());
	}

	public Set<Role> getRoles()
	{
		return roles;
	}

	public void setRoles(Set<Role> roles)
	{
		this.roles = roles;
	}

}
