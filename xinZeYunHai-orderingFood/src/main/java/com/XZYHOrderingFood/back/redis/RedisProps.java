package com.XZYHOrderingFood.back.redis;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * @author yuxinchao
 * @Description: redis 属性配置 接收yml中的redis属性
 * @date 2018/3/2018:18
 */
@Component
@Setter
@Getter
public class RedisProps {
    // 主机IP
    @Value(value = "${spring.redis.host}")
    private String host;
    // 端口号
    @Value(value = "${spring.redis.port}")
    private Integer port;
}
