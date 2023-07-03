# Network programming is very bitter, that was before using t-io
t-io is a high-performance network programming framework developed based on java. Its explosive performance, long battery life stability, extremely easy-to-use API, and rich and sufficient built-in functions make it popular among engineers as soon as it debuts. , and has been continuously passed on by word of mouth

# source code repository
[![t-io gitee](https://www.tiocloud.com/2/imgs/product/tio/mayun.png) ](https://gitee.com/tywo45/t-io)
[![t-io github](https://www.tiocloud.com/2/imgs/product/tio/Github.png) ](https://github.com/tywo45/t-io)


# Why develop t-io
Instead of sitting and studying the thick "xxx Authoritative Guide", it is better to stand up and independently develop and create programming APIs that more people can understand at a glance

#t-io's past and present
1. In 2010, talent-tan took over the communication module of the network management system in a major communication company. The old code used traditional IO, and a client needed at least 3 threads to guard, and frequent memory overflow and downtime. Talent-tan was ordered to rewrite the communication module and solve all the problems that the leaders were concerned about.
2. In 2012, the talent-nio framework was developed based on nio
3. In 2013, I wrote a transparent transmission module in mycat with talent-nio
4. In 2014, realized the IM module of Zebo live broadcast with talent-nio
5. In 2015, talent-tan began to pay attention to aio technology, and at the same time further abstracted the thread pool, lock processing, and concurrent data structure in talent-nio, so that these "Wang Xietang Qianyan" flew into the "mass code farmers"
6. In 2016, talent-nio was rewritten based on aio technology, named talent-aio, and the code was settled in the code cloud, which is low-key and open source
7. In 2017, talent-aio changed its name to t-io, and began to spread t-io in the form of news in open source China. In the same year, t-io became a GVP project, and t-io gained a large number of users
8. In 2018, peripheral products such as tio-http-server, tio-websocket-server, and tio-webpack were implemented based on t-io. These products not only verified the excellence of t-io, but also promoted the progress of t-io
9. In 2019, a Huawei team conducted a 3-6 month-long copying extreme pressure test on a smart product based on t-io. t-io withstood the test and made a solid foundation for officially entering Huawei's open source optimization library. foreshadowing
10. In 2020, t-io will officially enter Huawei's open source optimization library. The first commercial IM Tanchat based on t-io will be officially launched. The cluster version of t-io will also be officially developed and passed the pressure test.
11. In 2021, customer feedback based on t-io's research and development of Tanchao is very stable
12. In 2022, Tanchat, a cluster version based on t-io’s research and development, won the ultimate reputation from users, and at the same time brought a long-term financial guarantee for t-io’s continuous investment

# t-io ecology

| Component/Framework/Product | Description | Remarks |
| ----- | ----- | ----- |
| tio-http | http server based on t-io | http server of all official products of t-io adopts tio-http |
| tio-websocket | websocket server based on t-io | all official websocket servers of t-io use tio-websocket |
| TioMQ | mqtt broker based on t-io | Server: broker.tiomq.com; Port: ws: 8083, wss: 8084, mqtt: 1883, mqtts: 8883; mqtt-client test tool: https://www. tiocloud.com/tiomq-website/product/client.html |
| TioMeter | mqtt stress testing tool based on t-io | If you need mqtt stress testing, [contact t-io official website business customer service] (https://www.tiocloud.com) |
| TioMQ Enterprise | Large-scale Internet of Things access platform based on t-io | If you have any cooperation needs, [contact t-io official website business customer service] (https://www.tiocloud.com) |
| tio-smpp | smpp protocol based on t-io | If you need cooperation, [contact t-io official website business customer service] (https://www.tiocloud.com) |

# Pain points solved by t-io
The starting point of t-io is to solve the pain points of users in network programming. Its mission is to make the world no longer have difficult network programs to develop. Let's see the surprises t-io brings to users
1. Easy to learn and easy to use. The reason why talent-tan created t-io is because similar products on the market cost a lot to learn, so when designing the API, we pay special attention to user acceptance. The first batch of t-io users mastered t-io just by watching the demonstration project provided by t-io
2. Roll over the data monitoring capabilities of all well-known similar products - not only provide comprehensive monitoring data, but also ensure excellent performance
3. Built-in heartbeat timeout check, heartbeat timing sending capability
4. The extremely polished underlying cluster capability can seamlessly solve the cluster requirements of large-scale products such as IM and Internet of Things
5. Automatically reconnect when disconnected
6. t-io measured performance 1: 1.9G memory stably supports 300,000 TCP long connections: https://www.tiocloud.com/61
7. t-io measured performance 2: use t-io to run 10.51 million chat messages per second: https://www.tiocloud.com/41
8. t-io measured performance 3: Netty and t-io comparison test results: https://www.tiocloud.com/154
9. Built-in ack message capability
10. Built-in half-pack sticky bag processing
11. Self-created tool libraries such as synchronous locks, synchronous safe thread pools, and synchronous data structures, providing rich out-of-the-box APIs for business applications
12. Built-in slow attack defense mechanism to help applications automatically blacklist suspected IPs
13. Rich ecology, currently http, websocket, mqtt and a large number of private protocols have been implemented with t-io
14. Low requirements for development engineers, saving labor costs for enterprises
15. Excellent performance, saving hardware deployment costs for enterprises

# t-io documentation
https://www.tiocloud.com/doc/tio/85

# t-io technical white paper
["t-io Technical White Paper"](https://www.tiocloud.com/tio.pdf)

[![t-io technical white paper](https://images.gitee.com/uploads/images/2021/1123/155602_fde63447_355738.jpeg "t-io technical white paper.jpg")](https://www.tiocloud .com/tio.pdf)


#t-io word of mouth

![t-io user reputation (1)](https://res.tiocloud.com/202111/blog/upload/img/50/8931/1119484/88097537/74541310905/47/165441/1465242802995732480_sm.jpeg "t- io user word of mouth 1.jpg")

![t-io user reputation (2)](https://res.tiocloud.com/202111/blog/upload/img/50/8931/1119484/88097537/74541310905/30/165441/1465242803872342016_sm.jpeg "t- io user word of mouth 2.jpg")

![t-io user reputation (3)](https://res.tiocloud.com/202111/blog/upload/img/50/8931/1119484/88097537/74541310905/20/165442/1465242804337909760_sm.jpeg "t- io user word of mouth 3.jpg")

![t-io user reputation (4)](https://res.tiocloud.com/202111/blog/upload/img/50/8931/1119484/88097537/74541310905/90/165441/1465242803121561600_sm.jpeg "t- io user word of mouth 4.jpg")

![t-io user reputation (5)](https://res.tiocloud.com/202111/blog/upload/img/50/8931/1119484/88097537/74541310905/29/165441/1465242803469688832_sm.jpeg "t- io user word of mouth 5.jpg")

![t-io user reputation (6)](https://res.tiocloud.com/202111/blog/upload/img/50/8931/1119484/88097537/74541310905/41/165441/1465242802333032448_sm.jpeg "t- io user word of mouth 6.jpg")

# t-io use cases
[![t-io use case](https://images.gitee.com/uploads/images/2021/1123/155431_8a7ea725_355738.jpeg "t-io use case.jpg")](https://www.tiocloud .com/2/case/index.html)

#t-io witnesses history
![t-io Witness History](https://images.gitee.com/uploads/images/2021/1123/155507_3cff18d2_355738.jpeg "t-io Witness History.jpg")
