import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Tela de Perguntas — o coração do jogo.
 *
 * Fluxo de uma sessão de quiz:
 *   1. carregarQuiz(quiz)  →  mostra a 1ª pergunta
 *   2. Aluno clica em uma das 4 alternativas
 *   3. Feedback imediato: verde (certo) ou vermelho (errado)
 *   4. Botão "Próxima Questão →" ou "Ver Resultado" aparece
 *   5. Ao terminar todas as perguntas → jogo.finalizarQuiz(resultado)
 *
 * Botão "Dica (-5 pts)":
 *   Exibe a dica e desconta 5 pontos do potencial de ganho desta questão.
 */
public class TelaPergunta extends JPanel {

    private JanelaJogo jogo;
    private Cabecalho  cabecalho;

    // ---- estado da sessão de quiz ----
    private Quiz          quizAtual;
    private int           indicePerguntaAtual; // 0, 1, 2, ...
    private int           acertos;
    private int           pontosTotais;
    private boolean       dicaUsada;           // dica já foi clicada nesta pergunta?

    // ---- componentes que mudam a cada pergunta ----
    private JLabel    lblContador;     // "Questão 1 / 5"
    private JLabel    lblDificuldade;  // badge FÁCIL / MÉDIO / DIFÍCIL
    private JLabel    lblEnunciado;    // texto da pergunta
    private JLabel    lblImagem;       // imagem opcional da pergunta (visível só quando há imagem)
    private JButton[] botoesMarcacao;  // [A, B, C, D]
    private JLabel[]  labelsMarcacao;  // "A", "B", "C", "D" abaixo de cada botão
    private JLabel    lblFeedback;     // "Correto!" ou "Errado. A resposta era B."
    private JButton   btnAvancar;      // "Próxima Questão →" ou "Ver Resultado"
    private JButton   btnDica;         // "Dica (-5 pts)"
    private JLabel    lblDicaTexto;    // texto da dica (aparece ao clicar)

    public TelaPergunta(JanelaJogo jogo) {
        this.jogo = jogo;

        setBackground(JanelaJogo.COR_FUNDO);
        setLayout(new BorderLayout());

        cabecalho = new Cabecalho(jogo, JanelaJogo.TELA_MENU_ALUNO);
        add(cabecalho, BorderLayout.NORTH);

        add(construirAreaPerguntas(), BorderLayout.CENTER);
    }

    /** Constrói toda a área de perguntas (cabeçalho da questão + alternativas). */
    private JPanel construirAreaPerguntas() {
        JPanel painel = new JPanel();
        painel.setBackground(JanelaJogo.COR_FUNDO);
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBorder(BorderFactory.createEmptyBorder(24, 36, 24, 36));

        // ---- linha de progresso: "Questão X / Y" + badge dificuldade ----
        JPanel linhaTopo = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        linhaTopo.setOpaque(false);
        linhaTopo.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblContador = new JLabel("Questão 1 / 1");
        lblContador.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblContador.setForeground(JanelaJogo.COR_TEXTO_CINZA);

        lblDificuldade = new JLabel("FÁCIL");
        lblDificuldade.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblDificuldade.setForeground(JanelaJogo.COR_VERDE);
        lblDificuldade.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(JanelaJogo.COR_VERDE, 1, true),
            BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));

        linhaTopo.add(lblContador);
        linhaTopo.add(lblDificuldade);

        // ---- card branco com o enunciado da pergunta (+ imagem opcional à direita) ----
        JPanel cardEnunciado = new JPanel(new BorderLayout(16, 0));
        cardEnunciado.setBackground(Color.WHITE);
        cardEnunciado.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(JanelaJogo.COR_BORDA, 1, true),
            BorderFactory.createEmptyBorder(16, 20, 16, 20)
        ));
        cardEnunciado.setAlignmentX(Component.LEFT_ALIGNMENT);
        // altura maior para acomodar imagens (160px de imagem + 32px de padding)
        cardEnunciado.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        lblEnunciado = new JLabel("<html>Enunciado da pergunta</html>");
        lblEnunciado.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cardEnunciado.add(lblEnunciado, BorderLayout.CENTER);

        // Imagem: oculta por padrão; exibida em mostrarPergunta() quando há imagem
        lblImagem = new JLabel();
        lblImagem.setPreferredSize(new Dimension(220, 160));
        lblImagem.setHorizontalAlignment(SwingConstants.CENTER);
        lblImagem.setVisible(false);
        cardEnunciado.add(lblImagem, BorderLayout.EAST);

        // ---- grade 2×2 com as alternativas ----
        JPanel gradeAlternativas = new JPanel(new GridLayout(2, 2, 12, 12));
        gradeAlternativas.setOpaque(false);
        gradeAlternativas.setAlignmentX(Component.LEFT_ALIGNMENT);
        gradeAlternativas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 240));

        botoesMarcacao = new JButton[4];
        labelsMarcacao = new JLabel[4];
        String[] letras = {"A", "B", "C", "D"};

        for (int i = 0; i < 4; i++) {
            final int indice = i; // variável final para usar no ActionListener

            JPanel celula = new JPanel(new BorderLayout(0, 4));
            celula.setOpaque(false);

            botoesMarcacao[i] = new JButton("Opção " + letras[i]);
            estilizarBotaoAlternativa(botoesMarcacao[i]);

            labelsMarcacao[i] = new JLabel(letras[i]);
            labelsMarcacao[i].setFont(new Font("Segoe UI", Font.PLAIN, 11));
            labelsMarcacao[i].setForeground(JanelaJogo.COR_TEXTO_CINZA);

            botoesMarcacao[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    responderPergunta(indice);
                }
            });

            celula.add(botoesMarcacao[i],  BorderLayout.CENTER);
            celula.add(labelsMarcacao[i],  BorderLayout.SOUTH);
            gradeAlternativas.add(celula);
        }

        // ---- área de feedback (aparece após responder) ----
        lblFeedback = new JLabel(" ");
        lblFeedback.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblFeedback.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ---- dica ----
        btnDica = new JButton("Dica (-5 pts)");
        btnDica.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnDica.setBackground(new Color(255, 240, 150));
        btnDica.setForeground(new Color(100, 80, 0));
        btnDica.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 170, 0), 1, true),
            BorderFactory.createEmptyBorder(6, 16, 6, 16)
        ));
        btnDica.setFocusPainted(false);
        btnDica.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnDica.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarDica();
            }
        });

        lblDicaTexto = new JLabel(" ");
        lblDicaTexto.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblDicaTexto.setForeground(new Color(100, 80, 0));
        lblDicaTexto.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ---- botão de avanço (oculto até responder) ----
        btnAvancar = new JButton("Próxima Questão →");
        btnAvancar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAvancar.setBackground(JanelaJogo.COR_VERMELHO);
        btnAvancar.setForeground(Color.WHITE);
        btnAvancar.setBorderPainted(false);
        btnAvancar.setFocusPainted(false);
        btnAvancar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAvancar.setVisible(false);
        btnAvancar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                avancarPergunta();
            }
        });

        // ---- monta o layout vertical ----
        painel.add(linhaTopo);
        painel.add(Box.createVerticalStrut(14));
        painel.add(cardEnunciado);
        painel.add(Box.createVerticalStrut(20));
        painel.add(gradeAlternativas);
        painel.add(Box.createVerticalStrut(16));

        // Linha com Dica e Avançar lado a lado
        JPanel linhaRodape = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        linhaRodape.setOpaque(false);
        linhaRodape.setAlignmentX(Component.LEFT_ALIGNMENT);
        linhaRodape.add(btnDica);
        linhaRodape.add(btnAvancar);

        painel.add(linhaRodape);
        painel.add(Box.createVerticalStrut(10));
        painel.add(lblDicaTexto);
        painel.add(Box.createVerticalStrut(10));
        painel.add(lblFeedback);

        return painel;
    }

    // ------------------------------------------------------------------
    //  Lógica da sessão de quiz
    // ------------------------------------------------------------------

    /**
     * Inicia uma nova sessão com o quiz informado.
     * Chamado por JanelaJogo.iniciarQuiz().
     */
    public void carregarQuiz(Quiz quiz) {
        this.quizAtual           = quiz;
        this.indicePerguntaAtual = 0;
        this.acertos             = 0;
        this.pontosTotais        = 0;

        cabecalho.atualizar();
        mostrarPergunta(0);
    }

    /** Atualiza todos os componentes com os dados da pergunta no índice dado. */
    private void mostrarPergunta(int indice) {
        List<Pergunta> perguntas = quizAtual.getPerguntas();
        Pergunta p = perguntas.get(indice);

        dicaUsada = false;

        // Contador e badge
        lblContador.setText("Questão " + (indice + 1) + " / " + perguntas.size());
        lblDificuldade.setText(quizAtual.getDificuldade().getRotulo().toUpperCase());
        atualizarCorBadge();

        // Enunciado
        lblEnunciado.setText("<html>" + p.getEnunciado() + "</html>");

        // Imagem opcional
        if (p.temImagem()) {
            try {
                ImageIcon original = new ImageIcon(p.getCaminhoImagem());
                Image escalonada   = original.getImage()
                                             .getScaledInstance(220, 160, Image.SCALE_SMOOTH);
                lblImagem.setIcon(new ImageIcon(escalonada));
                lblImagem.setVisible(true);
            } catch (Exception ex) {
                lblImagem.setVisible(false);
            }
        } else {
            lblImagem.setIcon(null);
            lblImagem.setVisible(false);
        }

        // Alternativas
        String[] opcoes = p.getOpcoes();
        String[] letras = {"A", "B", "C", "D"};
        for (int i = 0; i < 4; i++) {
            botoesMarcacao[i].setText(letras[i] + ") " + opcoes[i]);
            botoesMarcacao[i].setEnabled(true);
            estilizarBotaoAlternativa(botoesMarcacao[i]); // restaura estilo padrão
        }

        // Limpa feedback e dica
        lblFeedback.setText(" ");
        lblDicaTexto.setText(" ");
        btnDica.setVisible(p.getDica() != null && !p.getDica().isEmpty());
        btnAvancar.setVisible(false);

        revalidate();
        repaint();
    }

    /** Processa a resposta escolhida pelo aluno. */
    private void responderPergunta(int indiceEscolhido) {
        Pergunta p       = quizAtual.getPerguntas().get(indicePerguntaAtual);
        boolean  certa   = p.isCorreta(indiceEscolhido);
        int      correto = p.getIndiceCorreto();

        // Bloqueia os botões para evitar duplo clique
        for (JButton btn : botoesMarcacao) {
            btn.setEnabled(false);
        }

        // Destaca a alternativa correta em verde
        botoesMarcacao[correto].setBackground(JanelaJogo.COR_VERDE);
        botoesMarcacao[correto].setForeground(Color.WHITE);

        if (certa) {
            // Calcula pontos: valor base menos desconto da dica
            int valorBase = quizAtual.getDificuldade().getPontosPorAcerto();
            int desconto  = dicaUsada ? 5 : 0;
            int pontos    = Math.max(0, valorBase - desconto);

            acertos++;
            pontosTotais += pontos;

            lblFeedback.setForeground(JanelaJogo.COR_VERDE);
            lblFeedback.setText("✓  Correto! +" + pontos + " pts");
        } else {
            // Destaca a escolha errada em vermelho
            botoesMarcacao[indiceEscolhido].setBackground(new Color(200, 50, 50));
            botoesMarcacao[indiceEscolhido].setForeground(Color.WHITE);

            String[] letras = {"A", "B", "C", "D"};
            lblFeedback.setForeground(JanelaJogo.COR_VERMELHO);
            lblFeedback.setText("✗  Errado. A resposta correta era " + letras[correto] + ".");
        }

        // Altera o texto do botão se for a última pergunta
        boolean ultima = (indicePerguntaAtual == quizAtual.getNumeroDePerguntas() - 1);
        btnAvancar.setText(ultima ? "Ver Resultado →" : "Próxima Questão →");
        btnAvancar.setVisible(true);

        revalidate();
        repaint();
    }

    /** Avança para a próxima pergunta ou finaliza o quiz. */
    private void avancarPergunta() {
        indicePerguntaAtual++;

        if (indicePerguntaAtual < quizAtual.getNumeroDePerguntas()) {
            mostrarPergunta(indicePerguntaAtual);
        } else {
            // Fim do quiz: cria o resultado e avisa o jogo
            ResultadoQuiz resultado = new ResultadoQuiz(
                quizAtual.getNome(),
                quizAtual.getDificuldade(),
                acertos,
                quizAtual.getNumeroDePerguntas(),
                pontosTotais
            );
            jogo.finalizarQuiz(resultado);
        }
    }

    /** Exibe a dica e desconta 5 pts do potencial desta pergunta. */
    private void mostrarDica() {
        if (dicaUsada) return;
        dicaUsada = true;
        btnDica.setEnabled(false);

        String dica = quizAtual.getPerguntas().get(indicePerguntaAtual).getDica();
        lblDicaTexto.setText("Dica: " + dica);
        revalidate();
    }

    // ------------------------------------------------------------------
    //  Helpers visuais
    // ------------------------------------------------------------------

    private void estilizarBotaoAlternativa(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setBackground(Color.WHITE);
        btn.setForeground(JanelaJogo.COR_TEXTO_ESCURO);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(JanelaJogo.COR_BORDA, 1, true),
            BorderFactory.createEmptyBorder(16, 14, 16, 14)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
    }

    private void atualizarCorBadge() {
        Color cor;
        switch (quizAtual.getDificuldade()) {
            case FACIL:   cor = JanelaJogo.COR_VERDE;    break;
            case MEDIO:   cor = JanelaJogo.COR_LARANJA;  break;
            case DIFICIL: cor = JanelaJogo.COR_VERMELHO; break;
            default:      cor = JanelaJogo.COR_TEXTO_CINZA;
        }
        lblDificuldade.setForeground(cor);
        lblDificuldade.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(cor, 1, true),
            BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));
    }
}
