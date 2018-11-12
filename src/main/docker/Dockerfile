FROM registry.saas.hand-china.com/tools/javabase:0.7.0
COPY app.jar /test-manager-service.jar
ENTRYPOINT exec java $JAVA_OPTS -jar test-manager-service.jar