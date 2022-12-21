FROM ringcentral/jdk:11.0.11-alpine3.14
RUN set -eux && apk add --no-cache curl tzdata

ENV TZ='Asia/Shanghai'
ADD build/libs/*.jar app.jar
RUN sh -c 'touch /app.jar'
ENTRYPOINT [ "sh", "-c", "java -jar /app.jar" ]
EXPOSE 8080