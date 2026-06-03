# syntax=docker/dockerfile:1

# Estágio 1: Baixar dependências (usando cache do Maven)
FROM eclipse-temurin:25-jdk-alpine as deps

WORKDIR /build

# Copiar o wrapper do Maven com permissão de execução
COPY --chmod=0755 mvnw mvnw
COPY .mvn/ .mvn/

# Baixar dependências offline (cache para builds futuros)
RUN --mount=type=bind,source=pom.xml,target=pom.xml \
    --mount=type=cache,target=/root/.m2 \
    ./mvnw dependency:go-offline -DskipTests

# Estágio 2: Compilar e empacotar a aplicação
FROM deps as package

WORKDIR /build

COPY ./src src/

RUN --mount=type=bind,source=pom.xml,target=pom.xml \
    --mount=type=cache,target=/root/.m2 \
    ./mvnw package -DskipTests && \
    mv target/$(./mvnw help:evaluate -Dexpression=project.artifactId -q -DforceStdout)-$(./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout).jar target/app.jar

# Estágio 3: Extrair as camadas do JAR (layertools do Spring Boot)
FROM package as extract

WORKDIR /build

RUN java -Djarmode=layertools -jar target/app.jar extract --destination target/extracted

# Estágio 4: Imagem final (somente JRE)
FROM eclipse-temurin:25-jre-alpine AS final

# Criar usuário não-root
ARG UID=10001
RUN adduser \
    --disabled-password \
    --gecos "" \
    --home "/nonexistent" \
    --shell "/sbin/nologin" \
    --no-create-home \
    --uid "${UID}" \
    appuser
USER appuser

# Copiar as camadas extraídas (otimiza rebuilds)
COPY --from=extract build/target/extracted/dependencies/ ./
COPY --from=extract build/target/extracted/spring-boot-loader/ ./
COPY --from=extract build/target/extracted/snapshot-dependencies/ ./
COPY --from=extract build/target/extracted/application/ ./

# Expor a porta que sua aplicação vai usar (ajuste conforme necessário)
EXPOSE 8889

# Ponto de entrada usando o JarLauncher (Spring Boot)
ENTRYPOINT [ "java", "org.springframework.boot.loader.launch.JarLauncher" ]