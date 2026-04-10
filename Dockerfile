FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY src src

RUN chmod +x gradlew
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app

RUN groupadd -g 10001 appgroup && \
    useradd -u 10001 -g 10001 -m -s /usr/sbin/nologin appuser

COPY --from=builder /app/build/libs/*.jar app.jar
RUN chown -R 10001:10001 /app

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]