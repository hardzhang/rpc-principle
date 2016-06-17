package com.xinc.zhang.netty.rpc.sieve.core;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.xinc.zhang.netty.rpc.codec.RpcDecoder;
import com.xinc.zhang.netty.rpc.codec.RpcEncoder;
import com.xinc.zhang.netty.rpc.codec.RpcRequest;
import com.xinc.zhang.netty.rpc.codec.RpcResponse;

public class RpcServer implements ApplicationContextAware, InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);

	private String serverAddress;
	private ServiceRegistry serviceRegistry;

	private Map<String, Object> handlerMap = new HashMap<String,Object>(); // 瀛樻斁鎺ュ彛鍚嶄笌鏈嶅姟瀵硅薄涔嬮棿鐨勬槧灏勫叧绯�
	public RpcServer(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	public RpcServer(String serverAddress, ServiceRegistry serviceRegistry) {
		this.serverAddress = serverAddress;
		this.serviceRegistry = serviceRegistry;
	}

	@Override
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        // 鑾峰彇鎵�湁甯︽湁RpcService娉ㄨВ鐨凷pringBean
        Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(RpcService.class);
		if (MapUtils.isNotEmpty(serviceBeanMap)) {
			for (Object serviceBean : serviceBeanMap.values()) {
				String interfaceName = serviceBean.getClass().getAnnotation(RpcService.class).value().getName();
				handlerMap.put(interfaceName, serviceBean);
			}
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel channel) throws Exception {
							channel.pipeline().addLast(new RpcDecoder(RpcRequest.class)) // 灏� // RPC // 璇锋眰杩涜瑙ｇ爜锛堜负浜嗗鐞嗚姹傦�?									
							.addLast(new RpcEncoder(RpcResponse.class)) // 灏�  // 鍝嶅簲杩涜缂栫爜锛堜负浜嗚繑鍥炲搷搴旓�?									
							.addLast(new RpcHandler(handlerMap)); // 澶勭�?	// RPC
					  }
					}).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);

			String[] array = serverAddress.split(":");
			String host = array[0];
			int port = Integer.parseInt(array[1]);

			ChannelFuture future = bootstrap.bind(host, port).sync();
			LOGGER.debug("server started on port {}", port);

			if (serviceRegistry != null) {
				serviceRegistry.register(serverAddress); // 娉ㄥ唽鏈嶅姟鍦板�?			
		    }

			future.channel().closeFuture().sync();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}

}
