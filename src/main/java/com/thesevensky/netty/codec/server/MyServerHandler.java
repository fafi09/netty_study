package com.thesevensky.netty.codec.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.UUID;

public class MyServerHandler extends SimpleChannelInboundHandler<Long> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Long msg) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + " , " + msg);
        //ctx.channel().writeAndFlush("from server " + UUID.randomUUID());
        ctx.writeAndFlush(654321L);
        ctx.close();
    }

    /**
     * 出现异常怎么办
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        //链接关闭掉
        ctx.close();
    }
}
