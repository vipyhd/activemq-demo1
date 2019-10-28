 [TOC]
# Redis下载安装
```
下载redis
https://redis.io/download
# 下载
wget http://download.redis.io/releases/redis-5.0.3.tar.gz
# Installation
tar xzf redis-5.0.3.tar.gz
cd redis-5.0.3
make
# 创建文件夹 
mkdir /usr/local/redis/conf
mkdir /usr/local/redis/data
mkdir /usr/local/redis/logs
# run
src/redis-server

# warning 1 > 提示修改 linux内核参数
# WARNING: The TCP backlog setting of 511 cannot be enforced because /proc/sys/net/core/somaxconn is set to the lower value of 128.
echo 1024 >/proc/sys/net/core/somaxconn

# warn 2 > 提示如下
# WARNING overcommit_memory is set to 0! Background save may fail under low memory condition. To fix this issue add 'vm.overcommit_memory = 1' to /etc/sysctl.conf and then reboot or run the command 'sysctl vm.overcommit_memory=1' for this to take effect.
echo "vm.overcommit_memory = 1" >> /etc/sysctl.conf
sysctl vm.overcommit_memory=1

# warning 3
# WARNING you have Transparent Huge Pages (THP) support enabled in your kernel. This will create latency and memory usage issues with Redis. To fix this issue run the command 'echo never > /sys/kernel/mm/transparent_hugepage/enabled' as root, and add it to your /etc/rc.local in order to retain the setting after a reboot. Redis must be restarted after THP is disabled
echo never > /sys/kernel/mm/transparent_hugepage/enabled


# 云服务器要注意ip要写对，端口要开放
# 虚拟机要注意防火墙要关闭 systemctl stop firewalld.service
```

# 准备配置文件
```
# 配置文件进行了精简，完整配置可自行和官方提供的完整conf文件进行对照。端口号自行对应修改
#后台启动的意思
daemonize yes 
 #端口号(如果同一台服务器上启动，注意要修改为不同的端口)
port 6380
# IP绑定，redis不建议对公网开放，直接绑定0.0.0.0没毛病
bind 0.0.0.0
# 这个文件会自动生成(如果同一台服务器上启动，注意要修改为不同的端口)
pidfile /var/run/redis_6380.pid 
```

# 准备三个redis服务
```
# 1、启动三个Redis
/usr/local/redis/bin/redis-server /usr/local/redis/conf/redis-6380.conf
/usr/local/redis/bin/redis-server /usr/local/redis/conf/redis-6381.conf
/usr/local/redis/bin/redis-server /usr/local/redis/conf/redis-6382.conf

# 2、配置为 1主2从
/usr/local/redis/bin/redis-cli -p 6381 slaveof 192.168.100.241 6380
/usr/local/redis/bin/redis-cli -p 6382 slaveof 192.168.100.241 6380

# 3、检查集群
/usr/local/redis/bin/redis-cli -p 6380 info Replication

```

# 准备哨兵配置文件
```
# 配置文件：sentinel.conf，在sentinel运行期间是会被动态修改的
# sentinel如果重启时，根据这个配置来恢复其之前所监控的redis集群的状态
# 绑定IP
bind 0.0.0.0
# 后台运行
daemonize yes
# 默认yes，没指定密码或者指定IP的情况下，外网无法访问
protected-mode no
# 哨兵的端口，客户端通过这个端口来发现redis
port 26380
# 哨兵自己的IP，手动设定也可自动发现，用于与其他哨兵通信
# sentinel announce-ip
# 临时文件夹
dir /tmp
# 日志
logfile "/usr/local/redis/logs/sentinel-26380.log"
# sentinel监控的master的名字叫做mymaster,初始地址为 192.168.100.241 6380,2代表两个及以上哨兵认定为死亡，才认为是真的死亡
sentinel monitor mymaster 192.168.100.241 6380 2
# 发送心跳PING来确认master是否存活
# 如果master在“一定时间范围”内不回应PONG 或者是回复了一个错误消息，那么这个sentinel会主观地(单方面地)认为这个master已经不可用了
sentinel down-after-milliseconds mymaster 1000
# 如果在该时间（ms）内未能完成failover操作，则认为该failover失败
sentinel failover-timeout mymaster 3000
# 指定了在执行故障转移时，最多可以有多少个从Redis实例在同步新的主实例，在从Redis实例较多的情况下这个数字越小，同步的时间越长，完成故障转移所需的时间就越长
sentinel parallel-syncs mymaster 1
```

# 启动哨兵集群
```
/usr/local/redis/bin/redis-server /usr/local/redis/conf/sentinel-26380.conf --sentinel
/usr/local/redis/bin/redis-server /usr/local/redis/conf/sentinel-26381.conf --sentinel
/usr/local/redis/bin/redis-server /usr/local/redis/conf/sentinel-26382.conf --sentinel
```

# 测试
```
# 停掉master，主从切换过程
启动哨兵(客户端通过哨兵发现Redis实例信息)
哨兵通过连接master发现主从集群内的所有实例信息
哨兵监控redis实例的健康状况
哨兵一旦发现master不能正常提供服务，则通知给其他哨兵
当一定数量的哨兵都认为master挂了
选举一个哨兵作为故障转移的执行者
执行者在slave中选取一个作为新的master
将其他slave重新设定为新master的从属
```

# 哨兵同步pubsub机制发出来的消息
```
# https://redis.io/topics/sentinel#pubsub-messages
+reset-master <instance details> -- 当master被重置时.
+slave <instance details> -- 当检测到一个slave并添加进slave列表时.
+failover-state-reconf-slaves <instance details> -- Failover状态变为reconf-slaves状态时
+failover-detected <instance details> -- 当failover发生时
+slave-reconf-sent <instance details> -- sentinel发送SLAVEOF命令把它重新配置时
+slave-reconf-inprog <instance details> -- slave被重新配置为另外一个master的slave，但数据复制还未发生时。
+slave-reconf-done <instance details> -- slave被重新配置为另外一个master的slave并且数据复制已经与master同步时。
-dup-sentinel <instance details> -- 删除指定master上的冗余sentinel时 (当一个sentinel重新启动时，可能会发生这个事件).
+sentinel <instance details> -- 当master增加了一个sentinel时。
+sdown <instance details> -- 进入SDOWN状态时;
-sdown <instance details> -- 离开SDOWN状态时。
+odown <instance details> -- 进入ODOWN状态时。
-odown <instance details> -- 离开ODOWN状态时。
+new-epoch <instance details> -- 当前配置版本被更新时。
+try-failover <instance details> -- 达到failover条件，正等待其他sentinel的选举。
+elected-leader <instance details> -- 被选举为去执行failover的时候。
+failover-state-select-slave <instance details> -- 开始要选择一个slave当选新master时。
+no-good-slave <instance details> -- 没有合适的slave来担当新master
+selected-slave <instance details> -- 找到了一个适合的slave来担当新master
+promoted-slave -- 确认成功
+failover-state-reconf-slaves -- 开始对slaves进行reconfig操作
+slave-reconf-sent -- 向指定的slave发送“slaveof”指令，告知此slave跟随新的master
+slave-reconf-inprog -- 此slave正在执行slaveof + SYNC过程，slave收到“+slave-reconf-sent”之后将会执行slaveof操作
+slave-reconf-done -- 此slave同步完成，此后leader可以继续下一个slave的reconfig操作
failover-state-send-slaveof-noone <instance details> -- 当把选择为新master的slave的身份进行切换的时候。
failover-end-for-timeout <instance details> -- failover由于超时而失败时。
failover-end <instance details> -- failover成功完成,故障转移结束
switch-master <master name> <oldip> <oldport> <newip> <newport> -- 当master的地址发生变化时。通常这是客户端最感兴趣的消息了。
+tilt -- 进入Tilt模式。
-tilt -- 退出Tilt模式。
```

# 哨兵日志分析
```
sdow (sdown subjectively down)
odown (objectively down)
# 通过日志逐步分析
996:X 23 Nov 01:00:30.020 # +sdown master mymaster 60.205.209.106 6381
996:X 23 Nov 01:00:30.143 # +new-epoch 4
996:X 23 Nov 01:00:30.144 # +vote-for-leader 699538b978f33f677c8be471eed344b3933eca8c 4
996:X 23 Nov 01:00:31.111 # +odown master mymaster 60.205.209.106 6381 #quorum 3/2
996:X 23 Nov 01:00:31.111 # Next failover delay: I will not start a failover before Thu Nov 23 01:00:36 2017
996:X 23 Nov 01:00:31.200 # +config-update-from sentinel 699538b978f33f677c8be471eed344b3933eca8c 172.17.171.34 26381 @ mymaster 60.205.209.106 6381
996:X 23 Nov 01:00:31.200 # +switch-master mymaster 60.205.209.106 6381 60.205.209.106 6380
996:X 23 Nov 01:00:31.200 * +slave slave 60.205.209.106:6382 60.205.209.106 6382 @ mymaster 60.205.209.106 6380
996:X 23 Nov 01:00:31.200 * +slave slave 60.205.209.106:6381 60.205.209.106 6381 @ mymaster 60.205.209.106 6380
996:X 23 Nov 01:00:32.233 # +sdown slave 60.205.209.106:6381 60.205.209.106 6381 @ mymaster 60.205.209.106 6380
1073:X 23 Nov 01:00:30.087 # +sdown master mymaster 60.205.209.106 6381
1073:X 23 Nov 01:00:30.139 # +odown master mymaster 60.205.209.106 6381 #quorum 3/2
1073:X 23 Nov 01:00:30.139 # +new-epoch 4
1073:X 23 Nov 01:00:30.139 # +try-failover master mymaster 60.205.209.106 6381
1073:X 23 Nov 01:00:30.141 # +vote-for-leader 699538b978f33f677c8be471eed344b3933eca8c 4
1073:X 23 Nov 01:00:30.142 # 8412b6b2ac39a3d36c171590cd23cbe025517c15 voted for 8412b6b2ac39a3d36c171590cd23cbe025517c15 4
1073:X 23 Nov 01:00:30.144 # f8c7e052744926747ef1f31c27da4721fde3faf4 voted for 699538b978f33f677c8be471eed344b3933eca8c 4
1073:X 23 Nov 01:00:30.232 # +elected-leader master mymaster 60.205.209.106 6381
1073:X 23 Nov 01:00:30.232 # +failover-state-select-slave master mymaster 60.205.209.106 6381
1073:X 23 Nov 01:00:30.294 # +selected-slave slave 60.205.209.106:6380 60.205.209.106 6380 @ mymaster 60.205.209.106 6381
1073:X 23 Nov 01:00:30.294 * +failover-state-send-slaveof-noone slave 60.205.209.106:6380 60.205.209.106 6380 @ mymaster 60.205.209.106 6381
1073:X 23 Nov 01:00:30.356 * +failover-state-wait-promotion slave 60.205.209.106:6380 60.205.209.106 6380 @ mymaster 60.205.209.106 6381
1073:X 23 Nov 01:00:31.153 # +promoted-slave slave 60.205.209.106:6380 60.205.209.106 6380 @ mymaster 60.205.209.106 6381
1073:X 23 Nov 01:00:31.153 # +failover-state-reconf-slaves master mymaster 60.205.209.106 6381
1073:X 23 Nov 01:00:31.200 * +slave-reconf-sent slave 60.205.209.106:6382 60.205.209.106 6382 @ mymaster 60.205.209.106 6381
1073:X 23 Nov 01:00:32.149 * +slave-reconf-inprog slave 60.205.209.106:6382 60.205.209.106 6382 @ mymaster 60.205.209.106 6381
1073:X 23 Nov 01:00:32.149 * +slave-reconf-done slave 60.205.209.106:6382 60.205.209.106 6382 @ mymaster 60.205.209.106 6381
1073:X 23 Nov 01:00:32.220 # -odown master mymaster 60.205.209.106 6381
1073:X 23 Nov 01:00:32.220 # +failover-end master mymaster 60.205.209.106 6381
1073:X 23 Nov 01:00:32.220 # +switch-master mymaster 60.205.209.106 6381 60.205.209.106 6380
1073:X 23 Nov 01:00:32.220 * +slave slave 60.205.209.106:6382 60.205.209.106 6382 @ mymaster 60.205.209.106 6380
1073:X 23 Nov 01:00:32.220 * +slave slave 60.205.209.106:6381 60.205.209.106 6381 @ mymaster 60.205.209.106 6380
1073:X 23 Nov 01:00:33.227 # +sdown slave 60.205.209.106:6381 60.205.209.106 6381 @ mymaster 60.205.209.106 6380
1009:X 23 Nov 01:00:30.039 # +sdown master mymaster 60.205.209.106 6381
1009:X 23 Nov 01:00:30.139 # +odown master mymaster 60.205.209.106 6381 #quorum 2/2
1009:X 23 Nov 01:00:30.139 # +new-epoch 4
1009:X 23 Nov 01:00:30.139 # +try-failover master mymaster 60.205.209.106 6381
1009:X 23 Nov 01:00:30.142 # +vote-for-leader 8412b6b2ac39a3d36c171590cd23cbe025517c15 4
1009:X 23 Nov 01:00:30.142 # 699538b978f33f677c8be471eed344b3933eca8c voted for 699538b978f33f677c8be471eed344b3933eca8c 4
1009:X 23 Nov 01:00:30.144 # f8c7e052744926747ef1f31c27da4721fde3faf4 voted for 699538b978f33f677c8be471eed344b3933eca8c 4
1009:X 23 Nov 01:00:31.200 # +config-update-from sentinel 699538b978f33f677c8be471eed344b3933eca8c 172.17.171.34 26381 @ mymaster 60.205.209.106 6381
1009:X 23 Nov 01:00:31.200 # +switch-master mymaster 60.205.209.106 6381 60.205.209.106 6380
1009:X 23 Nov 01:00:31.200 * +slave slave 60.205.209.106:6382 60.205.209.106 6382 @ mymaster 60.205.209.106 6380
1009:X 23 Nov 01:00:31.200 * +slave slave 60.205.209.106:6381 60.205.209.106 6381 @ mymaster 60.205.209.106 6380
1009:X 23 Nov 01:00:32.258 # +sdown slave 60.205.209.106:6381 60.205.209.106 6381 @ mymaster 60.205.209.106 6380
```

数据一致性的处理办法之一
```
# 这些配置仅当redis为master时才有效
# 当master不符合这些条件时，它将停止对外的服务。这种场景主要是用于master在网络上被孤立了。
min-slaves-to-write 1
min-slaves-max-lag 10
```
