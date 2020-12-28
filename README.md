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

# netty分析

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

## reactor5组件
### handle
- 句柄(描述符)根据操作系统不同而不同，表示一种资源，表示一个一个事件，既可以来自内部，也可以来自外部。
内部指定时器事件，外部指socket事件等，handle是产生事件的地方，监听事件就是监听handle
### Initiation Dispatcher初始分发器
- 实际就是reactor角色，本身提供规范，规范用于控制事件调度方式，事件本身有select方法产生。  
一旦事件发生首先会分离出每个事件然后调用事件处理器，最后调用相关的回调方法来处理事件。
### syn event demultiplexer (selector)
- 同步事件分离器，本身是系统调用用于等待系统发生，调用时会被阻塞，，一直阻塞到事件分离器有事件产生为止  
对于linux就是IO多路复用器，select，poll，epoll等
### event handler
- 本身有多个回调方法构成，对应某个事件的反馈机制，netty提供了SimpleChannelInboundHandler等
### concrete event handler
- 事件处理器的实现.实现事件处理器的各个回调方法，实现处理逻辑(由workergroup调用)
### 流程
1. 当应用向Initiation Dispatcher注册具体事件处理器，应用会标识出该事件处理器希望在某个事件发生时  
向其通知的事件，该事件与handle关联，
2. Initiation Dispatcher会要求每个处理器向其传递内部handle，  
handle向操作系统标识了事件处理器
3. 当所有事件处理器调用完毕，应用调用handleevent方法启动循环。Initiation Dispatcher将每个handler合并  
并使用同步时间分离器来等待这些事件的发生。当与某个事件源对应的handle变为ready状态。同步事件分离器将通知Initiation Dispatcher  
Initiation Dispatcher会触发事件处理器的回调方法。将handle作为key得到事件处理器。
### channeloption常量类TCP配置 channelconfig constantpool常量池 attributeMap  
attrbuteKey维护业务数据，后期可取出
### attr作用域
### channelcontext是channelhandler与channelpipeline,channel的桥梁  
一个handler一个context，如果同一个handler添加多次会有不同的context对应。
pipeline是context的链表
channelpipeline存放channelhandlercontext，context存放channelhandler
### 当channelinitializer的initialchannel方法返回是这个initializer将被从channelpipe中移除

### channel attr在整个作用域中都有效 context attr只在当前上下文中有效
4.0版本channel.attr==channelhandlercontext.attr 被所有的handler共享

### NioEventLoopGroup extends MultithreadEventLoopGroup
### NioEventLoop extends SingleThreadEventLoop

### newUnsafe->NioMessageUnsafe 

### eventLoop.inEventLoop()判断 防止多线程并发执行register0
- eventLoopGroup中包含一个或多个eventLoop
- eventLoop在他的整个生命周期中只有一个thread绑定
- 所有由eventloop所处理的各种io事件都将在他所关联的thread上进行处理
- 一个channel在他的整个生命周期中只会注册在一个eventLoop上
- 一个eventLoop在运行过程中，会被分配给一个或多个channel
- 所有属于同一个channel的操作的提交任务顺序与执行顺序一样
- netty中channel是线程安全的，可以存储channel引用，调用响应write，read方法。
即便当时有很多线程都使用他，也不会出现多线程问题，而且消息一定会按顺序发送出去
- 业务开发不要讲长时间任务放入到eventloop队列，会阻塞该线程所有channel任务
eventExecutor
 1. 在channelhandler回掉方法启动自己线程池
 2. netty提供channelpipeline添加channelhandler是调用addlast方法来传递eventExecutor默认情况下channelhandler的  
回调方法都是由io线程执行，如果调用了这个方法addLast(EventExecutorGroup group, String name, ChannelHandler handler)
就是由group执行
### Future
> 1. 各状态参见ChannelFuture类注释
> 2. jdk future 取结果手工检查会阻塞，netty future以回调方式来获取执行结果  
>channelfuturelistener 的 complete方法是有io线程执行，不要执行耗时操作，否则则需要在另外的线程执行
> 3. future怎么知道操作完成。（promise可写只能写一次
> 4. SimpleChannelInboundHandler类注释，当中有读完read后ReferenceCountUtil.release(msg);不应保存msg否则失效。  
>引用计数release
> 5. channelRead异步读取可能会引起release先释放资源了。所以这时不能继承SimpleChannelInboundHandler

### handler
#### ctx.writeAndFlush与ctx.channel.writeAndFlush区别。
> - ctx.channel.writeAndFlush从channel的最后一个处理器通过出站handler逐个向前到达目标
> ctx.writeAndFlush当前所调用的handler的下一个handler开始传输到目标
> - 结论：ctx与handler关联关系是不变的，将其缓存无问题。对于同名方法，ctx具有较短的事件流

#### 关系
>                     group
>                     /   \
>                    loop  loop
>                   /  \    /  \
>             channel  c     c   channel
>
## nio ByteBuffer回顾
### channel
- 可以向其写入数据和读取数据的对象，都是通过buffer进行的。与stream比较channnel是双向的。
### nio文件读取步骤
> 1. 从fileinputstream获取channel对象
> 2. 创建buffer
> 3. 将从channel读取到buffer
>> a. 0 < mark < positon < limit < capacity  
>> b. flip() 将position=0, limit = position  
>> c. clear() 将limit=capacity 将position=0
>> d. compact()
>>> 将所有未读数据复制到buffer起始位置，  
>>> 将position=最后一个未读元素后面，  
>>> 将limit=capacity
>>> 将mark=-1丢弃
### directbuffer
- 本身在堆内，但数据在Java内存外，形成zerocpy
## ByteBuf 类注释 三类堆内，堆外，composite
### writeindex readindex readablebytes
### jvm buf heapbuf
- 存入array jvm堆中，可以快速创建和释放，提供访问内部数组方法 
- 缺点：每次读写都需要copy到直接缓冲区进行网络传输
### directbuf 在堆外分配，由操作系统在本地内进行分配
- 优点：使用socket传递时，性能好。不需要jvm复制到直接缓冲区
- 缺点：directbuf在os内存中，内存的分配释放要比堆复杂。速度慢，netty通过内存池解决此问题。  
直接缓冲区不能通过数组方式访问数据。
### compositeBuf复合缓冲，容纳其他buf。
## jdk buf 与netyy buf
### netty采用writeindex readindex
### 读写索引超出就会抛异常
### 位于buf任何读写操作都会单独维护读索引写索引。maxcapacity默认为integer.max
### netty buf满了会自动扩容？
### jdk buf 底层final byte[] hb;使用前需要估计容量，扩容需要自己手动实现。创建全新的bytebuf对象然后将数据cpy过来。
### position一个变量来标识信息，读写需要调用flip来切换
### netty buf 不足会自动扩容， 读写是分开的。
## ReferenceCounted引用计数 AbstractReferenceCountedByteBuf
### retain0采用cas AtomicIntegerFieldUpdater  为什么不用AtomicInteger
#### AtomicIntegerFieldUpdater
- 必须是int类型 如果包装类型AtomicReferenceFieldUpdater
- 必须violatile
- 变量必须是实例变量
## netty处理器种类：入栈  出栈
### 入栈 ChannelInboundHandler
### 出栈 ChannelOutboundHandler
### 编解码器
- 数据原本形式->字节流 编码 出栈处理器
- 将字节->另外格式 解码 入栈处理器
- 如果前一个处理器出来的数据与后一个处理器处理类型不同，则会跳出
#### 结论
- 无论编解码器接收的消息类型必须与参数类型一致，否则会执行下一个处理器
- 一定要判断缓冲是否够字节数，如long型if(in.readableBytes() >= 8)
#### ReplayingDecoder header content 类型消息
#### MessageToMessageDecoder 一种类型转换成另一种类型
#### LineBasedFrameDecoder基于行的
#### FixedLengthFrameDecoder根据固定长度分割
#### DelimiterBasedFrameDecoder分割符
#### LengthFieldBasedFrameDecoder基于长度字段
### tcp粘包拆包

# 中继服务
## 服务向另外一个服务发请求应该在一个eventloop内 ？
    //server
    channelActive() {
        //client
        Bootstrap bootstrap = new Bootstrap();
                    bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                            .handler(
                            // 两个channel公用一个loop
                            bootstrap.group(ctx.channel().eventloop());
                            bootstrap.connect();
                            
                            );
        
    }
    
  