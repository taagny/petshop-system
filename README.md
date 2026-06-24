# 🐾 PetShop System

Sistema de gerenciamento de pet shop desenvolvido em Java com Swing, MySQL e arquitetura MVC.

## Tecnologias
- Java 17
- Java Swing + FlatLaf
- MySQL (XAMPP)
- Maven
- iText (geração de PDF)

## Funcionalidades
- Cadastro de animais, proprietários e serviços
- Lançamento de serviços para animais
- Histórico de serviços com filtros
- Geração de relatórios em PDF

## Como executar
1. Inicie o MySQL via XAMPP
2. Execute o script `database/schema.sql`
3. Configure `src/main/resources/database.properties`
4. Execute `mvn clean install` e rode a classe `Main`

## Estrutura do projeto
```
src/
├── main/
│   ├── java/com/petshop/
│   │   ├── model/
│   │   ├── dao/
│   │   ├── controller/
│   │   └── view/
│   └── resources/
└── database/
    └── schema.sql
```