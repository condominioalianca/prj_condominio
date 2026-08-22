# 📋 Documentação e Comparação dos Arquivos `application.properties`

Este documento apresenta a análise comparativa detalhada dos quatro arquivos de configuração do Spring Boot localizados no projeto:
1. `application.properties` (Configuração base comum e centralizada)
2. `application-dev.properties` (Especificações do ambiente de Desenvolvimento local)
3. `application-prd.properties` (Especificações do ambiente de Produção/Nuvem)
4. `application-test.properties` (Especificações do ambiente de Testes unitários/integrados)

---

## 🔍 O que cada linha faz (Dicionário de Propriedades)

### 1. Configuração do Servidor e Perfis Ativos
*   `spring.profiles.active`: Define qual perfil de configuração está carregado por padrão. No arquivo base, lê a variável `${APP_PROFILE}` e, caso não exista, assume `dev`.
*   `server.port`: A porta TCP em que o servidor web do Spring Boot (Tomcat embutido) escuta. 
    *   No DEV e TEST é fixada em `8086`.
    *   No PRD usa a variável `${PORT}` com fallback para `8086` para se adaptar a provedores de nuvem (como Heroku ou Railway) que injetam dinamicamente a porta.

### 2. Configurações do Banco de Dados (Spring Data Datasource)
*   `spring.datasource.url`: URL de conexão JDBC para se conectar ao banco de dados PostgreSQL.
*   `spring.datasource.username`: Nome do usuário de login no banco de dados.
*   `spring.datasource.password`: Senha correspondente ao usuário.
*   `spring.jpa.database-platform`: Especifica o dialeto SQL do Hibernate a ser utilizado. Configurado como `org.hibernate.dialect.PostgreSQLDialect` para otimizar comandos SQL específicos do PostgreSQL.

### 3. Mecanismos do ORM Hibernate & JPA
*   `spring.jpa.hibernate.naming.physical-strategy`: Define como os nomes das tabelas e colunas em Java são convertidos no banco de dados físico (ex: `PhysicalNamingStrategyStandardImpl` preserva o nome exato mapeado nas annotations JPA).
*   `spring.sql.init.mode`: Configurado como `always`. Controla quando a inicialização SQL do Spring Boot (`import.sql` ou DDLs) deve ser disparada.
*   `spring.jpa.defer-datasource-initialization`: Adia a execução de scripts de sementes de dados (`import.sql`) até que o Hibernate já tenha terminado de construir a estrutura de tabelas.
*   `spring.jpa.hibernate.ddl-auto`: `update`. Faz com que o Hibernate compare automaticamente o mapeamento das classes Java `@Entity` com as tabelas do banco e aplique alterações na estrutura no momento de inicialização.
*   `spring.jpa.generate-ddl`: `true`. Habilita a geração automática de arquivos DDL pelo JPA em runtime.
*   `spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation`: `true`. Desativa a verificação desnecessária do contexto JDBC para criação de tipos LOB, reduzindo mensagens redundantes no log.
*   `spring.jpa.open-in-view`: Controla se a conexão com o banco e a transação/sessão JPA permanecem abertas até o término da renderização do Controller/View (permite carregar dados Lazy fora do Service). No arquivo base é desabilitado (`false`) por boas práticas de performance.
*   `spring.jpa.show-sql` e `spring.jpa.properties.hibernate.show_sql`: Controla a exibição das queries SQL geradas pelo ORM no console de saída.
*   `spring.jpa.properties.hibernate.format_sql`: Formata a exibição do SQL no console para que fique legível e identado.

### 4. Controle de Logs e Níveis de Depuração
*   `logging.level.org.springframework.web`: Define o nível de log dos frameworks web. Configurado como `INFO` em todos os perfis.
*   `logging.level.org.hibernate`: Define o nível de log do Hibernate. Em DEV/TEST é `INFO`, em PRD é `off` para evitar poluição visual dos logs e logs lentos em produção.
*   `logging.level.org.hibernate.type.descriptor.sql.BasicBinder`: Quando ativo em nível `TRACE` (como no DEV e TEST), exibe os valores exatos de parâmetros que o Spring vinculou nas queries SQL (útil para debug). Em PRD é setado como `off`.

### 5. MVC e Roteamento
*   `spring.mvc.pathmatch.matching-strategy`: Define o motor de correspondência de URLs do Spring MVC. `ant-path-matcher` é usado para compatibilidade com bibliotecas antigas de segurança e Swagger.

### 6. Segurança, OAuth2 e JWT (Autenticação)
*   `security.oauth2.client.client-id` e `security.oauth2.client.client-secret`: Identificadores do Client cadastrado no servidor de autorização OAuth2 (recebidos via variáveis de ambiente).
*   `jwt.secret`: Segredo base para cifrar e decifrar as assinaturas digitais dos tokens JWT gerados aos usuários.
*   `jwt.duration`: Tempo limite de validade de um token emitido (em segundos). `86400` equivale a 24 horas.
*   `cors.origins`: Define quais URLs externas (ex: `http://localhost:3002/` do Frontend) possuem permissão para realizar requisições REST ao Backend sem sofrer bloqueio do navegador.

### 7. Parâmetros de Integração com API Banco Inter (Cobrança/Boleto e Extrato)
*   `banco.interurl.path_prod`: URL base do ambiente de Produção do Banco Inter.
*   `banco.interurl.path_sand`: URL base do ambiente Sandbox (Homologação) do Banco Inter.
*   `banco.inter.url.boleto`: Endpoint específico para gerar e cancelar boletos no Inter.
*   `banco.inter.url.token`: Endpoint do Inter para solicitar Tokens OAuth2 (mTLS).
*   `banco.inter.url.extrato`: Endpoint específico para buscar dados da conta e extrato consolidado.
*   `boleto.cpfcnpj.benificiario`: CPF ou CNPJ do beneficiário emissor dos boletos.
*   `boleto.numerocontacorrente`: Número de conta corrente cadastrada no Banco Inter.
*   `banco.inter.caminho.certificado`: Caminho físico no disco onde está salvo o arquivo do certificado SSL de segurança `.pfx` necessário para autenticação mTLS de duas vias.
*   `banco.inter.senha.certificado`: Senha usada para desbloquear o certificado `.pfx`.
*   `banco.inter.client.id` e `banco.inter.client.secret`: Credenciais OAuth2 geradas dentro da área do desenvolvedor do Banco Inter.
*   `banco.inter.grant.type`: Escopo da credencial no Inter (geralmente `client_credentials`).

### 8. Envio de E-mails via SMTP do Gmail
*   `mail.from`: O texto e e-mail que aparecem no cabeçalho "De:" do destinatário.
*   `spring.mail.host`: Servidor SMTP de envio do Gmail (`smtp.gmail.com`).
*   `spring.mail.username` e `spring.mail.password`: E-mail e senha de aplicativo do Gmail para autenticação SMTP.
*   `spring.mail.properties.mail.smtp.auth`: Ativa autenticação obrigatória do e-mail.
*   `spring.mail.properties.mail.smtp.starttls.enable` e `spring.mail.properties.mail.smtp.starttls.required`: Exige criptografia TLS segura na conexão do e-mail.
*   `spring.mail.port`: Porta do SMTP TLS do Gmail (`587`).

### 9. Containerization e Agendamento (Cron Jobs)
*   `source.docker`: Indica ao Spring Boot se a execução está em ambiente de container Docker (seta variáveis especiais internamente).
*   `cron.schedule.*`: Parâmetros no padrão Cron UNIX para configurar a periodicidade em que as tarefas agendadas em background iniciam (Valida Envio, Recupera Boleto, Envia E-mail e Atualiza Extrato).

---

## 📊 Matriz Comparativa (Diferenças entre os Ambientes)

Abaixo está a comparação de como as principais propriedades variam de valor conforme o perfil carregado:

| Propriedade / Chave | `application.properties` (Base) | `application-dev.properties` (DEV) | `application-prd.properties` (PRD) | `application-test.properties` (TEST) |
| :--- | :--- | :--- | :--- | :--- |
| **Perfil Padrão** | `${APP_PROFILE:dev}` | *Carrega Destaque DEV* | *Carrega Destaque PRD* | *Carrega Destaque TEST* |
| **Porta do Servidor** | *Não definida* | `8086` | `${PORT:8086}` | `8086` |
| **URL JDBC Postgres** | `${DB_URL_DEV}` | `${DB_URL_DEV}` | `${DB_URL_PRD}` | `${DB_URL:...}` (com fallback local) |
| **Username do Banco** | `${DB_USERNAME_DEV}` | `${DB_USERNAME_DEV}` | `${DB_USERNAME_PRD}` | `${DB_USERNAME:postgres}` |
| **Password do Banco** | `${DB_PASSWORD_DEV}` | `${DB_PASSWORD_DEV}` | `${DB_PASSWORD_PRD}` | `${DB_PASSWORD:140908}` |
| **`open-in-view` (Lazy)**| `false` (Desativado) | `true` (Ativo) | `true` (Ativo) | `true` (Ativo) |
| **Logs SQL (Binder)** | *Não definido* | `TRACE` | `off` | `TRACE` |
| **Logs Hibernate** | *Não definido* | `INFO` | `off` | `INFO` |
| **OAuth / JWT / CORS** | Lê dinâmico de ENVs | *Herda o Base* | *Herda o Base* | *Herda o Base* |
| **Cron Extrato Bancário**| *Não definido* | `0 0 */1 * * *` (1 hora) | `0 0 * * * *` (Hora Cheia) | `0 */2 * * * *` (2 minutos) |
