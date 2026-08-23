# ============================================================
# Script de limpeza total do Jenkins (Docker Desktop - Windows 11)
# Execute no PowerShell como Administrador:
#   .\cleanup_jenkins.ps1
# ============================================================

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  LIMPEZA TOTAL DO JENKINS (DOCKER)"     -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 1. Para todos os containers que tenham "jenkins" no nome
Write-Host ">>> [1/6] Parando containers Jenkins..." -ForegroundColor Yellow
$containers = docker ps -a --filter "name=jenkins" --format "{{.Names}}"
if ($containers) {
    $containers | ForEach-Object { docker stop $_ }
} else {
    Write-Host "    Nenhum container Jenkins encontrado."
}
Write-Host "    OK" -ForegroundColor Green

# 2. Remove todos os containers com "jenkins" no nome
Write-Host ">>> [2/6] Removendo containers Jenkins..." -ForegroundColor Yellow
$containers = docker ps -a --filter "name=jenkins" --format "{{.Names}}"
if ($containers) {
    $containers | ForEach-Object { docker rm -f $_ }
} else {
    Write-Host "    Nenhum container para remover."
}
Write-Host "    OK" -ForegroundColor Green

# 3. Remove todas as imagens que contenham "jenkins" no nome
Write-Host ">>> [3/6] Removendo imagens Jenkins..." -ForegroundColor Yellow
$images = docker images --format "{{.Repository}}:{{.Tag}}" | Where-Object { $_ -match "jenkins" }
if ($images) {
    $images | ForEach-Object { docker rmi -f $_ }
} else {
    Write-Host "    Nenhuma imagem Jenkins encontrada."
}
Write-Host "    OK" -ForegroundColor Green

# 4. Remove TODOS os volumes com "jenkins" no nome (apaga configurações antigas)
Write-Host ">>> [4/6] Removendo volumes Jenkins (configuracoes antigas)..." -ForegroundColor Yellow
$volumes = docker volume ls --format "{{.Name}}" | Where-Object { $_ -match "jenkins" }
if ($volumes) {
    $volumes | ForEach-Object { docker volume rm -f $_ }
} else {
    Write-Host "    Nenhum volume Jenkins encontrado."
}
Write-Host "    OK" -ForegroundColor Green

# 5. Remove redes Docker com "jenkins" no nome
Write-Host ">>> [5/6] Removendo redes Jenkins..." -ForegroundColor Yellow
$networks = docker network ls --format "{{.Name}}" | Where-Object { $_ -match "jenkins" }
if ($networks) {
    $networks | ForEach-Object { docker network rm $_ }
} else {
    Write-Host "    Nenhuma rede Jenkins encontrada."
}
Write-Host "    OK" -ForegroundColor Green

# 6. Limpa imagens e layers orfaos
Write-Host ">>> [6/6] Limpando layers e imagens orfas..." -ForegroundColor Yellow
docker image prune -f
docker builder prune -f
Write-Host "    OK" -ForegroundColor Green

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  LIMPEZA CONCLUIDA! Estado atual:"      -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "--- Containers ---" -ForegroundColor White
docker ps -a
Write-Host ""
Write-Host "--- Volumes ---" -ForegroundColor White
docker volume ls
Write-Host ""
Write-Host "Pronto! Agora execute:" -ForegroundColor Green
Write-Host "  cd C:\GIT\prj_condominio\jenkins" -ForegroundColor White
Write-Host "  docker compose up -d --build"     -ForegroundColor White
