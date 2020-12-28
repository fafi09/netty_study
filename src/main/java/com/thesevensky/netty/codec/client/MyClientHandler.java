package com.thesevensky.netty.codec.client;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.Charset;
import java.time.LocalDateTime;

public class MyClientHandler extends SimpleChannelInboundHandler<Long> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Long msg) throws Exception {
        System.out.println(ctx.channel().remoteAddress());
        System.out.println("client output: " + msg);
        //ctx.writeAndFlush("from client: " + LocalDateTime.now());
        //ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(123456L);
        /*ctx.writeAndFlush(1L);
        ctx.writeAndFlush(123L);
        ctx.writeAndFlush(1234L);*/
        //ctx.writeAndFlush(123456);
        //ctx.writeAndFlush(Unpooled.copiedBuffer("helloworld", Charset.forName("utf-8")));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
