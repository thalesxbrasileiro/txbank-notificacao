# TxBank - Microsserviço de Notificações

Este projeto é um microsserviço parte do ecossistema **TxBank**. Sua principal responsabilidade é ouvir eventos assíncronos provenientes de outros serviços e notificar os clientes, principalmente via e-mail.

Atualmente, o serviço escuta eventos de criação de contas e envia um e-mail de boas-vindas.

## 🚀 Tecnologias Utilizadas

*   **Java 17** (LTS)
*   **Spring Boot 3.2.3**
*   **Apache Kafka** (Mensageria)
*   **Spring Email** (JavaMailSender)
*   **Maven** (Gerenciador de dependências)
*   **Lombok** (Redução de boilerplate)

## ⚙️ Arquitetura

O fluxo de funcionamento é o seguinte:

1.  Um microsserviço externo (ex: `txbank-conta`) publica uma mensagem no tópico do Kafka.
2.  O **txbank-notificacao** consome essa mensagem.
3.  O serviço processa os dados e envia um e-mail para o cliente utilizando um servidor SMTP.

## 📋 Pré-requisitos

Para rodar este projeto localmente, você precisará de:

*   JDK 17 instalado.
*   Maven instalado.
*   Um servidor **Kafka** rodando (localmente ou via Docker).
*   Uma conta no **Mailtrap** (ou outro servidor SMTP) para testes de envio de e-mail.

## 🔧 Configuração

### 1. Configurar o Kafka

#### Opção 1: Via Docker (Recomendado)
Certifique-se de que o Kafka está rodando na porta `9092`. Se você tiver o Docker instalado, pode usar o seguinte `docker-compose.yml` para subir o Kafka rapidamente:

```yaml
version: '3'
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:latest
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    ports:
      - 22181:2181
  
  kafka:
    image: confluentinc/cp-kafka:latest
    depends_on:
      - zookeeper
    ports:
      - 29092:29092
      - 9092:9092
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:29092,PLAINTEXT_HOST://localhost:9092
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
```

#### Opção 2: Instalação Manual (Modo KRaft - Sem Zookeeper)

Nas versões mais recentes do Kafka (3.x+), é recomendado usar o modo KRaft, que elimina a necessidade do Zookeeper.

1.  Baixe o Apache Kafka no site oficial: [https://kafka.apache.org/downloads](https://kafka.apache.org/downloads).
2.  Extraia o arquivo compactado.
3.  Navegue até a pasta extraída.

**No Windows:**

1.  Gere um UUID para o cluster:
    ```cmd
    .\bin\windows\kafka-storage.bat random-uuid
    ```
    *(Copie o código gerado)*

2.  Formate os diretórios de log (substitua `<UUID>` pelo código copiado):
    ```cmd
    .\bin\windows\kafka-storage.bat format --standalone -t <UUID> -c .\config\server.properties
    ```

3.  Inicie o servidor Kafka:
    ```cmd
    .\bin\windows\kafka-server-start.bat .\config\server.properties
    ```

### 2. Configurar o SMTP (Mailtrap)
No arquivo `src/main/resources/application.properties`, configure as credenciais do seu servidor SMTP. O projeto já vem pré-configurado para usar o Mailtrap (ambiente de teste):

```properties
spring.mail.host=sandbox.smtp.mailtrap.io
spring.mail.port=2525
spring.mail.username=SEU_USUARIO
spring.mail.password=SUA_SENHA
```

> **Nota:** Substitua `SEU_USUARIO` e `SUA_SENHA` pelas credenciais da sua conta no Mailtrap.

## 🏃‍♂️ Como Rodar

1.  Clone o repositório.
2.  Abra o terminal na raiz do projeto.
3.  Execute o comando para baixar as dependências e compilar:
    ```bash
    mvn clean install
    ```
4.  Execute o projeto:
    ```bash
    mvn spring-boot:run
    ```

## 🐳 Rodando com Docker

Você pode containerizar a aplicação para facilitar a execução e o deploy.

### 1. Construir a Imagem

Na raiz do projeto, execute:

```bash
docker build -t txbank-notificacao .
```

### 2. Executar o Container

Para rodar o container, é necessário passar as variáveis de ambiente para o E-mail e configurar o acesso ao Kafka (se estiver rodando no host).

```bash
docker run -p 8081:8081 \
  -e SPRING_KAFKA_BOOTSTRAP_SERVERS=host.docker.internal:9092 \
  -e EMAIL_USERNAME=seu_usuario_mailtrap \
  -e EMAIL_PASSWORD=sua_senha_mailtrap \
  -e EMAIL_DESTINO=seu_email@teste.com \
  txbank-notificacao
```

> **Nota:** O endereço `host.docker.internal` é usado para que o container consiga acessar o Kafka rodando na sua máquina local (Windows/Mac). Se estiver no Linux, use `--network="host"`.

## 📨 Eventos e Payloads

### Tópico: `conta-criada` (Exemplo)

O serviço espera receber um JSON no seguinte formato para enviar o e-mail de boas-vindas:

```json
{
  "id": 1,
  "clienteId": 100
}
```

*   **id**: ID da conta criada.
*   **clienteId**: ID do cliente dono da conta.

## 🧪 Testando Localmente

Para testar sem o microsserviço produtor, você pode enviar uma mensagem manualmente para o Kafka usando um terminal (se tiver o Kafka CLI instalado) ou uma ferramenta como **Kafka Tool** ou **Offset Explorer**.

---
Desenvolvido para o ecossistema TxBank.
