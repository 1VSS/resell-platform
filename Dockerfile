 FROM openjdk:23-jdk

 COPY target/resell-platform-0.0.1-SNAPSHOT.jar resell-platform-0.0.1-SNAPSHOT.jar

 ENTRYPOINT ["java", "-jar", "/resell-platform-0.0.1-SNAPSHOT.jar"]
