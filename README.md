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

## 客户端
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