package org.im.config.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.im.config.auth.PrincipalDetails;
import org.im.config.auth.PrincipalDetails;
import org.im.model.User;
import org.im.service.user.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

/**
 * 시큐리티의 BasicAuthenticationFilter 필터가 권한이나, 인증이 필요한 특정 주소 요청시 무조건 이 필터 탐
 * 만약 권한이나 인증이 필요한 주소가 아니라면 이 필터를 안탐
 *
 */
public class JwtAuthorizationFilter extends BasicAuthenticationFilter{
	private UserService userService;
	
	public JwtAuthorizationFilter(AuthenticationManager authenticationManager , UserService userService) {
		super(authenticationManager);
		this.userService = userService;
		System.out.println(" 인증이나 권한 필요 요청 들어옴!!");
	}

	
	// 인증이나 권한 요청이 들어왔을  여기 필터를 타게 됨.
	// -> 인증 확인 수행 
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		System.out.println(" 인증/궈한 필요 요청 확인 Chain!!");
		
		String header = request.getHeader(JwtProperties.HEADER_STRING);
		System.out.println(" >> header :" + header);
		
		if(header == null || !header.startsWith(JwtProperties.TOKEN_PREFIX)) {
			chain.doFilter(request, response);
                        return;
		}		
		String token = request.getHeader(JwtProperties.HEADER_STRING)
				              .replace(JwtProperties.TOKEN_PREFIX, "");
		System.out.println(" >> token :" + token);
		
		
		// 토큰 검증 (이게 인증이기 때문에 AuthenticationManager도 필요 없음)
		// 내가 SecurityContext에 집적접근해서 세션을 만들때 자동으로 UserDetailsService에 있는 loadByUsername이 호출됨.
		String username = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build().verify(token)
				.getClaim("username").asString();
		
		System.out.println(" >> username :" + username);
		
		if(username != null) {
			System.out.println(" [SUCC] 토큰 검증 완료!!");
			
			
			User user = null;
			try {
				user = userService.read(username);
				if(user != null) {
					System.out.println("  --> userservice >> user id :" + user.getUserid());
					System.out.println("  --> userservice>> user name :" + user.getUsername());					
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			PrincipalDetails principalDetails = new PrincipalDetails(user);
			
			/*
			PrincipalDetails userDetails= (PrincipalDetails) principalDetailsService.loadUserByUsername(username);
			
			System.out.println("  --> principal >> user id :" + userDetails.getUserid());
			System.out.println("  --> principal >> user name :" + userDetails.getUsername());
			*/
			
			// 하지만 스프링 시큐리티가 하는 권한 수행을 위해 시큐리티 토큰을 생성하여, 시큐리티 세션저장소에 저장
			// 그래야만 시큐리티 권한 검증을 사용할 수 있음.
			
			Authentication authentication =
					new UsernamePasswordAuthenticationToken(
							principalDetails, //나중에 컨트롤러에서 DI해서 쓸 때 사용하기 편함.
							null, // 패스워드는 모르니까 null 처리, 어차피 지금 인증하는게 아니니까!!
							principalDetails.getAuthorities());
			
			// 강제로 시큐리티의 세션에 접근하여 값 저장
			SecurityContextHolder.getContext().setAuthentication(authentication);			
		}
		
		super.doFilterInternal(request, response, chain);		
		//chain.doFilter(request, response);
	}

}
