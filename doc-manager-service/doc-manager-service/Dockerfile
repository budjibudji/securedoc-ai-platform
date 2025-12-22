# Etape 1 : On utilise une image avec Maven pour construire le .jar
FROM maven:3.8.5-openjdk-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
# On compile le projet et on crée le JAR
RUN mvn clean package -DskipTests

# Etape 2 : IMAGE CORRIGÉE (Eclipse Temurin au lieu de OpenJDK Slim)
FROM eclipse-temurin:17-jre
WORKDIR /app
# On récupère le JAR construit à l'étape 1
COPY --from=builder /app/target/*.jar app.jar
# La commande pour lancer l'app
ENTRYPOINT ["java", "-jar", "app.jar"]