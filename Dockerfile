# syntax=docker/dockerfile:1
FROM maven:3.6.1-jdk-11

ARG JAR_FILE=target/*.jar
WORKDIR /app
ADD ./target/tsar.planogram.jar /app/tsar.planogram.jar
#Java execution
ENV JAVA_OPTS="-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp -XX:+ExitOnOutOfMemoryError -Xms800M -Xmx800m"
ENV _JAVA_OPTIONS="-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp -XX:+ExitOnOutOfMemoryError -Xms800M -Xmx800m"

EXPOSE 3100
ENTRYPOINT ["java", "-jar", "/app/tsar.planogram.jar"]