import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Tela de Desempenho — serve para dois propósitos:
 *
 *   1) Resultado imediato após terminar um quiz
 *      → chamada por JanelaJogo.finalizarQuiz(resultado)
 *      → exibe o resultado deste quiz e um botão para jogar de novo
 *
 *   2) Histórico completo do aluno
 *      → chamada por JanelaJogo.abrirDesempenhoIndividual()
 *      → exibe todas as estatísticas e o histórico completo
 *
 * A variável 'modoResultadoImediato' controla qual modo exibir.
 */
public class TelaDesempenho extends JPanel {

    private JanelaJogo jogo;
    private Cabecalho  cabecalho;

    // Painel central reconstruído a cada exibição
    private JPanel areaCentral;

    public TelaDesempenho(JanelaJogo jogo) {
        this.jogo = jogo;

        setBackground(JanelaJogo.COR_FUNDO);
        setLayout(new BorderLayout());

        cabecalho = new Cabecalho(jogo, JanelaJogo.TELA_MENU_ALUNO);
        add(cabecalho, BorderLayout.NORTH);

        areaCentral = new JPanel();
        areaCentral.setBackground(JanelaJogo.COR_FUNDO);
        add(areaCentral, BorderLayout.CENTER);
    }

    // ------------------------------------------------------------------
    //  Modo 1: Resultado imediato (após terminar um quiz)
    // ------------------------------------------------------------------

    /**
     * Chamado por JanelaJogo.finalizarQuiz() logo após o aluno terminar.
     */
    public void mostrarResultadoDoQuiz(ResultadoQuiz resultado) {
        cabecalho.atualizar();

        remove(areaCentral);
        areaCentral = construirResultadoImediato(resultado);
        add(areaCentral, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private JPanel construirResultadoImediato(ResultadoQuiz r) {
        JPanel painel = new JPanel();
        painel.setBackground(JanelaJogo.COR_FUNDO);
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBorder(BorderFactory.createEmptyBorder(28, 36, 28, 36));

        // Botão "← Voltar"
        JPanel linhaBotao = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linhaBotao.setOpaque(false);
        linhaBotao.add(criarBotaoVoltar());
        linhaBotao.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Título "Resultado"
        JPanel linhaTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        linhaTitulo.setOpaque(false);
        linhaTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblResultado = new JLabel("Resultado do ");
        lblResultado.setFont(new Font("Segoe UI", Font.BOLD, 26));

        JLabel lblQuiz = new JLabel("Quiz");
        lblQuiz.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblQuiz.setForeground(JanelaJogo.COR_VERMELHO);

        linhaTitulo.add(lblResultado);
        linhaTitulo.add(lblQuiz);

        JLabel lblNomeQuiz = new JLabel(r.getNomeQuiz());
        lblNomeQuiz.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblNomeQuiz.setForeground(JanelaJogo.COR_TEXTO_CINZA);
        lblNomeQuiz.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Três cards de stat: acertos, porcentagem, pontos
        JPanel stats = new JPanel(new GridLayout(1, 3, 16, 0));
        stats.setOpaque(false);
        stats.setAlignmentX(Component.LEFT_ALIGNMENT);
        stats.setMaximumSize(new Dimension(700, 100));

        int porcento = r.getPorcentagem();
        Color corPorcento = porcento >= 70 ? JanelaJogo.COR_VERDE : JanelaJogo.COR_VERMELHO;

        stats.add(criarCardStat(
            r.getAcertos() + " / " + r.getTotalPerguntas(),
            "Acertos",
            JanelaJogo.COR_VERMELHO
        ));
        stats.add(criarCardStat(porcento + "%", "Porcentagem de Acertos", corPorcento));
        stats.add(criarCardStat("+" + r.getPontos(), "Pontos Ganhos",
            new Color(180, 140, 0)));

        // Mensagem motivacional
        JLabel lblMensagem = new JLabel(mensagemMotivacional(porcento));
        lblMensagem.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblMensagem.setForeground(JanelaJogo.COR_TEXTO_CINZA);
        lblMensagem.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Botões de ação
        JPanel linhaBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        linhaBotoes.setOpaque(false);
        linhaBotoes.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnJogarNovamente = new JButton("Jogar Novamente");
        estilizarBotao(btnJogarNovamente, JanelaJogo.COR_VERMELHO, Color.WHITE);
        btnJogarNovamente.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jogo.abrirEscolhaDeQuiz();
            }
        });

        JButton btnInicio = new JButton("Ir para o Início");
        estilizarBotao(btnInicio, Color.WHITE, JanelaJogo.COR_TEXTO_ESCURO);
        btnInicio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jogo.mostrarTela(JanelaJogo.TELA_MENU_ALUNO);
                jogo.getTelaMenuAluno().atualizarDados(); // atualiza pontos no menu
            }
        });

        linhaBotoes.add(btnJogarNovamente);
        linhaBotoes.add(btnInicio);

        painel.add(linhaBotao);
        painel.add(Box.createVerticalStrut(12));
        painel.add(linhaTitulo);
        painel.add(Box.createVerticalStrut(4));
        painel.add(lblNomeQuiz);
        painel.add(Box.createVerticalStrut(24));
        painel.add(stats);
        painel.add(Box.createVerticalStrut(20));
        painel.add(lblMensagem);
        painel.add(Box.createVerticalStrut(28));
        painel.add(linhaBotoes);

        return painel;
    }

    // ------------------------------------------------------------------
    //  Modo 2: Histórico completo (acessado pelo menu do aluno)
    // ------------------------------------------------------------------

    /**
     * Chamado por JanelaJogo.abrirDesempenhoIndividual().
     */
    public void mostrarHistoricoCompleto() {
        cabecalho.atualizar();

        remove(areaCentral);
        areaCentral = construirHistoricoCompleto();
        add(areaCentral, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private JPanel construirHistoricoCompleto() {
        JPanel painel = new JPanel();
        painel.setBackground(JanelaJogo.COR_FUNDO);
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBorder(BorderFactory.createEmptyBorder(28, 36, 28, 36));

        // Botão "← Voltar"
        JPanel linhaBotao = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linhaBotao.setOpaque(false);
        linhaBotao.add(criarBotaoVoltar());
        linhaBotao.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Título "Meu Desempenho Individual"
        JPanel linhaTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        linhaTitulo.setOpaque(false);
        linhaTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblMeu = new JLabel("Meu ");
        lblMeu.setFont(new Font("Segoe UI", Font.BOLD, 26));

        JLabel lblDesemp = new JLabel("Desempenho");
        lblDesemp.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblDesemp.setForeground(JanelaJogo.COR_VERMELHO);

        JLabel lblInd = new JLabel(" Individual");
        lblInd.setFont(new Font("Segoe UI", Font.BOLD, 26));

        linhaTitulo.add(lblMeu);
        linhaTitulo.add(lblDesemp);
        linhaTitulo.add(lblInd);

        // Stats gerais
        Usuario u = jogo.getUsuarioLogado();
        List<ResultadoQuiz> historico = (u != null) ? u.getHistorico() : new java.util.ArrayList<ResultadoQuiz>();

        int porcento = (u != null) ? u.calcularPorcentagemAcertos() : 0;
        Color corPorcento = porcento >= 70 ? JanelaJogo.COR_VERDE : JanelaJogo.COR_VERMELHO;

        JPanel stats = new JPanel(new GridLayout(1, 3, 16, 0));
        stats.setOpaque(false);
        stats.setAlignmentX(Component.LEFT_ALIGNMENT);
        stats.setMaximumSize(new Dimension(700, 110));

        stats.add(criarCardStat(String.valueOf(historico.size()),
            "Quizzes Feitos", JanelaJogo.COR_VERMELHO));
        stats.add(criarCardStat(porcento + "%",
            "Porcentagem de Acertos", corPorcento));
        stats.add(criarCardStat(String.valueOf(u != null ? u.getPontos() : 0),
            "Pontos Totais", new Color(180, 140, 0)));

        // Tabela de histórico
        JLabel lblHistorico = new JLabel("Histórico de Quizzes");
        lblHistorico.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblHistorico.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel tabelaHistorico = construirTabelaHistorico(historico);
        tabelaHistorico.setAlignmentX(Component.LEFT_ALIGNMENT);

        painel.add(linhaBotao);
        painel.add(Box.createVerticalStrut(12));
        painel.add(linhaTitulo);
        painel.add(Box.createVerticalStrut(24));
        painel.add(stats);
        painel.add(Box.createVerticalStrut(28));
        painel.add(lblHistorico);
        painel.add(Box.createVerticalStrut(12));
        painel.add(tabelaHistorico);

        return painel;
    }

    private JPanel construirTabelaHistorico(List<ResultadoQuiz> historico) {
        JPanel tabela = new JPanel();
        tabela.setOpaque(false);
        tabela.setLayout(new BoxLayout(tabela, BoxLayout.Y_AXIS));
        tabela.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        if (historico.isEmpty()) {
            JLabel vazio = new JLabel("Você ainda não completou nenhum quiz.");
            vazio.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            vazio.setForeground(JanelaJogo.COR_TEXTO_CINZA);
            tabela.add(vazio);
            return tabela;
        }

        // Cabeçalho da tabela
        JPanel cabTabela = new JPanel(new GridLayout(1, 5, 0, 0));
        cabTabela.setBackground(new Color(230, 230, 230));
        cabTabela.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        cabTabela.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        String[] cols = {"QUIZ", "DIFICULDADE", "ACERTOS", "% ACERTOS", "PONTOS"};
        for (String col : cols) {
            JLabel h = new JLabel(col);
            h.setFont(new Font("Segoe UI", Font.BOLD, 11));
            h.setForeground(JanelaJogo.COR_TEXTO_CINZA);
            cabTabela.add(h);
        }
        tabela.add(cabTabela);

        // Linhas
        for (ResultadoQuiz r : historico) {
            tabela.add(criarLinhaTabela(r));
            tabela.add(Box.createVerticalStrut(2));
        }

        return tabela;
    }

    private JPanel criarLinhaTabela(ResultadoQuiz r) {
        JPanel linha = new JPanel(new GridLayout(1, 5, 0, 0));
        linha.setBackground(Color.WHITE);
        linha.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(JanelaJogo.COR_BORDA, 1, true),
            BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));
        linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

        int porcento = r.getPorcentagem();
        Color corPorc = porcento >= 70 ? JanelaJogo.COR_VERDE : JanelaJogo.COR_VERMELHO;

        linha.add(texto(r.getNomeQuiz(), null));
        linha.add(badgeDificuldade(r.getDificuldade()));
        linha.add(texto(r.getAcertos() + "/" + r.getTotalPerguntas(), null));
        linha.add(texto(porcento + "%", corPorc));
        linha.add(texto("+" + r.getPontos() + " pts", JanelaJogo.COR_VERMELHO));

        return linha;
    }

    // ------------------------------------------------------------------
    //  Helpers visuais
    // ------------------------------------------------------------------

    private JPanel criarCardStat(String valor, String descricao, Color cor) {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, cor),
            BorderFactory.createEmptyBorder(14, 20, 14, 20)
        ));

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblValor.setForeground(cor);

        JLabel lblDesc = new JLabel(descricao);
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDesc.setForeground(JanelaJogo.COR_TEXTO_CINZA);

        card.add(lblValor);
        card.add(lblDesc);
        return card;
    }

    private JLabel texto(String t, Color cor) {
        JLabel lbl = new JLabel(t);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        if (cor != null) lbl.setForeground(cor);
        return lbl;
    }

    private JLabel badgeDificuldade(Quiz.Dificuldade d) {
        JLabel badge = new JLabel(d.getRotulo().toUpperCase());
        badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        Color cor = corDificuldade(d);
        badge.setForeground(cor);
        badge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(cor, 1, true),
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

    private String mensagemMotivacional(int porcento) {
        if (porcento == 100) return "Perfeito! Você acertou tudo!";
        if (porcento >= 80)  return "Excelente! Você foi muito bem!";
        if (porcento >= 60)  return "Bom trabalho! Continue estudando.";
        return "Não desista! Revise a matéria e tente novamente.";
    }

    private JButton criarBotaoVoltar() {
        JButton btn = new JButton("← Voltar");
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setBackground(Color.WHITE);
        btn.setForeground(JanelaJogo.COR_TEXTO_ESCURO);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(JanelaJogo.COR_BORDA, 1, true),
            BorderFactory.createEmptyBorder(6, 16, 6, 16)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jogo.mostrarTela(JanelaJogo.TELA_MENU_ALUNO);
            }
        });
        return btn;
    }

    private void estilizarBotao(JButton btn, Color fundo, Color texto) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(fundo);
        btn.setForeground(texto);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(JanelaJogo.COR_BORDA, 1, true),
            BorderFactory.createEmptyBorder(10, 24, 10, 24)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
