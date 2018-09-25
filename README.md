# Test-Manager Service
`Test-Manager Service` is the core service of Choerodon.  

## Features
- **Test Case Management**
- **Test Plan**
- **Test Execute Management**
- **Test Result Management**
- **Test Status Management**
This service is the management center of the Choerodon Microservices Framework. It`s main functions include configuration management, route management, and swagger management.

## Requirements
- Java8
- [File Service](https://github.com/choerodon/file-service.git)
- [Iam Service](https://github.com/choerodon/iam-service.git)
- [MySQL](https://www.mysql.com)
- [Kafka](https://kafka.apache.org)

## Installation and Getting Started

Create a `test_manager_service` database in MySQL：

```sql
CREATE USER 'choerodon'@'%' IDENTIFIED BY "123456";
CREATE DATABASE test_manager_service DEFAULT CHARACTER SET utf8;
GRANT ALL PRIVILEGES ON test_manager_service.* TO choerodon@'%';
FLUSH PRIVILEGES;
```
New file of `init-local-database.sh` in the root directory of the `test_manager_service` project：

```sh
mkdir -p target
if [ ! -f target/choerodon-tool-liquibase.jar ]
then
    curl http://nexus.choerodon.com.cn/repository/choerodon-release/io/choerodon/choerodon-tool-liquibase/0.6.3.RELEASE/choerodon-tool-liquibase-0.6.3.RELEASE.jar -o target/choerodon-tool-liquibase.jar
fi
java -Dspring.datasource.url="jdbc:mysql://localhost/manager_service?useUnicode=true&characterEncoding=utf-8&useSSL=false" \
 -Dspring.datasource.username=choerodon \
 -Dspring.datasource.password=123456 \
 -Ddata.drop=false -Ddata.init=true \
 -Ddata.dir=src/main/resources \
 -jar target/choerodon-tool-liquibase.jar
```

And executed in the root directory of the `manager-service` project：

```sh
sh init-local-database.sh
```
Then run the project in the root directory of the project：

```sh
mvn spring-boot:run
```

## Dependencies
- `go-register-server`: Register server
- `iam-service`：iam service
- `kafka`
- `mysql`: test_manager_service database
- `api-gateway`: api gateway server
- `gateway-helper`: gateway helper server
- `oauth-server`: oauth server
- `manager-service`: manager service
- `agile-service`: agile service

## Reporting Issues
If you find any shortcomings or bugs, please describe them in the  [issue](https://github.com/choerodon/choerodon/issues/new?template=issue_template.md).

## How to Contribute
Pull requests are welcome! [Follow](https://github.com/choerodon/choerodon/blob/master/CONTRIBUTING.md) to know for more information on how to contribute.