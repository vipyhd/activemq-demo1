[TOC]

> 参考项：https://github.com/openresty/lua-nginx-module#installation

# 安装依赖项
```
yum -y install gcc zlib zlib-devel pcre-devel openssl openssl-devel

# lua_jit
wget http://luajit.org/download/LuaJIT-2.0.5.tar.gz 
tar -xvf LuaJIT-2.0.5.tar.gz
cd LuaJIT-2.0.5
make install
```

# 下载安装nginx和依赖项
```
cd /netease
# nginx
wget http://nginx.org/download/nginx-1.14.2.tar.gz
tar -xvf nginx-1.14.2.tar.gz
# ndk
wget https://github.com/simplresty/ngx_devel_kit/archive/v0.3.0.zip 
unzip v0.3.0.zip 
# ngx_lua 模块
wget https://github.com/openresty/lua-nginx-module/archive/v0.10.14.zip
unzip v0.10.14.zip

# 安装nginx
cd nginx-1.14.2
./configure --prefix=/usr/local/nginx-1.14.2 \
         --add-module=../ngx_devel_kit-0.3.0 \
         --add-module=../lua-nginx-module-0.10.14
```

# 测试lua模块是否安装成功
## 0、 创建一个文件夹存储lua脚本
```
mkdir /usr/local/nginx-1.14.2/lua_scripts
```

## 1、创建mydata.lua
```
-- mydata.lua
 local _M = {}

 local data = {
     dog = 3,
     cat = 4,
     pig = 5,
 }

 function _M.get_age(name)
     return data[name]
 end
 return _M
```

## 2、 nginx.conf文件
```
   lua_package_path "/usr/local/nginx-1.14.2/lua_scripts/?.lua;;";
    server {
        ...
        location /lua {
            content_by_lua_block {
               local mydata = require "mydata"
               ngx.say(mydata.get_age("dog"))
            }
        }
```

## 可能的错误
```
# 1、动态库找不到
./sbin/nginx: error while loading shared libraries: libluajit-5.1.so.2: cannot open shared object file: No such file or directory
# 解决办法：
echo "/usr/local/lib" > /etc/ld.so.conf.d/usr_local_lib.conf
ldconfig

# 2、warn提示
nginx: [alert] detected a LuaJIT version which is not OpenResty's; many optimizations will be disabled and performance will be compromised (see https://github.com/openresty/luajit2 for OpenResty's LuaJIT or, even better, consider using the OpenResty releases from https://openresty.org/en/download.html)
# 告诉你，你不要用这个luajit版本，可以用openresty提供的luajit优化版本，或者干脆直接用openresty

```

# 安装lua_redis
```
wget https://codeload.github.com/openresty/lua-resty-redis/zip/master
unzip master
cd lua-resty-redis-master
make && make install
```
