package com.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class CharEncoding implements Filter{

	private String charset;
	
	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		String uri;
		
		if(request instanceof HttpServletRequest) {
			
			HttpServletRequest req = (HttpServletRequest)request;
			
			uri = req.getRequestURI();
			
			if(req.getMethod().equalsIgnoreCase("post")) {
				
				if(uri.indexOf("ajax.do")!=-1) {
					req.setCharacterEncoding("euc-kr");
				} else {
					req.setCharacterEncoding(charset);
				}
				
			}
			
		}
		
		//넘겨주는 코드, 이 코드가 없으면 다음 필터나 서버로 넘어가지 않는다.
		chain.doFilter(request, response);
		
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
		charset = filterConfig.getInitParameter("charset");
		if(charset == null) {
			charset="UTF-8";
					
		}
		
	}

	
	
	
}
