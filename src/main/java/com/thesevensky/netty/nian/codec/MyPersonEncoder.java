package com.thesevensky.netty.nian.codec;

import com.thesevensky.netty.nian.protocol.Person;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MyPersonEncoder extends MessageToByteEncoder<Person> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Person msg, ByteBuf out) throws Exception {
        System.out.println("MyPersonEncoder");
        out.writeInt(msg.getLength());
        out.writeBytes(msg.getContent());
    }
}
