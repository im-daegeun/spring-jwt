package org.im.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

	@Bean
	public CorsFilter corsFilter() {
		System.out.println(" >> corsFilter!!");
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedOrigin("*"); // 모든 IP,도메인 허용
		config.addAllowedHeader("*"); // 모든 Header 허용
		config.addAllowedMethod("*"); // 모든 메소드 허용 
		
		source.registerCorsConfiguration("/api/**", config);
		return new CorsFilter(source);		
	}
}
