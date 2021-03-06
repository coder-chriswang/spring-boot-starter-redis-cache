# spring-boot-starter-redis-cache
这是基于spring cache开发的starter，已在生产环境多种业务情景下得以验证，请放心使用。

## 几个特点
- 实现了基于注解中的缓存时间设置
- 使用fastJson进行序列化，在时间和空间上更快，更节省
- 解决了原始key值乱码无法很好地查阅的问题

## 使用步骤
- 1、下载源码，install至本地仓库
- 2、本地工程pom文件中引入：
```xml
    <dependency>
	<groupId>net.ytoframework</groupId>
	<artifactId>yto-framework-starter-redis-cache</artifactId>
	<version>1.0.0</version>
    </dependency>
```
- 3、配置文件中增加redis config:
```properties
    spring.redis.host=localhost
    spring.redis.port=6379
    spring.redis.pool.max-idle=8
    spring.redis.pool.min-idle=0
    spring.redis.pool.max-active=8
    spring.redis.pool.max-wait=-1
    spring.redis.database=1
```
- 4、一般来说，我们习惯在serviceImpl类及接口实现方法上增加注解,Controller类同样可以加：
方法上用@分割，后面加上过期的时间，单位为秒
```java
    @Cacheable(value = "UserMacRelationService.findAllUserMacInfo@120", keyGenerator = "keyGenerator")
```
# 联系
- Chris chris_coder@163.com
