[TOC]

# 前提-配置java环境变量

# tomcat
```
# 下载
curl "http://mirror.bit.edu.cn/apache/tomcat/tomcat-8/v8.5.31/bin/apache-tomcat-8.5.31.tar.gz" -o apache-tomcat-8.5.31.tar.gz

# 解压
tar -xvf apache-tomcat-8.5.31.tar.gz 

# 启动
./apache-tomcat-8.5.31/bin/startup.sh
```

# nginx
## 下载及解压 相关的模块到/u01目录
```
curl "http://nginx.org/download/nginx-1.14.0.tar.gz" -o nginx-1.14.0.tar.gz
tar -xvf nginx-1.14.0.tar.gz

curl "http://www.zlib.net/zlib-1.2.11.tar.gz" -o zlib-1.2.11.tar.gz
tar -xvf zlib-1.2.11.tar.gz

curl "https://jaist.dl.sourceforge.net/project/pcre/pcre/8.41/pcre-8.41.tar.gz" -o pcre-8.41.tar.gz
tar -xvf pcre-8.41.tar.gz

curl "https://www.openssl.org/source/openssl-1.0.2o.tar.gz" -o openssl-1.0.2o.tar.gz
tar -xvf openssl-1.0.2o.tar.gz

curl "http://labs.frickle.com/files/ngx_cache_purge-2.3.tar.gz" -o ngx_cache_purge-2.3.tar.gz
tar -xvf ngx_cache_purge-2.3.tar.gz
```

## 编译安装nginx
```
cd nginx-1.14.0

./configure --add-module=../ngx_cache_purge-2.3 --prefix=/usr/local/nginx --with-http_ssl_module --with-pcre=../pcre-8.41 --with-zlib=../zlib-1.2.11 --with-openssl=../openssl-1.0.2o

make 

make install
```

> 如果有异常：./configure: error: C compiler cc is not found，则安装gcc等软件

``` yum -y install gcc gcc-c++ autoconf automake make ```

# keepalived
## 下载安装相关相关的组件
```
yum -y install openssl-devel 
yum -y install libnl libnl-devel
yum install -y libnfnetlink-devel
```

## 下载安装keepalived
```
# 下载
curl "http://www.keepalived.org/software/keepalived-1.4.4.tar.gz" -o keepalived-1.4.4.tar.gz
tar -xvf keepalived-1.4.4.tar.gz

cd keepalived-1.4.4  

# 安装到/usr/local/keepalived目录
./configure --prefix=/usr/local/keepalived --sysconf=/etc  
make && make install
```

## 配置文件存放地址 
> 配置放在/etc/keepalived/，三份配置文件（一个nginx_monitor监控脚本，主备各一份keepalived配置）
### nginx监控shell脚本 nginx_monitor.sh 文件
```
# 创建nginx monitor 脚本，并赋予可执行权限
chmod +x /etc/keepalived/nginx_monitor.sh
# 测试一下脚本能不能执行
执行命令：/etc/keepalived/nginx_monitor.sh 
没报错即表示为成功
```

### keepalived配置
```
# - master主机
keepalived-nginx-master.conf
# - backup备机
keepalived-nginx-backup.conf

```

## 启动keepalived
```
# 启动master主机
/usr/local/keepalived/sbin/keepalived -f /etc/keepalived/keepalived-nginx-master.conf
# 启动backup备机
/usr/local/keepalived/sbin/keepalived -f /etc/keepalived/keepalived-nginx-backup.conf
```

## 停止
```
ps -ef | grep keepalived
kill -9 关闭相关的进程
```

## 测试
```
1. 关掉备机，功能完全不受影响。
2. 关掉主机，虚拟IP漂移到备机，备机开始工作。
3. 关掉主机nginx，主机监控到无nginx后，自动切换
```
# LVS安装测试
```
LVS全称为Linux Virtual Server，工作在ISO模型中的第四层，由于其工作在第四层，因此与iptables类似，必须工作在内核空间上。因此lvs与iptables一样，是直接工作在内核中的，叫ipvs，主流的linux发行版默认都已经集成了ipvs，因此用户只需安装一个管理工具ipvsadm即可。
```

## 安装所需依赖包
```
yum install popt-static kernel-devel make gcc openssl-devel lftplibnl* popt* openssl-devel lftplibnl* popt* libnl* libpopt* gcc*
```
## 加载 ip_vs 
```
1、lvs安装部署：在两台LVS Server上安装lvs、keepalived软件（LVS_MASTER & LVS_BACKUP）
查看内核模块是否支持
# lsmod |grep ip_vs 
ip_vs 35009 0 
如果没有显示，则说明没有加载，执行命令 modprobe ip_vs 就可以把ip_vs模块加载到内核 

```
## 2、安装ipvsadm
```
curl http://www.linuxvirtualserver.org/software/kernel-2.6/ipvsadm-1.26.tar.gz -o ipvsadm-1.26.tar.gz
tar zxf ipvsadm-1.26.tar.gz  
cd ipvsadm-1.26 
rpm -qa | grep kernel-devel（默认已经安装）
make && make install

curl "http://www.linuxvirtualserver.org/software/kernel-2.6/ipvsadm-1.25-1.src.rpm" -o ipvsadm-1.25-1.src.rpm
rpm -ivh ipvsadm-1.25-1.src.rpm

```
# DR模式
## 3、   dr配置
```
#执行ipvsadm命令加载 
ipvsadm 
#查看结果 (能够显示表示成功)
DR模式配置
# vim /usr/local/sbin/lvs_dr.sh
#! /bin/bash
echo 1 > /proc/sys/net/ipv4/ip_forward
ipv=/sbin/ipvsadm
vip=192.168.100.235
ifconfig ens192:0 down
ifconfig ens192:0 $vip broadcast $vip netmask 255.255.255.255 up
route add -host $vip dev ens192:0
$ipv -C
$ipv -A -t $vip:8080 -s wrr

rs1=192.168.100.112
$ipv -a -t $vip:8080 -r $rs1:8080 -g -w 3

# rs配置 真实web服务器运行
# vim /usr/local/sbin/lvs_dr_rs.sh
#! /bin/bash
vip=192.168.100.235
ifconfig lo:0 $vip broadcast $vip netmask 255.255.255.255 up
route add -host $vip lo:0
echo "1" >/proc/sys/net/ipv4/conf/lo/arp_ignore
echo "2" >/proc/sys/net/ipv4/conf/lo/arp_announce
echo "1" >/proc/sys/net/ipv4/conf/all/arp_ignore
echo "2" >/proc/sys/net/ipv4/conf/all/arp_announce

ipvsadm -C 清除
说明：
-A  --add-service在服务器列表中新添加一条新的虚拟服务器记录
-t 表示为tcp服务
-u 表示为udp服务
-s --scheduler 使用的调度算法， rr | wrr | lc | wlc | lblb | lblcr | dh | sh | sed | nq 默认调度算法是 wlc
-a --add-server 在服务器表中添加一条新的真实主机记录
-r --real-server  真实服务器地址
-m --masquerading 指定LVS工作模式为NAT模式
-w --weight 真实服务器的权值
-g --gatewaying 指定LVS工作模式为直接路由器模式（也是LVS默认的模式）
-i --ipip 指定LVS的工作模式为隧道模式

# 可能出现的错误
1、没有轮询效果
ipvsadm -L --timeout  一条tcp的连接经过lvs后,lvs会把这台记录保存15分钟
可以设置短一点，达到实验效果：ipvsadm --set 1 2 1

注： 保存添加的虚拟ip记录和ipvsadm的规则可以使用service ipvsadm save，还可以用-S或--save。清除所有记录和规则除了使用-C，还以使用--clear
错误：Memory allocation problem
查看一下vmlloc使用情况： cat /proc/meminfo | grep -i vmalloc
在/etc/default/grub文件的末尾添加如下一行：
GRUB_CMDLINE_LINUX="vmalloc=256MB"

完成上述操作之后，发现lvs状态仍然是SYN_RECV。抓包后的pcap文件中，没有syn ack。于是想到是不是在什么地方丢掉了。
看到官方文档中有描述要设置re_ filter。
查了一下这个参数的解释
======================================
rp_filter参数有三个值，0、1、2，具体含义：
0：不开启源地址校验。
1：开启严格的反向路径校验。对每个进来的数据包，校验其反向路径是否是最佳路径。如果反向路径不是最佳路径，则直接丢弃该数据包。
2：开启松散的反向路径校验。对每个进来的数据包，校验其源地址是否可达，即反向路径是否能通（通过任意网口），如果反向路径不同，则直接丢弃该数据包。
=======================================
default的值是1，这里改为2
echo 2 > /proc/sys/net/ipv4/conf/ 网卡名/rp_filter
echo 2 > /proc/sys/net/ipv4/conf/ 网卡名/rp_filter
systemctl restart network.service

echo "1" > /proc/sys/net/ipv4/conf/lo/arp_ignore
                                 echo "2"> /proc/sys/net/ipv4/conf/lo/arp_announce
                                 echo "1"> /proc/sys/net/ipv4/conf/all/arp_ignore
                                 echo "2" > /proc/sys/net/ipv4/conf/all/arp_announce


```
## lvs调度算法
```
 1、静态调度：
①rr（Round Robin）:轮询调度，轮叫调度

 轮询调度算法的原理是每一次把来自用户的请求轮流分配给内部中的服务器，从1开始，直到N(内部服务器个数)，然后重新开始循环。算法的优点是其简洁性，它无需记录当前所有连接的状态，所以它是一种无状态调度。【提示：这里是不考虑每台服务器的处理能力】

②wrr：weight,加权（以权重之间的比例实现在各主机之间进行调度）

由于每台服务器的配置、安装的业务应用等不同，其处理能力会不一样。所以，我们根据服务器的不同处理能力，给每个服务器分配不同的权值，使其能够接受相应权值数的服务请求。

③sh:source hashing,源地址散列。主要实现会话绑定，能够将此前建立的session信息保留了

源地址散列调度算法正好与目标地址散列调度算法相反，它根据请求的源IP地址，作为散列键（Hash Key）从静态分配的散列表找出对应的服务器，若该服务器是可用的并且没有超负荷，将请求发送到该服务器，否则返回空。它采用的散列函数与目标地址散列调度算法的相同。它的算法流程与目标地址散列调度算法的基本相似，除了将请求的目标IP地址换成请求的源IP地址，所以这里不一个一个叙述。

④Dh:Destination hashing:目标地址散列。把同一个IP地址的请求，发送给同一个server。

目标地址散列调度算法也是针对目标IP地址的负载均衡，它是一种静态映射算法，通过一个散列（Hash）函数将一个目标IP地址映射到一台服务器。目标地址散列调度算法先根据请求的目标IP地址，作为散列键（Hash Key）从静态分配的散列表找出对应的服务器，若该服务器是可用的且未超载，将请求发送到该服务器，否则返回空。

2、动态调度
①lc（Least-Connection）：最少连接
最少连接调度算法是把新的连接请求分配到当前连接数最小的服务器，最小连接调度是一种动态调度短算法，它通过服务器当前所活跃的连接数来估计服务器的负载均衡，调度器需要记录各个服务器已建立连接的数目，当一个请求被调度到某台服务器，其连接数加1，当连接中止或超时，其连接数减一，在系统实现时，我们也引入当服务器的权值为0时，表示该服务器不可用而不被调度。
简单算法：active*256+inactive(谁的小，挑谁)

②wlc(Weighted Least-Connection Scheduling)：加权最少连接。

加权最小连接调度算法是最小连接调度的超集，各个服务器用相应的权值表示其处理性能。服务器的缺省权值为1，系统管理员可以动态地设置服务器的权限，加权最小连接调度在调度新连接时尽可能使服务器的已建立连接数和其权值成比例。

简单算法：（active*256+inactive）/weight【（活动的连接数+1）/除以权重】（谁的小，挑谁）

③sed(Shortest Expected Delay)：最短期望延迟

基于wlc算法

简单算法：（active+1)*256/weight 【（活动的连接数+1）*256/除以权重】

④nq（never queue）:永不排队（改进的sed）

无需队列，如果有台realserver的连接数＝0就直接分配过去，不需要在进行sed运算。

⑤LBLC（Locality-Based Least Connection）：基于局部性的最少连接

基于局部性的最少连接算法是针对请求报文的目标IP地址的负载均衡调度，不签主要用于Cache集群系统，因为Cache集群中客户请求报文的布标IP地址是变化的，这里假设任何后端服务器都可以处理任何请求，算法的设计目标在服务器的负载基本平衡的情况下，将相同的目标IP地址的请求调度到同一个台服务器，来提高个太服务器的访问局部性和主存Cache命中率，从而调整整个集群系统的处理能力。

基于局部性的最少连接调度算法根据请求的目标IP地址找出该目标IP地址最近使用的RealServer，若该Real Server是可用的且没有超载，将请求发送到该服务器；若服务器不存在，或者该服务器超载且有服务器处于一半的工作负载，则用“最少链接”的原则选出一个可用的服务器，将请求发送到该服务器。

⑥LBLCR（Locality-Based Least Connections withReplication）：带复制的基于局部性最少链接

      带复制的基于局部性最少链接调度算法也是针对目标IP地址的负载均衡，该算法根据请求的目标IP地址找出该目标IP地址对应的服务器组，按“最小连接”原则从服务器组中选出一台服务器，若服务器没有超载，将请求发送到该服务器；若服务器超载，则按“最小连接”原则从这个集群中选出一台服务器，将该服务器加入到服务器组中，将请求发送到该服务器。同时，当该服务器组有一段时间没有被修改，将最忙的服务器从服务器组中删除，以降低复制的程度。
```

# 注意事项
## 防火墙
```
firewall-cmd --state #查看默认防火墙状态（关闭后显示notrunning，开启后显示running）
systemctl list-unit-files|grep firewalld.service
systemctl stop firewalld.service #停止firewall
systemctl disable firewalld.service #禁止firewall开机启动

[root@localhost ~]#systemctl stop firewalld.service
[root@localhost ~]#systemctl disable firewalld.service
启动一个服务：systemctl start firewalld.service
关闭一个服务：systemctl stop firewalld.service
重启一个服务：systemctl restart firewalld.service
显示一个服务的状态：systemctl status firewalld.service
在开机时启用一个服务：systemctl enable firewalld.service
在开机时禁用一个服务：systemctl disable firewalld.service
查看服务是否开机启动：systemctl is-enabled firewalld.service;echo $?
查看已启动的服务列表：systemctl list-unit-files|grep enabled
```


安装killall工具
yum install psmisc

    # 状态变成master会执行该脚本
    # notify_master "/etc/keepalived/be_master.sh"
    
    # 状态变成backup会执行该脚本
    # notify_backup  ""
    
    # 监控脚本执行发现异常时会执行该脚本
    # notify_fault "xxxx.sh"



