# Guia de Inicialização Local - Condomínio Nova Aliança

Este guia detalha como inicializar o banco de dados, o backend e o front-end do projeto localmente.

---

## Pré-Requisitos

Antes de iniciar, certifique-se de ter instalado em sua máquina:
1. **Node.js** (v18 ou superior recomendado, v24 instalado localmente)
2. **Java JDK 11** ou superior
3. **PostgreSQL** (com base de dados criada ou via Docker Compose)

---

## 1. Banco de Dados e Serviços Externos

O projeto do backend possui suporte a banco de dados PostgreSQL. Você pode iniciá-lo utilizando o arquivo `docker-compose.yml` que está na raiz do repositório (`C:\GIT2\prj_condominio`):

```bash
# Na pasta raiz do projeto:
docker-compose up -d
```

Se preferir rodar um banco de dados PostgreSQL local instalado na máquina, certifique-se de que a URL de conexão em `back_end/novaalianca/src/main/resources/application-dev.properties` aponta para seu banco local, com as credenciais corretas:
- **Database URL**: `jdbc:postgresql://localhost:5432/condominio`
- **Username**: `postgres`
- **Password**: `140908` (ou a senha configurada no seu PostgreSQL local)

---

## 2. Inicializando o Backend (Spring Boot)

O backend roda na porta **8086** por padrão no perfil `dev`.

1. Navegue até a pasta do backend:
   ```bash
   cd C:\GIT2\prj_condominio\back_end\novaalianca
   ```
2. Inicialize a aplicação via Maven Wrapper:
   - **No Windows (PowerShell/CMD)**:
     ```powershell
     .\mvnw.cmd spring-boot:run
     ```
   - **No Linux/Mac**:
     ```bash
     ./mvnw spring-boot:run
     ```

O servidor estará ativo em: `http://localhost:8086`

---

## 3. Inicializando o Front-End (React + Vite)

O front-end foi configurado para rodar na porta **3001** para se integrar nativamente com a configuração de CORS padrão do backend.

### Configurando a URL do Backend

Para definir o endereço do servidor backend que o front-end utilizará para fazer chamadas de API, configure a variável de ambiente no arquivo `.env`:

1. Crie ou edite o arquivo `.env` na raiz da pasta do front-end (`C:\GIT2\prj_condominio\front_end_new\.env`).
2. Adicione a variável `BACK_END_NOVA_ALIANCA` apontando para o IP e a porta corretos do seu backend:
   ```env
   BACK_END_NOVA_ALIANCA=http://192.168.15.10:8086
   ```
3. Se estiver rodando o backend na sua própria máquina local, utilize:
   ```env
   BACK_END_NOVA_ALIANCA=http://localhost:8086
   ```

### Executando o Front-End

1. Navegue até a pasta do front-end:
   ```bash
   cd C:\GIT2\prj_condominio\front_end_new
   ```
2. Instale as dependências (caso não tenha feito ainda):
   ```bash
   npm install
   ```
3. Inicie o servidor de desenvolvimento:
   ```bash
   npm run dev
   ```

O front-end estará ativo em: `http://localhost:3001`

---

## Informações de Acesso para Testes

O banco de dados é populado automaticamente com os dados do arquivo `import.sql`. Você pode usar as seguintes contas para testar:

### 1. Perfil Administrador (Acesso total + Parâmetros)
- **E-mail / Usuário**: `patrickmoura@gmail.com`
- **Senha padrão do hash do banco**: `123456` ou a senha configurada no banco.
- *Nota: Caso o hash `$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG` seja a senha padrão (criptografada em BCrypt), utilize `123` ou `123456`.*

### 2. Perfil Morador Comum (Visualização de Boletos Próprios + Dashboard de 30 dias)
- **E-mail / Usuário**: `danilolsf@gmail.com`
- **Senha padrão**: Mesma senha criptografada (`123456` ou `123`).

---

## Funcionalidades Prontas no Front-End

- **Segurança e Rotas**: Bloqueio completo do sistema para usuários deslogados. Redirecionamento automático e controle de acesso baseado nas Roles do Token JWT.
- **Dashboard Dinâmico**: 
  - Visualização de 30 dias de fluxo de caixa para moradores e 90 dias para administradores.
  - Gráfico com os **Top 5 Ofensores de Débito** nos últimos 90 dias (para Administradores).
  - Tabela com download em PDF direto do Boleto via conversão do campo Base64 da tabela de boletos.
- **CRUDs Administrativos (Síndico/Admin)**:
  - Usuários (com dados de Endereço, Unidade e Perfis).
  - Unidades (Apartamentos e Andares).
  - Empresas parceiras.
  - Lançamento de Cobranças Extras.
  - Parâmetros do Sistema (Apenas Admin).
  - Parâmetros de Perfis (Apenas Admin).
- **Configurações**: Edição de dados do próprio morador logado e alteração de sua senha de acesso de forma segura.
