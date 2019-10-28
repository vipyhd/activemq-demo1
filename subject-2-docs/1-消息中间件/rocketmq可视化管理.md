[TOC]
> rocketmq默认不带可视化控制台，需要去单独编译一个工具 https://github.com/apache/rocketmq-externals

### 1. git clone源码 ```git clone https://github.com/apache/rocketmq-externals```
### 2. 切换版本 ``` git checkout rocketmq-console-1.0.0 ```
### 3. 编译为jar ``` mvn clean package -Dmaven.test.skip=true ```
### 4. 启动
```
# jar包在target目录下面，你可以放在一台服务器上面运行
java -jar rocketmq-console-ng-1.0.0.jar --server.port=8081--rocketmq.config.namesrvAddr=192.168.100.242:9876
# --server.port springboot内置tomcat的端口号，默认8080；
# --rocketmq.config.namesrvAddr  nameserver的地址
```