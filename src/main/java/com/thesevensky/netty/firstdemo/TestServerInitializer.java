package com.thesevensky.netty.firstdemo;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * 客户端一旦和服务器端建立连接initChannel就会执行
 */
public class TestServerInitializer extends ChannelInitializer<SocketChannel> {

    /**
     * 初始化管道
     */
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //下面这个类是处理请求的编解码
        pipeline.addLast("httpServerCodec", new HttpServerCodec());
        pipeline.addLast("testHttpServerHandler", new TestHttpServerHandler());
    }
}
