package com.thesevensky.netty.nian.client;

import com.thesevensky.netty.nian.protocol.Person;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.Charset;

public class MyClientPersonHandler extends SimpleChannelInboundHandler<Person> {
    private int count;
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Person msg) throws Exception {
        int length = msg.getLength();
        byte[] bb = msg.getContent();
        System.out.println("length:"+length);
        System.out.println("content:"+new String(bb, Charset.forName("utf-8")));
        System.out.println("times:"+(++this.count));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //
        for(int i =0; i < 10; i++) {
            String str = "send from client;";
            byte[] content = str.getBytes(Charset.forName("utf-8"));
            int len = content.length;
            Person p = new Person();
            p.setContent(content);
            p.setLength(len);
            ctx.writeAndFlush(p);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //
        cause.printStackTrace();
        ctx.close();
    }
}
