package com.macie.server.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author macie
 * @date 2022/10/11
 */
@Configuration
@ComponentScan("com.macie.server")
@PropertySource("classpath:application.properties")
public class ServerConfig {
}
