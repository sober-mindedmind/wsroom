package com.mindedmind.wsroom.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.mindedmind.wsroom.domain.Role;

public class SecurityUtils
{
	public static boolean hasRole(Authentication auth, Role role)
	{
		for (GrantedAuthority ga : auth.getAuthorities())
		{
			if (ga.getAuthority().equals(role.getName()))
			{
				return true;
			}
		}
		return false;
	}
}
