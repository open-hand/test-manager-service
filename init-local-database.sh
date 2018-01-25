#!/bin/sh

#if [ ! -f target/hap-liquibase-tools.jar ]
#then
#    mkdir -p target
#    curl http://nexus.saas.hand-china.com/content/repositories/rdc/com/hand/hap/cloud/hap-liquibase-tools/1.0/hap-liquibase-tools-1.0.jar -o target/hap-liquibase-tools.jar
#fi
#java -Dspring.datasource.url="jdbc:mysql://localhost:3306/hap_deployment_service?useUnicode=true&characterEncoding=utf-8&useSSL=false" \
#    -Dspring.datasource.username=hapcloud \
#    -Dspring.datasource.password=handhand \
#    -Ddata.init=true -Ddata.drop=true \
#    -Ddata.dir=src/main/resources \
#    -jar target/hap-liquibase-tools.jar
