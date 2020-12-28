package com.thesevensky.netty.nian.codec;

import com.thesevensky.netty.nian.protocol.Person;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

public class MyPersonDecoder extends ReplayingDecoder<Void> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.println("MyPersonDecoder");
        int length = in.readInt();
        byte[] content = new byte[length];
        in.readBytes(content);
        Person p = new Person();
        p.setLength(length);
        p.setContent(content);
        out.add(p);
    }
}
