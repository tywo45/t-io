
# 网络编程很苦，那是在使用t-io前的事
t-io是基于java开发的一款高性能网络编程框架，其炸裂的性能、超长续航的稳定性、极简易用的API、丰富够用的内置功能，让其一出道就受到广大工程师喜爱，并被持续口碑传递

# 源代码仓库
[![t-io gitee](https://www.tiocloud.com/2/imgs/product/tio/mayun.png) ](https://gitee.com/tywo45/t-io)
[![t-io github](https://www.tiocloud.com/2/imgs/product/tio/Github.png) ](https://github.com/tywo45/t-io)


# 为什么要开发t-io
与其坐学厚厚的《xxx权威指南》，不如站着自主研发，创造更多人一眼就懂的编程API

# t-io的前世今生
1. 2010年，talent-tan在某通讯大厂接手网管系统的通讯模块，老代码采用的是传统IO，一个client需要至少3个线程来守，经常性内存溢出和宕机。talent-tan临危受命，重写了通讯模块，解决了领导关注的全部问题
2. 2012年，基于nio研发了talent-nio框架
3. 2013年，用talent-nio写了mycat中的一个透传模块
4. 2014年，用talent-nio实现了热波直播的IM模块
5. 2015年，talent-tan开始关注aio技术，同时把talent-nio中的线程池、锁处理、并发数据结构进行了进一步抽象，使这些“王谢堂前燕”，飞入“广大码农家”
6. 2016年，基于aio技术重写了talent-nio，命名为talent-aio，代码入驻码云，低调开源
7. 2017年，talent-aio更名为t-io，开始在开源中国用新闻的形式传播t-io，同年t-io成为GVP项目，t-io收获大量用户
8. 2018年，基于t-io实现了tio-http-server、tio-websocket-server、tio-webpack等周边产品，这些产品既验证了t-io的优秀，又反过来促进t-io的进步
9. 2019年，华为某团队对基于t-io的某智慧产品进行了长达3~6个月的拷机极限压测，t-io经受住考验，为正式进入华为开源优选库做了扎实的铺垫
10. 2020年，t-io正式入驻华为开源优选库，基于t-io开发的第一款商业IM谭聊正式上市，集群版t-io也正式完成研发并通过压测
11. 2021年，客户反馈基于t-io研发的谭聊非常稳定
12. 2022年，基于t-io研发的集群版本谭聊，获得用户极致的口碑，同时也为t-io的持续投入带来了相当长时间的资金保障

# t-io生态
| 组件/框架/产品      | 说明                    | 备注                 |
|---------------|-----------------------|--------------------|
| tio-http      | 基于t-io实现的http服务器      | t-io官方所有产品的http服务器均采用tio-http |
| tio-websocket | 基于t-io实现的websocket服务器 | t-io官方所有产品的websocket服务器均采用tio-websocket |
| TioMQ | 基于t-io实现的mqtt broker | 服务器：broker.tiomq.com； 端口：ws：8083， wss：8084， mqtt：1883，mqtts：8883；mqtt-client测试工具：https://www.tiocloud.com/tiomq-website/product/client.html |
|    TioMeter           |    基于t-io实现的mqtt压力测试工具                   |       如有mqtt压力测试需求，[可联系t-io官网商务客服](https://www.tiocloud.com)             |
|    TioMQ Enterprise           |    基于t-io实现的大型物联网接入平台，类似emqx enterprise             |       如有合作需求，[可联系t-io官网商务客服](https://www.tiocloud.com)              |
|    tio-smpp           |    基于t-io实现的smpp协议             |       如有合作需求，[可联系t-io官网商务客服](https://www.tiocloud.com)              |



# t-io解决的痛点
t-io的出发点是解决网络编程的用户痛点，其使命是让天下再也没有难开发的网络程序，且看t-io给用户带来的惊喜
1. 易学易用，talent-tan之所以创造t-io，就是因为市面上同类产品学习成本大，所以在设计api时，特别关切用户的接受度。t-io第一批用户仅仅是看了t-io官方提供的示范工程就掌握了t-io
2. 碾压全部知名同类产品的数据监控能力----既提供全面的监控数据，又保障性能的优异
3. 内置心跳超时检查、心跳定时发送能力
4. 极致打磨的底层集群能力，可无缝解决IM、物联网等大型产品的集群需求
5. 掉线自动重连能力
6. t-io实测性能一：1.9G内存稳定支持30万TCP长连接：https://www.tiocloud.com/61
7. t-io实测性能二：用t-io跑出每秒1051万条聊天消息：https://www.tiocloud.com/41
8. t-io实测性能三：netty和t-io对比测试结果：https://www.tiocloud.com/154
9. 内置ack消息能力
10. 内置半包粘包处理
11. 自创同步锁、同步安全线程池、同步数据结构等工具库，为业务应用提供丰富的开箱即用API
12. 内置慢攻击防御机制，帮助应用自动拉黑嫌疑IP
13. 丰富的生态，目前已经用t-io实现了http、websocket、mqtt及大量私有协议
14. 对开发工程师要求低，为企业节约人工成本
15. 性能卓越，为企业节约硬件部署成本

# t-io文档
https://www.tiocloud.com/doc/tio/85

# t-io技术白皮书
[《t-io技术白皮书》](https://www.tiocloud.com/tio.pdf)

[![t-io技术白皮书](https://images.gitee.com/uploads/images/2021/1123/155602_fde63447_355738.jpeg "t-io技术白皮书.jpg")](https://www.tiocloud.com/tio.pdf)


# t-io口碑

![t-io用户口碑(一)](https://res.tiocloud.com/202111/blog/upload/img/50/8931/1119484/88097537/74541310905/47/165441/1465242802995732480_sm.jpeg "t-io用户口碑1.jpg")

![t-io用户口碑(二)](https://res.tiocloud.com/202111/blog/upload/img/50/8931/1119484/88097537/74541310905/30/165441/1465242803872342016_sm.jpeg "t-io用户口碑2.jpg")

![t-io用户口碑(三)](https://res.tiocloud.com/202111/blog/upload/img/50/8931/1119484/88097537/74541310905/20/165442/1465242804337909760_sm.jpeg "t-io用户口碑3.jpg")

![t-io用户口碑(四)](https://res.tiocloud.com/202111/blog/upload/img/50/8931/1119484/88097537/74541310905/90/165441/1465242803121561600_sm.jpeg "t-io用户口碑4.jpg")

![t-io用户口碑(五)](https://res.tiocloud.com/202111/blog/upload/img/50/8931/1119484/88097537/74541310905/29/165441/1465242803469688832_sm.jpeg "t-io用户口碑5.jpg")

![t-io用户口碑(六)](https://res.tiocloud.com/202111/blog/upload/img/50/8931/1119484/88097537/74541310905/41/165441/1465242802333032448_sm.jpeg "t-io用户口碑6.jpg")

# t-io使用案例
[![t-io使用案例](https://images.gitee.com/uploads/images/2021/1123/155431_8a7ea725_355738.jpeg "t-io使用案例.jpg")](https://www.tiocloud.com/2/case/index.html)

# t-io见证历史
![t-io见证历史](https://images.gitee.com/uploads/images/2021/1123/155507_3cff18d2_355738.jpeg "t-io见证历史.jpg")

