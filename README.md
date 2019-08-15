# test-manager-service
`Test-Manager Service` is one of choerodon core service，the service is the test management center for the Choerodon microservices framework。Its main functions include test case management, test cycle, test analysis, etc.

## Feature
- **Test Case**（Create, view, and edit test cases, test case trees, excel imports/exports, and so on）
- **Test Plan**（Create test cycles, test phases, batch clone cycles, and more）
- **Test Execution**（Execute test, search execution, record step results, view execution details, delete execution）
- **Custom State**（State list, create state）
- **Automated Test**（Perform automated tests，View test results）
- **Setting**（Custom State）

## Dependency
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


## Service Config
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
        thread-num: 5 # saga Message consumption thread pool size
        max-poll-size: 200 # Maximum number of messages per pull
        enabled: true # Start consumer
        poll-interval-ms: 1000 # Pull interval, default 1000 ms
    schedule:
      consumer:
        enabled: true # Enable the task scheduling consumer
        thread-num: 1 # Task scheduling consumes the number of threads
        poll-interval-ms: 1000 # Pull interval, default 1000 ms
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

## Installation and startup steps

- Run `eureka-server`，[Coding is here](https://code.choerodon.com.cn/choerodon-framework/eureka-server.git)。


- Pull the current project to the loca
```shell
  git clone https://code.choerodon.com.cn/choerodon-agile/test-manager-service.git
```

- Create a database named `test_manager_service` in the Mysql database

```sql
CREATE USER 'choerodon'@'%' IDENTIFIED BY "123456";
CREATE DATABASE test_manager_service DEFAULT CHARACTER SET utf8;
GRANT ALL PRIVILEGES ON test_manager_service.* TO choerodon@'%';
FLUSH PRIVILEGES;
```
- Create `init-local-database.sh` data initialization script file in `test_manager_service` project root directory


- Execute the database initialization script

```sh
sh init-local-database.sh
```

- Startup `manager-service` project，run the cmd ：

```sh
mvn spring-boot:run
```
>Or in a local integration environment run the  `SpringBoot` Startup class
`\src\main\java\io\choerodon\test\manager\TestManagerServiceApplication.java`



## Report Problems
If you find any defects or bugs，Please describe it on[issue](https://github.com/choerodon/choerodon/issues/new?template=issue_template.md)and submit it to us.

## How to Contribute
Push requests are welcome! [Follow](https://github.com/choerodon/choerodon/blob/master/CONTRIBUTING.md) to know for more information on how to contribute.