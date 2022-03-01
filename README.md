## zzk:rpc

![GitHub](https://img.shields.io/github/license/CN-GuoZiyang/My-RPC-Framework)
![jdk](https://img.shields.io/static/v1?label=oraclejdk&message=11&color=blue)

The project is a tiny framework of RPC in the PRC learning process.

> 学习RPC过程中实现的简单RPC框架
### 当前实现特性
- 实现了两种通信方式：直接使用JDK socket和netty方式
- 实现了多种序列化方式：Json、Hessian、Kryo、Protostuff
- 支持注册中心/服务发现、序列化器、负载均衡策略可配置，非硬编码
- 实现注解进行服务自动注册

### 待添加特性：
- 支持配置文件进行服务自动注册
- 引入Spring，通过注解注册服务
- 设置gzip压缩
- 支持zookeeper做注册中心，最后能支持注册中心可配置
- 处理同一个接口可有多个类实现