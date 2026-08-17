# ============================================================
# user-service Dockerfile
# ビルドコンテキストはリポジトリルート（docker-compose.yml で指定）
# ============================================================

# ---------- ビルドステージ ----------
FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace

# common モジュールをローカルMavenリポジトリにインストール
COPY common common
RUN cd common && ./gradlew publishToMavenLocal --no-daemon

# user-service をビルド（bootJar で実行可能JARを生成）
COPY user-service user-service
RUN cd user-service && ./gradlew bootJar --no-daemon

# ---------- 実行ステージ ----------
FROM eclipse-temurin:25-jre AS runtime
WORKDIR /app

# ビルド成果物（実行可能JAR）をコピー
COPY --from=build /workspace/user-service/build/libs/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
