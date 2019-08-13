FROM registry.cn-shanghai.aliyuncs.com/choerodon-tools/javabase:0.8.2
COPY app.jar /test-manager-service.jar
CMD java $JAVA_OPTS $SKYWALKING_OPTS -jar /test-manager-service.jar