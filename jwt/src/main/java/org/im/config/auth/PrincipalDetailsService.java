package org.im.config.auth;

import org.im.model.User;
import org.im.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.extern.java.Log;
@Log
@Service
public class PrincipalDetailsService implements UserDetailsService{

	@Autowired
	private UserService userService;
	
	@Autowired
	private BCryptPasswordEncoder pwEncoder;	
	
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = null;
		log.info(" [SEC] loadUserByUsername : username :" + username);
		// User user = userRepository.findByUsername(username);
		try {
			user = userService.read(username);
			if(user == null) {
				throw new UsernameNotFoundException(username);
			}			
		}catch (Exception e) {
			throw new UsernameNotFoundException(username);
		}
		return new PrincipalDetails(user);
	}	
}
