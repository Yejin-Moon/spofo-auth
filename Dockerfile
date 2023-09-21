FROM openjdk:17-alpine

COPY ./build/libs/auth-0.0.1-SNAPSHOT.jar /usr/src/myapp/
CMD java -jar /usr/src/myapp/auth-0.0.1-SNAPSHOT.jar