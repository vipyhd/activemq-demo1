[TOC]
# 示例说明


此示例演示交换器、队列、消息持久化功能。和内存、磁盘预警


## 持久化

RabbitMQ 的持久化分交换器持久化、队列持久化和消息持久化。

- 定义持久化交换器，通过第三个参数 `durable` 开启/关闭持久化
  ```java
  channel.exchangeDeclare(exchangeName, exchangeType, durable)
  ```
  
- 定义持久化队列，通过第二个参数 `durable` 开启/关闭持久化
  ```java
  channel.queueDeclare(queue, durable, exclusive, autoDelete, arguments);
  ```

- 发送持久化消息，需要在消息属性中设置 `deliveryMode=2`， 此属性在 `BasicProperties` 中，通过 `basicPublish` 方法的 `props` 参数传入。
  ```java
  channel.basicPublish(exchange, routingKey, props, body);
  ```

    `BasicProperties` 对象可以从RabbitMQ 内置的 `MessageProperties` 类中获取
    ```java
    MessageProperties.PERSISTENT_TEXT_PLAIN
    ```

    如果还需要设置其它属性，可以通过 `AMQP.BasicProperties.Builder` 去构建一个 `BasicProperties` 对象
    ```java
    new AMQP.BasicProperties.Builder()
            .deliveryMode(2)
            .build()
    ```


## 内存告警

默认情况下 `set_vm_memory_high_watermark` 的值为 0.4，即内存阈值（临界值）为 0.4，表示当 RabbitMQ 使用的内存超过 40%时，就会产生内存告警并阻塞所有生产者的连接。一旦告警被解除(有消息被消费或者从内存转储到磁盘等情况的发生)， 一切都会恢复正常。

在出现内存告警后，所有的客户端连接都会被阻塞。阻塞分为 `blocking` 和 `blocked` 两种。
- blocking：表示没有发送消息的链接。
- blocked：表示试图发送消息的链接。

如果出现了内存告警，并且机器还有可用内存，可以通过命令调整内存阈值，解除告警。
```sh
rabbitmqctl set_vm_memory_high_watermark 1
```

或者
```sh
rabbitmqctl set_vm_memory_high_watermark absolute 1GB
```

但这种方式只是临时调整，RabbitMQ 服务重启后，会还原。如果需要永久调整，可以修改配置文件。但修改配置文件需要**重启RabbitMQ 服务才能生效**。
- 修改配置文件：`vim /etc/rabbitmq/rabbitmq.conf` 

    ```sh
    vm_memory_high_watermark.relative = 0.4
    ```

    或者
    ```sh
    vm_memory_high_watermark.absolute = 1GB
    ```

### **模拟内存告警**
  1. 调整内存阈值，模拟出告警，在RabbitMQ 服务器上修改。**`注意：修改之前，先在管理页面看一下当前使用了多少，调成比当前值小`**
      ```sh
      rabbitmqctl set_vm_memory_high_watermark absolute 50MB
      ```

  2. 刷新管理页面（可能需要刷新多次），在 `Overview -> Nodes` 中可以看到Memory变成了红色，表示此节点内存告警了
  3. 启动 `Producer` 和 `Consumer` 
  4. 查看管理界面的 `Connections` 页面，可以看到生产者和消费者的链接都处于 `blocking` 状态。
  5. 在 `Producer` 的控制台按回车健，再观察管理界面的 `Connections` 页面，会发现生产者的状态成了 `blocked`。
  6. 此时虽然在`Producer` 控制台看到了发送两条消息的信息，但 `Consumer` 并没有收到任何消息。并且在管理界面的 `Queues` 页面也看到不到队列的消息数量有变化。
  7. 解除内存告警后，会发现 `Consumer` 收到了 `Producer` 发送的两条消息。


## 内存换页

- 在Broker节点的使用内存即将达到内存阈值之前，它会尝试将队列中的消息存储到磁盘以释放内存空间，这个动作叫内存换页。
- 持久化和非持久化的消息都会被转储到磁盘中，其中持久化的消息本身就在磁盘中有一份副本，此时会将持久化的消息从内存中清除掉。
- 默认情况下，在内存到达内存阈值的 50%时会进行换页动作。也就是说，在默认的内存阈值为 0.4 的情况下，当内存超过 0.4 x 0 .5=0.2 时会进行换页动作。
- 通过修改配置文件，调整内存换页分页阈值（不能通过命令调整）。

    ```sh
    # 此值大于1时，相当于禁用了换页功能。
    vm_memory_high_watermark_paging_ratio = 0.75
    ```

## 磁盘告警

- 当磁盘剩余空间低于磁盘的阈值时，RabbitMQ 同样会阻塞生产者，这样可以避免因非持久化的消息持续换页而耗尽磁盘空间导致服务崩溃
- 默认情况下，磁盘阈值为50MB，表示当磁盘剩余空间低于50MB 时会阻塞生产者并停止内存中消息的换页动作
- 这个阈值的设置可以减小，但不能完全消除因磁盘耗尽而导致崩渍的可能性。比如在两次磁盘空间检测期间内，磁盘空间从大于50MB被耗尽到0MB
- 通过命令可以调整磁盘阈值，临时生效，重启恢复

    ```sh
    # disk_limit 为固定大小，单位为MB、GB
    rabbitmqctl set_disk_free_limit <disk_limit>
    ```

    或者
    ```sh
    # fraction 为相对比值，建议的取值为1.0~2.0之间
    rabbitmqctl set_disk_free_limit mem_relative <fraction>
    ```

### **模拟磁盘告警**
1. 在服务器通过命令，临时调整磁盘阈值（**需要设置一个绝对大与当前磁盘空间的数值**）

    ```sh
    rabbitmqctl set_disk_free_limit 102400GB
    ```

2. 刷新管理页面（可能需要刷新多次），在 Overview -> Nodes 中可以看到Disk space变成了红色，表示此节点磁盘告警了
3. 后续步骤同[模拟内存告警](#模拟内存告警)。