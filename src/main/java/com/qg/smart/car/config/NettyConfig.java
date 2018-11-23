package com.qg.smart.car.config;

import com.qg.smart.car.socket.GateServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author 小排骨
 * Date 2018/1/8
 */
@Configuration
@PropertySource("classpath:netty.properties")
public class NettyConfig {

    /**
     * Netty端口.
     */
    @Value("${netty.port}")
    private int port;

    /**
     * netty服务器注册.
     *
     * @return Object
     */
    @Bean
    public Object gateServer() {
        return new GateServer(port);
    }
}
