import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * ============================================================
 *  JanelaJogo — Controlador Central + Janela Principal (JFrame)
 * ============================================================
 *
 *  Esta classe tem dois papéis:
 *
 *  1) É o JFrame (a janela do jogo).
 *     - Define tamanho, título e comportamento ao fechar.
 *
 *  2) É o CONTROLADOR central (padrão "Controller").
 *     - Guarda o estado global: usuário logado, lista de quizzes.
 *     - Gerencia a troca de telas via CardLayout.
 *     - Todas as telas recebem "this" para poder chamar os métodos
 *       de navegação (mostrarTela, fazerLogin, iniciarQuiz, etc.).
 *
 *  Fluxo de navegação:
 *
 *    TelaLogin
 *       ├─(aluno)────► TelaMenuAluno ──► TelaEscolherQuiz ──► TelaPergunta ──► TelaDesempenho
 *       │                             ├─► TelaMateria
 *       │                             └─► TelaDesempenho (histórico)
 *       └─(professor)─► TelaMenuProfessor ──► TelaCriarQuiz
 *                                         └─► TelaQuizesProntos (a implementar)
 */
public class JanelaJogo extends JFrame {

    // ==========================================================
    //  Constantes de cor — usadas por todas as telas
    // ==========================================================
    public static final Color COR_VERMELHO     = new Color(180, 0, 0);
    public static final Color COR_FUNDO        = new Color(238, 238, 238);
    public static final Color COR_BRANCO       = Color.WHITE;
    public static final Color COR_VERDE        = new Color(40, 167, 69);
    public static final Color COR_LARANJA      = new Color(220, 120, 0);
    public static final Color COR_ROXO         = new Color(120, 0, 180);
    public static final Color COR_AZUL         = new Color(0, 80, 180);
    public static final Color COR_TEXTO_ESCURO = new Color(33, 33, 33);
    public static final Color COR_TEXTO_CINZA  = new Color(108, 117, 125);
    public static final Color COR_BORDA        = new Color(220, 220, 220);

    // ==========================================================
    //  Nomes das telas para o CardLayout
    //  (use estas constantes em vez de strings soltas)
    // ==========================================================
    public static final String TELA_LOGIN              = "login";
    public static final String TELA_MENU_ALUNO        = "menuAluno";
    public static final String TELA_MENU_PROFESSOR    = "menuProfessor";
    public static final String TELA_ESCOLHER_QUIZ     = "escolherQuiz";
    public static final String TELA_PERGUNTA          = "pergunta";
    public static final String TELA_DESEMPENHO        = "desempenho";
    public static final String TELA_MATERIA           = "materia";
    public static final String TELA_CRIAR_QUIZ        = "criarQuiz";
    public static final String TELA_QUIZES_PRONTOS    = "quizesProntos";
    public static final String TELA_DESEMPENHO_GERAL  = "desempenhoGeral";
    public static final String TELA_CADASTRO          = "cadastro";

    // ==========================================================
    //  Componentes internos
    // ==========================================================
    private JPanel     painelCartas; // o "baralho" com todas as telas
    private CardLayout cardLayout;   // gerencia qual tela está visível

    // ==========================================================
    //  Estado global do jogo
    // ==========================================================
    private Usuario      usuarioLogado; // null enquanto ninguém estiver logado
    private List<Quiz>   quizzesDoDomain; // quizzes do "banco de dados"

    // ==========================================================
    //  Referências às telas (para poder chamar métodos nelas)
    // ==========================================================
    private TelaMenuAluno       telaMenuAluno;
    private TelaMenuProfessor   telaMenuProfessor;
    private TelaEscolherQuiz    telaEscolherQuiz;
    private TelaPergunta        telaPergunta;
    private TelaDesempenho      telaDesempenho;
    private TelaQuizesProntos   telaQuizesProntos;
    private TelaDesempenhoGeral telaDesempenhoGeral;

    // ==========================================================
    //  Construtor
    // ==========================================================
    public JanelaJogo() {
        // ---- configurações do JFrame ----
        setTitle("QuimQuest — Jogo Educativo de Química");
        setSize(1280, 800);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);                     // centraliza na tela
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ---- carrega os quizzes de demonstração ----
        quizzesDoDomain = DadosDemostracao.criarQuizzesDemostracao();

        // ---- monta o CardLayout ----
        cardLayout   = new CardLayout();
        painelCartas = new JPanel(cardLayout);

        criarEAdicionarTelas();

        add(painelCartas);

        // começa pela tela de login
        cardLayout.show(painelCartas, TELA_LOGIN);

        setVisible(true);
    }

    /**
     * Instancia todas as telas e as registra no CardLayout.
     * A ordem de criação não importa para o CardLayout.
     */
    private void criarEAdicionarTelas() {
        // Cada tela recebe 'this' para poder navegar
        TelaCadastro       telaCadastro     = new TelaCadastro(this);
        TelaLogin          telaLogin        = new TelaLogin(this);
        telaMenuAluno       = new TelaMenuAluno(this);
        telaMenuProfessor   = new TelaMenuProfessor(this);
        telaEscolherQuiz    = new TelaEscolherQuiz(this);
        telaPergunta        = new TelaPergunta(this);
        telaDesempenho      = new TelaDesempenho(this);
        TelaMateria        telaMateria       = new TelaMateria(this);
        TelaCriarQuiz      telaCriarQuiz     = new TelaCriarQuiz(this);
        telaQuizesProntos   = new TelaQuizesProntos(this);
        telaDesempenhoGeral = new TelaDesempenhoGeral(this);

        // Registra cada tela com uma chave string
        painelCartas.add(telaLogin,          TELA_LOGIN);
        painelCartas.add(telaCadastro,       TELA_CADASTRO);
        painelCartas.add(telaMenuAluno,      TELA_MENU_ALUNO);
        painelCartas.add(telaMenuProfessor,  TELA_MENU_PROFESSOR);
        painelCartas.add(telaEscolherQuiz,   TELA_ESCOLHER_QUIZ);
        painelCartas.add(telaPergunta,       TELA_PERGUNTA);
        painelCartas.add(telaDesempenho,     TELA_DESEMPENHO);
        painelCartas.add(telaMateria,        TELA_MATERIA);
        painelCartas.add(telaCriarQuiz,      TELA_CRIAR_QUIZ);
        painelCartas.add(telaQuizesProntos,  TELA_QUIZES_PRONTOS);
        painelCartas.add(telaDesempenhoGeral,TELA_DESEMPENHO_GERAL);
    }

    // ==========================================================
    //  Métodos de navegação — chamados pelas telas
    // ==========================================================

    /**
     * Exibe a tela com o nome indicado.
     * Use as constantes TELA_LOGIN, TELA_MENU_ALUNO, etc.
     */
    public void mostrarTela(String nomeTela) {
        cardLayout.show(painelCartas, nomeTela);
    }

    /**
     * Chamado pela TelaLogin quando o login é bem-sucedido.
     * Define o usuário logado e redireciona para o menu correto.
     */
    public void fazerLogin(Usuario usuario) {
        this.usuarioLogado = usuario;

        if (usuario.isProfessor()) {
            telaMenuProfessor.atualizarDados();
            mostrarTela(TELA_MENU_PROFESSOR);
        } else {
            telaMenuAluno.atualizarDados();
            mostrarTela(TELA_MENU_ALUNO);
        }
    }

    /**
     * Desloga o usuário atual e volta para a tela de login.
     */
    public void fazerLogout() {
        this.usuarioLogado = null;
        mostrarTela(TELA_LOGIN);
    }

    /**
     * Abre a tela de seleção de quiz.
     * Passa a lista de quizzes disponíveis para a tela.
     */
    public void abrirEscolhaDeQuiz() {
        telaEscolherQuiz.carregarQuizzes(quizzesDoDomain);
        mostrarTela(TELA_ESCOLHER_QUIZ);
    }

    /**
     * Inicia a sessão de jogo com o quiz selecionado.
     * @param quiz o quiz que o aluno escolheu
     */
    public void iniciarQuiz(Quiz quiz) {
        telaPergunta.carregarQuiz(quiz);
        mostrarTela(TELA_PERGUNTA);
    }

    /**
     * Chamado por TelaPergunta ao terminar o último quiz.
     * Salva o resultado no histórico e exibe a tela de desempenho.
     */
    public void finalizarQuiz(ResultadoQuiz resultado) {
        if (usuarioLogado != null) {
            usuarioLogado.adicionarPontos(resultado.getPontos());
            usuarioLogado.adicionarResultado(resultado);
        }
        telaDesempenho.mostrarResultadoDoQuiz(resultado);
        mostrarTela(TELA_DESEMPENHO);
    }

    /**
     * Abre a tela de desempenho individual (a partir do menu do aluno).
     */
    public void abrirDesempenhoIndividual() {
        telaDesempenho.mostrarHistoricoCompleto();
        mostrarTela(TELA_DESEMPENHO);
    }

    /**
     * Abre a tela de quizes prontos (visão do professor).
     */
    public void abrirQuizesProntos() {
        telaQuizesProntos.carregarQuizzes();
        mostrarTela(TELA_QUIZES_PRONTOS);
    }

    /**
     * Abre a tela de desempenho geral dos alunos (visão do professor).
     */
    public void abrirDesempenhoGeral() {
        telaDesempenhoGeral.carregarDados();
        mostrarTela(TELA_DESEMPENHO_GERAL);
    }

    // ==========================================================
    //  Getters
    // ==========================================================

    public Usuario         getUsuarioLogado()    { return usuarioLogado; }
    public List<Quiz>      getQuizzesDoDomain()  { return quizzesDoDomain; }
    public TelaMenuAluno   getTelaMenuAluno()     { return telaMenuAluno; }
}
