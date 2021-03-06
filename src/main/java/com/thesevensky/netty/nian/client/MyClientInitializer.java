package com.thesevensky.netty.nian.client;

import com.thesevensky.netty.codec.decode.MyByteToLongDecoder2;
import com.thesevensky.netty.codec.encode.MyLongToByteEncoder;
import com.thesevensky.netty.nian.codec.MyPersonDecoder;
import com.thesevensky.netty.nian.codec.MyPersonEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class MyClientInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //这个地方都是几个编解码器

        pipeline.addLast(new MyPersonDecoder());
        pipeline.addLast(new MyPersonEncoder());
        //pipeline.addLast(new MyClientHandler());
        pipeline.addLast(new MyClientPersonHandler());
    }
}
