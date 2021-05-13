package org.im.config.filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MyFilter1 implements Filter{

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		System.out.println(" my filer 1");
		
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse res = (HttpServletResponse) response;
		
		/* 토큰 만들기
		 * - 요청시 마다 Authorization (JWT Token)을 확인 함
		 * - 토큰 존재하고, 유효성 검사를 하여 정상적인 경우 chain 
		 * - 만약 토큰이 없고, id,pwd를 통한 로그인이라면 id,pwd로 사용자를 검증하고 토큰 생성
		 * - 토큰이 넘어온다면, 이 토큰을 내(본 서버)가 만든것인가만 확인 하면 됨
		 */
		/*
		String headerAuth = req.getHeader("Authorization");
		
		if("POST".equals(req.getMethod())) {
			System.out.println(" headerAuth : " + headerAuth);
			System.out.println(" Method : " + req.getMethod());
			
			if(headerAuth != null  && "auth".equals(headerAuth)) {
				System.out.println(" .. chaind!!");
				chain.doFilter(req, res);
			} else {
				System.out.println(" .. not chaind!!");
				PrintWriter out = res.getWriter();
				out.println(" 인증 안됨");
			}
		}
		*/
		chain.doFilter(req, res);
	}
	

}
