package com.xinc.zhang.netty.nio.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * @author zhaoliangang 2015-2-12
 */
public class TimeServerHandler extends ChannelInboundHandlerAdapter{

    private int counter;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws UnsupportedEncodingException{
       /* ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req, "UTF-8").substring(0, req.length
                - System.getProperty("line.separator").length());

        System.out.println("The time server receive order :" + body
                + " ; the counter is:" + ++counter);

        String currentTime = "Query time order".equalsIgnoreCase(body) ? new Date(
                System.currentTimeMillis()).toString() : "Bad order";
        currentTime = currentTime + System.getProperty("line.separator");
        ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
        ctx.writeAndFlush(resp);*/
        
        String body = (String) msg;
        //byte[] req = new byte[buf.readableBytes()];
        //buf.readBytes(req);
        //String body = new String(req, "UTF-8").substring(0, req.length
        //        - System.getProperty("line.separator").length());

        System.out.println("The time server receive order :" + body
                + " ; the counter is:" + ++counter);

        String currentTime = "Query time order".equalsIgnoreCase(body) ? new Date(
                System.currentTimeMillis()).toString() : "Bad order";
        currentTime = currentTime + System.getProperty("line.separator");
        ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
        ctx.writeAndFlush(resp);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

}