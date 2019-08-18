package com.XZYHOrderingFood.back.filter;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

@Configuration
//@EnableWebMvc
public class MvcConfig extends WebMvcConfigurationSupport{

	@Autowired
	private MyInterceptor myInterceptor;

	@Override
	protected void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(myInterceptor).addPathPatterns("/**")
		.excludePathPatterns("/index.html")
		;
		//registry.addResourceHandler("/assets/**").addResourceLocations("classpath:/assets/").setCacheControl(CacheControl.noCache());
		super.addInterceptors(registry);
	}
	
	/**
	 * 释放静态资源
	 */
	@Override
	protected void addResourceHandlers(ResourceHandlerRegistry registry) {
		 registry.addResourceHandler("/**")
         .addResourceLocations("classpath:/resources/")
         .addResourceLocations("classpath:/static/")
         .addResourceLocations("classpath:/templates/");
        super.addResourceHandlers(registry);
 
	}
	
	/**
	 * 设置跨域问题
	 */
	/*@Override
	protected void addCorsMappings(CorsRegistry registry) {
		 registry.addMapping("/**")
		 		  .allowedOrigins("*")
		 		  .allowedMethods("GET","HEAD","POST","PUT","PATCH","DELETE","TRACE","OPTIONS")
		 		  .allowCredentials(true);
		super.addCorsMappings(registry);
	}*/

//	@ResponseBody注解在转换日期类型时会默认把日期转换为时间戳（例如： date：2017-10-25  转换为 时间戳：15003323990）。
	@Override
	protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
	    ObjectMapper objectMapper = new ObjectMapper();

	    SimpleModule simpleModule = new SimpleModule();
	    simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
	    simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
	    objectMapper.registerModule(simpleModule);
	    jackson2HttpMessageConverter.setObjectMapper(objectMapper);

	    converters.add(jackson2HttpMessageConverter);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
	public FilterRegistrationBean corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);// 是否支持安全证书
		config.addAllowedOrigin("*");
		config.addAllowedHeader("*");
		config.addAllowedMethod("OPTION");
		config.addAllowedMethod("GET");
		config.addAllowedMethod("POST");
		config.addAllowedMethod("PUT");
		config.addAllowedMethod("HEAD");
		config.addAllowedMethod("DELETE");
		source.registerCorsConfiguration("/**", config); // 注册跨域
		FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
		bean.setOrder(0);
		return bean;
	}
}
