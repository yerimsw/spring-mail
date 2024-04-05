# Java 실행 환경
FROM openjdk:11

# 애플리케이션 파일 복사
COPY build/libs/mail-0.0.1-SNAPSHOT-plain.jar app.jar

# 애플리케이션 실행
ENTRYPOINT ["java","-jar","/app.jar"]
