import javax.swing.*;
import java.awt.*;
import java.util.List;

public class JanelaJogo extends JFrame {

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

    private JPanel     painelCartas;
    private CardLayout cardLayout;

    private Usuario      usuarioLogado;
    private List<Quiz>   quizzesDoDomain;

    private TelaMenuAluno       telaMenuAluno;
    private TelaMenuProfessor   telaMenuProfessor;
    private TelaEscolherQuiz    telaEscolherQuiz;
    private TelaPergunta        telaPergunta;
    private TelaDesempenho      telaDesempenho;
    private TelaQuizesProntos   telaQuizesProntos;
    private TelaDesempenhoGeral telaDesempenhoGeral;

    public JanelaJogo() {
        setTitle("QuimQuest — Jogo Educativo de Química");
        setSize(1280, 800);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        quizzesDoDomain = DadosDemostracao.criarQuizzesDemostracao();

        cardLayout   = new CardLayout();
        painelCartas = new JPanel(cardLayout);

        criarEAdicionarTelas();

        add(painelCartas);

        cardLayout.show(painelCartas, TELA_LOGIN);

        setVisible(true);
    }

    private void criarEAdicionarTelas() {
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

    public void mostrarTela(String nomeTela) {
        cardLayout.show(painelCartas, nomeTela);
    }

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

    public void fazerLogout() {
        this.usuarioLogado = null;
        mostrarTela(TELA_LOGIN);
    }

    public void abrirEscolhaDeQuiz() {
        telaEscolherQuiz.carregarQuizzes(quizzesDoDomain);
        mostrarTela(TELA_ESCOLHER_QUIZ);
    }

    public void iniciarQuiz(Quiz quiz) {
        telaPergunta.carregarQuiz(quiz);
        mostrarTela(TELA_PERGUNTA);
    }

    public void finalizarQuiz(ResultadoQuiz resultado) {
        if (usuarioLogado != null) {
            usuarioLogado.adicionarPontos(resultado.getPontos());
            usuarioLogado.adicionarResultado(resultado);
        }
        telaDesempenho.mostrarResultadoDoQuiz(resultado);
        mostrarTela(TELA_DESEMPENHO);
    }

    public void abrirDesempenhoIndividual() {
        telaDesempenho.mostrarHistoricoCompleto();
        mostrarTela(TELA_DESEMPENHO);
    }

    public void abrirQuizesProntos() {
        telaQuizesProntos.carregarQuizzes();
        mostrarTela(TELA_QUIZES_PRONTOS);
    }

    public void abrirDesempenhoGeral() {
        telaDesempenhoGeral.carregarDados();
        mostrarTela(TELA_DESEMPENHO_GERAL);
    }

    public Usuario         getUsuarioLogado()    { return usuarioLogado; }
    public List<Quiz>      getQuizzesDoDomain()  { return quizzesDoDomain; }
    public TelaMenuAluno   getTelaMenuAluno()     { return telaMenuAluno; }
}
