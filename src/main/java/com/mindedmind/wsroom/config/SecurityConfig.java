package com.mindedmind.wsroom.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

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
             .antMatchers("/registration", "/css/*", "/js/*").permitAll()             
             .anyRequest().authenticated()
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
