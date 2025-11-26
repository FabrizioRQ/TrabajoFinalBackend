FROM openjdk:17.0.2-jdk-oracle

ENV TZ=America/Lima

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} backend.jar

CMD apt-get update -y

EXPOSE 8080

ENTRYPOINT ["java", "-Xmx2048M", "-jar", "/backend.jar"]
