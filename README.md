# 步骤
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