# 🐾 PetShop System

Sistema desktop de gerenciamento de pet shop desenvolvido em Java 17, aplicando
os conceitos de Programação Orientada a Objetos com arquitetura MVC e padrão DAO.

> **Disciplina:** Programação Orientada a Objetos  
> **Instituição:** UNIPAC Barbacena — Ciência da Computação  
> **Professor:** Felipe Roncalli de Paula Carneiro  
> **Autor:** Taagny Augusto Reis da Silva

---

## 🚀 Tecnologias

| Tecnologia | Versão | Finalidade |
|---|---|---|
| Java | 17 | Linguagem principal |
| Java Swing + FlatLaf | 3.6 | Interface gráfica moderna |
| MySQL | 8.0 | Banco de dados relacional |
| XAMPP | — | Servidor MySQL local |
| iText | 5.5.13 | Geração de relatórios PDF |
| Maven | 3.x | Gerenciamento de dependências |
| IntelliJ IDEA | 2025 | Ambiente de desenvolvimento |

---

## ✅ Funcionalidades

- **Cadastro de proprietários** — nome, endereço, telefone e e-mail
- **Cadastro de animais** — nome, espécie, raça, idade, sexo, peso e foto (opcional)
- **Cadastro de serviços** — nome, descrição e preço
- **Lançamento de serviços** — vincula serviço a um animal com data, valor e observações
- **Histórico de serviços** — filtros por animal, tipo de serviço e período (data início/fim)
- **Relatórios em PDF** — dados do cliente, animais e serviços prestados com totais por tipo

---

## 🗂️ Estrutura do Projeto

```
petshop-system/
├── database/
│   └── schema.sql                  # Script de criação do banco
├── src/main/
│   ├── java/com/petshop/
│   │   ├── Main.java
│   │   ├── model/                  # Entidades (Animal, Proprietario, Servico, LancamentoServico)
│   │   ├── dao/                    # Acesso ao banco (CRUD por entidade)
│   │   ├── controller/             # Regras de negócio e validações
│   │   ├── view/                   # Interface gráfica (Swing)
│   │   └── util/                   # ConexaoDB
│   └── resources/
│       └── database.properties     # Configuração da conexão
└── pom.xml
```

---

## ⚙️ Como Executar

### Pré-requisitos

- Java 17+
- Maven 3.x
- XAMPP com MySQL ativo

### Passo a passo

1. **Clone o repositório**
   ```bash
   git clone https://github.com/taagny/petshop-system.git
   cd petshop-system
   ```

2. **Inicie o MySQL via XAMPP**

3. **Crie o banco de dados**
   ```sql
   -- Execute no phpMyAdmin ou MySQL Workbench:
   source database/schema.sql
   ```

4. **Configure a conexão**  
   Edite o arquivo `src/main/resources/database.properties`:
   ```properties
   db.url=jdbc:mysql://localhost:3306/petshop
   db.user=root
   db.password=
   ```

5. **Compile e execute**
   ```bash
   mvn clean install
   mvn exec:java -Dexec.mainClass="com.petshop.Main"
   ```
   Ou abra no IntelliJ IDEA e rode a classe `Main.java` diretamente.

---

## 🗄️ Banco de Dados

O sistema utiliza 4 tabelas no MySQL:

```
proprietario       animal              servico
─────────────      ──────────────      ────────────
id                 id                  id
nome               nome                nome
endereco           especie             descricao
telefone           raca                preco
email              idade
data_cadastro      sexo (M/F)
                   peso
                   foto_path
                   proprietario_id ──► proprietario.id
                   data_cadastro

lancamento_servico
──────────────────
id
animal_id  ──────────────────────────► animal.id
servico_id ──────────────────────────► servico.id
data_lancamento
valor
observacoes
```

---

## 📁 Histórico de Commits

| # | Descrição |
|---|---|
| 1 | Setup inicial — estrutura Maven, .gitignore, README |
| 2 | Script SQL do banco e database.properties |
| 3 | Camada Model (Animal, Proprietario, Servico, LancamentoServico) |
| 4 | Camada DAO com operações CRUD para todas as entidades |
| 5 | Camada Controller com validações de negócio |
| 6 | Views de cadastro (MainFrame, AnimalView, ProprietarioView, ServicoView) |
| 7 | LancamentoView e HistoricoView com filtros por período e tipo de serviço |
| 8 | RelatorioView com geração de PDF via iText |
| 9 | Ajustes finais, SplashScreen e polimento da interface |

---

## 📄 Relatório

O relatório técnico no padrão SBC está disponível junto aos entregáveis do projeto.
