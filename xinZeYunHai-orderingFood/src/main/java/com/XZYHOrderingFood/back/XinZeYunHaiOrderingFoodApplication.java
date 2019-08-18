package com.XZYHOrderingFood.back;

 
import com.XZYHOrderingFood.back.service.WebSocket;
import com.XZYHOrderingFood.back.util.Coder;
import com.google.common.collect.Maps;
import org.apache.catalina.core.ApplicationContext;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Map;


@SpringBootApplication
@MapperScan("com.XZYHOrderingFood.back.dao")
@EnableScheduling//开启定时任务开关
@EnableCaching
public class XinZeYunHaiOrderingFoodApplication extends SpringBootServletInitializer{

	public static void main(String[] args) {
		ConfigurableApplicationContext applicationContext = SpringApplication.run(XinZeYunHaiOrderingFoodApplication.class, args);
		WebSocket.setApplicationContext(applicationContext);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(XinZeYunHaiOrderingFoodApplication.class);
	}
//	public static void main (String[] s)throws Exception {
//		System.out.println(Coder.encodeMD5("123456", "D8F3D9F0D78E416D833F6C49ED4A9322257240897173100"));
//		Map map = Maps.newHashMap();
//		map.put("ddd", 1234);
//		System.out.println(map.toString());
////		map.remove("ddd");
//		map.replace("ddd",4444);
////		map.clear();
//		System.out.println(map);
//	}

}
