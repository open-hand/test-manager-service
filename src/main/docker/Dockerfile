FROM registry.cn-hangzhou.aliyuncs.com/choerodon-tools/javabase:0.6.0
COPY app.jar /test-manager-service.jar
ENTRYPOINT exec java $JAVA_OPTS -jar test-manager-service.jar