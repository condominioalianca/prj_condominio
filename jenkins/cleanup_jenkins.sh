#!/bin/bash
# ============================================================
# Script de limpeza total do Jenkins (Docker)
# Execute como: bash cleanup_jenkins.sh
# ============================================================

echo ""
echo "========================================"
echo "  LIMPEZA TOTAL DO JENKINS (DOCKER)"
echo "========================================"
echo ""

# 1. Para todos os containers que tenham "jenkins" no nome
echo ">>> [1/6] Parando containers Jenkins..."
docker ps -a --filter "name=jenkins" --format "{{.Names}}" | xargs -r docker stop
echo "    OK"

# 2. Remove todos os containers com "jenkins" no nome
echo ">>> [2/6] Removendo containers Jenkins..."
docker ps -a --filter "name=jenkins" --format "{{.Names}}" | xargs -r docker rm -f
echo "    OK"

# 3. Remove todas as imagens que contenham "jenkins" no nome
echo ">>> [3/6] Removendo imagens Jenkins..."
docker images --filter "reference=*jenkins*" --format "{{.Repository}}:{{.Tag}}" | xargs -r docker rmi -f
# Remove também a imagem customizada local
docker images --filter "reference=jenkins*" --format "{{.ID}}" | xargs -r docker rmi -f
echo "    OK"

# 4. Remove TODOS os volumes com "jenkins" no nome (apaga configurações antigas)
echo ">>> [4/6] Removendo volumes Jenkins (configurações antigas)..."
docker volume ls --filter "name=jenkins" --format "{{.Name}}" | xargs -r docker volume rm -f
echo "    OK"

# 5. Remove redes Docker com "jenkins" no nome
echo ">>> [5/6] Removendo redes Jenkins..."
docker network ls --filter "name=jenkins" --format "{{.Name}}" | xargs -r docker network rm
echo "    OK"

# 6. Limpa imagens e layers órfãos
echo ">>> [6/6] Limpando layers e imagens órfãs..."
docker image prune -f
docker builder prune -f
echo "    OK"

echo ""
echo "========================================"
echo "  LIMPEZA CONCLUÍDA!"
echo ""
docker ps -a
echo ""
docker volume ls
echo "========================================"
