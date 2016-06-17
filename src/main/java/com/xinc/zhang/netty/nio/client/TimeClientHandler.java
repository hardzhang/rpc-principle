package com.xinc.zhang.netty.nio.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.UnsupportedEncodingException;

public class TimeClientHandler extends ChannelInboundHandlerAdapter{

    private int counter;

    private byte[] req;

    private ChannelHandlerContext ctx;

    public TimeClientHandler() {
        System.out.println("init TimeClientHandler");
        req = ("Query time order" + System.getProperty("line.separator")).getBytes();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.ctx = ctx;
        ByteBuf message = null;
        for (int i = 0; i < 100; i++) {
            message = Unpooled.buffer(req.length);
            message.writeBytes(req);
            ctx.writeAndFlush(message);
            System.out.println("req.length:"+req.length);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws UnsupportedEncodingException {
        //ByteBuf buf = (ByteBuf) msg;
        //byte[] req = new byte[buf.readableBytes()];
        //buf.readBytes(req);
        //String body = new String(req, "UTF-8");
        String body = (String) msg;
        System.out.println("Now is :" + body + ";the counter is :" + ++counter);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(ctx);
        //ctx.close();
    }
    
}