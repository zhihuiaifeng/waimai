package com.XZYHOrderingFood.back.config;

import java.util.EventListener;

import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.request.RequestContextListener;

import lombok.extern.slf4j.Slf4j;

//@Configuration
@Slf4j
public class ListenerConfig {

	@Bean
	public RequestContextListener requestContextListener() {
		log.info("RequestContextListener 监听器已经进入 =============");
		return new RequestContextListener();
	}
	
	@Bean
    public ServletListenerRegistrationBean<EventListener> getDemoListener(){
        ServletListenerRegistrationBean<EventListener> registrationBean
                                   =new ServletListenerRegistrationBean<>();
        registrationBean.setListener(new RequestContextListener());
//       registrationBean.setOrder(1);
        return registrationBean;
    }
}
