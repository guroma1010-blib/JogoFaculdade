import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Tela "Quizes Prontos" — visão do professor sobre todos os quizzes cadastrados.
 *
 * Exibe:
 *   - Botões de filtro por dificuldade (Todos / Fácil / Médio / Difícil)
 *   - Cards expansivos com o nome do quiz e a lista de perguntas
 *
 * Chamada por JanelaJogo.abrirQuizesProntos().
 */
public class TelaQuizesProntos extends JPanel {

    private static final String[] CHAVES_FILTRO  = {"TODOS", "FACIL", "MEDIO", "DIFICIL"};
    private static final String[] ROTULOS_FILTRO = {"Todos", "Fácil", "Médio", "Difícil"};

    private JanelaJogo jogo;
    private Cabecalho  cabecalho;
    private JPanel     painelLista;
    private JButton[]  botosFiltro;
    private String     filtroAtivo = "TODOS";

    private final Color[] coresFiltro = {
        JanelaJogo.COR_TEXTO_ESCURO,
        JanelaJogo.COR_VERDE,
        JanelaJogo.COR_LARANJA,
        JanelaJogo.COR_VERMELHO
    };

    public TelaQuizesProntos(JanelaJogo jogo) {
        this.jogo = jogo;

        setBackground(JanelaJogo.COR_FUNDO);
        setLayout(new BorderLayout());

        cabecalho = new Cabecalho(jogo, JanelaJogo.TELA_MENU_PROFESSOR);
        add(cabecalho, BorderLayout.NORTH);

        JPanel conteudo = new JPanel();
        conteudo.setBackground(JanelaJogo.COR_FUNDO);
        conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));
        conteudo.setBorder(BorderFactory.createEmptyBorder(24, 36, 24, 36));

        // Botão "← Voltar"
        JButton btnVoltar = criarBotaoVoltar();
        JPanel linhaBotao = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linhaBotao.setOpaque(false);
        linhaBotao.setAlignmentX(Component.LEFT_ALIGNMENT);
        linhaBotao.add(btnVoltar);

        // Título "Quizes Prontos"
        JPanel linhaTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        linhaTitulo.setOpaque(false);
        linhaTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblQuizes  = new JLabel("Quizes ");
        lblQuizes.setFont(new Font("Segoe UI", Font.BOLD, 24));
        JLabel lblProntos = new JLabel("Prontos");
        lblProntos.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblProntos.setForeground(JanelaJogo.COR_VERMELHO);
        linhaTitulo.add(lblQuizes);
        linhaTitulo.add(lblProntos);

        // Filtros por dificuldade
        JPanel linhaFiltros = criarLinhaFiltros();

        // Painel dinâmico com a lista de cards
        painelLista = new JPanel();
        painelLista.setOpaque(false);
        painelLista.setLayout(new BoxLayout(painelLista, BoxLayout.Y_AXIS));
        painelLista.setAlignmentX(Component.LEFT_ALIGNMENT);

        conteudo.add(linhaBotao);
        conteudo.add(Box.createVerticalStrut(12));
        conteudo.add(linhaTitulo);
        conteudo.add(Box.createVerticalStrut(16));
        conteudo.add(linhaFiltros);
        conteudo.add(Box.createVerticalStrut(20));
        conteudo.add(painelLista);

        add(new JScrollPane(conteudo), BorderLayout.CENTER);
    }

    /**
     * Atualiza o cabeçalho e recarrega a lista.
     * Chamado por JanelaJogo.abrirQuizesProntos().
     */
    public void carregarQuizzes() {
        cabecalho.atualizar();
        atualizarLista();
    }

    // ------------------------------------------------------------------
    //  Filtros
    // ------------------------------------------------------------------

    private JPanel criarLinhaFiltros() {
        JPanel linha = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        linha.setOpaque(false);
        linha.setAlignmentX(Component.LEFT_ALIGNMENT);

        botosFiltro = new JButton[ROTULOS_FILTRO.length];

        for (int i = 0; i < ROTULOS_FILTRO.length; i++) {
            final int idx = i;
            JButton btn = new JButton(ROTULOS_FILTRO[i]);
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    filtroAtivo = CHAVES_FILTRO[idx];
                    refrescarEstiloBotoesFiltro();
                    atualizarLista();
                }
            });
            botosFiltro[i] = btn;
            linha.add(btn);
        }

        refrescarEstiloBotoesFiltro();
        return linha;
    }

    private void refrescarEstiloBotoesFiltro() {
        for (int i = 0; i < botosFiltro.length; i++) {
            boolean ativo = filtroAtivo.equals(CHAVES_FILTRO[i]);
            Color   cor   = coresFiltro[i];
            botosFiltro[i].setForeground(ativo ? Color.WHITE : cor);
            botosFiltro[i].setBackground(ativo ? cor : Color.WHITE);
            botosFiltro[i].setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(cor, 1, true),
                BorderFactory.createEmptyBorder(5, 14, 5, 14)
            ));
        }
    }

    // ------------------------------------------------------------------
    //  Lista de quizzes
    // ------------------------------------------------------------------

    private void atualizarLista() {
        painelLista.removeAll();
        List<Quiz> quizzes = jogo.getQuizzesDoDomain();
        boolean algum = false;

        for (Quiz q : quizzes) {
            if (passaFiltro(q)) {
                painelLista.add(criarCardQuiz(q));
                painelLista.add(Box.createVerticalStrut(12));
                algum = true;
            }
        }

        if (!algum) {
            JLabel lblVazio = new JLabel("Nenhum quiz encontrado para este filtro.");
            lblVazio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            lblVazio.setForeground(JanelaJogo.COR_TEXTO_CINZA);
            lblVazio.setAlignmentX(Component.LEFT_ALIGNMENT);
            painelLista.add(lblVazio);
        }

        revalidate();
        repaint();
    }

    private boolean passaFiltro(Quiz q) {
        if (filtroAtivo.equals("TODOS"))   return true;
        if (filtroAtivo.equals("FACIL")   && q.getDificuldade() == Quiz.Dificuldade.FACIL)   return true;
        if (filtroAtivo.equals("MEDIO")   && q.getDificuldade() == Quiz.Dificuldade.MEDIO)   return true;
        if (filtroAtivo.equals("DIFICIL") && q.getDificuldade() == Quiz.Dificuldade.DIFICIL) return true;
        return false;
    }

    // ------------------------------------------------------------------
    //  Card de um quiz (cabeçalho + linhas de perguntas)
    // ------------------------------------------------------------------

    private JPanel criarCardQuiz(Quiz quiz) {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setBorder(BorderFactory.createLineBorder(JanelaJogo.COR_BORDA, 1, true));

        Color corDif = corDificuldade(quiz.getDificuldade());

        // ---- Cabeçalho do card ----
        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setBackground(Color.WHITE);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, corDif),
            BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        JPanel esquerda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        esquerda.setOpaque(false);
        JLabel lblNome = new JLabel(quiz.getNome());
        lblNome.setFont(new Font("Segoe UI", Font.BOLD, 14));
        esquerda.add(lblNome);
        esquerda.add(criarBadge(quiz.getDificuldade()));

        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        direita.setOpaque(false);
        JLabel lblMeta = new JLabel(
            quiz.getNumeroDePerguntas() + " questões  ·  por " + quiz.getCriadoPor()
        );
        lblMeta.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblMeta.setForeground(JanelaJogo.COR_TEXTO_CINZA);
        direita.add(lblMeta);

        header.add(esquerda, BorderLayout.WEST);
        header.add(direita,  BorderLayout.EAST);
        card.add(header);

        // Separador entre cabeçalho e perguntas
        JSeparator sep = new JSeparator();
        sep.setForeground(JanelaJogo.COR_BORDA);
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        card.add(sep);

        // ---- Linhas de perguntas ----
        List<Pergunta> perguntas = quiz.getPerguntas();
        for (int i = 0; i < perguntas.size(); i++) {
            Pergunta p = perguntas.get(i);

            JPanel linha = new JPanel(new BorderLayout(10, 0));
            linha.setBackground(i % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
            linha.setAlignmentX(Component.LEFT_ALIGNMENT);
            linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            linha.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 14));

            JLabel lblNum = new JLabel((i + 1) + ".");
            lblNum.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblNum.setForeground(JanelaJogo.COR_TEXTO_CINZA);
            lblNum.setPreferredSize(new Dimension(24, 24));

            String textoEnunciado = p.getEnunciado().length() > 90
                ? p.getEnunciado().substring(0, 90) + "…"
                : p.getEnunciado();
            JLabel lblEnunciado = new JLabel(textoEnunciado);
            lblEnunciado.setFont(new Font("Segoe UI", Font.PLAIN, 13));

            JLabel lblTag = new JLabel(p.temImagem() ? "📎 imagem" : "");
            lblTag.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lblTag.setForeground(JanelaJogo.COR_TEXTO_CINZA);

            linha.add(lblNum,       BorderLayout.WEST);
            linha.add(lblEnunciado, BorderLayout.CENTER);
            linha.add(lblTag,       BorderLayout.EAST);
            card.add(linha);
        }

        return card;
    }

    // ------------------------------------------------------------------
    //  Helpers visuais
    // ------------------------------------------------------------------

    private JLabel criarBadge(Quiz.Dificuldade d) {
        JLabel badge = new JLabel(d.getRotulo().toUpperCase());
        badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        Color cor = corDificuldade(d);
        badge.setForeground(cor);
        badge.setBackground(new Color(cor.getRed(), cor.getGreen(), cor.getBlue(), 30));
        badge.setOpaque(true);
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
                jogo.mostrarTela(JanelaJogo.TELA_MENU_PROFESSOR);
            }
        });
        return btn;
    }
}
