[TOC]

# 1、 环境需要
```
centos7
默认的 python ，应该是2.7.x
```

# 2、安装pip
```
# 首先安装epel扩展源：
yum -y install epel-release
# 更新完成之后，就可安装pip：
yum -y install python-pip
# 安装完成之后清除cache：
yum clean all

```

# 3、 安装redisLive依赖
```
# tornado 
pip install tornado
# redis.py （此处一定要注意redis.py的版本）
pip install redis==2.10.6
# python-dateutil 
pip install python-dateutil


# 如果提示你要升级pip，就升级一下
You are using pip version 8.1.2, however version 19.0.2 is available.
You should consider upgrading via the 'pip install --upgrade pip' command.

```

# 4、下载redisLive
```
wget https://github.com/nkrode/RedisLive/archive/master.zip
mv master.zip redisLive.zip
# 解压
unzip redisLive.zip -d /usr/local/redisLive
# 没有wget这个命令怎么办？百度一下怎么在centos7下面安装wget。
```
# 5、 配置
```
cd  /usr/local/redisLive/RedisLive-master/src
cp redis-live.conf.example redis-live.conf
# redis-live.conf配置文件示例内容如下 
# RedisServers 要监控的节点，里面可以配置密码 "password" : "some-password"
# RedisStatsServer redis方式存储监控数据，建议是独立的redis
# 启动redis：/usr/local/redis/bin/redis-server --protected-mode no 默认端口6379
# SqliteStatsStore sqlite方式存储监控数据
# "DataStoreType" : "sqlite" 切换监控数据的存储模式
{
        "RedisServers":
        [ 
                {
                        "server": "192.168.100.242",
                        "port" :6381
                },
                {
                        "server": "192.168.100.242",
                        "port" : 6382
                }
        ],

        "DataStoreType" : "redis",

        "RedisStatsServer":
        {
                "server" : "192.168.100.242",
                "port" :6379
        },

        "SqliteStatsStore" :
        {
                "path": "to your sql lite file"
        }
}
```

# 5、 运行redisLive
```
cd /usr/local/redisLive/RedisLive-master/src

# 运行数据采集
/usr/local/redisLive/RedisLive-master/src/redis-monitor.py --duration=120
# 运行web控制台
/usr/local/redisLive/RedisLive-master/src/redis-live.py
# web gui页面
http://192.168.100.242:8888/index.html
```

# 6、运行时可能的异常
```
# ImportError: No module named _sqlite3
解决办法：
```