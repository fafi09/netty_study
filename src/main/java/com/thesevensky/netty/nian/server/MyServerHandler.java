package com.thesevensky.netty.nian.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.Charset;
import java.util.UUID;

public class MyServerHandler extends SimpleChannelInboundHandler<ByteBuf> {

    //每次都是新的实例
    private int count;
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {

        byte[] buffer = new byte[msg.readableBytes()];
        msg.readBytes(buffer);
        String str = new String(buffer, Charset.forName("utf-8"));
        System.out.println("from clinet:"+ str);
        System.out.println("from clinet count:"+ (++this.count));

        ByteBuf bb = Unpooled.copiedBuffer(UUID.randomUUID().toString(),Charset.forName("utf-8"));
        ctx.writeAndFlush(bb);
        //ctx.close();
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
