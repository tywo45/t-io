### 本项目在码云的地址（主战场）：[https://gitee.com/tywo45/t-io](https://gitee.com/tywo45/t-io "https://gitee.com/tywo45/t-io")


### 一张图了解t-io的历史和能力
![](https://res.t-io.org/doc/t-io-base_01.png?434)

### 一张图了解t-io及官方衍生品
![](https://res.t-io.org/doc/t-io-base_02.png?434)

### 修改历史
#### 3.2.4.v20181218-RELEASE
- 新需求：https://gitee.com/tywo45/t-io/issues/IOZQB
- 增加org.tio.utils.lock.ReadLockHandler<T>
- 增加org.tio.utils.lock.WriteLockHandler<T>
- 优化http解码过程中的字符编码(对于ws原来是固定utf-8的，现在改成可配) 
- 拉黑相关的从GroupContext移到ServerGroupContext
- **ws握手包改名，原名：handshakeRequestPacket，现名：handshakeRequest**
- 合并PR

### 最新pom
```xml
<dependency>
    <groupId>org.t-io</groupId>
    <artifactId>tio-core</artifactId>
    <version>3.2.4.v20181218-RELEASE</version>
</dependency>
```

### 例行说明
- t-io是OSC官方人员也在使用的通讯框架，譬如这个网站的动弹：http://lifes77.com
- 欢迎来https://www.t-io.org/guide, 用t-io写的t-io官网（注：官网用的是tio-core、tio-http、tio-websocket、tio-webpack，并未使用类似tomcat这样的容器）

[![](https://res.t-io.org/blog/upload/img/50/8931/1119484/88097537/74541310905/89/095501/1_sm.png)](https://www.t-io.org/guide/index.html)


