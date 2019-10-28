[TOC]

# Server 服务器基本信息
```
# redis版本号
redis_version:5.0.3
# redis源码包git信息
redis_git_sha1:00000000
redis_git_dirty:0
redis_build_id:da69b07a37c06dc8
# 运行模式(“独立”，“哨兵”或“集群”)
redis_mode:standalone
# 操作系统信息
os:Linux 3.10.0-514.el7.x86_64 x86_64
# 64位架构
arch_bits:64
# 编译运行的底层依赖信息
multiplexing_api:epoll
atomicvar_api:atomic-builtin
gcc_version:4.8.5
# 进程ID
process_id:10040
# 实例运行的随机值标识符(sentinel和集群中有用)
run_id:df903681f11f712523e0615cd4c7e45afbf500b6
# 端口
tcp_port:6379
# 运行时长
uptime_in_seconds:42593
# 运行时长(天)
uptime_in_days:0
# 服务器的频率设置
hz:10
configured_hz:10
# LRU运作的时钟(分钟为单位)
lru_clock:7473903
# 可执行文件
executable:/usr/local/redis/./bin/redis-server
# 当前读取的配置
config_file:/usr/local/redis/conf/redis_6379.conf
```

# Clients 客户端连接信息
```
# 连接数
connected_clients:1
# 客户端输入缓冲区
client_recent_max_input_buffer:2
# 客户端输出缓冲区
client_recent_max_output_buffer:0
# 阻塞的客户端数量(卡住了就看看这个)
blocked_clients:0
```

# Memory内存信息
```
# 内存总量(字节数)
used_memory:854280
# 内存总量(更方便查看的格式)
used_memory_human:834.26K
# 已分配的内存总量
used_memory_rss:8388608
used_memory_rss_human:8.00M
# 内存消耗峰值
used_memory_peak:854280
used_memory_peak_human:834.26K
# 峰值内存占用的内存百分比
used_memory_peak_perc:100.15%
# 内部机制所需的内存
used_memory_overhead:840838
# 启动时消耗的内存
used_memory_startup:791032
# 数据占用的内存大小
used_memory_dataset:13442
# 数据占用的内存大小百分比
used_memory_dataset_perc:21.25%
# 未说明（从名字可以看出是内存申请的信息）
allocator_allocated:844856
allocator_active:1011712
allocator_resident:3665920
# 整个系统内存
total_system_memory:1041199104
total_system_memory_human:992.96M
# Lua脚本存储占用的内存
used_memory_lua:37888
used_memory_lua_human:37.00K
# 未说明
used_memory_scripts:0
used_memory_scripts_human:0B
number_of_cached_scripts:0
# 最大内存配置
maxmemory:0
maxmemory_human:0B
# 内存管理策略
maxmemory_policy:noeviction
# 官方未说明
allocator_frag_ratio:1.20
allocator_frag_bytes:166856
allocator_rss_ratio:3.62
allocator_rss_bytes:2654208
rss_overhead_ratio:2.29
rss_overhead_bytes:4722688
mem_fragmentation_ratio:10.33
mem_fragmentation_bytes:7576576
mem_not_counted_for_evict:0
mem_replication_backlog:0
mem_clients_slaves:0
mem_clients_normal:49694
mem_aof_buffer:0
# 内存分配器，在编译时选择
mem_allocator:jemalloc-5.1.0
# 碎片整理是否存于活动状态
active_defrag_running:0
# 等待被释放的对象数量
lazyfree_pending_objects:0
```

# Persistence持久化相关
```
# 表示Redis是否正在加载备份文件的标志
loading:0
# 从最近一次转储至今，RDB的修改次数
rdb_changes_since_last_save:2
# 表示Redis正在保存RDB的标志
rdb_bgsave_in_progress:0
# 上次RDB成功保存的时间戳
rdb_last_save_time:1550935182
# 最后一次RDB保存操作的状态
rdb_last_bgsave_status:ok
# 最后一次RDB保存操作的持续时间（以秒为单位）
rdb_last_bgsave_time_sec:-1
# 正在进行的RDB保存操作的持续时间（如果有）
rdb_current_bgsave_time_sec:-1
# 上次RBD保存操作期间写时复制分配的字节大小
rdb_last_cow_size:0
# 表示AOF记录的标志已激活
aof_enabled:1
# 表示AOF重写操作的标志正在进行中
aof_rewrite_in_progress:0
# 一旦正在进行的RDB保存完成，将指定表示AOF重写操作的标志。
aof_rewrite_scheduled:0
# 最后一次AOF重写操作的持续时间，以秒为单位
aof_last_rewrite_time_sec:-1
# 正在进行的AOF重写操作的持续时间（如果有）
aof_current_rewrite_time_sec:-1
# 最后一次AOF重写操作的状态
aof_last_bgrewrite_status:ok
# 最后一次写入操作到AOF的状态
aof_last_write_status:ok
# 上次AOF重写操作期间写时复制分配的大小（以字节为单位）
aof_last_cow_size:0
# AOF当前文件大小
aof_current_size:77
# 最新启动或重写时的AOF文件大小
aof_base_size:77
# 一旦正在进行的RDB保存完成，将指定表示AOF重写操作的标志。
aof_pending_rewrite:0
# AOF缓冲区的大小
aof_buffer_length:0
# AOF重写缓冲区的大小
aof_rewrite_buffer_length:0
# fsync挂起作业数
aof_pending_bio_fsync:0
# 延迟fsync计数器
aof_delayed_fsync:0
# 如果数据恢复中可能会有这些值
# loading_start_time：加载操作开始的时间戳
# loading_total_bytes：文件总大小
# loading_loaded_bytes：已加载的字节数
# loading_loaded_perc：相同的值表示为百分比
# loading_eta_seconds：ETA在几秒钟内完成负载
```

# Stats 试试监控信息
```
# Redis服务器接受的连接总数
total_connections_received:1
# Redis服务器处理的命令总数
total_commands_processed:1
# 每秒钟处理的命令数量
instantaneous_ops_per_sec:0
# 通过网络接收的数据总量，以字节为单位
total_net_input_bytes:34
# 通过网络发送的数据总量，以字节为单位
total_net_output_bytes:7
# 每秒钟接收数据的速率，以kbps为单位
instantaneous_input_kbps:0.00
# 每秒钟发送数据的速率，以kbps为单位
instantaneous_output_kbps:0.00
# Redis服务器由于maxclients限制而拒绝的连接数量
rejected_connections:0
# Redis主机和从机进行完全同步的次数
sync_full:0
# Redis服务器接受PSYNC请求的次数
sync_partial_ok:0
# Redis服务器拒绝PSYNC请求的次数
sync_partial_err:0
# 键过期事件的总数
expired_keys:0
expired_stale_perc:0.00
expired_time_cap_reached_count:0
# 由于maxmemory限制，而被回收内存的键的总数
evicted_keys:0
# 在主字典中成功查找到键的次数
keyspace_hits:1
# 在主字典中未能成功查找到键的次数
keyspace_misses:0
# 发布/订阅频道的总数量
pubsub_channels:0
# 客户端订阅的发布/订阅模式的总数量
pubsub_patterns:0
# 最近一次fork操作消耗的时间，以微秒为单位
latest_fork_usec:0
# 迁移已缓存的套接字的数量
migrate_cached_sockets:0
# 为实现key过期而跟踪的key数数量（仅适用于可写副本）
slave_expires_tracked_keys:0
# 碎片整理过程执行的值重新分配的数量
active_defrag_hits:0
# 碎片整理过程启动的中止值重新分配数
active_defrag_misses:0
# 碎片整理的key数量
active_defrag_key_hits:0
# 碎片整理过程跳过的key数量
active_defrag_key_misses:0
```

# Replication主从复制相关信息
```
# 角色 master或者 slave
role:master
# 已连接的Redis从机的数量
connected_slaves:0
# 主从复制过程中master的标识id
master_replid:6ea01bd968c7f14cb6de138462ddaf11930a4269
master_replid2:0000000000000000000000000000000000000000
# 全局的复制偏移量
master_repl_offset:0
second_repl_offset:-1
# 表示Redis服务器是否为部分同步开启复制备份日志
repl_backlog_active:0
# 备份日志的循环缓冲区的大小
repl_backlog_size:1048576
# 备份日志缓冲区中的首个字节的复制偏移量
repl_backlog_first_byte_offset:0
# 备份日志的实际数据长度
repl_backlog_histlen:0
# 主从复制情况下可能会有的一些额外信息
# master_host：Redis主机的主机名或IP地址
# master_port：Redis主机监听的TCP端口
# master_link_status：链路状态（连接/断开
# master_last_io_seconds_ago：最近一次和Redis主机交互至今的消耗时间，以秒为单位
# master_sync_in_progress：表示Redis主机正在将数据同步至从机
# master_sync_left_bytes：在同步完成之前，还剩余的数据总量，以字节为单位
# master_sync_last_io_seconds_ago：在一次SYNC操作期间，最近一次传输数据的I/O操作至今的消耗时间，以秒为单位
# master_link_down_since_seconds：从链路断开至今的时间，以秒为单位
```

# CPU信息
```
# 服务器耗费的系统 CPU
used_cpu_sys:26.932586
# 服务器耗费的用户 CPU
used_cpu_user:36.964424
# 后台进程耗费的系统 CPU
used_cpu_sys_children:0.000000
# 后台进程耗费的用户 CPU
used_cpu_user_children:0.000000
```

# Cluster集群信息
```
# 一个标志值，记录集群功能是否已经开启
cluster_enabled:0
```

# Keyspace键存储空间信息
```
# 数据库的key数量、处于有效时间内的key数量，过期key数量
db0:keys=2,expires=0,avg_ttl=0
```