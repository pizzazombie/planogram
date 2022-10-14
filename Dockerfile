# syntax=docker/dockerfile:1
FROM registry.tools.3stripes.net/base-images/alpine_java-11

ARG JAR_FILE=target/*.jar
WORKDIR /app
ADD ./target/tsar.planogram.jar /app/tsar.planogram.jar
ADD ./src/main/resources/keystore /app/keystore
#Java execution
ENV JAVA_OPTS="-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp -XX:+ExitOnOutOfMemoryError -Xms800M -Xmx1500m"
ENV _JAVA_OPTIONS="-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp -XX:+ExitOnOutOfMemoryError -Xms800M -Xmx1500m"

EXPOSE 3100
ENTRYPOINT ["java", "-jar", "/app/tsar.planogram.jar"]