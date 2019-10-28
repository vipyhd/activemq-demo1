[TOC]

# **RabbitMQ 安装和使用**

## **一、安装依赖环境**

1. 在 http://www.rabbitmq.com/which-erlang.html 页面查看安装rabbitmq需要安装erlang对应的版本

2. 在 https://github.com/rabbitmq/erlang-rpm/releases 页面找到需要下载的erlang版本，`erlang-*.centos.x86_64.rpm`就是centos版本的。

3. 复制下载地址后，使用wget命令下载
   ```sh
   wget -P /home/download https://github.com/rabbitmq/erlang-rpm/releases/download/v21.2.3/erlang-21.2.3-1.el7.centos.x86_64.rpm
   ```

4. 安装 Erlang
   ```sh
   sudo rpm -Uvh /home/download/erlang-21.2.3-1.el7.centos.x86_64.rpm
   ```

5. 安装 socat
   ```sh
   sudo yum install -y socat
   ```


## **二、安装RabbitMQ**

1. 在[官方下载页面](http://www.rabbitmq.com/download.html)找到CentOS7版本的下载链接，下载rpm安装包
   ```sh
   wget -P /home/download https://github.com/rabbitmq/rabbitmq-server/releases/download/v3.7.9/rabbitmq-server-3.7.9-1.el7.noarch.rpm
   ```
   **`提示：可以在`https://github.com/rabbitmq/rabbitmq-server/tags`下载历史版本`**

2. 安装RabbitMQ
   ```sh
   sudo rpm -Uvh /home/download/rabbitmq-server-3.7.9-1.el7.noarch.rpm
   ```


## **三、启动和关闭**

- 启动服务
  ```sh
  sudo systemctl start rabbitmq-server
  ```

- 查看状态
  ```sh
  sudo systemctl status rabbitmq-server
  ```

- 停止服务
  ```sh
  sudo systemctl stop rabbitmq-server
  ```

- 设置开机启动
  ```sh
  sudo systemctl enable rabbitmq-server
  ```


## **四、开启Web管理插件**

1. 开启插件
   ```sh
   rabbitmq-plugins enable rabbitmq_management
   ```
   **`说明：rabbitmq有一个默认的guest用户，但只能通过localhost访问，所以需要添加一个能够远程访问的用户。`**

2. 添加用户
   ```sh
   rabbitmqctl add_user admin admin
   ```

3. 为用户分配操作权限
   ```sh
   rabbitmqctl set_user_tags admin administrator
   ```

4. 为用户分配资源权限
   ```sh
   rabbitmqctl set_permissions -p / admin ".*" ".*" ".*"
   ```


## **五、防火墙添加端口**

- RabbitMQ 服务启动后，还不能进行外部通信，需要将端口添加都防火墙

1. 添加端口
   ```sh
   sudo firewall-cmd --zone=public --add-port=4369/tcp --permanent
   sudo firewall-cmd --zone=public --add-port=5672/tcp --permanent
   sudo firewall-cmd --zone=public --add-port=25672/tcp --permanent
   sudo firewall-cmd --zone=public --add-port=15672/tcp --permanent
   ```

2. 重启防火墙
   ```sh
   sudo firewall-cmd --reload
   ```

---

# **多机多节点集群部署**


## **一、 环境准备**

- 准备三台安装好RabbitMQ 的机器，安装方法见 [安装步骤](#rabbitmq-安装和使用)
  - 10.10.1.41
  - 10.10.1.42
  - 10.10.1.43

  **`提示：如果使用虚拟机，可以在一台VM上安装好RabbitMQ后，创建快照，从快照创建链接克隆，会节省很多磁盘空间`**


## **二、修改配置文件**

1. 修改`10.10.1.41`机器上的`/etc/hosts`文件
   ```sh
   sudo vim /etc/hosts
   ```

2. 添加IP和节点名
   ```sh
   10.10.1.41 node1
   10.10.1.42 node2
   10.10.1.43 node3
   ```
3. 修改对应主机的hostname
```sh
hostname node1
hostname node2
hostname node3
```
4. 将`10.10.1.41`上的hosts文件复制到另外两台机器上
   ```sh
   sudo scp /etc/hosts root@node2:/etc/
   sudo scp /etc/hosts root@node3:/etc/
   ```
   **`说明：命令中的root是目标机器的用户名，命令执行后，可能会提示需要输入密码，输入对应用户的密码就行了`**
5. 将`10.10.1.41`上的`/var/lib/rabbitmq/.erlang.cookie`文件复制到另外两台机器上
   ```sh
   scp /var/lib/rabbitmq/.erlang.cookie root@node2:/var/lib/rabbitmq/
   scp /var/lib/rabbitmq/.erlang.cookie root@node3:/var/lib/rabbitmq/
   ```
   **`提示：如果是通过克隆的VM，可以省略这一步`**

## **三、防火墙添加端口**

- 给每台机器的防火墙添加端口

1. 添加端口
   ```sh
   sudo firewall-cmd --zone=public --add-port=4369/tcp --permanent
   sudo firewall-cmd --zone=public --add-port=5672/tcp --permanent
   sudo firewall-cmd --zone=public --add-port=25672/tcp --permanent
   sudo firewall-cmd --zone=public --add-port=15672/tcp --permanent
   ```

2. 重启防火墙
   ```sh
   sudo firewall-cmd --reload
   ```


### **四、启动RabbitMQ**

1. 启动每台机器的RabbitMQ
   ```sh
   sudo systemctl start rabbitmq-server
   ```
   或者
   ```sh
   rabbitmq-server -detached
   ```

2. 将`10.10.1.42`加入到集群
   ```sh
   # 停止RabbitMQ 应用
   rabbitmqctl stop_app
   # 重置RabbitMQ 设置
   rabbitmqctl reset
   # 加入到集群
   rabbitmqctl join_cluster rabbit@node1 --ram
   # 启动RabbitMQ 应用
   rabbitmqctl start_app
   ```

3. 查看集群状态，看到`running_nodes,[rabbit@node1,rabbit@node2]`表示节点启动成功
   ```sh
   rabbitmqctl cluster_status
   ```
   **`提示：在管理界面可以更直观的看到集群信息`**

4. 将`10.10.1.43`加入到集群
   ```sh
   # 停止 RabbitMQ 应用
   rabbitmqctl stop_app
   # 重置 RabbitMQ 设置
   rabbitmqctl reset
   # 节点加入到集群
   rabbitmqctl join_cluster rabbit@node1 --ram
   # 启动 RabbitMQ 应用
   rabbitmqctl start_app
   ```

5. 重复地3步，查看集群状态

---

# **单机多节点部署**

## **一、环境准备**

- 准备一台已经安装好RabbitMQ 的机器，安装方法见 [安装步骤](#rabbitmq-安装和使用)
  - 10.10.1.41


## **二、启动RabbitMQ**

1. 在启动前，先修改RabbitMQ 的默认节点名（非必要），在`/etc/rabbitmq/rabbitmq-env.conf`增加以下内容
   ```sh
   # RabbitMQ 默认节点名，默认是rabbit
   NODENAME=rabbit1
   ```

2. RabbitMQ 默认是使用服务的启动的，单机多节点时需要改为手动启动，先停止运行中的RabbitMQ 服务
   ```sh
   sudo systemctl stop rabbitmq-server
   ```

3. 启动第一个节点
   ```sh
   rabbitmq-server -detached
   ```

4. 启动第二个节点
   ```sh
   RABBITMQ_NODE_PORT=5673 RABBITMQ_SERVER_START_ARGS="-rabbitmq_management listener [{port,15673}]" RABBITMQ_NODENAME=rabbit2 rabbitmq-server -detached
   ```

5. 启动第三个节点
   ```sh
   RABBITMQ_NODE_PORT=5674 RABBITMQ_SERVER_START_ARGS="-rabbitmq_management listener [{port,15674}]" RABBITMQ_NODENAME=rabbit3 rabbitmq-server -detached
   ```

6. 将rabbit2加入到集群
   ```sh
   # 停止 rabbit2 的应用
   rabbitmqctl -n rabbit2 stop_app
   # 重置 rabbit2 的设置
   rabbitmqctl -n rabbit2 reset
   # rabbit2 节点加入到 rabbit1的集群中
   rabbitmqctl -n rabbit2 join_cluster rabbit1 --ram
   # 启动 rabbit2 节点
   rabbitmqctl -n rabbit2 start_app
   ```

7. 将rabbit3加入到集群
   ```sh
   # 停止 rabbit3 的应用
   rabbitmqctl -n rabbit3 stop_app
   # 重置 rabbit3 的设置
   rabbitmqctl -n rabbit3 reset
   # rabbit3 节点加入到 rabbit1的集群中
   rabbitmqctl -n rabbit3 join_cluster rabbit1 --ram
   # 启动 rabbit3 节点
   rabbitmqctl -n rabbit3 start_app
   ```

8. 查看集群状态，看到`{running_nodes,[rabbit3@node1,rabbit2@node1,rabbit1@node1]}`说明节点已启动成功。
   ```sh
   rabbitmqctl cluster_status
   ```
   **`提示：在管理界面可以更直观的看到集群信息`**


## **三、防火墙添加端口**

- 需要将每个节点的端口都添加到防火墙

1. 添加端口
   ```sh
   sudo firewall-cmd --zone=public --add-port=4369/tcp --permanent
   sudo firewall-cmd --zone=public --add-port=5672/tcp --permanent
   sudo firewall-cmd --zone=public --add-port=25672/tcp --permanent
   sudo firewall-cmd --zone=public --add-port=15672/tcp --permanent
   sudo firewall-cmd --zone=public --add-port=5673/tcp --permanent
   sudo firewall-cmd --zone=public --add-port=25673/tcp --permanent
   sudo firewall-cmd --zone=public --add-port=15673/tcp --permanent
   sudo firewall-cmd --zone=public --add-port=5674/tcp --permanent
   sudo firewall-cmd --zone=public --add-port=25674/tcp --permanent
   sudo firewall-cmd --zone=public --add-port=15674/tcp --permanent
   ```

2. 重启防火墙
   ```sh
   sudo firewall-cmd --reload
   ```

---

# **镜像队列模式集群**
- 镜像队列属于RabbitMQ 的高可用方案，见：https://www.rabbitmq.com/ha.html#mirroring-arguments
- 通过前面的步骤搭建的集群属于普通模式集群，是通过共享元数据实现集群
- 开启镜像队列模式需要在管理页面添加策略，添加方式：
  1. 进入管理页面 -> Admin -> Policies（在页面右侧）-> Add / update a policy
  2. 在表单中填入：
  
     ```
           name: ha-all
        Pattern: ^
       Apply to: Queues
       Priority: 0
     Definition: ha-mode = all
     ```
     **参数说明**
     
     name: 策略名称，如果使用已有的名称，保存后将会修改原来的信息
     
     Apply to：策略应用到什么对象上

     Pattern：策略应用到对象时，对象名称的匹配规则（正则表达式）

     Priority：优先级，数值越大，优先级越高，相同优先级取最后一个

     Definition：策略定义的类容，对于镜像队列的配置来说，只需要包含3个部分: `ha-mode` 、`ha-params` 和 `ha-sync-mode`。其中，`ha-sync-mode`是同步的方式，自动还是手动，默认是自动。`ha-mode` 和 `ha-params` 组合使用。组合方式如下：

    | ha-mode | ha-params | 说明 |
    | :-----: | :-------: | :--------------------------------------------------------------------------------------------------------------------------------------------------------- |
    | all | (empty) | 队列镜像到集群类所有节点 |
    | exactly | count | 队列镜像到集群内指定数量的节点。如果集群内节点数少于此值，队列将会镜像到所有节点。如果大于此值，而且一个包含镜像的节点停止，则新的镜像不会在其它节点创建。 |
    | nodes | nodename | 队列镜像到指定节点，指定的节点不在集群中不会报错。当队列申明时，如果指定的节点不在线，则队列会被创建在客户端所连接的节点上。 |

- 镜像队列模式相比较普通模式，镜像模式会占用更多的带宽来进行同步，所以镜像队列的吞吐量会低于普通模式
- 但普通模式不能实现高可用，某个节点挂了后，这个节点上的消息将无法被消费，需要等待节点启动后才能被消费。

