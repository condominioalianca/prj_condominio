FROM openjdk:17
VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
#ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -Dserver.port=8085 -jar /app.jar"]


#Comando para gerar Imagem
#docker build --build-arg JAR_FILE=target/*.jar -t patrickmoura/novaalianca .
#Comando para Criar container e expor porta 8085
#docker run --name backnovaalianca  --restart=always -d -p 8085:8085 patrickmoura/novaalianca
