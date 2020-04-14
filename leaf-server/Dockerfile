FROM openjdk
MAINTAINER ryoma ryomahan1996@gmail.com

COPY ./wait-for /bin/wait-for
COPY ./target/leaf.jar /leaf.jar

ENTRYPOINT ["java", "-jar", "/leaf.jar"]
