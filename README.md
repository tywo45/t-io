## Introduction to t-io
T-io is a network programming framework developed based on Java AIO. From the collected cases, t-io is widely used for IoT, IM, and customer service, making it a top-notch network programming framework
- [Quick Start](https://www.tiomq.com/books/doc/126/1013): Show me the code and document
- [User Case](https://www.tiomq.com/books/doc/36/1017): Look at who is using t-io and is willing to let everyone know that they are using t-io
- [Submit Case](https://www.tiomq.com/books/doc/36/1136): This will increase the exposure of your product and let more people know about it
- [Delete Case](https://www.tiomq.com/books/doc/36/1204): There was a moment when you didn't want your case to be known to more people

## Source code repository
[![](https://www.tiocloud.com/1/imgs/product/tio/Github.png)](https://github.com/tywo45/t-io)
[![](https://www.tiocloud.com/1/imgs/product/tio/mayun.png)](https://gitee.com/tywo45/t-io)

## Design Mission
Reduce the difficulty and complexity of network programming, allowing business developers to focus only on business design and implementation, without spending too much time on network programming details, multi-threaded security, and concurrency performance

## Function Introduction
1. **Automatic heartbeat processing**: heartbeat timeout detection (server side), heartbeat timing sending (client side)
1. **User Management**: Tio.bindUser(), Tio.getByUserid()
1. **Token Management**: Tio.bindToken(), Tio.getByToken()
1. **Group Management**: Tio.bindGroup(), Tio.getByGroup()
1. **Synchronous sending**: Tio.synSend(), requires protocol cooperation
1. **Block sending**: Tio.bSend(), wait for the message to be successfully sent before returning
1. **Asynchronous sending**: Tio.send(), putting the message into the queue for sending
1. **Unified TCP&UDP Programming API**: Except for slightly different startup codes, the business oriented APIs are almost identical. Provides great convenience for one click switching between TCP/UDP
1. **Protocol Adaptation**: Different protocols can be converted into the same protocol through protocol conversion, facilitating unified business processing
1. **Single channel traffic monitoring**: time of last received business message packet, time of last sent business message packet, time of last received byte, time of last sent byte, time of last packet entering the sending queue, time of ChannelContext object creation, time of first successful connection, time of connection closure, number of bytes sent by this connection, number of packets sent by this connection, number of bytes processed by this connection, number of packets processed by this connection, time taken to process message packets, number of bytes received by this connection, number of TCP/UDP packets received by this connection, number of packets received by this connection T count, heartbeat timeout count, average number of bytes received per TCP, average packet processing time, average number of business packets received per TCP
1. **Channel Group Traffic Monitoring**: Refer to GroupStat.java
1. **Resource Sharing**: When the same JVM starts multiple servers, the resources of each server can be directly shared, simplifying programming and statistics
1. **Automatic reconnection**: When the client disconnects, t-io provides an automatic reconnection mechanism
1. **Cluster (attached to commercial products)**: T-IO has a powerful clustering capability built-in, supporting single machine clusters, dual machine clusters, and multi machine clusters. Business nodes can switch freely between servers in each cluster
1. **Subscription Tree (attached to specified commercial products)**: t-io has a powerful high-performance subscription tree built-in, which can easily complete subscription, publishing and other capabilities
1. **MQTT protocol (attached to specified commercial products)**: t-io has built-in mqtt protocol, including server-side and client-side
## Ecological and Case Introduction
### HTTP server
Tio HTTP has implemented the HTTP protocol and built-in an MVC, which can replace containers such as Tomcat and Jetty to complete HTTP access

### Websocket server
Tio websocket has implemented the websocket protocol, and currently many users use it as their websocket access server

### MQTT stress testing tool (commercial product)
TiOMQ Meter is an MQTT stress testing tool developed based on t-io and swing. It can not only test pure MQTT protocol servers, but also test adaptive MQTT protocols
![Enter image description](https://res.tiocloud.com/202405/tiomq/doc/img/50/8931/1119484/88097537/74541310905/43/103347/1787672144549912576.png)

### Network debugging tool (not open source)
Tio Network Debug is a network protocol debugging tool developed based on t-io and swing, mainly used for early protocol integration of products
![Enter image description](https://res.tiocloud.com/202312/tiomq/doc/img/50/8931/1119484/88097537/74541310905/45/193509/1733087818730840064.png)

### MQTT client (free)
Please refer to:[https://www.tiomq.com/product/client](https://www.tiomq.com/product/client)
![Enter image description](https://res.tiocloud.com/202405/tiomq/doc/img/50/8931/1119484/88097537/74541310905/13/104103/1787673973295816704.png)

### TiOMQ Platform (Commercial Product)
This is an IoT platform, details can refer to:[https://env1.tiomq.com](https://env1.tiomq.com)
![Enter image description](https://res.tiocloud.com/202405/tiomq/doc/img/50/8931/1119484/88097537/74541310905/89/104318/1787674538654441472.png)
### Other cases
More ecological or case studies can be found at [https://www.tiocloud.com/1/case/](https://www.tiocloud.com/1/case/) View

## Business cooperation
![Enter image description](https://www.tiocloud.com/1/imgs/product/ercode2.png?8888)
