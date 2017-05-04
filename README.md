### 这个项目的最新代码在：https://git.oschina.net/tywo45/t-io

### 
  t-io: 百万级TCP长连接即时通讯框架，让天下没有难开发的即时通讯。

1.  ### 简 介

             **t-io**是基于jdk aio实现的易学易用、稳定耐操、性能强悍、内置功能丰富、核心代码只有3000多行的即时通讯框架。字母** t **取**talent**（天才）的首字母，也可以理解为**"特快"**，同时也是**作者姓氏**的首字母。
2.  ### **最新maven坐标**

    <pre>
`&lt;dependency&gt;
    &lt;groupId&gt;org.t-io&lt;/groupId&gt;
    &lt;artifactId&gt;tio-core&lt;/artifactId&gt;
    &lt;version&gt;1.7.0.v20170501-RELEASE&lt;/version&gt;
&lt;/dependency&gt;`</pre>
3.  ### 各种传送门

        *   [官 网](http://www.t-io.org:9292/)
    *   [30分钟极速入门](http://www.t-io.org:9292/quickstart.html)
    *   [API](http://www.t-io.org:9292/apidocs/index.html)（先看[Aio.java](http://www.t-io.org:9292/apidocs/org/tio/core/Aio.html)的就好，其它的看[30分钟极速入门](http://www.t-io.org:9292/quickstart.html)）
    *   [资料及问题汇总](https://my.oschina.net/talenttan/blog/863545)
    *   [作者博客](https://my.oschina.net/talenttan/blog)
4.  ### 常见应用场景

            IM、实时监控、推送服务（已内置API）、RPC、游戏、物联网等实时通讯类型的场景
5.  ### 特 点

        *   **极简洁清晰易懂的API**: 没有生涩难懂的新概念，只需**花上30分钟**[学习helloworld](http://www.t-io.org:9292/quickstart.html)就能很好地掌握并实现一个性能极好的即时通讯应用
    *   **极震撼的性能**

                *   轻松支持**百万级**tcp长连接，彻底甩开业界**C1000K**烦恼（centos 单CPU4核 16G 测试数据: 17.82万长连接，只消耗800M内存，CPU毫无压力）
        *   最高时，每秒可以收发**500万**条消息，约**165M**（windows10、i7、8g、固态硬盘、群聊场景、服务器和客户机在同一台机器）
    *   **极亲民的内置功能**

                *   框架层面帮你**检测心跳**(tcp server)、**发送心跳**(tcp client)
        *   框架层面支持**自动重连**(可设置重连间隔时间和重连次数)
        *   框架层面支持**同步消息**(消息发送后，等到响应消息再往下执行)
        *   框架层面支持**绑定userid**(用于用户关联)、**绑定groupid**(用于群聊)
        *   内置各项统计功能----接受过多少连接、关闭过多少连接、已发送的消息数、已接收的消息数、当前是多少正常连接、当前多少断开的连接等。
6.  ### 性能数据

        1.  #### IM实例收发速度**500万条/秒**----此数据系网友提供（i7 6700 + 固态硬盘 + win10），我本地只能跑到333万/秒

        2.  #### IM实例**17.82万TCP长连接且正常收发消息只消耗800M内存，CPU使用率极低**，目测t-io可以支撑**200万长连接**

        3.  #### 17万长连接反复破坏性测试（譬如断网又连网、反复断开客户端又连上客户端等），服务器内存保持稳定（600多M到900M间）
7.  ### 性能测试步骤

        1.  #### 测试单机吞吐量

            1.  #### 机器准备

                    1.  CPU: i7 6700
            2.  内存：8G/4G
            3.  操作系统：windows7/windows10
            4.  说明：客户机和服务器位于同一台机器
        2.  #### 测试步骤

                    1.  双击 "bin/start-im-server.bat" 启动im server
            2.  双击 "bin/start-im-client.bat" 启动im client
            3.  保持下图参数进行测试
                            ![](http://git.oschina.net/tywo45/t-io/raw/master/docs/performance/500%E4%B8%87.png)
        3.  #### 测试结果

                    1.  500万条/秒约165M----此数据系网友提供（i7 6700 + 固态硬盘 + win10）
            2.  333万条/秒约97M----此数据系本人亲测数据（i7 4790 + 固态硬盘 + win7），测试参数与上图略有差别，不一一说明
    2.  #### 测试centos下可以支持多少长连接数

            1.  #### 机器准备

                    1.  服务器一台：centos6.x,  虚拟机，一个4核E5 CPU，内存16G
            2.  客户机11台：windows，硬件没什么特别要求
        2.  #### 测试步骤

                    1.  修改centos操作系统参数，使之支持更大的长连接数，细节略（可百度之）
            2.  在centos上运行 "bin/start-im-server.sh" 启动im server
            3.  修改dist\examples\im\client\config\app.conf，参考下面的值，注意把server指向centos的ip，
                            <pre>
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
                            </pre>
            4.  把dist\examples\im\client拷到各客户机并运行"bin/start-im-client.bat"
        3.  #### 测试结果

                    1.  11个客户机 ，每个客户机连16200个TCP连接，服务器一共承受17.82万TCP长连接，服务器内存只消耗800M，CPU使用率极低
            2.  根据测试结果初步推测：t-io支持200万长连接没什么问题，各位有条件的可以测测。
8.  ### t-io学习步骤（供参考，具体步骤根据各人而异）

            学习t-io的最好方式，是从helloworld的例子入手，顺藤摸瓜阅读t-io的源代码，已经有很多人阅读过t-io的源代码，譬如j-net的作者、hutool的作者，并且反馈良好，源代码毕竟只有4000多行，读读无妨！如果懒于阅读代码，就按照下面的步骤来学习吧！

        1.  #### 初步认识t-io

            1.  安装1.7以上版本的jdk及maven（已安装的略过此步骤）
        2.  从[https://git.oschina.net/tywo45/t-io](https://git.oschina.net/tywo45/t-io)处下载源代码（已下载的略过此步骤）
        3.  双击 "bin/install.bat" 安装t-io到本地maven仓库（此步骤可省略）
        4.  双击 "bin/start-im-server.bat" 启动im server
        5.  双击 "bin/start-im-client.bat" 启动im client
        6.  对着界面把玩几下，测试一把性能数据，对t-io形成感性认识

                                    客户端界面
                        ![](http://git.oschina.net/tywo45/t-io/raw/master/docs/performance/500%E4%B8%87.png)

                                    服务器端界面（这里显示的都是一些统计信息，方便用户了解服务器运作情况）
                        ![](http://git.oschina.net/tywo45/t-io/raw/master/docs/im/server.png)
    2.  #### 了解代码目录结构（所有工程都是maven工程）

    ###<pre>
├─bin<font color='#06AD3'>----------------脚本目录（方便快速操作）</font>
│      clean.bat<font color='#06AD3'>----------------清空所有工程的target目录</font>
│      clean.sh<font color='#06AD3'></font>
│      deploy.bat<font color='#06AD3'>----------------作者用来发布到maven中心仓库的脚本，放出来主要是供大家参考</font>
│      deploy.sh<font color='#06AD3'></font>
│      dist-examples.bat<font color='#06AD3'>----------------把所有的例子打包到dist目录，方便用户直接执行</font>
│      dist-examples.sh<font color='#06AD3'></font>
│      install.bat<font color='#06AD3'>----------------安装工程到本地仓库</font>
│      install.sh<font color='#06AD3'></font>
│      start-helloworld-client.bat<font color='#06AD3'>----------------启动helloworld的客户端</font>
│      start-helloworld-client.sh<font color='#06AD3'></font>
│      start-helloworld-server.bat<font color='#06AD3'>----------------启动helloworld的服务端</font>
│      start-helloworld-server.sh<font color='#06AD3'></font>
│      start-im-client.bat<font color='#06AD3'>----------------启动im的客户端</font>
│      start-im-client.sh<font color='#06AD3'></font>
│      start-im-server.bat<font color='#06AD3'>----------------启动im的服务端</font>
│      start-im-server.sh<font color='#06AD3'></font>
│      start-im-simple-client.bat<font color='#06AD3'>----------------启动简化版协议的im的客户端</font>
│      start-im-simple-client.sh<font color='#06AD3'></font>
│      start-im-simple-server.bat<font color='#06AD3'>----------------启动简化版协议的im的服务端</font>
│      start-im-simple-server.sh<font color='#06AD3'></font>
│      start-showcase-client.bat<font color='#06AD3'>----------------启动showcase的客户端</font>
│      start-showcase-client.sh<font color='#06AD3'></font>
│      start-showcase-server.bat<font color='#06AD3'>----------------启动showcase的服务端</font>
│      start-showcase-server.sh<font color='#06AD3'></font>
├─docs
│  ││  ├─blog<font color='#06AD3'>----------------本人博客草稿（大部分博客是在线编辑，所以此处就没有了）</font>
│  ││  ├─performance<font color='#06AD3'>----------------一些性能测试截图（随着版本的增多，有些截图已经过时，但仍保留）</font>
│  │
│  ├─release<font color='#06AD3'>----------------新版本发布时的log</font>
│├─dist<font color='#06AD3'>----------------成品</font>
│  └─examples<font color='#06AD3'>----------------用t-io写的例子成品</font>
│      ├─helloworld
│      │  ├─client<font color='#06AD3'>----------------helloworld的客户端</font>
│      │  └─server<font color='#06AD3'>----------------helloworld的服务端</font>
│      ├─im
│      │  ├─client<font color='#06AD3'>----------------im的客户端</font>
│      │  └─server<font color='#06AD3'>----------------im的服务端</font>
│      │─im-simple
│      │  ├─client<font color='#06AD3'>----------------简化版协议的im的客户端</font>
│      │  └─server<font color='#06AD3'>----------------简化版协议的im的服务端</font>
│      └─showcase
│          ├─client<font color='#06AD3'>----------------showcase的客户端</font>
│          └─server<font color='#06AD3'>----------------showcase的服务端</font>
└─src
    ├─core<font color='#06AD3'>----------------t-io的核心代码</font>
    ├─example<font color='#06AD3'>----------------用t-io写的例子的源代码</font>
    │  ├─parent<font color='#06AD3'>----------------例子的maven parent</font>
    │  ├─helloworld<font color='#06AD3'>----------------helloworld的源代码</font>
    │  │  ├─client
    │  │  ├─common
    │  │  └─server
    │  ├─im<font color='#06AD3'>----------------im的源代码</font>
    │  │  ├─client
    │  │  ├─common
    │  │  └─server
    │  ├─im-simple<font color='#06AD3'>----------------简化版协议的im的源代码</font>
    │  │  ├─client
    │  │  ├─common
    │  │  └─server
    │  └─showcase<font color='#06AD3'>----------------showcase的源代码，这个例子是为了帮助用户学习t-io专门写的</font>
    │      ├─client
    │      ├─common
    │      └─server
    └─parent<font color='#06AD3'>----------------maven工程的parent</font>
</pre>

        3.  #### 了解t-io源代码及用于学习的例子

                        去[https://git.oschina.net/tywo45/t-io](https://git.oschina.net/tywo45/t-io)下载源代码及例子，里面的showcase例子是专门为学习t-io而写的，其设计也是准生产级别的，可以直接拿来做您项目的手脚架。下载完成后，请按下面步骤导入到eclipse中
                <div>
                    [![](https://git.oschina.net/tywo45/t-io/raw/master/docs/blog/t-io%E7%B3%BB%E5%88%97%E6%96%87%E6%A1%A3%E4%B9%8Bhelloworld%EF%BC%881%EF%BC%89/import-1.png)](https://git.oschina.net/tywo45/t-io/raw/master/docs/blog/t-io%E7%B3%BB%E5%88%97%E6%96%87%E6%A1%A3%E4%B9%8Bhelloworld%EF%BC%881%EF%BC%89/import-1.png)
                </div>
                <div>
                    [![](https://git.oschina.net/tywo45/t-io/raw/master/docs/blog/t-io%E7%B3%BB%E5%88%97%E6%96%87%E6%A1%A3%E4%B9%8Bhelloworld%EF%BC%881%EF%BC%89/import-2.png)](https://git.oschina.net/tywo45/t-io/raw/master/docs/blog/t-io%E7%B3%BB%E5%88%97%E6%96%87%E6%A1%A3%E4%B9%8Bhelloworld%EF%BC%881%EF%BC%89/import-2.png)
                </div>
                <div>
                    [![](https://git.oschina.net/tywo45/t-io/raw/master/docs/blog/t-io%E7%B3%BB%E5%88%97%E6%96%87%E6%A1%A3%E4%B9%8Bhelloworld%EF%BC%881%EF%BC%89/import-3.png)](https://git.oschina.net/tywo45/t-io/raw/master/docs/blog/t-io%E7%B3%BB%E5%88%97%E6%96%87%E6%A1%A3%E4%B9%8Bhelloworld%EF%BC%881%EF%BC%89/import-3.png)
                </div>
    4.  #### 花30分钟看一下t-io的helloworld，了解一下TCP编程的大概流程

                        传送门: [t-io系列文档之hello world（1）](https://my.oschina.net/talenttan/blog/884806)
    5.  #### 花半天时间学习showcase

                        下载例子代码学习即可，很容易懂，[天蓬元帅](https://git.oschina.net/kobe577590/im)就是这样学习的，可以和他交流，他后面会出详细的教程。
9.  ### 案 例（案例太多，此处仅列举t-io开源第一个月内的案例）

        *   某网管系统(管理数百台刀片服务器的系统)
    *   某直播平台(视频直播+聊天)
    *   某智能设备检测系统(数据采集)<!--小白-->
    *   某物联网系统(服务端)<!--好像是jackkang-->
    *   深圳市某在线技术发展有限公司(中银联投资)：某网络安全运营支撑平台<!--小宇-->
    *   [redisx](https://git.oschina.net/websterlu/redisx)<!--小宇-->
    *   [talent_dubbo](https://git.oschina.net/kangjie1209/talent_dubbo)<!--jackkang-->
    *   某移动省公司CRM业务受理消息采集平台(数据采集)<!--福州-精灵-java-->
10.  ### 参与t-io

        1.  t-io是将多线程技巧运用到极致的框架，所以一旦您参与到本项目，将会从本项目中学到很多关于多线程的技巧。
    2.  [
            提交Issue
            ](/tywo45/t-io/issues/new?issue%5Bassignee_id%5D=&amp;issue%5Bmilestone_id%5D= "提交issue")
            给项目提出有意义的新需求，或是帮项目发现BUG，或是上传你本地测试的一些数据让作者参考以便进一步优化。
    3.  点击右上方的
            <span class="basic buttons mini star-container ui">
            [Star](javascritp:void(0);)
            </span>
            以便随时掌握本项目的动态
11.  ### 捐赠t-io

    由于各种原因，当然根本原因是作者自身的问题，t-io曾经有三天是要打算闭源的，并且在此期间关闭了所有捐赠渠道包括码云官方的捐赠渠道（你现在点下面的捐赠会提示你 “该项目还没开启捐赠功能，快去开启吧！”），作者也无意再次打扰码云的小伙伴们。

    昨天发了1.7.0版本后，有几个朋友问到了如何捐赠，那么就在此处也放一个巨幅捐赠传送门吧

    [

    # t-io捐赠传送门
](http://www.t-io.org:9292/donate.html?v=1)

    捐赠不是必须，求勿喷或轻喷！

    捐赠不是必须，求勿喷或轻喷！

    捐赠不是必须，求勿喷或轻喷！

    不是重复了，而是重要的事说3遍！