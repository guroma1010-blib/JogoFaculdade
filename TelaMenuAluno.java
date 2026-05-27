import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Menu principal do aluno ("Painel do Aluno").
 *
 * Exibe:
 *   - Cabeçalho vermelho com nome, pontos, Início e Sair
 *   - Saudação "Olá, <nome>"
 *   - Três cards de ação: JOGAR, MATÉRIA, DESEMPENHO INDIVIDUAL
 *   - Stats rápidos: Meus pontos e Quizzes feitos
 *   - Meu Histórico (últimos quizzes feitos)
 *
 * O método atualizarDados() atualiza tudo com os dados do usuário logado.
 * Ele é chamado por JanelaJogo antes de mostrar esta tela.
 */
public class TelaMenuAluno extends JPanel {

    private JanelaJogo jogo;
    private Cabecalho  cabecalho;

    // Componentes que mudam conforme o usuário
    private JLabel       lblSaudacao;
    private JLabel       lblPontos;
    private JLabel       lblQuizzesFeitosNum;
    private JPanel       painelHistorico;

    public TelaMenuAluno(JanelaJogo jogo) {
        this.jogo = jogo;

        setBackground(JanelaJogo.COR_FUNDO);
        setLayout(new BorderLayout());

        // O cabeçalho está na parte de cima; "Início" leva para esta mesma tela
        cabecalho = new Cabecalho(jogo, JanelaJogo.TELA_MENU_ALUNO);
        add(cabecalho, BorderLayout.NORTH);

        // Conteúdo central (rolável)
        JPanel conteudo = new JPanel();
        conteudo.setBackground(JanelaJogo.COR_FUNDO);
        conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));
        conteudo.setBorder(BorderFactory.createEmptyBorder(28, 36, 28, 36));

        conteudo.add(construirSaudacao());
        conteudo.add(Box.createVerticalStrut(24));
        conteudo.add(construirCardsDeAcao());
        conteudo.add(Box.createVerticalStrut(20));
        conteudo.add(construirStats());
        conteudo.add(Box.createVerticalStrut(24));
        conteudo.add(construirSecaoHistorico());

        add(new JScrollPane(conteudo), BorderLayout.CENTER);
    }

    // ------------------------------------------------------------------
    //  Seção: Saudação
    // ------------------------------------------------------------------
    private JPanel construirSaudacao() {
        JPanel painel = new JPanel();
        painel.setOpaque(false);
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // "Olá, aluno" — "aluno" em vermelho
        JPanel linhaNome = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linhaNome.setOpaque(false);

        JLabel lblOla = new JLabel("Olá, ");
        lblOla.setFont(new Font("Segoe UI", Font.BOLD, 30));

        lblSaudacao = new JLabel("aluno"); // atualizado em atualizarDados()
        lblSaudacao.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblSaudacao.setForeground(JanelaJogo.COR_VERMELHO);

        linhaNome.add(lblOla);
        linhaNome.add(lblSaudacao);

        JLabel lblSubtitulo = new JLabel("O que você quer fazer hoje?");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitulo.setForeground(JanelaJogo.COR_TEXTO_CINZA);

        painel.add(linhaNome);
        painel.add(Box.createVerticalStrut(4));
        painel.add(lblSubtitulo);

        return painel;
    }

    // ------------------------------------------------------------------
    //  Seção: Cards de ação (JOGAR, MATÉRIA, DESEMPENHO)
    // ------------------------------------------------------------------
    private JPanel construirCardsDeAcao() {
        JPanel linha = new JPanel(new GridLayout(1, 3, 16, 0));
        linha.setOpaque(false);
        linha.setAlignmentX(Component.LEFT_ALIGNMENT);
        linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        linha.add(criarCardDeAcao("JOGAR",                "Escolher um quiz",        JanelaJogo.COR_VERMELHO,
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    jogo.abrirEscolhaDeQuiz();
                }
            }
        ));

        linha.add(criarCardDeAcao("MATÉRIA",              "Revisar conteúdo",         JanelaJogo.COR_ROXO,
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    jogo.mostrarTela(JanelaJogo.TELA_MATERIA);
                }
            }
        ));

        linha.add(criarCardDeAcao("DESEMPENHO INDIVIDUAL","Ver meu desempenho",       JanelaJogo.COR_AZUL,
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    jogo.abrirDesempenhoIndividual();
                }
            }
        ));

        return linha;
    }

    /**
     * Cria um card clicável (botão estilizado como card branco).
     *
     * @param titulo  texto em destaque (ex: "JOGAR")
     * @param subtitu texto descritivo menor
     * @param cor     cor do título
     * @param acao    o que acontece quando clicado
     */
    private JPanel criarCardDeAcao(String titulo, String subtitu,
                                    Color cor, ActionListener acao) {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(JanelaJogo.COR_BORDA, 1, true),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitulo.setForeground(cor);

        JLabel lblSub = new JLabel(subtitu);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(JanelaJogo.COR_TEXTO_CINZA);

        card.add(Box.createVerticalGlue());
        card.add(lblTitulo);
        card.add(Box.createVerticalStrut(6));
        card.add(lblSub);
        card.add(Box.createVerticalGlue());

        // O card todo funciona como botão
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                acao.actionPerformed(null);
            }
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBackground(new Color(248, 248, 248)); // leve destaque
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBackground(Color.WHITE);
            }
        });

        return card;
    }

    // ------------------------------------------------------------------
    //  Seção: Stats rápidos (pontos e quizzes feitos)
    // ------------------------------------------------------------------
    private JPanel construirStats() {
        JPanel linha = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        linha.setOpaque(false);
        linha.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblPontos = new JLabel("0");
        linha.add(criarMiniStat(lblPontos, "Meus pontos", JanelaJogo.COR_VERMELHO));

        lblQuizzesFeitosNum = new JLabel("0");
        linha.add(criarMiniStat(lblQuizzesFeitosNum, "Quizzes feitos", JanelaJogo.COR_TEXTO_ESCURO));

        return linha;
    }

    private JPanel criarMiniStat(JLabel lblNumero, String descricao, Color cor) {
        JPanel stat = new JPanel();
        stat.setBackground(Color.WHITE);
        stat.setLayout(new BoxLayout(stat, BoxLayout.Y_AXIS));
        stat.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 3, 0, 0, cor), // barra colorida à esquerda
            BorderFactory.createEmptyBorder(10, 14, 10, 40)
        ));

        lblNumero.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblNumero.setForeground(cor);

        JLabel lblDesc = new JLabel(descricao);
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDesc.setForeground(JanelaJogo.COR_TEXTO_CINZA);

        stat.add(lblNumero);
        stat.add(lblDesc);
        return stat;
    }

    // ------------------------------------------------------------------
    //  Seção: Histórico de quizzes
    // ------------------------------------------------------------------
    private JPanel construirSecaoHistorico() {
        JPanel secao = new JPanel();
        secao.setOpaque(false);
        secao.setLayout(new BoxLayout(secao, BoxLayout.Y_AXIS));
        secao.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Título "Meu Histórico"
        JPanel linhaTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linhaTitulo.setOpaque(false);

        JLabel lblMeu = new JLabel("Meu ");
        lblMeu.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JLabel lblHistorico = new JLabel("Histórico");
        lblHistorico.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHistorico.setForeground(JanelaJogo.COR_VERMELHO);

        linhaTitulo.add(lblMeu);
        linhaTitulo.add(lblHistorico);

        // Painel onde as linhas do histórico são adicionadas dinamicamente
        painelHistorico = new JPanel();
        painelHistorico.setOpaque(false);
        painelHistorico.setLayout(new BoxLayout(painelHistorico, BoxLayout.Y_AXIS));

        secao.add(linhaTitulo);
        secao.add(Box.createVerticalStrut(12));
        secao.add(painelHistorico);

        return secao;
    }

    // ------------------------------------------------------------------
    //  Atualização de dados (chamado por JanelaJogo antes de exibir)
    // ------------------------------------------------------------------

    /**
     * Atualiza todos os campos dinâmicos com os dados do usuário logado.
     * Chamado por JanelaJogo.fazerLogin() e sempre que a tela é exibida.
     */
    public void atualizarDados() {
        cabecalho.atualizar();

        Usuario u = jogo.getUsuarioLogado();
        if (u == null) return;

        lblSaudacao.setText(u.getNome());
        lblPontos.setText(String.valueOf(u.getPontos()));
        lblQuizzesFeitosNum.setText(String.valueOf(u.getHistorico().size()));

        // Reconstrói o histórico
        painelHistorico.removeAll();
        List<ResultadoQuiz> historico = u.getHistorico();

        if (historico.isEmpty()) {
            JLabel vazio = new JLabel("Você ainda não fez nenhum quiz.");
            vazio.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            vazio.setForeground(JanelaJogo.COR_TEXTO_CINZA);
            painelHistorico.add(vazio);
        } else {
            // Exibe do mais recente para o mais antigo
            for (int i = historico.size() - 1; i >= 0; i--) {
                painelHistorico.add(criarLinhaHistorico(historico.get(i)));
                painelHistorico.add(Box.createVerticalStrut(6));
            }
        }

        revalidate();
        repaint();
    }

    /** Cria uma linha do histórico para um resultado de quiz. */
    private JPanel criarLinhaHistorico(ResultadoQuiz r) {
        JPanel linha = new JPanel(new BorderLayout(16, 0));
        linha.setBackground(Color.WHITE);
        linha.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(JanelaJogo.COR_BORDA, 1, true),
            BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));
        linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        // Nome do quiz (esquerda)
        JLabel lblNome = new JLabel(r.getNomeQuiz());
        lblNome.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Dificuldade + acertos + pontos (direita)
        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        direita.setOpaque(false);

        JLabel lblDificuldade = badgeDificuldade(r.getDificuldade());
        JLabel lblAcertos = new JLabel("Acertos: " + r.getAcertos() + "/" + r.getTotalPerguntas());
        lblAcertos.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JLabel lblPontosR = new JLabel("+" + r.getPontos() + " pts");
        lblPontosR.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPontosR.setForeground(JanelaJogo.COR_VERMELHO);

        direita.add(lblDificuldade);
        direita.add(lblAcertos);
        direita.add(lblPontosR);

        linha.add(lblNome,  BorderLayout.WEST);
        linha.add(direita, BorderLayout.EAST);
        return linha;
    }

    /** Cria o badge colorido de dificuldade. */
    private JLabel badgeDificuldade(Quiz.Dificuldade d) {
        JLabel badge = new JLabel(d.getRotulo().toUpperCase());
        badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        badge.setForeground(corDificuldade(d));
        badge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(corDificuldade(d), 1, true),
            BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));
        return badge;
    }

    private Color corDificuldade(Quiz.Dificuldade d) {
        switch (d) {
            case FACIL:   return JanelaJogo.COR_VERDE;
            case MEDIO:   return JanelaJogo.COR_LARANJA;
            case DIFICIL: return JanelaJogo.COR_VERMELHO;
            default:      return JanelaJogo.COR_TEXTO_CINZA;
        }
    }
}
