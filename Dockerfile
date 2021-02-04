FROM gradle:jdk8 as builder
WORKDIR /app
COPY src ./src
COPY build.gradle .
COPY gradle.properties .
COPY settings.gradle .
RUN gradle build

FROM amazoncorretto:8u282
LABEL application.name="Feature Access Manager"
LABEL application.author="Gunardy Sutanto<gunardy.sutanto@gmail.com>"
COPY --from=builder /app/build/libs/feature-access-manager-*.jar /feature-accaess-manager.jar
ENTRYPOINT ["java","-jar","/feature-accaess-manager.jar"]