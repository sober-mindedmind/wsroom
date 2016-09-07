package com.mindedmind.wsroom.domain;

import java.util.Arrays;
import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "users")
public class User extends AbstractEntity
{	
	@NotNull
	@Size(min = 1) 
	@Column(unique = true)
	private String name;
	
	@Lob
	@Basic(fetch = FetchType.LAZY)
	private byte[] photo;
	
	@NotNull
	@Size(min = 6)	
	private String password;
	
	@Column(nullable = true)
	private Boolean active = true;
	
	private String email;
	
	private String birthday;
		
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
		result = prime * result + ((active == null) ? 0 : active.hashCode());
		result = prime * result + ((birthday == null) ? 0 : birthday.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + Arrays.hashCode(photo);
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
		return user.active == active 
				&& Objects.equals(user.password , password)
				&& Objects.equals(user.email, email)
				&& Objects.equals(user.birthday, birthday)
				&& Objects.equals(user.name , name)				
				&& Arrays.equals(user.getPhoto() , photo);
	}
	
}
