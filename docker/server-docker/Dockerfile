# Java 17을 사용하는 가벼운 베이스 이미지
FROM eclipse-temurin:17-jdk-alpine

# 작업 디렉토리 설정
WORKDIR /app

# 환경 변수는 docker-compose에서 주입됨
# JAR 파일 실행
CMD ["java", "-jar", "/app.jar"]