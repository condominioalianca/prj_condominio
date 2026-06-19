# Estágio de Build
FROM node:20-alpine AS build

WORKDIR /app

# Copiar arquivos de dependências
COPY package.json package-lock.json ./

# Instalar dependências de forma limpa
RUN npm ci

# Copiar todos os arquivos do projeto
COPY . .

# Variável de build-time (importante para embutir a URL da API no build do Vite)
ARG BACK_END_NOVA_ALIANCA
ENV BACK_END_NOVA_ALIANCA=$BACK_END_NOVA_ALIANCA

# Executar a compilação do front-end
RUN npm run build

# Estágio de Produção (servidor web Nginx)
FROM nginx:alpine

# Copiar configuração personalizada do Nginx para suportar React Router
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copiar os arquivos compilados do estágio anterior para o diretório padrão do Nginx
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
