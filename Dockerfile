# Estágio 1: Build da aplicação
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Estágio 2: Imagem final para execução
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta 8081 (conforme seu application.yml)
EXPOSE 8081

# Comando para iniciar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]