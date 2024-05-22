## t-io简介
t-io是基于java aio研发的网络编程框架，从收集到的案例来看，用t-io做物联网、IM、客服的比较多，堪称殿堂级网络开发框架
- [快速开始](https://www.tiomq.com/books/doc/126/1013)：Show me the code and document
- [用户案例](https://www.tiomq.com/books/doc/36/1017)：看看哪些人在使用t-io，并且愿意让大家知道他们使用了t-io
- [提交案例](https://www.tiomq.com/books/doc/36/1135)：这将增加您产品的曝光率，让更多人知道您的产品
- [删除案例](https://www.tiomq.com/books/doc/36/1202)：有那么一瞬间，您又不想让您的案例让更多人知道

## 设计使命
减少网络编程难度和繁琐度，让业务开发人员只需要关注业务设计与实现，不需要花太多时间在网络编程细节、多线程安全、并发性能

## 功能介绍
1.  **心跳自动处理** ：心跳超时检测（服务器端）、心跳定时发送（客户端）
1.  **用户管理** ：Tio.bindUser()、Tio.getByUserid()
1.  **Token管理** ：Tio.bindToken()、Tio.getByToken()
1.  **Group管理** ：Tio.bindGroup()、Tio.getByGroup()
1.  **同步发送** ：Tio.synSend()，需要协议配合
1.  **阻塞发送** ：Tio.bSend()，等消息发送成功，再返回
1.  **异步发送** ：Tio.send()，把消息丢进队列发送
1.  **统一TCP&UDP编程API** ：除了启动代码略有差异外，面向业务的API几乎一模一样。为一键切换TCP/UDP提供了极大便利
1.  **协议适配** ：可以通过协议转换，将不同协议转成同一个协议，方便业务进行统一处理
1.  **单一通道流量监控** ：最近一次收到业务消息包的时间、最近一次发送业务消息包的时间、最近一次收到字节的时间、最近一次发送字节的时间、最近一次packet进入进入发送队列的时间、ChannelContext对象创建的时间、第一次连接成功的时间、连接关闭的时间、本连接已发送的字节数、本连接已发送的packet数、本连接已处理的字节数、本连接已处理的packet数、处理消息包耗时、本连接已接收的字节数、本连接已接收了多少次TCP/UDP数据包、本连接已接收的packet数、心跳超时次数、平均每次TCP接收到的字节数、处理packet平均耗时、平均每次TCP接收到的业务包数
1.  **通道组流量监控** ：参考GroupStat.java
1.  **资源共享** ：同一jvm启动多个服务器时，各服务器的资源可以直接共享，简化编程和统计
1.  **自动重连** ：客户端断开连接时，t-io提供自动重连机制
1.  **集群（依附于商业产品）** ：t-io内置了强大的集群能力，支持单机集群、双机集群、多机集群，业务节点可在各集群服务器间随意切换
1.  **订阅树（依附于指定商业产品）** ：t-io内置了强大的高性能订阅树，可以轻松完成订阅、发布等能力
1.  **MQTT协议（依附于指定商业产品）** ：t-io内置了mqtt协议，含服务器端和客户端

... ...

## 生态及案例介绍
### HTTP服务器
tio-http已经实现了HTTP协议，并内置了一个mvc，可以代替tomcat、jetty等容器完成http接入

### Websocket服务器
tio-websocket已经实现了websocket协议，目前有不少用户用它做为自己的websocket接入服务器

### MQTT压力测试工具（商业产品）
TiOMQ Meter是基于t-io和swing研发的MQTT压力测试工具，它不光能测纯粹的MQTT协议服务器，还能测适配型的MQTT协议
![输入图片说明](https://res.tiocloud.com/202405/tiomq/doc/img/50/8931/1119484/88097537/74541310905/43/103347/1787672144549912576.png)

### 网络调试工具（未开源）
Tio Network Debug是基于t-io和swing研发的网络协议调试工具，主要用于产品早期的协议对接
![输入图片说明](https://res.tiocloud.com/202312/tiomq/doc/img/50/8931/1119484/88097537/74541310905/45/193509/1733087818730840064.png)

### MQTT客户端（免费）
具体请参考：[https://www.tiomq.com/product/client](https://www.tiomq.com/product/client)
![输入图片说明](https://res.tiocloud.com/202405/tiomq/doc/img/50/8931/1119484/88097537/74541310905/13/104103/1787673973295816704.png)

### TiOMQ Platform（商业产品）
这是一个物联网平台，细节可参考：[https://env1.tiomq.com](https://env1.tiomq.com)
![输入图片说明](https://res.tiocloud.com/202405/tiomq/doc/img/50/8931/1119484/88097537/74541310905/89/104318/1787674538654441472.png)

### 其他案例
更多生态或案例可前往[https://www.tiocloud.com/1/case/](https://www.tiocloud.com/1/case/)查看

## 商务合作
![输入图片说明](https://www.tiocloud.com/1/imgs/product/ercode2.png?8888)
