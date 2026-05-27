# QuimQuest — Como Compilar e Executar

## Requisitos

- **JDK 8 ou superior** — baixe em: https://adoptium.net  
- **MySQL 8.x** rodando localmente com o banco `QuimQuest` criado  
- **Driver JDBC** do MySQL (veja a seção abaixo)

---

## PASSO OBRIGATÓRIO — Instalar o driver JDBC do MySQL

O projeto usa banco de dados. Sem o `.jar` do driver o programa não inicia.

### 1. Baixar o Connector/J

Acesse: https://dev.mysql.com/downloads/connector/j/  
Escolha **"Platform Independent"** → baixe o `.zip`.

Dentro do zip há um arquivo chamado:
```
mysql-connector-j-9.x.x.jar   (ou mysql-connector-java-8.x.x.jar)
```

### 2. Copiar para a pasta `lib/`

Coloque o `.jar` dentro de:
```
JogoFaculdade/
└── lib/
    └── mysql-connector-j-9.x.x.jar   ← aqui
```

### 3. VS Code — reconhecimento automático

O arquivo `.vscode/settings.json` já está configurado para ler `lib/**/*.jar`.  
Após copiar o `.jar`, pressione **Ctrl+Shift+P → "Java: Clean Java Language Server Workspace"**
e reinicie o VS Code. O erro `ClassNotFoundException` desaparecerá.

### 4. Executar pelo terminal (com classpath manual)

```powershell
# Compilar
javac -cp ".;lib\mysql-connector-j-9.x.x.jar" *.java

# Executar
java -cp ".;lib\mysql-connector-j-9.x.x.jar" Main
```

> No Linux/Mac substitua `;` por `:` no `-cp`.

---

## Executar pelo VS Code (recomendado)

1. Instale a extensão **"Extension Pack for Java"** (Microsoft)
2. Coloque o `.jar` do driver em `lib/` (passo acima)
3. Abra a pasta `JogoFaculdade` no VS Code
4. Abra `Main.java` e clique em ▶ **Run**

---

## Executar pelo IntelliJ IDEA

1. **File > Open** → pasta `JogoFaculdade`
2. **File > Project Structure > Modules > Dependencies > + > JARs**  
   → selecione o `.jar` em `lib/`
3. Clique com botão direito em `Main.java` → **Run 'Main.main()'**

---

## Fluxo de acesso

| Ação           | Tela de Cadastro                               | Tela de Login                  |
|----------------|------------------------------------------------|-------------------------------|
| Aluno novo     | Código = RM numérico, perfil = Aluno           | RM + senha                    |
| Professor novo | Código = matrícula, perfil = Professor, chave = `ETEC_QUIMICA_2026` | matrícula + senha |

O e-mail institucional é gerado automaticamente:
- Aluno:     `<rm>@aluno.cps.sp.gov.br`
- Professor: `<matricula>@cps.sp.gov.br`

---

## Estrutura dos arquivos

```
JogoFaculdade/
│
├── lib/
│   └── mysql-connector-j-*.jar   ← driver JDBC (baixar manualmente)
│
├── Main.java                     ← Ponto de entrada
├── JanelaJogo.java               ← Janela principal + CardLayout
├── DatabaseConnection.java       ← Conexão JDBC com MySQL
├── SessaoUsuario.java            ← Dados do usuário logado (singleton)
│
├── (Modelos)
│   ├── Usuario.java
│   ├── Quiz.java, Pergunta.java, ResultadoQuiz.java
│   └── DadosDemostracao.java
│
├── Cabecalho.java
│
└── (Telas)
    ├── TelaLogin.java            ← Autenticação via banco (RM + senha)
    ├── TelaCadastro.java         ← Cadastro com geração de e-mail CPS
    ├── TelaMenuAluno.java
    ├── TelaMenuProfessor.java
    ├── TelaEscolherQuiz.java
    ├── TelaPergunta.java
    ├── TelaDesempenho.java
    ├── TelaMateria.java          ← Conteúdo carregado do banco MySQL
    ├── TelaCriarQuiz.java
    ├── TelaQuizesProntos.java
    └── TelaDesempenhoGeral.java
```
