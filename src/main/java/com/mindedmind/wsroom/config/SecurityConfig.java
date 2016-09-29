package com.mindedmind.wsroom.config;

import static com.mindedmind.wsroom.domain.Role.ROLE_ADMIN;
import static com.mindedmind.wsroom.domain.Role.ROLE_USER;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.mindedmind.wsroom.domain.Role;
import com.mindedmind.wsroom.repository.UserRepository;
import com.mindedmind.wsroom.service.impl.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter
{
	@Autowired UserRepository userRepository;

	@Override protected void configure(HttpSecurity http) throws Exception
	{
		 http
         .authorizeRequests()
             .antMatchers("/users/registration", "/css/*", "/js/*").permitAll()
             .antMatchers("/admin").hasAuthority(ROLE_ADMIN.getName())
             .anyRequest().hasAnyAuthority(ROLE_USER.getName(), ROLE_ADMIN.getName())
             .and()
             .csrf()
             .disable()             
         .formLogin()
             .loginPage("/login") 
             .defaultSuccessUrl("/index")             
             .permitAll()
             .and()
         .logout()
         	 .logoutUrl("/logout")
             .permitAll();
	}
	
	@Bean
	@Override public UserDetailsService userDetailsServiceBean()
	{
		return new UserDetailsServiceImpl(userRepository);
	}
	
	@Override protected void configure(AuthenticationManagerBuilder auth) throws Exception
	{ 
		auth.userDetailsService(userDetailsServiceBean());
	}

}
