# 测试管理服务
`Test-Manager Service` 是猪齿鱼核心服务之一 ，该服务是Choerodon微服务框架的测试管理中心。它的主要功能包括测试用例管理、测试循环、测试分析等。

## 特点
- **测试用例**（创建、查看和编辑测试用例、测试用例树、excel导入和导出等）
- **测试计划**（创建测试循环、测试阶段以及批量克隆循环等）
- **测试执行**（执行测试、搜索执行、记录步骤结果、查看执行测试详情、删除执行）
- **自定义状态**（状态列表，创建状态）
- **自动化测试**（执行自动化测试，查看测试结果）
- **设置**（自定义状态）

## 依赖
- Java8
- mysql 5.6+
- redis 4.0+
- [File Service](https://github.com/choerodon/file-service.git)
- [go-register-server](https://github.com/choerodon/go-register-server.git)
- [agile-service](https://github.com/choerodon/agile-service.git)
- [Iam Service](https://github.com/choerodon/iam-service.git)
- [DevOps Service](https://github.com/choerodon/devops-service.git)
- [Redis](https://redis.io)
- [MySQL](https://www.mysql.com)


## 服务配置

- `application.yml`
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/test_manager_service?useUnicode=true&characterEncoding=utf-8&useSSL=false&useInformationSchema=true&remarks=true
    username: choerodon
    password: 123456
  aop:
    auto: true
  http:
    encoding:
      charset: UTF-8
      force: true
      enabled: true
  redis:
    host: localhost
    port: 6379
  servlet:
    multipart:
      max-file-size: 30MB
      max-request-size: 30MB
choerodon:
  saga:
    consumer:
      enabled: true # 是否启用消费端
      thread-num: 5  # 消费线程数
      max-poll-size: 200 # 每次拉取的最大消息数量
      poll-interval-ms: 1000 # 拉取消息的间隔(毫秒)，默认1000毫秒
  schedule:
    consumer:
      enabled: true # 启用任务调度消费端
      thread-num: 1 # 任务调度消费线程数
      poll-interval-ms: 1000 # 拉取间隔，默认1000毫秒
eureka:
  instance:
    preferIpAddress: true
    leaseRenewalIntervalInSeconds: 1
    leaseExpirationDurationInSeconds: 3
  client:
    serviceUrl:
      defaultZone: http://localhost:8000/eureka/
    registryFetchIntervalSeconds: 1
mybatis:
  mapperLocations: classpath*:/mapper/*.xml
  configuration:
    mapUnderscoreToCamelCase: true
feign:
  hystrix:
    shareSecurityContext: true
    command:
      default:
        execution:
          isolation:
            thread:
              timeoutInMilliseconds: 30000
ribbon:
  ConnectTimeout: 5000
  ReadTimeout: 5000
logging:
  level:
    root: info
    io.choerodon.test.manager: debug
```

- `bootstrap.yml`
```yaml
server:
  port: 8093
spring:
  application:
    name: test-manager-service
  cloud:
    config:
      failFast: true
      retry:
        maxAttempts: 6
        multiplier: 1.5
        maxInterval: 2000
      uri: localhost:8010
      enabled: false
  mvc:
    static-path-pattern: /**
  resources:
    static-locations: classpath:/static,classpath:/public,classpath:/resources,classpath:/META-INF/resources,file:/dist
management:
  server:
    port: 8094
  endpoints:
    web:
      exposure:
        include: '*'
  endpoint:
    health:
      show-details: "ALWAYS"
```

## 安装和启动步骤

- 运行 `eureka-server`，[代码在这里](https://code.choerodon.com.cn/choerodon-framework/eureka-server.git)。


- 拉取当前项目到本地
```shell
  git clone https://code.choerodon.com.cn/choerodon-agile/test-manager-service.git
```

- 在Mysql数据库创建一个 `test_manager_service` 数据库

```sql
CREATE USER 'choerodon'@'%' IDENTIFIED BY "123456";
CREATE DATABASE test_manager_service DEFAULT CHARACTER SET utf8;
GRANT ALL PRIVILEGES ON test_manager_service.* TO choerodon@'%';
FLUSH PRIVILEGES;
```
- 在 `test_manager_service` 项目根目录下创建 `init-local-database.sh` 数据初始化脚本文件


- 执行数据库初始化脚本

```sh
sh init-local-database.sh
```

- 启动项目，在根目录文件下运行 `manager-service` 项目：

```sh
mvn spring-boot:run
```
>或者在本地集成环境中运行 `SpringBoot` 启动类
`\src\main\java\io\choerodon\test\manager\TestManagerServiceApplication.java`



## 报告问题
如果你发现任何缺陷或者bugs，请在[issue](https://github.com/choerodon/choerodon/issues/new?template=issue_template.md)上面描述并提交给我们。

## 贡献
我们十分欢迎您的参与！ [Follow](https://github.com/choerodon/choerodon/blob/master/CONTRIBUTING.md) 去获得更多关于提交贡献的信息。



