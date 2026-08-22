# Plano de Migração e Modernização: Java 25 & Spring Boot 4.1.1
**Projeto:** Condomínio Aliança (Backend)  
**Versão Alvo:** 2.0.0.0  
**Data:** 22 de Agosto de 2026  

---

## 1. Resumo Executivo

Este plano de migração descreve a elevação da arquitetura do backend do projeto Condomínio Aliança. O projeto foi migrado de **Java 17 / Spring Boot 2.7.9** diretamente para a vanguarda tecnológica do **Java 25 LTS e Spring Boot 4.1.1**.

### Principais Objetivos Alcançados:
1.  **Atualização de Infraestrutura:** Uso de Maven 3.9.9 (via wrapper) e runtime do Java 25 no Docker.
2.  **Injeção de Dependência Moderna:** Remoção completa da anotação `@Autowired` em favor de construtores implícitos gerados pelo Lombok (`@RequiredArgsConstructor`).
3.  **Configurações YAML:** Conversão de arquivos flat `.properties` para estrutura hierárquica `.yml`.
4.  **Monitoramento & Configuração Dinâmica:** Integração do Spring Actuator expondo `/actuator/health` e suporte a `@RefreshScope` no endpoint `/actuator/refresh` para alteração de variáveis em tempo real sem redeploy.
5.  **Modernização da Segurança (OAuth2/Security 7):** Substituição do pacote legado e removido `spring-security-oauth2-autoconfigure` por uma arquitetura nativa com `spring-boot-starter-oauth2-resource-server` e criação de um Custom Token Controller para garantir compatibilidade reversa com o front-end.
6.  **Migração de Namespace:** Migração total de imports Java EE (`javax.*`) para Jakarta EE (`jakarta.*`).
7.  **Resolução de Incompatibilidades Java 25 & Hibernate 6:** Correção de construtores depreciados de `Locale` e `URL`, e ajuste na anotação `@Type` do Hibernate.
8.  **Migração do JavaMail & Activation:** Atualização dos pacotes de envio de e-mails para o ecossistema Jakarta.

---

## 2. Estrutura de Commits Realizada (Branch Atual)

Para garantir rastreabilidade e segurança, a migração foi executada de forma incremental:

*   **Commit 1 (Build & Runtime):**
    *   pom.xml: Versão do projeto alterada para `2.0.0.0`, propriedade `<java.version>` atualizada para `25`.
    *   maven-wrapper.properties: Atualizado para Maven 3.9.9.
    *   Dockerfile: Imagem de runtime atualizada para `eclipse-temurin:25-jre`.
*   **Commit 2 (Configuração & Monitoramento):**
    *   Conversão dos 4 arquivos properties (`application`, `dev`, `prd`, `test`) para YAML.
    *   Adição das dependências do Actuator e Spring Cloud Context.
    *   Exposição dos endpoints `/actuator/health` e `/actuator/refresh`.
*   **Commit 3 (Injeção de Dependência):**
    *   Substituição sistemática em 41 arquivos de `@Autowired` por atributos `private final` combinados com `@RequiredArgsConstructor` do Lombok.
*   **Commit 4 (Core Frameworks & Segurança):**
    *   Parent do Spring Boot no pom.xml elevado para `4.1.1`.
    *   Substituição da dependência `spring-security-oauth2-autoconfigure` por `spring-boot-starter-oauth2-resource-server`.
    *   Migração global de `javax.*` para `jakarta.*` em 31 arquivos `.java`.
    *   Remoção das classes legadas `AuthorizationServerConfig.java` e `JwtTokenEnhancer.java`.
    *   Criação do controller `OAuthTokenController.java` para prover autenticação JWT transparente para o front-end.
    *   Simplificação e adequação de `ResourceServerConfig.java` e `WebSecurityConfig.java` ao padrão do Spring Security 7.
*   **Commit 5 (Correções do Java 25 & Hibernate 6):**
    *   Substituição de construtores depreciados de `Locale` e `URL` no Java 25.
    *   Remoção da anotação `@Type` legada do Hibernate em atributos binários para compatibilidade com o Hibernate 6.x.
*   **Commit 6 (Correções de Transações, Annotations e Lombok Builder):**
    *   Substituição de imports legados `javax.transaction.Transactional` por `jakarta.transaction.Transactional` em classes de serviços e controllers.
    *   Substituição do import `javax.annotation.Generated` por `jakarta.annotation.Generated` no metamodelo JPA `Usuario_.java`.
    *   Adição da anotação `@lombok.Builder.Default` nos campos inicializados inline das classes anotadas com `@Builder` (`CategoriaGasto.java`, `UsuarioDTO.java` e `Usuario.java`).
*   **Commit 7 (Correções do JavaMail & Activation Framework):**
    *   Substituição dos imports de e-mail `javax.mail.*` e ativação `javax.activation.*` por `jakarta.mail.*` e `jakarta.activation.*` no `EmailService.java`.

---

## 3. Detalhamento Técnico das Modificações

### 3.1. Injeção de Dependência por Construtor (Lombok)
Eliminamos a necessidade de acoplamento direto com o Spring usando `@Autowired` em campos privados (o que dificulta testes unitários e permite referências nulas). 

**Antes:**
```java
@Service
public class UnidadeService {
    @Autowired
    private UnidadeRepository unidadeRepository;
}
```

**Depois:**
```java
@Service
@RequiredArgsConstructor
public class UnidadeService {
    private final UnidadeRepository unidadeRepository;
}
```

### 3.2. Configurações Dinâmicas e Actuator
Configurado no `application.yml` para expor os endpoints de saúde e recarga de propriedades em tempo de execução:
```yaml
management:
  endpoints:
    web:
      exposure:
        include: "health,refresh"
```
Classes de serviço ou propriedades anotadas com `@RefreshScope` recarregarão seus valores do ambiente ao fazer uma chamada `POST` para `/actuator/refresh`.

### 3.3. Nova Arquitetura de Segurança (Spring Security 7 + Nimbus JWT)
Como a biblioteca obsoleta do Spring Boot 2.x foi descontinuada, implementamos um controlador customizado `/oauth/token` compatível com o fluxo `password` do front-end original.

#### Endpoint de Autenticação (`OAuthTokenController.java`):
*   Recebe requisições do tipo POST em `/oauth/token`.
*   Chama o `AuthenticationManager` do Spring Security para validar as credenciais.
*   Gera um token assinado por HMAC-SHA256 usando a biblioteca nativa **Nimbus JOSE** (inclusa no Starter de Resource Server).
*   Garante as claims idênticas exigidas pelo front-end: `userName`, `userId` e as `roles` (perfis do usuário).
*   Retorna exatamente a estrutura de resposta esperada:
    ```json
    {
      "access_token": "ey...",
      "token_type": "bearer",
      "expires_in": 86400,
      "scope": "read write",
      "userName": "Nome do Usuário",
      "userId": 1
    }
    ```

#### Filtro de Segurança (`ResourceServerConfig.java`):
*   Define as rotas públicas: `/oauth/token`, `/actuator/health`, `/swagger-ui/**`, `/h2-console/**`, etc.
*   Usa o `NimbusJwtDecoder` configurado com a chave secreta existente em `properties.getJwtSecret()`.
*   Configura um `JwtAuthenticationConverter` para extrair os perfis da claim `roles` do JWT e convertê-los em Authorities com prefixo `ROLE_`.

### 3.4. Resolução de Quebras de Compilação no Java 25 & Hibernate 6

Para que a aplicação seja compilada de forma limpa, resolvemos as seguintes incompatibilidades:

1.  **Construtor depreciado do Locale (Java 19+):**
    *   *Antes:* `new Locale("pt", "BR")` no `BoletoBuilder.java`.
    *   *Depois:* Substituído pela factory estática moderna `Locale.of("pt", "BR")`.
2.  **Construtor depreciado do URL (Java 20+):**
    *   *Antes:* `new URL(certPath).openStream()` no `RestTemplateUtil.java`.
    *   *Depois:* Substituído pela forma segura `java.net.URI.create(certPath).toURL().openStream()`.
3.  **Anotação `@Type` do Hibernate (Hibernate 6.x):**
    *   *Antes:* `@org.hibernate.annotations.Type(type = "org.hibernate.type.BinaryType")` em campos `byte[]` das entidades `BoletoNovaAlianca`, `Comprovante` e `Conciliacao`.
    *   *Depois:* Removido por completo. No Hibernate 6, atributos do tipo `byte[]` são mapeados implicitamente como LOBs/binários corretos no banco sem a necessidade desta anotação (que teve seu atributo `type` removido e resultava em falha na compilação).
4.  **JavaMail e Activation Namespaces:**
    *   *Antes:* `import javax.mail.*` e `import javax.activation.*` no `EmailService.java`.
    *   *Depois:* Migrado para `import jakarta.mail.*` e `import jakarta.activation.*`.

---

## 4. Configuração do Docker e Pipeline Jenkins

### 4.1. Dockerfile (Runtime Java 25)
O Dockerfile utiliza o JRE otimizado oficial do Eclipse Temurin para Java 25:
```dockerfile
FROM eclipse-temurin:25-jre
VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
COPY src/main/resources/certs /etc/certs
ENV TZ=America/Sao_Paulo
ENV DOCKER=true
EXPOSE 8086
ENTRYPOINT ["java","-jar","/app.jar"]
```

### 4.2. Sugestão de Pipeline Jenkins (Docker Agent)
Para que o Jenkins consiga realizar o build sem a necessidade de instalar localmente o JDK 25 e o Maven 3.9.9 no host (evitando poluição do ambiente), a recomendação é utilizar um agente Docker no Jenkinsfile:

```groovy
pipeline {
    agent {
        docker {
            image 'maven:3.9.9-eclipse-temurin-25'
            args '-v /root/.m2:/root/.m2' // Cache de dependências do Maven
        }
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Compile & Test') {
            steps {
                sh 'mvn -f back_end/novaalianca/pom.xml clean test'
            }
        }
        stage('Package') {
            steps {
                sh 'mvn -f back_end/novaalianca/pom.xml package -DskipTests'
            }
        }
        stage('Build Docker Image') {
            agent any // Exige docker instalado no host para gerar imagem
            steps {
                sh 'docker build --build-arg JAR_FILE=back_end/novaalianca/target/*.jar -t patrickmoura/novaalianca:latest back_end/novaalianca/'
            }
        }
    }
}
```

---

## 5. Plano de Validação e Homologação

### 5.1. Pré-Requisitos para Desenvolvimento Local
*   Instalar o **JDK 25** na máquina local do desenvolvedor e configurar a variável `JAVA_HOME`.
*   IDE atualizada (IntelliJ IDEA 2025+ ou VS Code com as extensões Java mais recentes) para suportar a sintaxe do Java 25 e Jakarta EE 10+.

### 5.2. Validação da Compilação
```powershell
./mvnw clean compile
```

### 5.3. Testes nos Endpoints do Actuator
1.  **Health Check:**
    *   `GET http://localhost:8086/actuator/health`
    *   Resposta esperada: `{"status":"UP"}`
2.  **Refresh Scope:**
    *   `POST http://localhost:8086/actuator/refresh`
    *   Resposta esperada: Lista de propriedades recarregadas (caso existam modificações).
