# ============================================================
# Imagem customizada do Jenkins
# Jenkins roda em JDK 21 (LTS), builds do projeto em JDK 25 (Zulu)
# Inclui: Docker CLI, JDK 25 (Azul Zulu), Maven 3.9.9, Node.js 20
# ============================================================
FROM jenkins/jenkins:lts-jdk21

USER root

# ------- Docker CLI -------
RUN apt-get update && apt-get install -y \
    ca-certificates \
    curl \
    gnupg \
    lsb-release \
    apt-transport-https && \
    mkdir -p /etc/apt/keyrings && \
    curl -fsSL https://download.docker.com/linux/debian/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg && \
    echo \
      "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/debian \
      $(lsb_release -cs) stable" | tee /etc/apt/sources.list.d/docker.list > /dev/null && \
    apt-get update && \
    apt-get install -y docker-ce-cli && \
    rm -rf /var/lib/apt/lists/*

# ------- Azul Zulu JDK 25 (para compilar o projeto) -------
RUN apt-get update && apt-get install -y gnupg2 && \
    curl -fsSL https://repos.azul.com/azul-repo.key | gpg --dearmor -o /usr/share/keyrings/azul.gpg && \
    echo "deb [signed-by=/usr/share/keyrings/azul.gpg] https://repos.azul.com/zulu/deb stable main" \
      | tee /etc/apt/sources.list.d/zulu.list && \
    apt-get update && \
    apt-get install -y zulu25-jdk && \
    rm -rf /var/lib/apt/lists/*

ENV JAVA_25_HOME=/usr/lib/jvm/zulu25
ENV JAVA_HOME=${JAVA_25_HOME}
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# ------- Maven 3.9.9 -------
ARG MAVEN_VERSION=3.9.9
RUN curl -fsSL https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
    | tar -xz -C /opt && \
    ln -s /opt/apache-maven-${MAVEN_VERSION} /opt/maven
ENV MAVEN_HOME=/opt/maven
ENV PATH="${MAVEN_HOME}/bin:${PATH}"

# ------- Node.js 20 LTS -------
RUN curl -fsSL https://deb.nodesource.com/setup_20.x | bash - && \
    apt-get install -y nodejs && \
    rm -rf /var/lib/apt/lists/*

# Adiciona jenkins ao grupo docker para acesso ao socket
RUN groupadd -f docker && usermod -aG docker jenkins

USER jenkins
