package com.thesevensky.netty.codec.decode;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class MyLongToStringDecoder extends MessageToMessageDecoder<Long> {
    @Override
    protected void decode(ChannelHandlerContext ctx, Long msg, List<Object> out) throws Exception {
        System.out.println("MyLongToStringDecoder.decode");
        System.out.println(msg.toString());
        ctx.writeAndFlush(msg.toString());
    }
}
