

# 最新代码在更好用的码云：https://git.oschina.net/tywo45/t-io
## **t-io: 不仅仅是百万级即时通讯框架**
[![image](https://gitee.com/tywo45/t-io/raw/master/docs/logo/preview.png)](https://gitee.com/tywo45/t-io)


## **t-io简介**
- t-io是基于aio的性能爆炸却又稳如泰山、API丰富却又极易掌握、监控全面无死角却又无损性能、将多线程玩到极致的即时通讯框架，字母"t"寓意"talent"。

## **t-io诞生的意义**
- 旧时王谢堂前燕，飞入寻常百姓家----当年那些王谢贵族们才拥有的"百万级即时通讯"应用，将因为t-io的诞生，纷纷飞入普通人家的屋檐下。

## **t-io官网**

### [http://t-io.org](http://t-io.org "t-io官网")

## **t-io各组件全景图**

[![image](https://gitee.com/tywo45/t-io/raw/master/docs/api/tio-parent.png)](https://gitee.com/tywo45/t-io/raw/master/docs/api/tio-parent.png)

##  t-io常用api全景图


[![image](https://gitee.com/tywo45/t-io/raw/master/docs/api/t-io-api.png)](https://gitee.com/tywo45/t-io/raw/master/docs/api/t-io-api.png)


##  **t-io常见应用场景**
    
- IM（官方提供了im例子，含web端）
- 实时监控
- 推送服务（已内置API）
- RPC
- 游戏
- 物联网（已有很多案例）
- 其它实时通讯类型的场景，不一一列举

##  **用t-io做的[web im](http://t-io.org/webim/)**

- [演示地址](http://t-io.org/webim/)

---

[![image](https://gitee.com/tywo45/t-io/raw/master/docs/tchat/1.png?v=89)](http://t-io.org/webim/)

---

[![image](https://gitee.com/tywo45/t-io/raw/master/docs/tchat/2.png?v=89)](http://t-io.org/webim/)

---


## maven坐标
```
<dependency>
    <groupId>org.t-io</groupId>
    <artifactId>tio-core</artifactId>
    <version>2.0.0.v20170824-RELEASE</version>
</dependency>
```
## **t-io特点**
###  极简洁、清晰、易懂的API
- 没有生涩难懂的新概念，原生态bytebuffer既减少学习成本，又减少各种中间对象的创建
- 只需花上30分钟学习helloworld，就能较好地掌握并实现一个性能极好的即时通讯应用

###  极震撼的性能
- 单机轻松支持**百万级tcp长连接**，彻底甩开业界C1000K烦恼；
- 最高时，每秒可以收发500万条业务消息，约165M（**1.6.9版本数据，想验证的，后面有验证步骤，1.7.1版本的im由于加入了许多业务功能，所以不能用于测试框架性能**）

###  极其体贴的内置API
- **内置心跳检测**
- **内置心跳发送**
- **各种便捷的绑定API，譬如绑定群组用于消息群发、绑定用户用于将消息发往指定的user等等**
- **各种便捷的发送API，譬如sendToGroup()、sendToUser()等等**
- **阻塞发送和异步发送仅仅是bSendXxx和sendXxx的一字之别，极其方便**
- **客户端内置断线自动重连功能**
- **对连接的监控数据做到了极致：发送\接收\处理了多少个字节多少个业务包，全部有监控**
- **针对IP做了内置的监控：譬如某IP有多少个连接、某段时间发送了多少条消息等全部一应俱有，为防攻击作了很好的基础**
![](https://gitee.com/tywo45/t-io/raw/master/docs/monitor/1.png)

![](https://gitee.com/tywo45/t-io/raw/master/docs/monitor/2.png)



## **各种传送门**


 - [t-io + layim + vue + ivivew + webpack + google-protobuf开发的web im](http://t-io.org/webim/)
 - [官 网][1]
 - [代码托管平台码云](https://gitee.com/tywo45/t-io)
 - [开源中国收录地址](https://www.oschina.net/p/t-io)
 - [talent-tan写的t-io系列文档](https://my.oschina.net/talenttan/blog?catalog=5625247)
    - [aio系列文档（1）----t-io的hello world][2]
    - [aio系列文档（2）----图解bytebuffer](https://my.oschina.net/talenttan/blog/889887)
    - [aio系列文档（3）----protobuf入门与使用](https://my.oschina.net/talenttan/blog/885477)
    - [aio系列文档（4）----t-io源代码阅读小记](https://my.oschina.net/talenttan/blog/884466)（具体内容[millions_chan](http://www.jianshu.com/u/be7966e52d09)所写）
    
 - [卡尔码农写的t-io系列文档](https://my.oschina.net/u/2461727/blog)
    - [t-io 入门篇（三）即时消息发送demo学习](https://my.oschina.net/u/2461727/blog/897548)
    - [t-io入门篇（二）](https://my.oschina.net/u/2461727/blog/894533)
    - [t-io学习入门篇（一）](https://my.oschina.net/u/2461727/blog/893899)
 - 其它小伙伴写的零碎文章和作品
    - [给jfinal写的t-io插件](https://my.oschina.net/u/1168934/blog/864239)----小徐同学花10分钟完成的作品
    - [用t-io实现的简单rpc](https://my.oschina.net/longtutengfei/blog/892053)----仅作思路参考，部分实现待完善
    - [jfinal + t-io完成的im项目](https://gitee.com/kobe577590/im)----作者天蓬小猪正在完善
 - [资料及问题汇总][5]


## **性能测试步骤**

### 测试单机吞吐量（实际上就是非网络环境啦）
1. 机器准备
    - CPU: i7 6700 / i7 4790
    - 内存：8G/4G
    - 操作系统：windows7/windows10
    - 说明：**客户机和服务器位于同一台机器**
2. 测试步骤
    - **参数调优**：修改t-io\dist\examples\im\client\startup.bat，把-Dtio.default.read.buffer.size的值换成4096
    - **参数调优**：修改t-io\dist\examples\im\server\startup.bat，把-Dtio.default.read.buffer.size的值换成4096
    - 双击 "bin/start-im-server.bat" 启动im server
    - 双击 "bin/start-im-client.bat" 启动im client
    - 保持下图参数进行测试（**强调：你需要多试几次，前面几次的性能数据是最差的，貌似跟线程池的预热有关系，有研究的朋友可以交流一下**）
    ![image](http://gitee.com/tywo45/t-io/raw/master/docs/performance/500%E4%B8%87.png)
3. 测试结果
    - 500万条/秒约165M----此数据系网友提供（i7 6700 + 固态硬盘 + win10）
    - 333万条/秒约97M----此数据系本人亲测数据（i7 4790 + 固态硬盘 + win7），测试参数与上图略有差别，不一一说明
4. 测试说明
    - 数据中的消息条数指的是业务包，不是指tcp的交互次数，了解tcp协议的人知道，tcp是双向确认可靠的传输协议，对业务而言，其实并不关心tcp了多少次，而是我们的业务数据收发了多少条。
    - **请用t-io 1.6.9分支进行测试，1.7.0加了链路行为跟踪功能、1.7.1会加上ip防黑功能，这些功能会使t-io框架本身的性能降低（就像操作系统一个开了防火墙，一个没开防火墙，性能不是一个级别的）。作为一个io框架，其实并不需要实现这些功能，但是为了让业务层更舒服，t-io还是舍弃了亮眼的性能数据去拥抱更实用的业务层功能。**
    - netty是一个知名度极高的一个框架，而且功能更多，t-io如果满足不了你或不是你的菜，可以尝试netty

### 测试centos下可以支持多少长连接数
1. 机器准备
    - 服务器一台：**centos6.x,  虚拟机，一个4核E5 CPU，内存16G**
    - 客户机11台：windows，硬件没什么特别要求，能跑起1.62万个长连接，配置不低得离谱就行
2. 测试步骤
    - 修改centos操作系统参数，使之支持更大的长连接数，细节略（可百度之）
    - 在centos上运行 "bin/start-im-server.sh" 启动im server
    - 修改dist\examples\im\client\config\app.conf，参考下面的值，注意把server指向centos的ip
    ```
        #######服务器
        server=127.0.0.1
        
        #######服务器port
        port=9321
        
        #######连接多少个连接到服务器
        client.count=16200
        
        #######进入到哪个组
        group=g
        
        #######聊天消息发的内容
        chat.content=he
        
        #######一次发多少条(这个数字不要太大)
        send.count=1
    ```
    - 把dist\examples\im\client拷到各客户机并运行"bin/start-im-client.bat"
3. 测试结果
    - 11个客户机 ，每个客户机连16200个TCP连接，服务器一共承受17.82万TCP长连接，服务器内存只消耗800M，CPU使用率极低（其中有一台客户担任破坏性测试机）
    - 根据测试结果初步推测：乐观点：t-io支持200万长连接没什么问题，保守点：100万吧，各位有条件的可以测测，毕竟推测的数据有时候会让人跌眼境。
4. 测试说明
    - 因为这17.82万长连接位于同一个组中，你用客户机发一条消息，服务器就要推送17.82万条数据，所以在测试发消息时，请慎重。
    - 这些数据是1.6.9版本测出来的，1.7.0加了链路行为跟踪功能、1.7.1会加上ip监控功能，这些功能的增加对tcp长连接个数没什么影响，但是可能内存会增加一些，毕竟多了不少维护数据。

### 针对tio-httpserver的ab test
1. 机器准备
    - 服务器一台：windows7 ssd i7-4790
2. 测试步骤及结果
    - 运行org.tio.http.server.demo1.HttpServerDemo1Starter.java
    - 测试脚本及测试结果见图，大家应该都看得懂，也欢迎大家下载更换参数进行测试
![image](http://gitee.com/tywo45/t-io/raw/master/docs/performance/ab-test-1.png)


## t-io学习步骤（供参考，具体步骤根据各人而异）
学习t-io的最好方式，是从helloworld的例子入手，顺瓜摸藤阅读t-io的源代码，已经有很多人阅读过t-io的源代码，譬如j-net的作者、[hutool](https://gitee.com/loolly/hutool/)的作者、[天蓬小猪](https://my.oschina.net/u/257950/)、[守护天使](https://gitee.com/yyljlyy)，并且反馈良好，源代码毕竟只有3000多行，读读无妨！如果懒于阅读代码，就按照下面的步骤来学习吧！

### 初步认识t-io
- 安装jdk1.8及maven3.5
- 从[https://gitee.com/tywo45/t-io](https://gitee.com/tywo45/t-io)处下载源代码（已下载的略过此步骤）
- 双击 "bin/start-im-server.bat" 启动im server
- 双击 "bin/start-im-client.bat" 启动im client
- 对着界面把玩几下，测试一把性能数据，对t-io形成感性认识（注意：好的性能数据需要预热几把，让线程池活起来）


### 了解代码目录结构
所有工程都是maven工程，后续目录有可能稍有变动，不定期更新

```
├─bin----------------脚本目录（方便快速操作）
│      clean.bat----------------清空所有工程的target目录
│      clean.sh
│      deploy.bat----------------作者用来发布到maven中心仓库的脚本，放出来主要是供大家参考
│      deploy.sh
│      dist-examples.bat----------------把所有的例子打包到dist目录，方便用户直接执行
│      dist-examples.sh
│      install.bat----------------安装工程到本地仓库
│      install.sh
|      start-http-server.bat----------------启动tio-httpserver
│      start-helloworld-client.bat----------------启动helloworld的客户端
│      start-helloworld-client.sh
│      start-helloworld-server.bat----------------启动helloworld的服务端
│      start-helloworld-server.sh
│      start-im-client.bat----------------启动im的客户端
│      start-im-client.sh
│      start-im-server.bat----------------启动im的服务端
│      start-im-server.sh
│      start-showcase-client.bat----------------启动showcase的客户端
│      start-showcase-client.sh
│      start-showcase-server.bat----------------启动showcase的服务端
│      start-showcase-server.sh
├─docs
│  │  
│  ├─blog----------------本人博客草稿（大部分博客是在线编辑，所以此处就没有了）
│  │      
│  ├─performance----------------一些性能测试截图（随着版本的增多，有些截图已经过时，但仍保留）
│  │
│  ├─release----------------新版本发布时的log
│  
├─dist----------------成品
│  └─examples----------------用t-io写的例子成品
│      ├─helloworld
│      │  ├─client----------------helloworld的客户端
│      │  └─server----------------helloworld的服务端
│      ├─im
│      │  ├─client----------------im的客户端
│      │  └─server----------------im的服务端
│      └─showcase
│          ├─client----------------showcase的客户端
│          └─server----------------showcase的服务端
└─src
    ├─core----------------t-io的核心代码
    ├─zoo----------------t-io的生态圈
    │  ├─http----------------用t-io实现的http服务器
    │  ├─websocket----------------用t-io实现的websocket服务器
    ├─example----------------用t-io写的例子的源代码
    │  ├─parent----------------例子的maven parent
    │  ├─helloworld----------------helloworld的源代码
    │  │  ├─client
    │  │  ├─common
    │  │  └─server
    │  └─showcase----------------showcase的源代码，这个例子是为了帮助用户学习t-io专门写的
    │      ├─client
    │      ├─common
    │      └─server
    └─parent----------------maven工程的parent
```


### 导入t-io官方提供的例子

去[t-io码云托管地址](https://gitee.com/tywo45/t-io)下载源代码及例子，里面的showcase例子是专门为学习t-io而写的，其设计也是准生产级别的，**可以直接拿来做您项目的手脚架**。下载完成后，请按下面步骤导入到eclipse中

[![image](https://gitee.com/tywo45/t-io/raw/master/docs/blog/t-io%E7%B3%BB%E5%88%97%E6%96%87%E6%A1%A3%E4%B9%8Bhelloworld%EF%BC%881%EF%BC%89/import-1.png)](https://gitee.com/tywo45/t-io/raw/master/docs/blog/t-io%E7%B3%BB%E5%88%97%E6%96%87%E6%A1%A3%E4%B9%8Bhelloworld%EF%BC%881%EF%BC%89/import-1.png)

[![image](https://gitee.com/tywo45/t-io/raw/master/docs/blog/t-io%E7%B3%BB%E5%88%97%E6%96%87%E6%A1%A3%E4%B9%8Bhelloworld%EF%BC%881%EF%BC%89/import-2.png)](https://gitee.com/tywo45/t-io/raw/master/docs/blog/t-io%E7%B3%BB%E5%88%97%E6%96%87%E6%A1%A3%E4%B9%8Bhelloworld%EF%BC%881%EF%BC%89/import-2.png)

[![image](https://gitee.com/tywo45/t-io/raw/master/docs/blog/t-io%E7%B3%BB%E5%88%97%E6%96%87%E6%A1%A3%E4%B9%8Bhelloworld%EF%BC%881%EF%BC%89/import-3.png)](https://gitee.com/tywo45/t-io/raw/master/docs/blog/t-io%E7%B3%BB%E5%88%97%E6%96%87%E6%A1%A3%E4%B9%8Bhelloworld%EF%BC%881%EF%BC%89/import-3.png)


### 学习万能的helloworld例子
花30分钟看一下t-io官方提供的helloworld，了解一下TCP编程的大概流程，文档传送门: [t-io的hello world](https://my.oschina.net/talenttan/blog/884806)

### 学习用于进阶的showcase例子
showcase一词是从springside借来的，放这很应景，[天蓬元帅](https://gitee.com/kobe577590/im)就是这样学习的，可以和他交流。

## 列一下作者本人用过的国产开源软件（统计时间：2017-10-04）
网上很多人对国产开源的印象还停留在很多年前，此处列一下作者本人一直在用的部分国产开源软件（用开源软件，回馈开源社区）

1. [https://www.oschina.net/p/weixin-java-tools-new](https://www.oschina.net/p/weixin-java-tools-new) （使用一年）
2. [http://layim.layui.com](http://layim.layui.com)（2017年5月11号开始使用）
3. [https://www.oschina.net/p/echarts](https://www.oschina.net/p/echarts) （使用两年以上吧）
4. [http://gitee.com/tywo45/talent-validate](http://gitee.com/tywo45/talent-validate) （使用十年了，开源出来有五年以上吧，原来是博客开源，现在移到开源中国了）
5. [https://www.oschina.net/p/hutool](https://www.oschina.net/p/hutool)（懒人必备，使用8个月左右）
6. [https://www.oschina.net/p/t-io](https://www.oschina.net/p/t-io)（使用五年了，开源出来半年）
7. [https://www.oschina.net/p/druid](https://www.oschina.net/p/druid)（使用三年以上吧）
8. [https://www.oschina.net/p/dubbo](https://www.oschina.net/p/dubbo)（使用两年）
9. [https://gitee.com/jfinal/jfinal-weixin](https://gitee.com/jfinal/jfinal-weixin/)（使用一年左右）
10. [https://www.oschina.net/p/fastjson](https://www.oschina.net/p/fastjson)（使用三年以上）
11. [https://www.oschina.net/p/ztree](https://www.oschina.net/p/ztree) （使用五年以上吧）
12. [https://www.oschina.net/p/jfinal](https://www.oschina.net/p/jfinal) （使用半年左右）
## t-io公众号

[![image](https://gitee.com/tywo45/t-io/raw/master/docs/wechat/1.jpg)](https://gitee.com/tywo45/t-io/raw/master/docs/wechat/1.jpg)

## t-io荣誉及口碑
1. 真实用户的口碑反馈:
[https://www.oschina.net/p/t-io/comments](https://www.oschina.net/p/t-io/comments)

2. 码云最有价值开源项目

[![image](https://gitee.com/tywo45/t-io/raw/master/docs/honor/gvp.png)](https://gitee.com/tywo45/t-io/raw/master/docs/honor/gvp.png)





  [1]: http://t-io.org
  [2]: https://my.oschina.net/talenttan/blog/884806
  [5]: https://my.oschina.net/talenttan/blog/863545
