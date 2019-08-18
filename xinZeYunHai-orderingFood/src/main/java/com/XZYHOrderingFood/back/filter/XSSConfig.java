package com.XZYHOrderingFood.back.filter;

import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * XSS config
 * @author zxm
 *FilterRegistrationBean 排序filter
 */
@Configuration
public class XSSConfig {
	
	@Bean
	public FilterRegistrationBean<XSSFilter> registrationBean(){
		FilterRegistrationBean<XSSFilter> filterRegistrationBean = new FilterRegistrationBean<XSSFilter>();
		filterRegistrationBean.setFilter(new XSSFilter());
		filterRegistrationBean.setEnabled(true);
		filterRegistrationBean.setOrder(1);
		filterRegistrationBean.addUrlPatterns("/*");
		EnumSet<DispatcherType> set =EnumSet.allOf(DispatcherType.class);
		set.add(DispatcherType.REQUEST);
		set.add(DispatcherType.FORWARD);
		filterRegistrationBean.setDispatcherTypes(set);
		return filterRegistrationBean;
	}
	

}
