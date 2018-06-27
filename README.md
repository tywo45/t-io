
<<<<<<< HEAD
=======

## 这里没有及时维护，最新代码请点击：https://gitee.com/tywo45/t-io


>>>>>>> 1ea911e426502b29b190291780b914557d41feae
## **t-io: 让天下没有难开发的网络编程**
**旧时王谢堂前燕，飞入寻常百姓家**----当年那些王谢贵族们才拥有的"百万级即时通讯"应用，将因为t-io的诞生，纷纷飞入普通人家的屋檐下。

## **t-io是啥**
- 大家口中的t-io一般是指tio-core，它是基于java aio的网络编程框架，和netty属于同类
- 基于tio-core来开发**IM**、**TCP私有协议**、**RPC**、**游戏服务器端**、**推送服务**、**实时监控**、**物联网**将会变得**空前的简单**。
- t-io家族除了tio-core外，还有tio-websocket-server、tio-http-server、tio-webpack-core、tio-flash-policy-server等，后面所列都是基于tio-core开发的应用层组件
- 列一下t-io家族成员：
	- **tio-core**：基于java aio的网络编程框架。使用示例：[tio-showcase](https://gitee.com/tywo45/tio-showcase "tio-showcase")
	- **tio-websocket-server**：基于tio-core开发的websocket服务器。使用示例：[tio-websocket-showcase](https://gitee.com/tywo45/tio-websocket-showcase "tio-websocket-showcase")
	- **tio-http-server**：基于tio-core开发的http服务器。使用示例：[tio-http-server-showcase](https://gitee.com/tywo45/tio-http-server-showcase "tio-http-server-showcase")
	- **tio-webpack-core**：基于tio-core开发的js/css/html编译压缩工具，代码已经开源，但尚无使用文档和示例，暂时属于内用阶段，使用案例：http://www.nb350.com
	- **tio-flash-policy-server**：基于tio-core开发的flash-policy-server，代码已经开源，但尚无使用文档和示例
- 列一下tio-core应用场景的一些案例
	- **IM**：[j-im](https://gitee.com/xchao/j-im "j-im")
	- **游戏服务器端**：[贝密游戏](https://gitee.com/beimigame/beimi "贝密游戏")
	- **推送服务**：[牛吧云播](https://www.nb350.com "牛吧云播")
	- **物联网**：[氦氪云](https://hekr.me/cn/ "氦氪云")
	- **TCP私有协议**：[这个太多了，直接看案例列表吧](https://t-io.org/case/index.html "这个太多了，直接看案例列表吧")
	
## **tio-core提供了哪些功能**
- 内置对半包和粘包的处理：源码分析见：https://my.oschina.net/talenttan/blog/1610690
- ChannelContext资源维护：会话资源维护是个工程量大，复杂度高的活，尤其是涉及到各种资源绑定、解绑、遍历时，极易出错和OOM，不过这些复杂的事件tio-core全部给你做好了，除非你要自己额外去绑定资源
- 心跳检测（防止不良客户端占着TCP连接无所事事）
- 心跳发送（client）
- 断链重连（client）
- 流量监控统计，既提供单条ChannelContext流量统计，又提供所有ChannelContext流量统计，下面所列为部分监控数据，详情请见：[ChannelStat.java](https://apidoc.gitee.com/tywo45/t-io/org/tio/core/stat/ChannelStat.html "ChannelStat.java")和[GroupStat.java](https://apidoc.gitee.com/tywo45/t-io/org/tio/core/stat/GroupStat.html "GroupStat.java")
	- 已接收的字节数
	- 已接收了多少次TCP数据包
	- 已接收的packet数
	- 已处理的字节数
	- 已处理的packet数
	- 处理消息包耗时，单位：毫秒。拿这个值除以handledPackets，就是处理每个消息包的平均耗时
	- 已发送的字节数
	- 已发送的packet数
	- 最近一次收到业务消息包的时间(一个完整的业务消息包，一部分消息不算)
	- 最近一次发送业务消息包的时间(一个完整的业务消息包，一部分消息不算)
	- 最近一次收到业务消息包的时间:收到字节就算
	- 最近一次发送业务消息包的时间：发送字节就算
	- 第一次连接成功的时间
	- 进入重连队列时间
- userid绑定（将ChannelContext和业务中的userid绑定，并提供查询、发送、解绑等API供业务端使用，一个userid可以绑定多个ChannelContext）
	- org.tio.core.Tio.bindUser(ChannelContext channelContext, String userid)：将ChannelContext和userid绑定
	- org.tio.core.Tio.sendToUser(GroupContext groupContext, String userid, Packet packet)：基于userid异步发送消息
	- org.tio.core.Tio.bSendToUser(GroupContext groupContext, String userid, Packet packet)：基于userid阻塞发送消息
	- org.tio.core.Tio.getChannelContextsByUserid(GroupContext groupContext, String userid)：通过userid获取ChannelContext
	- org.tio.core.Tio.unbindUser(GroupContext groupContext, String userid)：解绑指定的userid
	- org.tio.core.Tio.unbindUser(ChannelContext channelContext)：解绑所有的userid
- token绑定（将ChannelContext和业务中的token绑定，并提供查询、发送、解绑等API供业务端使用，一个token可以绑定多个ChannelContext）
	- 方法基本同userid绑定，不一一列举
- group绑定（将ChannelContext和群组绑定，并提供查询、发送、解绑等API供业务端使用，一个group可以绑定多个ChannelContext，如果你有IM群聊场景，这个功能会大大减少你的业务端代码）
	- 方法基本同userid绑定，不一一列举
- bsId绑定（将ChannelContext和业务id绑定，并提供查询、发送、解绑等API供业务端使用，一个bsId只能绑定一个ChannelContext）
	- 方法基本同userid绑定，不一一列举
- 框架内置自动和ip绑定（将ChannelContext和对端ip绑定，并提供查询、发送等API供业务端使用）
	- org.tio.core.Tio.sendToIp(GroupContext groupContext, String ip, Packet packet, ChannelContextFilter channelContextFilter)
	- org.tio.core.Tio.sendToIp(GroupContext groupContext, String ip, Packet packet)
	- org.tio.core.Tio.bSendToIp(GroupContext groupContext, String ip, Packet packet, ChannelContextFilter channelContextFilter)
	- org.tio.core.Tio.bSendToIp(GroupContext groupContext, String ip, Packet packet)
- 框架内置自动和ip:port绑定（将ChannelContext和对端ip:port绑定，并提供查询、发送等API供业务端使用）
	- org.tio.core.Tio.send(GroupContext groupContext, String ip, int port, Packet packet)
	- org.tio.core.Tio.bSend(GroupContext groupContext, String ip, int port, Packet packet)
- 框架内置自动和唯一uuid绑定（将ChannelContext和对端uuid绑定，并提供查询、发送等API供业务端使用）
	- org.tio.core.Tio.sendToId(GroupContext groupContext, String channelId, Packet packet)
	- org.tio.core.Tio.bSendToId(GroupContext groupContext, String channelId, Packet packet)
- 提供IP拉黑功能
	- org.tio.core.Tio.IpBlacklist.add(GroupContext groupContext, String ip)：把指定ip拉黑
	- org.tio.core.Tio.IpBlacklist.remove(GroupContext groupContext, String ip)：从黑名单中移除
	- org.tio.core.Tio.IpBlacklist.isInBlacklist(GroupContext groupContext, String ip)：是否在黑名单中
	- org.tio.core.Tio.IpBlacklist.clear(GroupContext groupContext)：清除黑名单
- 提供了分页查询会话功能
	- Page<ChannelContext> org.tio.core.Tio.getPageOfAll(GroupContext groupContext, Integer pageIndex, Integer pageSize)
	- Page<T> org.tio.core.Tio.getPageOfAll(GroupContext groupContext, Integer pageIndex, Integer pageSize, Converter<T> converter)
	- Page<ChannelContext> org.tio.core.Tio.getPageOfGroup(GroupContext groupContext, String group, Integer pageIndex, Integer pageSize
	- Page<T> org.tio.core.Tio.getPageOfGroup(GroupContext groupContext, String group, Integer pageIndex, Integer pageSize, Converter<T> converter)
- 异步发送能力（把packet丢到队列即返回）
	- Tio.java中以send开头的方法，譬如sendToAll()、sendToUser()、sendToGroup()等
- 阻塞发送能力（确认把packet发送到对端后再返回）
	- Tio.java中以bSend开头的方法，譬如bSendToAll()、bSendToUser()、bSendToGroup()等
- 同步发送能力（相当于act机制，需要业务端配合设置synSeq才能完成此功能）
	- org.tio.core.Tio.synSend(ChannelContext channelContext, Packet packet, long timeout)
- 基于t-io已经实现了tio-http-server
	- 示例：https://gitee.com/tywo45/tio-http-server-showcase
- 基于t-io已经实现了tio-websocket-server
	- 示例：https://gitee.com/tywo45/tio-websocket-showcase
- 基于t-io已经实现了IM能力
	- 示例：https://gitee.com/xchao/j-im
- 提供UDP能力
	- 示例：https://gitee.com/tywo45/tio-udp-showcase
- 内置SSL能力，业务层只需要添加一行：https://my.oschina.net/talenttan/blog/1587197
	- groupContext.useSsl("/cert/xxx.jks", "/cert/xxx.jks", "******");
- 内置集群能力，这里也有一篇关于tio集群的文章，可以参考：https://my.oschina.net/zyw205/blog/1827495
	- org.tio.core.GroupContext.setTioClusterConfig(TioClusterConfig tioClusterConfig)
- 内置群组成员排序能力，一般用在直播间成员排序显示、IM群组排序显示等需要排序的场景
	- groupContext.groups.setChannelContextComparator(Comparator<ChannelContext> channelContextComparator)
	
## **t-io性能**
- t-io 30万TCP长连接测试报告，见：https://my.oschina.net/u/2369298/blog/915435
- tio官网不间断运行88天，各项监控数据良好，见：https://gitee.com/uploads/images/2018/0607/150205_de698afe_351037.png

## **t-io生态**
- 在册案例（更多的案例是不在册的）：https://t-io.org/case/index.html
- 商务合作：https://t-io.org/bs/index.html
- t-io相关博客：https://www.oschina.net/search?q=t-io&scope=blog&sort_by_time=1
- t-io相关讨论：https://www.oschina.net/search?q=t-io&scope=bbs&catalog=1&sort_by_time=1

## **引入t-io**
- 如果你只是想用tio-core，只需引入
```
<dependency>
	<groupId>org.t-io</groupId>
	<artifactId>tio-core</artifactId>
	<version>3.0.6.v20180626-RELEASE</version>
</dependency>
```
- 如果你想用tio-websocket-server，只需引入
```
<dependency>
	<groupId>org.t-io</groupId>
	<artifactId>tio-websocket-server</artifactId>
	<version>3.0.6.v20180626-RELEASE</version>
</dependency>
```
- 如果你想用tio-http-server，只需引入
```
<dependency>
	<groupId>org.t-io</groupId>
	<artifactId>tio-http-server</artifactId>
	<version>3.0.6.v20180626-RELEASE</version>
</dependency>
```
- 查看t-io最新版本
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

## **学习tio-core的步骤**
- 熟练掌握ByteBuffer的使用，这是所有基于aio/nio编程的必修课程，这个懒没人躲得掉
- 下载tio-core示范程序：[tio-showcase](https://gitee.com/tywo45/tio-showcase "tio-showcase")
- 导入到eclipse或其它ide中
- 对照helloworld例子和[t-io的hello world](https://my.oschina.net/talenttan/blog/884806 "t-io的hello world")走一遍
- 下载t-io源代码：[t-io](https://gitee.com/tywo45/t-io "t-io")
- 简单地扫一下：org.tio.core.ChannelContext.java
- 简单地扫一下：org.tio.core.GroupContext.java
- 简单地扫一下：org.tio.core.Tio.java
- 最好要掌握org.tio.utils.lock.ObjWithLock<T>及其子类（因为多线程环境下，对集合的遍历、元素删除、元素添加等操作必须是线程安全的）
- 我的个人博客可以关注一下，上面有不少t-io的文章：[三流程序员的博客](https://my.oschina.net/talenttan "三流程序员的博客")
- 忠告：tio-core已经封装了大量网络开发细节，如果你连上面的步骤都不愿意走一遍，建议你还是继续你的CRUD编程。

## **t-io推荐**
- 智能客服系统 + 呼叫中心
	- 服务了上万家的优质客服系统：[优客服 - 开源的智能客服系统 + 呼叫中心](https://t-io.org/api/ad/1.php "优客服 - 开源的智能客服系统 + 呼叫中心")
- 更专业的通用后台管理模板----[layuiAdmin，layui作者亲自倾力打造](https://t-io.org/api/ad/12.php "layuiAdmin，layui作者亲自倾力打造")
	
	[![image](https://t-io.org/res/layui.png)](https://t-io.org/api/ad/12.php)

