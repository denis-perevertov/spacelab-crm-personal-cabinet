FROM openjdk:17-alpine
EXPOSE 1488
VOLUME /tmp
COPY target/spacelab_personal_cabinet.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]