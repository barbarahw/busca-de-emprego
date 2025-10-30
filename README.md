🧭 Busca de Emprego — API REST
API desenvolvida em Spring Boot para gerenciar usuários e autenticação em um sistema de busca de empregos. O sistema permite o cadastro, login, leitura, edição e exclusão de perfis de usuários comuns e de empresas, além de logout com autenticação via token.

⚙️ Como Executar o Projeto

Pré-requisitos: 

- Java 17 ou superior instalado 
- Maven instalado
- Xampp com o MySQL e Apache em execução

1\. Clonar o repositório https://github.com/barbarahw/busca-de-emprego.git

ou baixar o arquivo zip enviado

2\. Acessar a pasta do projeto

3\. Inicar o SQL via XAMPP

- Abra o XAMPP Control Panel
- Clique em Start no módulo MySQL
- Acesse o phpMyAdmin (botão “Admin”) e crie um banco de dados com o nome "busca\_emprego"

4\. Configurar a conexão com o banco de dados

No arquivo *src/main/resources/application.properties* adicione:

```
spring.datasource.url=jdbc:mysql://localhost:3306/busca\_de\_emprego

spring.datasource.username=root

spring.datasource.password=

spring.jpa.hibernate.ddl-auto=update

spring.jpa.show-sql=true

spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

5\. Executar o projeto

👩‍💻 Desenvolvido para fins acadêmicos



👩‍💻 Desenvolvido para fins acadêmicos

