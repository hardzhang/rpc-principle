package com.xinc.zhang.netty.rpc.serve.modules.simple.impl;

import com.xinc.zhang.netty.rpc.serve.modules.simple.HelloService;
import com.xinc.zhang.netty.rpc.sieve.core.RpcService;

// 指定远程接口
@RpcService(HelloService.class)
public class HelloServiceImpl implements HelloService {

	@Override
	public String hello(String name) {
		return "Hello! " + name;
	}

}
