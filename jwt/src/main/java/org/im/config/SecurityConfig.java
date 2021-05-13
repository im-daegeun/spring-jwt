package org.im.config;

import org.im.config.auth.PrincipalDetailsService;
import org.im.config.jwt.JwtAuthenticationFilter;
import org.im.config.jwt.JwtAuthorizationFilter;
import org.im.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration // IoC 빈(bean)을 등록
@EnableWebSecurity // 필터 체인 관리 시작 어노테이션
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true) // 특정 주소 접근시 권한 및 인증을 위한 어노테이션 활성화
public class SecurityConfig  extends WebSecurityConfigurerAdapter {

	@Autowired
	private CorsConfig corsConfig;
	
	@Autowired
	private UserService userService;
	
	
	
	@Bean
	public BCryptPasswordEncoder encodePwd() {
		return new BCryptPasswordEncoder();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// http.addFilterAfter(new MyFilter1(), BasicAuthenticationFilter.class);
		http
			.addFilter(corsConfig.corsFilter())
			.csrf().disable()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // session 사용 안함
		.and()
			.formLogin().disable()
			.httpBasic().disable()  // 기본 인증방식으로 인증정보(아이디, 암호)를 Header에 포함하여 전달
			// formLogin을 Disable하여 security 로그인 동작 안함
			// 대신에 UsernamePasswordAuthenticationFilter 필터를 활성화 하여 
			// -> 시큐리티 로그인 처리 함
			// -> JwtAuthenticationFilter 이놈이 일을 하게 할려면 WebSecurityConfigurerAdapter가 들고 있는 AuthenticationManager를 던져줘서 일을 하게 해야 함
			
			.addFilter(new JwtAuthenticationFilter(authenticationManager()))
			.addFilter(new JwtAuthorizationFilter(authenticationManager() , userService))
			.authorizeRequests()
			.antMatchers("/api/v1/user/**")
				.access("hasRole('ROLE_USER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
			.antMatchers("/api/v1/manager/**")
				.access("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
			.antMatchers("/api/v1/admin/**")
				.access("hasRole('ROLE_ADMIN')")
			.anyRequest().permitAll(); 
	}	
}
