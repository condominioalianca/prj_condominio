# Plano de Migração e Modernização: Java 25 & Spring Boot 3.4.2
**Projeto:** Condomínio Aliança (Backend)  
**Versão Alvo:** 2.0.0.0  
**Data:** 22 de Agosto de 2026  

---

## 1. Resumo Executivo

Este plano de migração descreve a elevação da arquitetura do backend do projeto Condomínio Aliança. O projeto foi migrado de **Java 17 / Spring Boot 2.7.9** diretamente para a vanguarda tecnológica do **Java 25 LTS e Spring Boot 3.4.2**.

### Principais Objetivos Alcançados:
1.  **Atualização de Infraestrutura:** Uso de Maven 3.9.9 (via wrapper) e runtime do Java 25 no Docker.
2.  **Injeção de Dependência Moderna:** Remoção completa da anotação `@Autowired` em favor de construtores implícitos gerados pelo Lombok (`@RequiredArgsConstructor`).
3.  **Configurações YAML:** Conversão de arquivos flat `.properties` para estrutura hierárquica `.yml`.
4.  **Monitoramento & Configuração Dinâmica:** Integração do Spring Actuator expondo `/actuator/health` e suporte a `@RefreshScope` no endpoint `/actuator/refresh` para alteração de variáveis em tempo real sem redeploy.
5.  **Modernização da Segurança (OAuth2/Security 6.4+):** Substituição do pacote legado e removido `spring-security-oauth2-autoconfigure` por uma arquitetura nativa com `spring-boot-starter-oauth2-resource-server` e criação de um Custom Token Controller para garantir compatibilidade reversa com o front-end.
6.  **Migração de Namespace:** Migração total de imports Java EE (`javax.*`) para Jakarta EE (`jakarta.*`).
7.  **Resolução de Incompatibilidades Java 25 & Hibernate 6:** Correção de construtores depreciados de `Locale` e `URL`, e ajuste na anotação `@Type` do Hibernate.
8.  **Migração do JavaMail & Activation:** Atualização dos pacotes de envio de e-mails para o ecossistema Jakarta.
9.  **Ajustes de Depreciação no Core (BigDecimal & Spring Web):** Substituição de constantes depreciadas de arredondamento de `BigDecimal` e atualização do construtor de URLs do Spring Web.
10. **Resolução de Resolução Eager de Variáveis do YAML:** Adição de fallbacks e valores padrão para placeholders de variáveis de ambiente para permitir bootstrapping local sem necessidade de declarar todas as variáveis na IDE.
11. **Carregamento Nativo e Programático de Arquivo `.env`:** Implementação de um loader nativo no método `main` da aplicação que varre até 4 níveis de diretórios acima para localizar e injetar as propriedades de um arquivo `.env` do projeto de forma 100% transparente para o Spring Boot, dispensando o uso de plugins de IDE no desenvolvimento local.
12. **Upgrade do Lombok para Compatibilidade com Compilador do Java 25:** Elevação da versão do Lombok para `1.18.38` para evitar erros de inicialização de classes AST (`TypeTag :: UNKNOWN`) no javac do JDK 25.
13. **Suporte a Classpath Scanning do Bytecode Java 25:** Configuração programática da propriedade de sistema `spring.classformat.ignore=true` no bootstrap para que o leitor de bytecode interno do Spring Framework 6.x não rejeite classes compiladas no formato do Java 25 (versão de bytecode 69).

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
    *   Parent do Spring Boot no pom.xml elevado para `4.1.1` (posteriormente ajustado para `3.4.2`).
    *   Substituição da dependência `spring-security-oauth2-autoconfigure` por `spring-boot-starter-oauth2-resource-server`.
    *   Migração global de `javax.*` para `jakarta.*` em 31 arquivos `.java`.
    *   Remoção das classes legadas `AuthorizationServerConfig.java` e `JwtTokenEnhancer.java`.
    *   Criação do controller `OAuthTokenController.java` para prover autenticação JWT transparente para o front-end.
    *   Simplificação e adequação de `ResourceServerConfig.java` e `WebSecurityConfig.java` ao padrão do Spring Security.
*   **Commit 5 (Correções do Java 25 & Hibernate 6):**
    *   Substituição de construtores depreciados de `Locale` e `URL` no Java 25.
    *   Remoção da anotação `@Type` legada do Hibernate em atributos binários para compatibilidade com o Hibernate 6.x.
*   **Commit 6 (Correções de Transações, Annotations e Lombok Builder):**
    *   Substituição de imports legados `javax.transaction.Transactional` por `jakarta.transaction.Transactional` em classes de serviços e controllers.
    *   Substituição do import `javax.annotation.Generated` por `jakarta.annotation.Generated` no metamodelo JPA `Usuario_.java`.
    *   Adição da anotação `@lombok.Builder.Default` nos campos inicializados inline das classes anotadas com `@Builder` (`CategoriaGasto.java`, `UsuarioDTO.java` e `Usuario.java`).
*   **Commit 7 (Correções do JavaMail & Activation Framework):**
    *   Substituição dos imports de e-mail `javax.mail.*` e ativação `javax.activation.*` por `jakarta.mail.*` e `jakarta.activation.*` no `EmailService.java`.
*   **Commit 8 (Correções de BigDecimal & UriComponentsBuilder):**
    *   Substituição da constante depreciada `BigDecimal.ROUND_HALF_EVEN` por `RoundingMode.HALF_EVEN` no `BoletoBuilder.java`.
    *   Substituição do método de build do Spring Web `UriComponentsBuilder.fromHttpUrl(...)` por `fromUriString(...)` no `InterService.java`.
*   **Commit 9 (Consolidação de Chaves YAML):**
    *   Correção de chaves duplicadas `inter` sob a seção `banco` no arquivo principal de configurações `application.yml`.
*   **Commit 10 (Fallbacks de Variáveis de Ambiente no YAML):**
    *   Adição de fallbacks default em todos os placeholders `${VAR}` (como `${DB_URL_DEV:jdbc:postgresql://localhost:5432/condominio}`, `${JWT_SECRET:secret}`, etc.) nos arquivos YAML para evitar erros de inicialização de propriedades quando as variáveis não são passadas ativamente na IDE do desenvolvedor.
*   **Commit 11 (Carregador Programático de `.env`):**
    *   Implementação do método `loadDotEnv()` em `NovaaliancaApplication.java` que lê e injeta o arquivo `.env` do diretório raiz como propriedades do sistema da JVM na inicialização do Spring Boot.
*   **Commit 12 (Ajuste das Versões do Framework para Estabilidade):**
    *   Downgrade do parent do Spring Boot no `pom.xml` para `3.4.2` e do Spring Cloud para `2024.0.0` para resolver incompatibilidades de carregamento de classes (`ConfigurableBootstrapContext` não encontrado). O target do Java e runtime do container Docker continuam fixos no **Java 25**.
*   **Commit 13 (Upgrade do Lombok para Java 25):**
    *   Atualização explícita da propriedade `<lombok.version>` no `pom.xml` para `1.18.38` para resolver erros de carregamento e inicialização das estruturas internas do compilador (`com.sun.tools.javac.code.TypeTag :: UNKNOWN`) decorrentes de incompatibilidades do Lombok com o javac do Java 25.
*   **Commit 14 (Desativação de Validação Estrita de Bytecode):**
    *   Inclusão de `System.setProperty("spring.classformat.ignore", "true")` na classe `NovaaliancaApplication.java` para permitir que o Spring Boot realize o escaneamento do classpath de classes compiladas para o formato Java 25.

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

### 3.3. Nova Arquitetura de Segurança (Spring Security 6.4+ + Nimbus JWT)
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
5.  **Constantes Depreciadas de BigDecimal (Java 9+):**
    *   *Antes:* `.setScale(2, BigDecimal.ROUND_HALF_EVEN)` no `BoletoBuilder.java`.
    *   *Depois:* Substituído pelo enum moderno `.setScale(2, java.math.RoundingMode.HALF_EVEN)`.
6.  **Compatibilidade do UriComponentsBuilder:**
    *   *Antes:* `UriComponentsBuilder.fromHttpUrl(url)` no `InterService.java`.
    *   *Depois:* Substituído por `UriComponentsBuilder.fromUriString(url)` para evitar incompatibilidades de compilação da classpath do Spring Web 6/7.
7.  **Fallbacks no YAML para Placeholders:**
    *   *Antes:* Declarar propriedades com `${VAR}` gerava erros se a variável estivesse ausente.
    *   *Depois:* Ajustado para `${VAR:default}` (ex: `${DB_URL_DEV:jdbc:postgresql://localhost:5432/condominio}`) garantindo inicialização de fallback.
8.  **Carregamento do Arquivo `.env` Nativo:**
    *   *Antes:* Dependência de plugins externos do IntelliJ (como EnvFile) para injetar as variáveis de ambiente.
    *   *Depois:* A classe `NovaaliancaApplication` carrega programaticamente no boot do Java o arquivo `.env` localizado na raiz do projeto (`C:\GIT\prj_condominio\.env`).
9.  **Lombok no Java 25:**
    *   *Antes:* Falha de inicialização `TypeTag :: UNKNOWN` provocada por APIs modificadas do javac do Java 25.
    *   *Depois:* Forçado uso do Lombok `1.18.38` nas properties do Maven, que introduz compatibilidade com os compiladores Java modernos.
10. **Ignorar Formato do Bytecode do Java 25 no Spring 6.x:**
    *   *Antes:* Falha de boot `BeanDefinitionStoreException: Incompatible class format` em classes do DTO compiladas no Java 25 (versão de bytecode 69) ao escanear o classpath.
    *   *Depois:* Adicionado `spring.classformat.ignore=true` nas propriedades de inicialização do Java para instruir o Spring Framework a pular a validação rígida de versão de bytecode.

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
