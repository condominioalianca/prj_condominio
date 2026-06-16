# Guia de Implantação e Configuração com Docker

Este guia descreve o passo a passo para compilar a aplicação, construir a imagem Docker, exportar/importar a imagem (caso queira mover para outro servidor) e executar o contêiner com as variáveis de ambiente necessárias.

---

## Passo 1: Compilar o Backend (Gerar o .jar)

Antes de construir a imagem Docker, precisamos gerar o arquivo `.jar` atualizado da aplicação.

### Opção A: Pelo IntelliJ IDEA (Recomendado)
1. No lado direito do IntelliJ, abra a aba **Maven** (ou clique em `View -> Tool Windows -> Maven`).
2. Expanda o projeto **novaalianca** e depois expanda a pasta **Lifecycle**.
3. (Opcional) Para pular os testes, clique no ícone de **"Skip Tests"** (geralmente um frasco de laboratório com um risco vermelho ou um raio) na barra superior da janela do Maven.
4. Dê um duplo clique em **clean**.
5. Dê um duplo clique em **package**.
6. O arquivo `novaalianca-1.0.2-SNAPSHOT.jar` será gerado automaticamente na pasta `target/` do projeto.

### Opção B: Pelo Terminal
1. Abra o terminal (PowerShell ou Prompt de Comando) na pasta do backend:
   ```bash
   cd c:\GIT2\prj_condominio\back_end\novaalianca
   ```
2. Execute a compilação garantindo o JDK 17 (se necessário, ajuste a variável `JAVA_HOME` temporariamente):
   - **No CMD / Windows**:
     ```cmd
     set JAVA_HOME=C:\Program Files\Java\jdk-17.0.18
     .\mvnw.cmd clean package -DskipTests
     ```
   - **No PowerShell**:
     ```powershell
     $env:JAVA_HOME="C:\Program Files\Java\jdk-17.0.18"
     .\mvnw.cmd clean package -DskipTests
     ```
3. Isso irá gerar o arquivo `novaalianca-1.0.2-SNAPSHOT.jar` dentro da pasta `target/`.

---

## Passo 2: Construir a Imagem Docker

Com o `.jar` gerado, podemos construir a imagem Docker localmente.

1. Na mesma pasta do `Dockerfile` (`c:\GIT2\prj_condominio\back_end\novaalianca`), execute:
   ```bash
   docker build --build-arg JAR_FILE=target/novaalianca-1.0.2-SNAPSHOT.jar -t patrickmoura/novaalianca:1.0.2 .
   ```
2. Verifique se a imagem foi criada com sucesso:
   ```bash
   docker images | findstr patrickmoura/novaalianca
   ```

---

## Passo 3: Exportar a Imagem Docker (Opcional)

Se você precisa rodar essa imagem em outro servidor ou máquina que não possui o código-fonte para compilar, você pode exportar a imagem para um arquivo compactado `.tar`.

1. Exporte a imagem gerada:
   ```bash
   docker save -o novaalianca-image-1.0.2.tar patrickmoura/novaalianca:1.0.2
   ```
2. O arquivo `novaalianca-image-1.0.2.tar` estará pronto para ser transferido para outro servidor (via SCP, Pendrive, etc.).

---

## Passo 4: Importar a Imagem no Servidor de Destino

Na máquina ou servidor onde a aplicação de fato irá rodar:

1. Transfira o arquivo `.tar` e execute o comando para carregar a imagem no Docker local:
   ```bash
   docker load -i novaalianca-image-1.0.2.tar
   ```
2. Confirme que a imagem foi importada com sucesso:
   ```bash
   docker images
   ```

---

## Passo 5: Configurar e Executar (Docker Compose)

Recomendamos usar o **Docker Compose** para gerenciar a execução, pois ele simplifica a declaração das portas e variáveis de ambiente.

1. Crie ou copie o arquivo `docker-compose.yml` na pasta onde deseja rodar o serviço:
   ```yaml
   version: '3.8'

   services:
     novaalianca-backend:
       image: patrickmoura/novaalianca:1.0.2
       container_name: novaalianca-backend
       ports:
         - "8086:8086"
       environment:
         - DOCKER=true
         - APP_PROFILE=dev
         - DB_URL=jdbc:postgresql://host.docker.internal:5432/condominio
         - DB_USERNAME=postgres
         - DB_PASSWORD=140908
         - INTER_CERT_PATH=/etc/certs/CONDOMINIONOVAALIANCA.pfx # Altere para /etc/certs/Sandbox.pfx se necessário
         - INTER_CERT_PASSWORD=140908
         - INTER_CLIENT_ID=61851b55-cd4c-4932-bfb4-0b38ea941aad
         - INTER_CLIENT_SECRET=87c6f9e2-e497-4611-bd98-d55b0ea085be
         - MAIL_USERNAME=condominionovaaliancasbc
         - MAIL_PASSWORD=xuvjwatpamgpaywy
       extra_hosts:
         - "host.docker.internal:host-gateway"
       restart: always
   ```

   > [!NOTE]
   > Certifique-se de que os arquivos de certificados fisicamente existam ou estejam mapeados se quiser montar externamente.
   > Como a imagem foi construída copiando a pasta de certificados local para `/etc/certs`, os certificados `CONDOMINIONOVAALIANCA.pfx` e `Sandbox.pfx` já estão embutidos na imagem.

2. Inicie o contêiner:
   ```bash
   docker compose up -d
   ```
3. Acompanhe a inicialização pelos logs para garantir que o banco e o certificado foram encontrados:
   ```bash
   docker logs -f novaalianca-backend
   ```
