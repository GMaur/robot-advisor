FROM openjdk:8-jre-alpine3.7

RUN mkdir -p /usr/app
WORKDIR /usr/app
COPY target/*jar /usr/app/app.jar
EXPOSE 8082
CMD ["java", "-jar", "app.jar"]