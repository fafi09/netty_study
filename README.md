# netty步骤
## 1.服务端
- bossgroup,workergroup,绑定端口
- channelinitializer
- 编写自己的handler

1. 客户端连上netty服务器后，马上调用handlerAdded方法完成channel的添加操作(所谓channel可以理解为一个客户端)
2. 添加操作执行完成以后立马调用channelRegistered方法将channel注入到netty中管理起来
3. 注册好以后调用服务器端的channelActive方法，让其处于激活状态
4. 调用channelRead0方法完成客户端数据的读取和相应
5. 调用完成以后curl主动断开服务器的链接，并通知服务器端，服务器端就会调用channelInactive方法处理回调事件
6. 最后从netty的注册中将该channel删除掉channelUnRegistered
7. handlerRemoved表示连接断开，如果客户端强制断开，服务器感知不到，心跳机制保证 channelGroup.remove会自动调用

## 2.客户端
- Bootstrap.connect

# protobuf步骤
- 编写.proto文件
- gradle task generateProto生成java类
- https://github.com/grpc/grpc-java
## 解决服务器端与客户端引用相同的包问题
### git submodule

# thrift
## 安装
thrift -r --gen java data.thrift

# gRpc
## protobuf文件编写建议使用proto3
## protoc 编译
## 实现接口类
## 编写服务端代码与客户端代码

# gradlew-gradlewraper
- 使得无gradle环境的本机能够得到构建

# nio
- put() : 存入数据到缓冲区中
- get() : 获取缓冲区中的数据
- flip(); 切换读取数据模式
- rewind() : 可重复读
- clear() : 清空缓冲区. 但是缓冲区中的数据依然存在，但是处于“被遗忘”状态
- mark() : 标记是一个索引，通过 Buffer 中的 mark() 方法  
指定 Buffer 中一个特定的 position ，之后可以通过调用 reset() 方法恢复到这个 position.


|io|nio|
|io|面向快|

## read操作
向内核发送读取数据请求，
操作系统从用户模式切换到内核模式，
dma向磁盘读取数据到内核空间缓冲区。
从内核空间缓冲区cpy到用户空间缓冲区

## write操作
向内核发送写数据请求，
从用户空间缓冲区cpy到内核空间缓冲区

## 0cpy
sendfile内核请求，在内核空间实现cpy
将目标写入到socket缓冲区
内存映射来使用户参与其中
https://www.cnblogs.com/ronnieyuan/p/12009692.html

## 

## nettyserver启动流程
### serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                        .handler(new LoggingHandler(LogLevel.INFO))
                        .childHandler(new HeartBeatServerInitializer());
- NioServerSocketChannel通过调用无参的构造方法由ReflectiveChannelFactory反射生成
- NioServerSocketChannel生成时,super(null, channel, SelectionKey.OP_ACCEPT);将OP_ACCEPT保存到此实例
- config = new NioServerSocketChannelConfig(this, javaChannel().socket());在DefaultChannelConfig中生成
可伸缩的bytebuf

    
### serverBootstrap.bind
#### regFuture = initAndRegister()
- ChannelFuture regFuture = config().group().register(channel);将serverchannel注册到boss
- io.netty.channel.nio.AbstractNioChannel.doRegister
- selectionKey = javaChannel().register(eventLoop().unwrappedSelector(), 0, this);
    将serversocketchannel注册到操作系统所实现的selector上
#### init(channel)
- 在pipeline添加server端的handler
- 在pipeline添加ServerBootstrapAcceptor

## nettyserver接收客户端
### ServerBootstrapAcceptor是一个handler
- 在channelRead中取得客户端socketchannel并在此channel的pipeline中追加work线程组的handler(initializerhandler)  
- childGroup.register(child)将workergroup与客户端socketchannel关联，并追加ChannelFutureListener  
如果完成不成功就强制关闭
### read-->NioEventLoop.processSelectedKey->unsafe.read(); 
如果存在感兴趣的事件就开始对应操作(这里的读与accept是同一个逻辑)
### unsafe.read(); ->doReadMessages(readBuf);(通过SocketUtils.accept(javaChannel())方法得到客户端socketchannel)
### 读到socketchannel封装成niosocketchannel然后pipeline.fireChannelRead(readBuf.get(i));触发channelread到  
serverBootstrap(ServerBootstrapAcceptor)的channelRead方法中
### ServerBootstrapAcceptor中childGroup.register(child)(在客户端多线程组中开启异步注册任务),  
pipeline.fireChannelActive()->defaultchannelpipeline.readIfIsAutoRead()->
AbstractChannel.doBeginRead
### AbstractNioChannel.doBeginRead->readInterestOp是1 将读事件注册到key
        if ((interestOps & readInterestOp) == 0) {
            selectionKey.interestOps(interestOps | readInterestOp);
        }

-----
## 调用栈
### IdleStateHandler.userEventTriggered
>userEventTriggered:11, HeartBeatServerHandler (com.thesevensky.netty.heartbeatdemo.server)
 invokeUserEventTriggered:329, AbstractChannelHandlerContext (io.netty.channel)
 invokeUserEventTriggered:315, AbstractChannelHandlerContext (io.netty.channel)
 fireUserEventTriggered:307, AbstractChannelHandlerContext (io.netty.channel)
 channelIdle:374, IdleStateHandler (io.netty.handler.timeout)
 run:497, IdleStateHandler$ReaderIdleTimeoutTask (io.netty.handler.timeout)
 run:469, IdleStateHandler$AbstractIdleTask (io.netty.handler.timeout)
 call:38, PromiseTask$RunnableAdapter (io.netty.util.concurrent)
 run:120, ScheduledFutureTask (io.netty.util.concurrent)
 safeExecute$$$capture:163, AbstractEventExecutor (io.netty.util.concurrent)
 safeExecute:-1, AbstractEventExecutor (io.netty.util.concurrent)
 runAllTasks:403, SingleThreadEventExecutor (io.netty.util.concurrent)
 run:462, NioEventLoop (io.netty.channel.nio)
 run:858, SingleThreadEventExecutor$5 (io.netty.util.concurrent)
 run:144, DefaultThreadFactory$DefaultRunnableDecorator (io.netty.util.concurrent)
 run:745, Thread (java.lang)

### channelRead
> channelRead0:12, MyServerHandler (com.thesevensky.netty.socketdemo)
  channelRead0:8, MyServerHandler (com.thesevensky.netty.socketdemo)
  channelRead:105, SimpleChannelInboundHandler (io.netty.channel)
  invokeChannelRead:362, AbstractChannelHandlerContext (io.netty.channel)
  invokeChannelRead:348, AbstractChannelHandlerContext (io.netty.channel)
  fireChannelRead:340, AbstractChannelHandlerContext (io.netty.channel)
  channelRead:102, MessageToMessageDecoder (io.netty.handler.codec)
  invokeChannelRead:362, AbstractChannelHandlerContext (io.netty.channel)
  invokeChannelRead:348, AbstractChannelHandlerContext (io.netty.channel)
  fireChannelRead:340, AbstractChannelHandlerContext (io.netty.channel)
  fireChannelRead:293, ByteToMessageDecoder (io.netty.handler.codec)
  channelRead:267, ByteToMessageDecoder (io.netty.handler.codec)
  invokeChannelRead:362, AbstractChannelHandlerContext (io.netty.channel)
  invokeChannelRead:348, AbstractChannelHandlerContext (io.netty.channel)
  fireChannelRead:340, AbstractChannelHandlerContext (io.netty.channel)
  channelRead:1334, DefaultChannelPipeline$HeadContext (io.netty.channel)
  invokeChannelRead:362, AbstractChannelHandlerContext (io.netty.channel)
  invokeChannelRead:348, AbstractChannelHandlerContext (io.netty.channel)
  fireChannelRead:926, DefaultChannelPipeline (io.netty.channel)
  read:134, AbstractNioByteChannel$NioByteUnsafe (io.netty.channel.nio)
  processSelectedKey:644, NioEventLoop (io.netty.channel.nio)
  processSelectedKeysOptimized:579, NioEventLoop (io.netty.channel.nio)
  processSelectedKeys:496, NioEventLoop (io.netty.channel.nio)
  run:458, NioEventLoop (io.netty.channel.nio)
  run:858, SingleThreadEventExecutor$5 (io.netty.util.concurrent)
  run:144, DefaultThreadFactory$DefaultRunnableDecorator (io.netty.util.concurrent)
  run:745, Thread (java.lang)