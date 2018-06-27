

# 最新代码在更好用的码云：https://gitee.com/tywo45/t-io


## **t-io: 让天下没有难开发的网络编程**
 旧时王谢堂前燕，飞入寻常百姓家----当年那些王谢贵族们才拥有的"百万级即时通讯"应用，将因为t-io的诞生，纷纷飞入普通人家的屋檐下。

 [![image](https://gitee.com/tywo45/t-io/raw/master/docs/logo/preview.png)](http://t-io.org/doc/index.html)

## **t-io是啥**
- 一个基于java aio的TCP长连接编程框架，同类型的编程框架还有netty等

## **t-io提供了哪些功能**
- 对半包和粘包的处理：见：https://my.oschina.net/talenttan/blog/1610690
- TCP会话维护
- 心跳检测（防止不良客户端占着TCP连接无所事事）
- 心跳发送（client）
- 断链重连（client）
- 流量统计（既提供单条TCP会话流量统计，又提供所有TCP会话流量统计）
- userid绑定（将TCP会话和业务中的userid绑定，并提供查询、发送、解绑等API供业务端使用，一个userid可以绑定多个TCP会话）
- token绑定（将TCP会话和业务中的token绑定，并提供查询、发送、解绑等API供业务端使用，一个token可以绑定多个TCP会话）
- group绑定（将TCP会话和群组绑定，并提供查询、发送、解绑等API供业务端使用，一个group可以绑定多个TCP会话，如果你有IM群聊场景，这个功能会大大减少你的业务端代码）
- bsId绑定（将TCP会话和业务id绑定，并提供查询、发送、解绑等API供业务端使用，一个bsId只能绑定一个TCP会话）
- 提供IP拉黑功能，见：https://apidoc.gitee.com/tywo45/t-io/org/tio/core/Tio.html
- 提供了分页查询TCP会话功能，见：https://apidoc.gitee.com/tywo45/t-io/org/tio/core/Tio.html
- 异步发送能力（把packet丢到队列即返回）
- 阻塞发送能力（确认把packet发送到对端后再返回）
- 同步发送能力（相当于act机制，需要业务端配合设置synSeq才能完成此功能）
- 基于t-io已经实现了tio-http-server，示例：https://gitee.com/tywo45/tio-http-server-showcase
- 基于t-io已经实现了tio-websocket-server，示例：https://gitee.com/tywo45/tio-websocket-showcase
- 基于t-io已经实现了IM能力，示例：https://gitee.com/xchao/j-im
- 提供UDP能力，示例：https://gitee.com/tywo45/tio-udp-showcase
- 内置SSL能力，业务层只需要添加一行：https://my.oschina.net/talenttan/blog/1587197
- 内置集群能力，这里也有一篇关于tio集群的文章，可以参考：https://my.oschina.net/zyw205/blog/1827495

## **t-io性能**
- t-io 30万TCP长连接测试报告，见：https://my.oschina.net/u/2369298/blog/915435
- tio官网不间断运行88天，各项监控数据良好，见：https://gitee.com/uploads/images/2018/0607/150205_de698afe_351037.png

## **t-io生态**
- 在册案例（更多的案例是不在册的）：https://t-io.org/case/index.html
- 商务合作：https://t-io.org/bs/index.html
- t-io相关博客：https://www.oschina.net/search?q=t-io&scope=blog&sort_by_time=1
- t-io相关讨论：https://www.oschina.net/search?q=t-io&scope=bbs&catalog=1&sort_by_time=1

## **引入t-io**
- 在你的pom.xml中加入如下代码片段
```
<dependency>
	<groupId>org.t-io</groupId>
	<artifactId>tio-core</artifactId>
	<version>3.0.6.v20180626-RELEASE</version>
</dependency>
```
- [t-io版本列表](http://repo.maven.apache.org/maven2/org/t-io/tio-core/ "t-io版本列表")

## **用于学习t-io各组件的showcase工程**
- [tio-showcase](https://gitee.com/tywo45/tio-showcase "tio-showcase") (学习tio-core的最好示例)
- [tio-websocket-showcase](https://gitee.com/tywo45/tio-websocket-showcase "tio-websocket-showcase") (学习tio-websocket-server的最好示例，这里有篇文章可以看一下：https://my.oschina.net/talenttan/blog/1806324)
- [tio-udp-showcase](https://gitee.com/tywo45/tio-udp-showcase "tio-udp-showcase") (学习tio-udp-server的最好示例，这篇博客可以参考：https://my.oschina.net/talenttan/blog/1823774)
- [tio-http-server-showcase](https://gitee.com/tywo45/tio-http-server-showcase "tio-http-server-showcase") (用于学习tio-http-server，可以关注里面的TestController)

## **t-io番外**
- [t-io文档](https://t-io.org/blog/index.html "t-io文档")
- [t-io成功案例](https://t-io.org/case/index.html "t-io成功案例")
- [t-io官网访问统计](https://t-io.org/stat/index.html "t-io官网访问统计")
- [tio-websocket文档](https://t-io.org/blog/index.html?p=%2Fblog%2Ftio%2Fws%2Fshowcase.html "tio-websocket文档")

## **t-io赞助商**

- [优客服 - 开源的智能客服系统 + 呼叫中心](https://t-io.org/api/ad/1.php "优客服 - 开源的智能客服系统 + 呼叫中心")
- [layuiAdmin - 通用后台管理模板](https://t-io.org/api/ad/12.php "layuiAdmin - 通用后台管理模板")
