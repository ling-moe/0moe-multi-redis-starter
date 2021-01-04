# 0moe-multi-redis-starter
> 本项目提供了一个可同时操作多个库的redis终端，并对redis终端进行了封装，简化操作复杂度。

本项目基于spring-boot、spring-boot-starter-data-redis，内置lettuce和jedis库，提供了一个可同时操作多个库的redis终端并简化操作复杂度。

## Getting Started 使用指南

### Prerequisites 项目使用条件

你需要安装什么软件以及如何去安装它们。

- Maven 3.6及更高版本
- Java 8 及更高版本

### Installation 安装

```shell
# 拉取仓库
git clone https://github.com/ling-moe/0moe-multi-redis-starter.git
cd ./0moe-multi-redis-starter
# 安装到本地
mvn clean install -Dmaven.test.skip=true
```

### Usage example 使用示例

1. 在maven中添加依赖

```xml
<dependency>
	<groupId>cn.lm</groupId>
    <artifactId>0moe-multi-redis-starter</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

2. 在启动类上添加注解

```java
@Enable0moeMultiDbRedis
```

注：其余配置按照使用spring-boot-starter-data-redis进行配置即可，默认库以配置文件中配置的库为准

3. 在代码中使用

```java
    @Autowired
    private RedisHelper redisHelper;

    public void test(){
        // 使用默认库
        redisHelper.strSet("lm:key", "0moe");
        // 使用指定库
        RedisUtil.db(3).strSet("lm:key", "0moe");
    }
```

## Contributing 贡献指南

请先fork到本地，然后切换到dev分支，提pr请求至dev分支

## Authors 关于作者

* **yukdawn** - *Initial work*

## License 授权协议

这个项目 Apache-2.0 License 协议， 请点击 [LICENSE.md](LICENSE.md) 了解更多细节。