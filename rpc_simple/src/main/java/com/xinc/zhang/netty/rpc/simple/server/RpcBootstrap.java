package com.xinc.zhang.netty.rpc.simple.server;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RpcBootstrap {
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		new ClassPathXmlApplicationContext("spring-server.xml");
	}
}
