package com.thesevensky.netty.firstdemo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * ChildHandler处理workerGroup 而 Handler处理 boosGroup
 * netty没有实现servlet标准
 * servlet标准如何定义request,如何取得请求参数等。
 * 没有对请求路由支持
 */
public class TestServer {
    public static void main(String[] args) throws InterruptedException {
        //事件循环组
        //这个boosGroup来获取连接并转发给workerGroup
        EventLoopGroup boosGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boosGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new TestServerInitializer()); //childHandler->worker handler->boss

            //sync表示netty一直等待 直到任务完成
            ChannelFuture channelFuture = serverBootstrap.bind(8899).sync();
            //如果关闭，要等关闭任务完成才能向下执行 一般程序会停留在这里
            channelFuture.channel().closeFuture().sync();
        } finally {
            //优雅关闭
            boosGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
