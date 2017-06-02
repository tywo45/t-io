
## **t-io: 百万级TCP长连接即时通讯框架，让天下没有难开发的即时通讯**

- t-io是基于jdk aio实现的易学易用、稳定、性能强悍、**将多线程运用到极致**、内置功能丰富的即时通讯框架(广义上的即时通讯，并非指im)，字母 t 寓意talent。
- 同类型的框架还有[voovan](https://www.oschina.net/p/voovan)、[netty](https://www.oschina.net/p/netty)、[mina](https://www.oschina.net/p/mina)、[baseio](https://git.oschina.net/generallycloud/baseio)等，不喜欢t-io的可以去尝试了解这几个，t-io对所有人按LGPL协议开源，但只服务于品行良好的开发人员！
- t-io在协议生态全面建立起来前，更多的是适合私有协议TCP连接项目，所以如果你想实现一个复杂的公有协议的产品，可以用协议生态更为完整的其它类似框架。

####  **常见应用场景**
    
- IM（官方提供了im例子，含web端）
- 实时监控
- 推送服务（已内置API）
- RPC
- 游戏
- 物联网（已有很多案例）
- 其它实时通讯类型的场景，不一一列举

####  **小晒一下作者花两天时间用t-io和[layim](http://layim.layui.com/)做的[web im](http://www.t-io.org:9292/newim/)**
  
- 先感谢一下贤心提供这么好的ui作品，也欢迎大家去捐赠获取[layim](http://layim.layui.com/)。  
- 东西刚刚出来，还需要打磨，有问题在所难免，毕竟只花了两天时间。
- [演示地址](http://www.t-io.org:9292/newim/)（2M带宽，请勿压测，谢谢！）
- 截图

---

![image](https://git.oschina.net/tywo45/t-io/raw/master/docs/tchat/1.png?v=89)

---

![image](https://git.oschina.net/tywo45/t-io/raw/master/docs/tchat/2.png?v=89)

---


## maven坐标
```
<dependency>
    <groupId>org.t-io</groupId>
    <artifactId>tio-core</artifactId>
    <version>1.7.0.v20170501-RELEASE</version>
</dependency>
```
## **t-io特点**
###  极简洁、清晰、易懂的API
- 没有生涩难懂的新概念，原生态bytebuffer既减少学习成本，又减少各种中间对象的创建
- 只需花上30分钟学习helloworld，就能较好地掌握并实现一个性能极好的即时通讯应用

###  极震撼的性能
- 单机轻松支持**百万级tcp长连接**，彻底甩开业界C1000K烦恼；
- 最高时，每秒可以收发500万条业务消息，约165M（**1.6.9版本数据，想验证的，后面有验证步骤，1.7.1版本的im由于加入了许多业务功能，所以不能用于测试框架性能**）

###  对开发人员极体贴的内置功能
- **内置心跳检测**
- **内置心跳发送**
- **各种便捷的绑定API**
- **各种便捷的发送API**
- **一行代码拥有自动重连功能**
- **各项消息统计等功能，全部一键内置搞定，省却各种烦恼**

###  鸟瞰t-io

---

[![image](https://git.oschina.net/tywo45/t-io/raw/master/docs/api/t-io-api.png)](https://git.oschina.net/tywo45/t-io/raw/master/docs/api/t-io-api.png)

---


## **各种传送门**

 - [t-io生态圈建设](http://www.t-io.org:9292/ecosphere.html?t_io_v=34344545676)
 - [t-io + layim + vue + ivivew + webpack + google-protobuf开发的web im](http://www.t-io.org:9292/newim?t_io_v=34344545676)（服务器由某公司免费提供，只有2M带宽，最近被Ddos攻击，所以随时都可能暂停而导致你访问失败）
 - [官 网][1]
 - [代码托管平台码云](https://git.oschina.net/tywo45/t-io)
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
    - [jfinal + t-io完成的im项目](https://git.oschina.net/kobe577590/im)----作者天蓬小猪正在完善
 - [API][3](只需要看[Aio.java][4])
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
    ![image](http://git.oschina.net/tywo45/t-io/raw/master/docs/performance/500%E4%B8%87.png)
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
        #服务器
        server=127.0.0.1
        
        #服务器port
        port=9321
        
        #连接多少个连接到服务器
        client.count=16200
        
        #进入到哪个组
        group=g
        
        #聊天消息发的内容
        chat.content=he
        
        #一次发多少条(这个数字不要太大)
        send.count=1
    ```
    - 把dist\examples\im\client拷到各客户机并运行"bin/start-im-client.bat"
3. 测试结果
    - 11个客户机 ，每个客户机连16200个TCP连接，服务器一共承受17.82万TCP长连接，服务器内存只消耗800M，CPU使用率极低（其中有一台客户担任破坏性测试机）
    - 根据测试结果初步推测：乐观点：t-io支持200万长连接没什么问题，保守点：100万吧，各位有条件的可以测测，毕竟推测的数据有时候会让人跌眼境。
4. 测试说明
    - 因为这17.82万长连接位于同一个组中，你用客户机发一条消息，服务器就要推送17.82万条数据，所以在发消息时，请慎重，当然如果你非要把内存、CPU耗光，然后看t-io宕机才高兴，只能说t-io不是你的菜，你还是另请高明吧。
    - 这些数据是1.6.9版本测出来的，1.7.0加了链路行为跟踪功能、1.7.1会加上ip防黑功能，这些功能的增加对tcp长连接个数没什么影响，但是可能内存会增加一些，毕竟多了不少维护数据。
    - 不管怎么说，用在生产环境，不管用什么框架，都是要自己测一遍的，有问题可以随时找作者，不接受恶意找茬，同时作为开源免费软件，作者也有权拒绝一切服务，这点请知悉----开源，只是把代码本身按照某种协议贡献出来，并无义务给你服务，你用得不爽，请另请高明。
    - netty是一个知名度极高的一个框架，而且功能更多，t-io如果满足不了你或不是你的菜，可以尝试netty

## t-io学习步骤（供参考，具体步骤根据各人而异）
学习t-io的最好方式，是从helloworld的例子入手，顺瓜摸藤阅读t-io的源代码，已经有很多人阅读过t-io的源代码，譬如j-net的作者、[hutool](https://git.oschina.net/loolly/hutool/)的作者、[天蓬小猪](https://my.oschina.net/u/257950/)、[守护天使](https://git.oschina.net/yyljlyy)，并且反馈良好，源代码毕竟只有3000多行，读读无妨！如果懒于阅读代码，就按照下面的步骤来学习吧！

### 初步认识t-io
- 安装1.7以上版本的jdk及maven（已安装的略过此步骤）
- 从[https://git.oschina.net/tywo45/t-io](https://git.oschina.net/tywo45/t-io)处下载源代码（已下载的略过此步骤）
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
    ├─example----------------用t-io写的例子的源代码
    │  ├─parent----------------例子的maven parent
    │  ├─helloworld----------------helloworld的源代码
    │  │  ├─client
    │  │  ├─common
    │  │  └─server
    │  ├─im----------------im的源代码
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
去[t-io码云托管地址](https://git.oschina.net/tywo45/t-io)下载源代码及例子，里面的showcase例子是专门为学习t-io而写的，其设计也是准生产级别的，**可以直接拿来做您项目的手脚架**。下载完成后，请按下面步骤导入到eclipse中
[![image](https://git.oschina.net/tywo45/t-io/raw/master/docs/blog/t-io%E7%B3%BB%E5%88%97%E6%96%87%E6%A1%A3%E4%B9%8Bhelloworld%EF%BC%881%EF%BC%89/import-1.png)](https://git.oschina.net/tywo45/t-io/raw/master/docs/blog/t-io%E7%B3%BB%E5%88%97%E6%96%87%E6%A1%A3%E4%B9%8Bhelloworld%EF%BC%881%EF%BC%89/import-1.png)
---
[![image](https://git.oschina.net/tywo45/t-io/raw/master/docs/blog/t-io%E7%B3%BB%E5%88%97%E6%96%87%E6%A1%A3%E4%B9%8Bhelloworld%EF%BC%881%EF%BC%89/import-2.png)](https://git.oschina.net/tywo45/t-io/raw/master/docs/blog/t-io%E7%B3%BB%E5%88%97%E6%96%87%E6%A1%A3%E4%B9%8Bhelloworld%EF%BC%881%EF%BC%89/import-2.png)
---
[![image](https://git.oschina.net/tywo45/t-io/raw/master/docs/blog/t-io%E7%B3%BB%E5%88%97%E6%96%87%E6%A1%A3%E4%B9%8Bhelloworld%EF%BC%881%EF%BC%89/import-3.png)](https://git.oschina.net/tywo45/t-io/raw/master/docs/blog/t-io%E7%B3%BB%E5%88%97%E6%96%87%E6%A1%A3%E4%B9%8Bhelloworld%EF%BC%881%EF%BC%89/import-3.png)

### 学习万能的helloworld例子
花30分钟看一下t-io官方提供的helloworld，了解一下TCP编程的大概流程，文档传送门: [t-io的hello world](https://my.oschina.net/talenttan/blog/884806)

### 学习用于进阶的showcase例子
showcase一词是从springside借来的，放这很应景，[天蓬元帅](https://git.oschina.net/kobe577590/im)就是这样学习的，可以和他交流，他后面会出详细的教程。

## 列一下作者本人用过的国产开源软件
网上很多人对国产开源的印象还停留在n年前，此处列一下作者本人一直在用的部分国产开源软件，其中有的是有争议的，也有暴过漏洞的，但是我们想一下struts、netty、mongodb这些国外知名软件不也暴过严重漏洞吗？
1. [https://www.oschina.net/p/weixin-java-tools-new](https://www.oschina.net/p/weixin-java-tools-new) （使用一年）
2. [http://layim.layui.com](http://layim.layui.com)（2017年5月11号开始使用）
2. [https://www.oschina.net/p/ztree](https://www.oschina.net/p/ztree) （使用五年以上吧）
3. [https://www.oschina.net/p/echarts](https://www.oschina.net/p/echarts) （使用两年以上吧）
4. [http://git.oschina.net/tywo45/talent-validate](http://git.oschina.net/tywo45/talent-validate) （使用十年了，开源出来有五年以上吧，原来是博客开源，现在移到开源中国了）
5. [https://www.oschina.net/p/hutool](https://www.oschina.net/p/hutool)（懒 人必备，强烈推荐，使用两个月）
6. [https://www.oschina.net/p/t-io](https://www.oschina.net/p/t-io)（使用五年了，开源出来半年）
7. [https://www.oschina.net/p/druid](https://www.oschina.net/p/druid)（使用三年以上吧）
8. [https://www.oschina.net/p/dubbo](https://www.oschina.net/p/dubbo)（使用两年）
9. [https://git.oschina.net/jfinal/jfinal-weixin](https://git.oschina.net/jfinal/jfinal-weixin/)（使用一年左右）
10. [https://www.oschina.net/p/fastjson](https://www.oschina.net/p/fastjson)（使用三年以上）








  [1]: http://www.t-io.org:9292?t_io_v=34344545676
  [2]: https://my.oschina.net/talenttan/blog/884806
  [3]: http://www.t-io.org:9292/apidocs/org/tio/core/Aio.html?t_io_v=34344545676
  [4]: http://www.t-io.org:9292/apidocs/org/tio/core/Aio.html?t_io_v=34344545676
  [5]: https://my.oschina.net/talenttan/blog/863545
  [6]: https://my.oschina.net/talenttan/blog
