package com.thesevensky.netty.nian.server;

import com.thesevensky.netty.nian.protocol.Person;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.Charset;
import java.util.UUID;

public class MyServerPersonHandler extends SimpleChannelInboundHandler<Person> {
    private  int count;
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Person msg) throws Exception {
        int length = msg.getLength();
        byte[] bb = msg.getContent();
        System.out.println("length:"+length);
        System.out.println("content:"+new String(bb, Charset.forName("utf-8")));

        System.out.println("times:"+(++this.count));
        String responseMsg = UUID.randomUUID().toString();
        byte[] resMsgbytes = responseMsg.getBytes("utf-8");
        int resMsglen = resMsgbytes.length;

        Person p = new Person();
        p.setLength(resMsglen);
        p.setContent(resMsgbytes);

        ctx.writeAndFlush(p);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
        ctx.close();
    }
}
