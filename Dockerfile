# Multi-stage build for Spring Boot 3.5 + Gradle 8.14 + Java 21

# Build stage
FROM amazoncorretto:21-alpine AS builder

# 작업 디렉토리 설정
WORKDIR /app

# Gradle wrapper와 설정 파일들 복사
COPY gradle/ gradle/
COPY gradlew gradlew.bat settings.gradle ./
COPY build.gradle ./

# Gradle wrapper 실행 권한 부여
RUN chmod +x gradlew

# 의존성 다운로드 (캐시 활용을 위해 소스코드 복사 전에 실행)
RUN ./gradlew dependencies --no-daemon

# 소스코드 복사
COPY src/ src/

# 애플리케이션 빌드
RUN ./gradlew clean build --no-daemon -x test

# Runtime stage
FROM amazoncorretto:21-alpine AS runtime

# 필요한 패키지 설치
RUN apk add --no-cache \
    tzdata \
    curl

# 시간대 설정
ENV TZ=Asia/Seoul

# 애플리케이션 사용자 생성
RUN addgroup -g 1001 appgroup && \
    adduser -u 1001 -G appgroup -s /bin/sh -D appuser

# 작업 디렉토리 설정
WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 애플리케이션 디렉토리 소유권 변경
RUN chown -R appuser:appgroup /app

# 비특권 사용자로 전환
USER appuser

# 포트 노출
EXPOSE 8080

# JVM 옵션 설정
ENV JAVA_OPTS="-Xmx1024m -Xms512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# 애플리케이션 실행
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]