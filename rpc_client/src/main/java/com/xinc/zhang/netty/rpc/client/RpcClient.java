package com.xinc.zhang.netty.rpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xinc.zhang.netty.rpc.codec.RpcDecoder;
import com.xinc.zhang.netty.rpc.codec.RpcEncoder;
import com.xinc.zhang.netty.rpc.codec.RpcRequest;
import com.xinc.zhang.netty.rpc.codec.RpcResponse;


@SuppressWarnings("rawtypes")
public class RpcClient extends SimpleChannelInboundHandler{

	private static final Logger LOGGER = LoggerFactory.getLogger(RpcClient.class);

	private String host;
	private int port;

	private RpcResponse response;

	private final Object obj = new Object();

	public RpcClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, Object response) throws Exception {
		this.response = (RpcResponse)response;
		synchronized (obj) {
			obj.notifyAll(); // �?��埌鍝嶅簲锛屽敜閱掔嚎绋�		
	   }
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		LOGGER.error("client caught exception", cause);
		cause.printStackTrace();
		ctx.close();
	}

	public RpcResponse send(RpcRequest request) throws Exception {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel channel) throws Exception {
					channel.pipeline().addLast(new RpcEncoder(RpcRequest.class)) // 灏�RPC 璇锋眰杩涜缂栫爜锛堜负浜嗗彂閫佽姹傦�?							
					.addLast(new RpcDecoder(RpcResponse.class)) // 灏�RPC 鍝嶅簲杩涜瑙ｇ爜锛堜负浜嗗鐞嗗搷搴旓�?							
					.addLast(RpcClient.this); // 浣跨�?RpcClient 鍙戦�?RPC 璇锋�?				}
			  }
			}).option(ChannelOption.SO_KEEPALIVE, true);

			ChannelFuture future = bootstrap.connect(host, port).sync();
			future.channel().writeAndFlush(request).sync();

			synchronized (obj) {
				  obj.wait(); // 鏈敹鍒板搷搴旓紝浣跨嚎绋嬬瓑寰�?		
			}

			if (response != null) {
				future.channel().closeFuture().sync();
			}
			return response;
		} finally {
			group.shutdownGracefully();
		}
	}

}