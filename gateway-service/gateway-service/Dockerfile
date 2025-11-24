# Etape 1 : On utilise une image avec Maven pour construire le .jar
FROM maven:3.8.5-openjdk-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
# On compile le projet et on crée le JAR (skip tests pour aller vite au début)
RUN mvn clean package -DskipTests

# Etape 2 : On crée l'image finale légère (juste le JRE pour lancer le code)
FROM openjdk:17-jdk-slim
WORKDIR /app
# On récupère le JAR construit à l'étape 1
COPY --from=builder /app/target/*.jar app.jar
# La commande pour lancer l'app
ENTRYPOINT ["java", "-jar", "app.jar"]