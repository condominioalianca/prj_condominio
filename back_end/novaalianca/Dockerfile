FROM eclipse-temurin:17-jre
VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
COPY src/main/resources/certs /etc/certs
ENV TZ=America/Sao_Paulo
ENV DOCKER=true
EXPOSE 8086
ENTRYPOINT ["java","-jar","/app.jar"]

#Comando para gerar Imagem
#docker build --build-arg JAR_FILE=target/*.jar -t patrickmoura/novaalianca .
#Comando para Criar container e expor porta 8086
#docker run --name backnovaalianca  --restart=always -d -p 8086:8086 patrickmoura/novaalianca

# Comandos para dar o Push
# docker login
# docker push patrickmoura/novaalianca