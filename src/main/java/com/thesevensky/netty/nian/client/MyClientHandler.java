package com.thesevensky.netty.nian.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.Charset;
import java.util.function.BiFunction;

public class MyClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private int count;
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        byte[] buffer = new byte[msg.readableBytes()];
        msg.readBytes(buffer);
        String str = new String(buffer, Charset.forName("utf-8"));
        System.out.println("from server:"+ str);
        System.out.println("from server count:"+ (++this.count));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //ctx.writeAndFlush(123456L);
        /*ctx.writeAndFlush(1L);
        ctx.writeAndFlush(123L);
        ctx.writeAndFlush(1234L);*/
        //ctx.writeAndFlush(123456);
        //ctx.writeAndFlush(Unpooled.copiedBuffer("helloworld", Charset.forName("utf-8")));
        for(int i =0; i < 10 ; i++) {
            ByteBuf buf = Unpooled.copiedBuffer("send helloworld", Charset.forName("utf-8"));
            ctx.writeAndFlush(buf);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
