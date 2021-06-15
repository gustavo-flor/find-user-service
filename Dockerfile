FROM adoptopenjdk/openjdk11:alpine-jre
LABEL maintainer="Gustavo Flôr <ogustaflor@gmail.com>"

ENV JAVA_OPTS=""
ENV SPRING_OPTS=""

WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup && chown -R appuser:appgroup /app

USER appuser

COPY --chown=appuser:appgroup /target/*.jar /app/find-user-service.jar

EXPOSE 8080

ENTRYPOINT exec java $JAVA_OPTS $SPRING_OPTS -jar find-user-service.jar $0 $@