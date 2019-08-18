package com.XZYHOrderingFood.back.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * XSS filter
 * @author zxm
 *
 */
public class XSSFilter implements Filter{

	FilterConfig  config;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		this.config=filterConfig;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		chain.doFilter(new XSSHttpServletRequestWrapper((HttpServletRequest)request), response);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		this.config = null;
	}

	
}
