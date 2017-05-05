### 这个项目的最新代码在：https://git.oschina.net/tywo45/t-io


# **t-io: 百万级TCP长连接即时通讯框架，让天下没有难开发的即时通讯**
## **简 介**
t-io是基于jdk aio实现的易学易用、稳定耐操、性能强悍、内置功能丰富、核心代码只有3000多行的即时通讯框架。字母 t 取talent（天才）的首字母，也可以理解为"特快"，同时也是作者姓氏的首字母。
## **最新maven坐标**

``` 
<dependency>
    <groupId>org.t-io</groupId>
    <artifactId>tio-core</artifactId>
    <version>1.7.0.v20170501-RELEASE</version>
</dependency>
```
## **各种传送门**

 - [官 网][1]
 - [极速入门][2]
 - [API][3](只需要看[Aio.java][4])
 - [资料及问题汇总][5]
 - [作者博客][6]
## **常见应用场景**
IM、实时监控、推送服务（已内置API）、RPC、游戏、物联网等实时通讯类型的场景
## **t-io特点**
 - **极简洁、清晰、易懂的API**：没有生涩难懂的新概念，只需花上30分钟学习helloworld，就能很好地掌握并实现一个性能极好的即时通讯应用
 - **极震撼的性能**：轻松支持百万级tcp长连接，彻底甩开业界C1000K烦恼；最高时，每秒可以收发500万条消息，约165M
 - **极亲民的内置功能**：心跳检测、心跳发送、自动重连、绑定用户id、绑定群组id、各项消息统计等功能，全部一键内置搞定，省却各种烦恼。
## **性能数据**
 - IM实例收发速度500万条/秒----此数据系网友提供（i7 6700 + 固态硬盘 + win10），我本地只能跑到333万/秒
 - IM实例17.82万TCP长连接且正常收发消息只消耗800M内存，CPU使用率极低，目测t-io可以支撑200万长连接
 - 17万长连接 + 各种破坏性测试，服务器内存保持稳定（600多M到900M间）
## **性能测试步骤**
### 测试单机吞吐量






  [1]: http://www.t-io.org:9292/
  [2]: https://my.oschina.net/talenttan/blog/884806
  [3]: http://www.t-io.org:9292/apidocs/org/tio/core/Aio.html
  [4]: http://www.t-io.org:9292/apidocs/org/tio/core/Aio.html
  [5]: https://my.oschina.net/talenttan/blog/863545
  [6]: https://my.oschina.net/talenttan/blog
