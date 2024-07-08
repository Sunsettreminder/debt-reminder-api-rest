# Usa una imagen base de Java
FROM openjdk:17-jdk-slim

# Establece el directorio de trabajo
WORKDIR /app

# Copia el archivo de inicio y el script de inicialización al contenedor
COPY scripts/start.sh /app/start.sh
COPY scripts/init.sql /app/init.sql
COPY target/*.jar /app/app.jar

# Da permisos de ejecución al script de inicio
RUN chmod +x /app/start.sh

# Comando para ejecutar el script de inicio
ENTRYPOINT ["/app/start.sh"]
