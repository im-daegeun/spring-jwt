package org.im.config.jwt;

import java.io.IOException;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.im.config.auth.PrincipalDetails;
import org.im.model.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

/**
 * Spring Security 의 UsernamePasswordAuthenticationFilter 사용 함
 * login 요청해서 userid, password를 post로 전송하면 필터 실행 함
 * 기본적으로  formLogin을 disable하면 동작 안함
 * 강제로 활성화 시켜야 함
 *  -> 시큐리티 필터에 등록 하면 동작 함
 * @author user
 *
 */

// 생성자 없이의존성 주임   
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter{
	private final AuthenticationManager authenticationManager;

	/**
	 * /login으로 접속하면 알아서 실행 되는 함수 
	 */
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		// TODO Auto-generated method stub
		
		System.out.println("JwtAuthenticationFilter : 진입");
		/**
		 * 1. id ,pwd 수신 
		 * 2. id ,pwd 정상인지 확인하기 위해서 로그인 시도
		 *  -> authenticationManager 로그인 시도 , PrincipalDetailsService .서비스 호출   
		 *  -> loadbyusername() 자동 실행
		 *  3. PrincippalDetails를 시큐리티 세션 저장
		 *  -> 이놈을 하지 않으면 권한관리 안됨(ROLE
		 *  -> 만약에 권한관리가 필요 없거나 다른 방법으로 권한관리를 할 경우 이부분 필요 없음.
		 */
		
		
		ObjectMapper om = new ObjectMapper();
		User user=  null;
		try {

			user= om.readValue(request.getInputStream(), User.class);
			System.out.println("===============================================1");	
			System.out.println("user id :" + user.getUserid());
			System.out.println("user name :" + user.getUsername());
			System.out.println("user pwd :" + user.getPassword());
			System.out.println("===============================================2");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// 유저네임패스워드 토큰 생성
		UsernamePasswordAuthenticationToken authenticationToken = 
				new UsernamePasswordAuthenticationToken(user.getUserid(), user.getPassword());			
		
		System.out.println("JwtAuthenticationFilter : 토큰생성완료");
		
		// authenticate() 함수가 호출 되면 인증 프로바이더가 유저 디테일 서비스의
		// loadUserByUsername(토큰의 첫번째 파라메터) 를 호출하고
		// UserDetails를 리턴받아서 토큰의 두번째 파라메터(credential)과
		// UserDetails(DB값)의 getPassword()함수로 비교해서 동일하면
		// Authentication 객체를 만들어서 필터체인으로 리턴해준다.
		
		// Tip: 인증 프로바이더의 디폴트 서비스는 UserDetailsService 타입
		// Tip: 인증 프로바이더의 디폴트 암호화 방식은 BCryptPasswordEncoder
		// 결론은 인증 프로바이더에게 알려줄 필요가 없음.
		Authentication authentication = 
				authenticationManager.authenticate(authenticationToken);
		
		PrincipalDetails principalDetailis = (PrincipalDetails) authentication.getPrincipal();
		System.out.println("Authentication : "+principalDetailis.getUser().getUsername());
				
		// 인증 처리 완료되고, 시큐리티 세션저장소에 세션 정보 저장 완료됨
		// jwt 토큰을 사용하면 세션을 사용할 필요가 없으나 시큐리티의 궈한관리를 사용하기위해서 처리 함.
		return authentication;
	}
	
	// JWT Token 생성해서 response에 담아주기
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		
		PrincipalDetails principalDetailis = (PrincipalDetails) authResult.getPrincipal();
		
		System.out.println(">>> security login success!!!");
		System.out.println(">>> login id  :"+ principalDetailis.getUserid());
		System.out.println(">>> login nm  :"+ principalDetailis.getUsername());
		
		
		String jwtToken = JWT.create()
				.withSubject(principalDetailis.getUsername())
				.withExpiresAt(new Date(System.currentTimeMillis()+JwtProperties.EXPIRATION_TIME))
				.withClaim("id", principalDetailis.getUser().getId())
				.withClaim("username", principalDetailis.getUser().getUsername())
				.sign(Algorithm.HMAC512(JwtProperties.SECRET));

		
		System.out.println(" >>> JWT Token :" + jwtToken);
		response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX+jwtToken);
		
		// super.successfulAuthentication(request, response, chain, authResult);
	}	
}
