package org.im.controller;

import org.im.config.auth.PrincipalDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class RestApiController {

	// 모든 사람이 접근 가능
	@GetMapping("/api/v1/home")
	public String home() {
		return "<h1>home</h1>";
	}
	
	// 모든 사람이 접근 가능
	@PostMapping("test")
	public String test() {
		return "<h1>test</h1>";
	}
	
	// 모든 사람이 접근 가능
	@PostMapping("/api/v1/user/api1")
	public String user() {
		System.out.println(" -- in");
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(principal != null) {
			if("anonymousUser".equals(principal)) { 
				System.out.println(" >> user ID : anonymousUser!!" );
			} else {
				PrincipalDetails  userDetails = (PrincipalDetails)principal;

				String username = userDetails.getUsername();  
				String password = userDetails.getPassword();
				String userId   = userDetails.getUserid();
				
				System.out.println(" >> controller >> user ID :" + userId);
				System.out.println(" >> controller >> user Nm :" + username);
				System.out.println(" >> controller >> user Pw :" + password);
			}
		}		
		return "<h1>user</h1>";
	}

	// 모든 사람이 접근 가능
	@GetMapping("/api/v1/manager/api1")
	public String manager() {
		return "<h1>manager</h1>";
	}
	
	// 모든 사람이 접근 가능
	@GetMapping("/api/v1/admin/api1")
	public String admin() {
		return "<h1>admin</h1>";
	}		
}
