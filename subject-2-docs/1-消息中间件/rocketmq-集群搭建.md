[TOC]
> 测试伪集群

# 环境准备
> 两个nameserver服务器，两个或以上broker服务器

# 配置文件（收集自 MessageStoreConfig.java、BrokerConfig.java）
```
#所属集群名字
brokerClusterName=DefaultCluster
#broker名字，注意此处不同的配置文件填写的不一样
brokerName=broker-a|broker-b
#0表示Master，>0表示Slave
brokerId=0
#nameServer地址，分号分割
namesrvAddr=10.10.1.31:9876;10.10.1.32:9876
#在发送消息时，自动创建服务器不存在的topic，默认创建的队列数
defaultTopicQueueNums=4
#是否允许 Broker 自动创建Topic，建议线下开启，线上关闭
autoCreateTopicEnable=true
#是否允许 Broker 自动创建订阅组，建议线下开启，线上关闭
autoCreateSubscriptionGroup=true
#Broker 对外服务的监听端口
listenPort=10911
#删除文件时间点，默认凌晨 4点
deleteWhen=04
#文件保留时间，默认 48 小时
fileReservedTime=120
#commitLog每个文件的大小默认1G
mapedFileSizeCommitLog=1073741824
#ConsumeQueue每个文件默认存30W条，根据业务情况调整
mapedFileSizeConsumeQueue=300000
#destroyMapedFileIntervalForcibly=120000
#redeleteHangedFileInterval=120000
#检测物理文件磁盘空间
diskMaxUsedSpaceRatio=88
#存储路径
storePathRootDir=/usr/local/alibaba-rocketmq/store
#commitLog 存储路径
storePathCommitLog=/usr/local/alibaba-rocketmq/store/commitlog
#消费队列存储路径存储路径
storePathConsumeQueue=/usr/local/alibaba-rocketmq/store/consumequeue
#消息索引存储路径
storePathIndex=/usr/local/alibaba-rocketmq/store/index
#checkpoint 文件存储路径
storeCheckpoint=/usr/local/alibaba-rocketmq/store/checkpoint
#abort 文件存储路径
abortFile=/usr/local/alibaba-rocketmq/store/abort
#限制的消息大小
maxMessageSize=65536
#flushCommitLogLeastPages=4
#flushConsumeQueueLeastPages=2
#flushCommitLogThoroughInterval=10000
#flushConsumeQueueThoroughInterval=60000
#Broker 的角色
#- ASYNC_MASTER 异步复制Master
#- SYNC_MASTER 同步双写Master
#- SLAVE
brokerRole=ASYNC_MASTER
#abort 文件存储路径
abortFile=/usr/local/alibaba-rocketmq/store/abort
#限制的消息大小
maxMessageSize=65536
#flushCommitLogLeastPages=4
#flushConsumeQueueLeastPages=2
#flushCommitLogThoroughInterval=10000
#flushConsumeQueueThoroughInterval=60000
#Broker 的角色
#- ASYNC_MASTER 异步复制Master
#- SYNC_MASTER 同步双写Master
#- SLAVE
brokerRole=ASYNC_MASTER

# 定时任务级别
messageDelayLevel=1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
# 事务检查时间，默认一分钟
transactionCheckInterval =60000
```