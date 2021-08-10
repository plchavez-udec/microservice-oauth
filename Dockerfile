FROM openjdk:8
VOLUME /tmp
EXPOSE 9100
ADD ./target/referencia-0.0.1-SNAPSHOT.jar oauth.jar
ENTRYPOINT ["java","-jar","oauth.jar"]
