package com.XZYHOrderingFood.back.filter;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.springframework.web.util.HtmlUtils;

/**
 * XSS requestWrapper
 * @author zxm
 *过滤修改 请求参数器  修改原参数值
 *HtmlUtils.htmlEscape(header) ;  //转义HTML请求 防止恶意脚本
 */
public class XSSHttpServletRequestWrapper extends HttpServletRequestWrapper  {

	public XSSHttpServletRequestWrapper(HttpServletRequest request) {
		super(request);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public String getParameter(String name) {
		// TODO Auto-generated method stub
		String parameter = super.getParameter(name);
		if(parameter == null) {
			return null;
		}
		return HtmlUtils.htmlEscape(parameter)  ;
	}
	
	//转义回来
	public static String getbackName(String name) {
		return HtmlUtils.htmlUnescape(name);
	}
	
	@Override
	public String[] getParameterValues(String name) {
		// TODO Auto-generated method stub
		String[] values = super.getParameterValues(name);
		
		if (values == null) {
			return null;
		}
		int count = values.length;
		String[] encodedValues = new String[count];
		for (int i = 0; i < count; i++) {
			encodedValues[i] = HtmlUtils.htmlEscape(values[i]);
		}
		return encodedValues;
	}
	
	@Override
	public String getHeader(String name) {
		// TODO Auto-generated method stub
		String header = super.getHeader(name);
		if(header == null) {
			return null;
		}
		return HtmlUtils.htmlEscape(header) ;
	}
}
