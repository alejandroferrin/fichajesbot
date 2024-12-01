FROM amazoncorretto:21-alpine
#FROM eclipse-temurin:21-jre-alpine

# Establecer el directorio de trabajo dentro del contenedor
WORKDIR /app

# Instalar dos2unix
RUN apk --no-cache add dos2unix

# Copiar el JAR de la aplicación al contenedor
COPY ./target/fichajesbot-0.1.0.jar app.jar

# Copiar el archivo application.properties al contenedor
#COPY application.properties application.properties

# Convertir los finales de línea de application.properties a formato Unix
#RUN dos2unix application.properties

# Comando por defecto para ejecutar la aplicación
CMD ["java","-Xmx500m", "-Xms100m", "-jar", "app.jar"]