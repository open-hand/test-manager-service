#!/usr/bin/env bash
mkdir -p target
if [ ! -f target/choerodon-tool-liquibase.jar ]
then
    curl https://nexus.choerodon.com.cn/repository/choerodon-release/io/choerodon/choerodon-tool-liquibase/0.6.3.RELEASE/choerodon-tool-liquibase-0.6.3.RELEASE.jar -o target/choerodon-tool-liquibase.jar
fi
java -Dspring.datasource.url="jdbc:oracle:thin:@192.168.12.113:49161/xe" \
 -Dspring.datasource.username=test_manager_service \
 -Dspring.datasource.password=123456 \
 -Ddata.drop=false -Ddata.init=true \
 -Ddata.dir=src/main/resources \
 -jar target/choerodon-tool-liquibase.jar