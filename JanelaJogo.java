import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.List;

public class JanelaJogo extends JFrame {

    public static final Color COR_FUNDO        = new Color(240, 244, 248);
    public static final Color COR_BORDA        = new Color(218, 226, 233);
    public static final Color COR_VERMELHO     = new Color(192, 57, 43);
    public static final Color COR_AZUL         = new Color(41, 128, 185);
    public static final Color COR_ROXO         = new Color(142, 68, 173);
    public static final Color COR_VERDE        = new Color(39, 174, 96);
    public static final Color COR_LARANJA      = new Color(211, 84, 0);
    public static final Color COR_TEXTO_CINZA  = new Color(127, 140, 141);
    public static final Color COR_TEXTO_ESCURO = new Color(44, 62, 80);

    public static final String TELA_LOGIN            = "LOGIN";
    public static final String TELA_CADASTRO         = "CADASTRO";
    public static final String TELA_MENU_ALUNO       = "MENU_ALUNO";
    public static final String TELA_MENU_PROFESSOR   = "MENU_PROFESSOR";
    public static final String TELA_ESCOLHER_QUIZ    = "ESCOLHER_QUIZ";
    public static final String TELA_PERGUNTA         = "PERGUNTA";
    public static final String TELA_DESEMPENHO       = "DESEMPENHO";
    public static final String TELA_DESEMPENHO_GERAL = "DESEMPENHO_GERAL";
    public static final String TELA_MATERIA          = "MATERIA";
    public static final String TELA_CRIAR_QUIZ       = "CRIAR_QUIZ";
    public static final String TELA_QUIZES_PRONTOS   = "QUIZES_PRONTOS";
    public static final String TELA_EDITAR_MATERIAL  = "EDITAR_MATERIAL";

    private CardLayout cardLayout;
    private JPanel     painelTelas;

    private Usuario    usuarioLogado;
    private List<Quiz> quizzes;

    private TelaMenuAluno       telaMenuAluno;
    private TelaMenuProfessor   telaMenuProfessor;
    private TelaEscolherQuiz    telaEscolherQuiz;
    private TelaPergunta        telaPergunta;
    private TelaDesempenho      telaDesempenho;
    private TelaDesempenhoGeral telaDesempenhoGeral;
    private TelaQuizesProntos   telaQuizesProntos;
    private TelaEditarMaterial  telaEditarMaterial;

    public JanelaJogo() {
        setTitle("QuimQuest - Etec Júlio de Mesquita");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        quizzes = DadosDemostracao.criarQuizzesDemostracao();
        carregarQuizzesDoBanco();

        cardLayout  = new CardLayout();
        painelTelas = new JPanel(cardLayout);

        painelTelas.add(new TelaLogin(this),    TELA_LOGIN);
        painelTelas.add(new TelaCadastro(this), TELA_CADASTRO);

        telaMenuAluno     = new TelaMenuAluno(this);
        telaMenuProfessor = new TelaMenuProfessor(this);
        painelTelas.add(telaMenuAluno,     TELA_MENU_ALUNO);
        painelTelas.add(telaMenuProfessor, TELA_MENU_PROFESSOR);

        telaEscolherQuiz    = new TelaEscolherQuiz(this);
        telaPergunta        = new TelaPergunta(this);
        telaDesempenho      = new TelaDesempenho(this);
        telaDesempenhoGeral = new TelaDesempenhoGeral(this);
        telaQuizesProntos   = new TelaQuizesProntos(this);
        telaEditarMaterial  = new TelaEditarMaterial(this);

        painelTelas.add(telaEscolherQuiz,        TELA_ESCOLHER_QUIZ);
        painelTelas.add(telaPergunta,            TELA_PERGUNTA);
        painelTelas.add(telaDesempenho,          TELA_DESEMPENHO);
        painelTelas.add(telaDesempenhoGeral,     TELA_DESEMPENHO_GERAL);
        painelTelas.add(new TelaMateria(this),   TELA_MATERIA);
        painelTelas.add(new TelaCriarQuiz(this), TELA_CRIAR_QUIZ);
        painelTelas.add(telaQuizesProntos,       TELA_QUIZES_PRONTOS);
        painelTelas.add(telaEditarMaterial,      TELA_EDITAR_MATERIAL);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                SessaoUsuario.getInstancia().encerrarSessao();
            }
        });

        add(painelTelas);
        mostrarTela(TELA_LOGIN);
    }

    public void mostrarTela(String nomeTela) {
        cardLayout.show(painelTelas, nomeTela);
    }

    public void fazerLogin(Usuario usuario) {
        this.usuarioLogado = usuario;
        carregarHistoricoDoUsuario();
        if (usuario.isProfessor()) {
            telaMenuProfessor.atualizarDados();
            mostrarTela(TELA_MENU_PROFESSOR);
        } else {
            telaMenuAluno.atualizarDados();
            mostrarTela(TELA_MENU_ALUNO);
        }
    }

    public void fazerLogout() {
        usuarioLogado = null;
        SessaoUsuario.getInstancia().encerrarSessao();
        mostrarTela(TELA_LOGIN);
    }

    public void abrirEscolhaDeQuiz() {
        telaEscolherQuiz.carregarQuizzes(quizzes);
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
            salvarResultadoNoBanco(resultado);
        }
        telaDesempenho.mostrarResultadoDoQuiz(resultado);
        mostrarTela(TELA_DESEMPENHO);
    }

    public void abrirDesempenhoIndividual() {
        telaDesempenho.mostrarHistoricoCompleto();
        mostrarTela(TELA_DESEMPENHO);
    }

    public void abrirDesempenhoGeral() {
        telaDesempenhoGeral.carregarDados();
        mostrarTela(TELA_DESEMPENHO_GERAL);
    }

    public void abrirQuizesProntos() {
        telaQuizesProntos.carregarQuizzes();
        mostrarTela(TELA_QUIZES_PRONTOS);
    }

    public void abrirEditarMaterial() {
        telaEditarMaterial.carregarMateriais();
        mostrarTela(TELA_EDITAR_MATERIAL);
    }

    private void carregarQuizzesDoBanco() {
        String sqlQ = "SELECT id_quiz, nome, dificuldade, criado_por FROM quizzes ORDER BY id_quiz";
        String sqlP = "SELECT enunciado, opcao_a, opcao_b, opcao_c, opcao_d, " +
                      "indice_correto, dica, caminho_imagem " +
                      "FROM perguntas WHERE id_quiz = ? ORDER BY id_pergunta";

        try (Connection con = DatabaseConnection.getConexao();
             PreparedStatement psQ = con.prepareStatement(sqlQ);
             ResultSet rsQ = psQ.executeQuery()) {

            while (rsQ.next()) {
                int    idQuiz    = rsQ.getInt("id_quiz");
                String nome      = rsQ.getString("nome");
                String difStr    = rsQ.getString("dificuldade");
                String criadoPor = rsQ.getString("criado_por");

                boolean jaExiste = false;
                for (Quiz q : quizzes) {
                    if (q.getNome().equalsIgnoreCase(nome)) { jaExiste = true; break; }
                }
                if (jaExiste) continue;

                Quiz.Dificuldade dif;
                try {
                    dif = Quiz.Dificuldade.valueOf(difStr);
                } catch (IllegalArgumentException ex) {
                    dif = Quiz.Dificuldade.FACIL;
                }

                Quiz quiz = new Quiz(nome, dif, criadoPor);

                try (PreparedStatement psP = con.prepareStatement(sqlP)) {
                    psP.setInt(1, idQuiz);
                    try (ResultSet rsP = psP.executeQuery()) {
                        while (rsP.next()) {
                            String[] opcoes = {
                                rsP.getString("opcao_a"),
                                rsP.getString("opcao_b"),
                                rsP.getString("opcao_c"),
                                rsP.getString("opcao_d")
                            };
                            quiz.adicionarPergunta(new Pergunta(
                                rsP.getString("enunciado"),
                                opcoes,
                                rsP.getInt("indice_correto"),
                                rsP.getString("dica"),
                                rsP.getString("caminho_imagem")
                            ));
                        }
                    }
                }

                quizzes.add(quiz);
            }

        } catch (SQLException e) {
            System.err.println("[JanelaJogo] Quizzes do banco indisponíveis: " + e.getMessage());
        }
    }

    private void carregarHistoricoDoUsuario() {
        int idUsuario = SessaoUsuario.getInstancia().getId();
        if (idUsuario == 0 || usuarioLogado == null || usuarioLogado.isProfessor()) return;

        try (Connection con = DatabaseConnection.getConexao()) {

            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT pontos FROM usuarios WHERE id_usuario = ?")) {
                ps.setInt(1, idUsuario);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int p = rs.getInt("pontos");
                        if (p > 0) usuarioLogado.adicionarPontos(p);
                    }
                }
            } catch (SQLException e) {
                System.err.println("[JanelaJogo] Coluna pontos indisponível: " + e.getMessage());
            }

            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT nome_quiz, dificuldade, acertos, total_perguntas, pontos " +
                    "FROM resultado_quizzes WHERE id_usuario = ? ORDER BY data_realizado ASC")) {
                ps.setInt(1, idUsuario);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Quiz.Dificuldade dif;
                        try {
                            dif = Quiz.Dificuldade.valueOf(rs.getString("dificuldade"));
                        } catch (IllegalArgumentException ex) {
                            dif = Quiz.Dificuldade.FACIL;
                        }
                        usuarioLogado.adicionarResultado(new ResultadoQuiz(
                            rs.getString("nome_quiz"),
                            dif,
                            rs.getInt("acertos"),
                            rs.getInt("total_perguntas"),
                            rs.getInt("pontos")
                        ));
                    }
                }
            } catch (SQLException e) {
                System.err.println("[JanelaJogo] Tabela resultado_quizzes indisponível: " + e.getMessage());
            }

        } catch (SQLException e) {
            System.err.println("[JanelaJogo] Conexão para histórico falhou: " + e.getMessage());
        }
    }

    private void salvarResultadoNoBanco(ResultadoQuiz resultado) {
        int idUsuario = SessaoUsuario.getInstancia().getId();
        if (idUsuario == 0) return;

        String sqlRes = "INSERT INTO resultado_quizzes " +
                        "(id_usuario, nome_quiz, dificuldade, acertos, total_perguntas, pontos) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";
        String sqlPts = "UPDATE usuarios SET pontos = pontos + ? WHERE id_usuario = ?";

        Connection con = null;
        try {
            con = DatabaseConnection.getConexao();
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(sqlRes)) {
                ps.setInt(1, idUsuario);
                ps.setString(2, resultado.getNomeQuiz());
                ps.setString(3, resultado.getDificuldade().name());
                ps.setInt(4, resultado.getAcertos());
                ps.setInt(5, resultado.getTotalPerguntas());
                ps.setInt(6, resultado.getPontos());
                ps.executeUpdate();
            }

            try (PreparedStatement ps = con.prepareStatement(sqlPts)) {
                ps.setInt(1, resultado.getPontos());
                ps.setInt(2, idUsuario);
                ps.executeUpdate();
            }

            con.commit();

        } catch (SQLException e) {
            System.err.println("[JanelaJogo] Erro ao salvar resultado: " + e.getMessage());
            if (con != null) { try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
        } finally {
            if (con != null) { try { con.close(); } catch (SQLException ex) { ex.printStackTrace(); } }
        }
    }

    public Usuario       getUsuarioLogado()   { return usuarioLogado; }
    public List<Quiz>    getQuizzesDoDomain() { return quizzes; }
    public TelaMenuAluno getTelaMenuAluno()   { return telaMenuAluno; }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new JanelaJogo().setVisible(true));
    }
}
