import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Tela de seleção de quiz ("Escolha um Quiz").
 *
 * Exibe a lista de quizzes disponíveis agrupados em:
 *   - "Quizzes do Sistema"  (criados pelos professores da plataforma)
 *   - "Quizzes do Professor" (criados pelo professor logado — a implementar)
 *
 * O aluno clica em um quiz e o jogo chama jogo.iniciarQuiz(quiz).
 *
 * Recebe a lista via carregarQuizzes(), chamado por JanelaJogo antes
 * de mostrar esta tela.
 */
public class TelaEscolherQuiz extends JPanel {

    private JanelaJogo jogo;
    private Cabecalho  cabecalho;
    private JPanel     painelLista; // painel onde os itens de quiz são montados

    public TelaEscolherQuiz(JanelaJogo jogo) {
        this.jogo = jogo;

        setBackground(JanelaJogo.COR_FUNDO);
        setLayout(new BorderLayout());

        cabecalho = new Cabecalho(jogo, JanelaJogo.TELA_MENU_ALUNO);
        add(cabecalho, BorderLayout.NORTH);

        // Área de conteúdo rolável — margens laterais confortáveis, cards
        // se esticam horizontalmente para preencher o espaço disponível.
        JPanel conteudo = new JPanel();
        conteudo.setBackground(JanelaJogo.COR_FUNDO);
        conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));
        conteudo.setBorder(BorderFactory.createEmptyBorder(24, 60, 24, 60));

        // Botão "← Voltar" — setMaximumSize(preferred) impede o BoxLayout.Y_AXIS
        // de esticar o botão verticalmente quando sobra espaço na tela.
        JButton btnVoltar = criarBotaoVoltar();
        btnVoltar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnVoltar.setMaximumSize(btnVoltar.getPreferredSize());

        // Título "Escolha um " + "Quiz" em vermelho — painel X_AXIS com
        // altura máxima travada para não inflar junto com o espaço extra.
        JPanel linhaTitulo = new JPanel();
        linhaTitulo.setOpaque(false);
        linhaTitulo.setLayout(new BoxLayout(linhaTitulo, BoxLayout.X_AXIS));
        linhaTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        linhaTitulo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel lblEscolha = new JLabel("Escolha um ");
        lblEscolha.setFont(new Font("Segoe UI", Font.BOLD, 24));

        JLabel lblQuiz = new JLabel("Quiz");
        lblQuiz.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblQuiz.setForeground(JanelaJogo.COR_VERMELHO);

        linhaTitulo.add(lblEscolha);
        linhaTitulo.add(lblQuiz);

        // Painel dinâmico com a lista de quizzes
        painelLista = new JPanel();
        painelLista.setOpaque(false);
        painelLista.setLayout(new BoxLayout(painelLista, BoxLayout.Y_AXIS));
        painelLista.setAlignmentX(Component.LEFT_ALIGNMENT);

        conteudo.add(btnVoltar);
        conteudo.add(Box.createVerticalStrut(10));
        conteudo.add(linhaTitulo);
        conteudo.add(Box.createVerticalStrut(12));
        conteudo.add(painelLista);
        // Glue absorbe todo o espaço vertical restante, mantendo os elementos
        // compactados no topo sem se espalharem pela tela.
        conteudo.add(Box.createVerticalGlue());

        add(new JScrollPane(conteudo), BorderLayout.CENTER);
    }

    /**
     * Recebe a lista de quizzes e reconstrói a exibição.
     * Chamado por JanelaJogo.abrirEscolhaDeQuiz().
     */
    public void carregarQuizzes(List<Quiz> quizzes) {
        cabecalho.atualizar();
        painelLista.removeAll();

        // Grupo "Quizzes do Sistema"
        painelLista.add(criarRotuloGrupo("Quizzes do Sistema"));
        painelLista.add(Box.createVerticalStrut(8));

        for (Quiz q : quizzes) {
            if (q.getCriadoPor().equals("Sistema")) {
                painelLista.add(criarItemQuiz(q));
                painelLista.add(Box.createVerticalStrut(6));
            }
        }

        // Grupo "Quizzes do Professor" — quizzes não criados pelo Sistema
        boolean temProfessor = false;
        for (Quiz q : quizzes) {
            if (!q.getCriadoPor().equals("Sistema")) {
                temProfessor = true;
                break;
            }
        }

        if (temProfessor) {
            painelLista.add(Box.createVerticalStrut(16));
            painelLista.add(criarRotuloGrupo("Quizzes do Professor"));
            painelLista.add(Box.createVerticalStrut(8));

            for (Quiz q : quizzes) {
                if (!q.getCriadoPor().equals("Sistema")) {
                    painelLista.add(criarItemQuiz(q));
                    painelLista.add(Box.createVerticalStrut(6));
                }
            }
        }

        revalidate();
        repaint();
    }

    /** Cria o rótulo de seção (ex: "Quizzes do Sistema"). */
    private JLabel criarRotuloGrupo(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(JanelaJogo.COR_TEXTO_CINZA);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    /**
     * Cria uma linha clicável representando um quiz.
     * Exibe: nome, badge de dificuldade, nº de questões, pontos e autor.
     */
    private JPanel criarItemQuiz(Quiz quiz) {
        JPanel item = new JPanel(new BorderLayout(16, 0));
        item.setBackground(Color.WHITE);
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        item.setAlignmentX(Component.LEFT_ALIGNMENT); // ← corrige alinhamento dentro do painelLista
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Barra colorida à esquerda (cor da dificuldade)
        item.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(JanelaJogo.COR_BORDA, 1, true),
                BorderFactory.createMatteBorder(0, 4, 0, 0, corDificuldade(quiz.getDificuldade()))
            ),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));

        // ---- Lado esquerdo: nome + badge ----
        JPanel esquerda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        esquerda.setOpaque(false);

        JLabel lblNome = new JLabel(quiz.getNome());
        lblNome.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel badge = criarBadge(quiz.getDificuldade());

        esquerda.add(lblNome);
        esquerda.add(badge);

        // ---- Lado direito: info + seta ----
        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        direita.setOpaque(false);

        JLabel lblInfo = new JLabel(
            quiz.getNumeroDePerguntas() + " questões  ·  +"
            + quiz.getDificuldade().getPontosPorAcerto() + " pts cada  ·  por "
            + quiz.getCriadoPor()
        );
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblInfo.setForeground(JanelaJogo.COR_TEXTO_CINZA);

        JLabel seta = new JLabel("▶");
        seta.setForeground(JanelaJogo.COR_TEXTO_CINZA);

        direita.add(lblInfo);
        direita.add(seta);

        item.add(esquerda, BorderLayout.WEST);
        item.add(direita,  BorderLayout.EAST);

        // Clique inicia o quiz
        item.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                jogo.iniciarQuiz(quiz);
            }
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                item.setBackground(new Color(248, 248, 248));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                item.setBackground(Color.WHITE);
            }
        });

        return item;
    }

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
                jogo.mostrarTela(JanelaJogo.TELA_MENU_ALUNO);
            }
        });
        return btn;
    }
}
