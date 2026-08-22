import os
import sys
import time
import subprocess
import xml.etree.ElementTree as ET

def get_pom_version():
    pom_path = os.path.join("back_end", "novaalianca", "pom.xml")
    if not os.path.exists(pom_path):
        print(f"[ERRO] Arquivo pom.xml não encontrado em {pom_path}")
        sys.exit(1)
    try:
        tree = ET.parse(pom_path)
        root = tree.getroot()
        # O namespace padrão do Maven
        ns = {"maven": "http://maven.apache.org/POM/4.0.0"}
        version_element = root.find("maven:version", ns)
        if version_element is None:
            version_element = root.find("version")
        
        if version_element is not None and version_element.text:
            return version_element.text.strip()
        else:
            print("[ERRO] Não foi possível encontrar a tag <version> no pom.xml")
            sys.exit(1)
    except Exception as e:
        print(f"[ERRO] Falha ao fazer parse do pom.xml: {e}")
        sys.exit(1)

def is_docker_running():
    try:
        # Executa docker info silenciando a saída
        result = subprocess.run(
            ["docker", "info"], 
            stdout=subprocess.DEVNULL, 
            stderr=subprocess.DEVNULL
        )
        return result.returncode == 0
    except Exception:
        return False

def start_docker():
    if is_docker_running():
        print("[INFO] Docker já está rodando.")
        return True

    print("[WARN] Docker não está rodando. Tentando iniciar o Docker Desktop...")
    
    # Caminhos comuns do Docker Desktop no Windows
    paths = [
        r"C:\Program Files\Docker\Docker\Docker Desktop.exe",
        r"C:\Program Files\Docker\Docker\DockerDesktop.exe"
    ]
    
    docker_executable = None
    for p in paths:
        if os.path.exists(p):
            docker_executable = p
            break
            
    if not docker_executable:
        print("[ERRO] Não foi possível encontrar o executável do Docker Desktop nos caminhos padrões.")
        print("Por favor, inicie o Docker Desktop manualmente antes de rodar este script.")
        return False

    try:
        os.startfile(docker_executable)
        print("[INFO] Docker Desktop foi executado. Aguardando inicialização do serviço...")
        
        # Faz polling a cada 3 segundos (limite de 90 segundos)
        for i in range(30):
            time.sleep(3)
            if is_docker_running():
                print("\n[SUCESSO] Docker inicializado e pronto!")
                return True
            print(".", end="", flush=True)
            
        print("\n[ERRO] Timeout: O Docker Desktop foi iniciado, mas o serviço não respondeu a tempo.")
        return False
    except Exception as e:
        print(f"[ERRO] Falha ao tentar iniciar o Docker Desktop: {e}")
        return False

def run_maven_build(backend_dir):
    # Usar mvnw.cmd no Windows ou ./mvnw no Linux/MacOS
    mvn_cmd = "mvnw.cmd" if os.name == 'nt' else "./mvnw"
    
    # Se o wrapper não existir, tenta usar o Maven global
    if not os.path.exists(os.path.join(backend_dir, mvn_cmd)):
        mvn_cmd = "mvn"

    print("\n==========================================")
    print(f"[INFO] Iniciando build com Maven ({mvn_cmd})...")
    print("==========================================")
    
    # Executa o comando clean package pulando testes para agilizar o build da imagem
    # Se preferir rodar testes, basta remover a flag '-DskipTests'
    cmd = [mvn_cmd, "clean", "package", "-DskipTests"]
    
    # No Windows usando cmd/shell para evitar problemas de path no subprocess
    use_shell = os.name == 'nt'
    result = subprocess.run(cmd, cwd=backend_dir, shell=use_shell)
    
    if result.returncode != 0:
        print("[ERRO] Falha ao compilar o projeto com o Maven.")
        sys.exit(1)
    print("[INFO] Compilação do Maven finalizada com sucesso!")

def build_docker_image(backend_dir, version):
    jar_file = f"target/novaalianca-{version}.jar"
    image_tag = f"patrickmoura/novaalianca:{version}"
    
    print("\n==========================================")
    print(f"[INFO] Construindo imagem Docker: {image_tag}...")
    print("==========================================")
    
    cmd = [
        "docker", "build",
        "--build-arg", f"JAR_FILE={jar_file}",
        "-t", image_tag,
        "."
    ]
    
    result = subprocess.run(cmd, cwd=backend_dir)
    if result.returncode != 0:
        print("[ERRO] Falha ao construir a imagem Docker.")
        sys.exit(1)
        
    print("\n==========================================")
    print("[SUCESSO] Processo concluído com êxito!")
    print(f"Imagem gerada: {image_tag}")
    print("==========================================")

def main():
    backend_dir = os.path.join("back_end", "novaalianca")
    
    # 1. Obter a versão no pom.xml
    print("[INFO] Lendo versão do pom.xml...")
    version = get_pom_version()
    print(f"[INFO] Versão detectada: {version}")
    
    # 2. Verificar/Iniciar Docker
    if not start_docker():
        print("[ERRO] Abortando o processo devido ao Docker não estar rodando.")
        sys.exit(1)
        
    # 3. Compilar projeto com Maven
    run_maven_build(backend_dir)
    
    # 4. Construir imagem Docker
    build_docker_image(backend_dir, version)

if __name__ == "__main__":
    main()
