# =========================================================================
# ESTÁGIO 1: BUILD (Compilação e empacotamento da aplicação)
# =========================================================================

FROM maven:3.9.6-eclipse-temurin-21-jammy AS builder

WORKDIR /build

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests

# =========================================================================
# ESTÁGIO 2: RUNTIME (Execução segura e otimizada em produção)
# =========================================================================

FROM gcr.io/distroless/java21-debian12:20260601

WORKDIR /app

USER nonroot:nonroot

COPY --from=builder --chown=nonroot:nonroot /build/target/*.jar app.jar

ENV JAVA_TOOL_OPTIONS="-XX:+UseG1GC -XX:+UseStringDeduplication"

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=15s --retries=3 \
  CMD ["java", "-jar", "/app/app.jar", "--health"]

ENTRYPOINT ["java", "-jar", "app.jar"]