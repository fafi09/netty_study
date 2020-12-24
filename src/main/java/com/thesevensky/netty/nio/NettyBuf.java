package com.thesevensky.netty.nio;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class NettyBuf {
    public static void main(String[] args) {
        ByteBuf buf = Unpooled.buffer(10);
        for(int i = 0; i < 10; i++) {
            buf.writeByte(i);
        }
        for(int i = 0; i < 10; i++) {
            System.out.println(buf.getByte(i));
        }
    }
}
