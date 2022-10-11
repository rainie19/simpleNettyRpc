package com.macie.server;

import com.macie.server.config.ServerConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


/**
 * @author macie
 * @date 2022/10/11
 */
public class RpcBootstrap {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ServerConfig.class);
        //ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring-config.xml");
        //Object rpcServer = ctx.getBean("rpcServer");
        //System.out.println(rpcServer.getClass());

    }
}
