import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Tela de Criação de Quiz — acessada pelo professor.
 *
 * Para cada questão o professor pode preencher:
 *   - Enunciado (texto)
 *   - Imagem opcional (botão abre JFileChooser → seleciona JPG/PNG)
 *   - Quatro opções A, B, C, D
 *   - Resposta correta (combo A/B/C/D)
 *   - Dica (opcional, custa -5 pts ao aluno)
 *
 * Ao salvar, cada pergunta é criada com o caminho de imagem selecionado
 * (ou null se não houver imagem), e o quiz é adicionado à lista global.
 */
public class TelaCriarQuiz extends JPanel {

    private JanelaJogo jogo;
    private Cabecalho  cabecalho;

    // ---- Campos do cabeçalho do quiz ----
    private JTextField        campoNomeQuiz;
    private JComboBox<String> comboDificuldade;
    private JComboBox<Integer> comboNumQuestoes;

    // ---- Painel dinâmico com as questões ----
    private JPanel painelQuestoes;

    // ---- Listas paralelas — índice i = questão i ----
    private List<JTextField>        camposEnunciado  = new ArrayList<>();
    private List<JTextField[]>      camposOpcoes     = new ArrayList<>(); // [A,B,C,D]
    private List<JComboBox<String>> combosCorreta    = new ArrayList<>();
    private List<JTextField>        camposDica       = new ArrayList<>();
    private List<String>            caminhosImagem   = new ArrayList<>(); // null = sem imagem
    private List<JLabel>            lblPreviewImagem = new ArrayList<>(); // exibe nome do arquivo

    // ---- Aviso de validação ----
    private JLabel lblAviso;

    public TelaCriarQuiz(JanelaJogo jogo) {
        this.jogo = jogo;

        setBackground(JanelaJogo.COR_FUNDO);
        setLayout(new BorderLayout());

        cabecalho = new Cabecalho(jogo, JanelaJogo.TELA_MENU_PROFESSOR);
        add(cabecalho, BorderLayout.NORTH);
        add(construirFormulario(), BorderLayout.CENTER);
    }

    // ------------------------------------------------------------------
    //  Construção do formulário
    // ------------------------------------------------------------------

    private JScrollPane construirFormulario() {
        JPanel corpo = new JPanel();
        corpo.setBackground(JanelaJogo.COR_FUNDO);
        corpo.setLayout(new BoxLayout(corpo, BoxLayout.Y_AXIS));
        corpo.setBorder(BorderFactory.createEmptyBorder(24, 36, 24, 36));

        // Botão "← Voltar"
        JPanel linhaBotaoVoltar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linhaBotaoVoltar.setOpaque(false);
        linhaBotaoVoltar.setAlignmentX(Component.LEFT_ALIGNMENT);
        linhaBotaoVoltar.add(criarBotaoVoltar());

        // Título "Criar  Quiz Completo"
        JPanel linhaTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        linhaTitulo.setOpaque(false);
        linhaTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblCriar = new JLabel("Criar");
        lblCriar.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblCriar.setForeground(JanelaJogo.COR_VERMELHO);

        JLabel lblQuizCompleto = new JLabel("Quiz Completo");
        lblQuizCompleto.setFont(new Font("Segoe UI", Font.BOLD, 26));

        linhaTitulo.add(lblCriar);
        linhaTitulo.add(lblQuizCompleto);

        // ---- Faixa de configuração: Nome | Dificuldade | Nº de Questões ----
        JPanel faixaConfig = new JPanel(new GridLayout(1, 3, 16, 0));
        faixaConfig.setBackground(Color.WHITE);
        faixaConfig.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(JanelaJogo.COR_BORDA, 1, true),
            BorderFactory.createEmptyBorder(16, 20, 16, 20)
        ));
        faixaConfig.setAlignmentX(Component.LEFT_ALIGNMENT);
        faixaConfig.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        // Coluna 1: Nome do Quiz
        JPanel colNome = coluna();
        colNome.add(rotulo("NOME DO QUIZ"));
        colNome.add(Box.createVerticalStrut(4));
        campoNomeQuiz = campo();
        campoNomeQuiz.setToolTipText("Ex: Quiz de Vidrarias...");
        colNome.add(campoNomeQuiz);

        // Coluna 2: Dificuldade
        JPanel colDif = coluna();
        colDif.add(rotulo("DIFICULDADE"));
        colDif.add(Box.createVerticalStrut(4));
        comboDificuldade = new JComboBox<>(new String[]{
            "Fácil (+10 pts)", "Médio (+20 pts)", "Difícil (+30 pts)"
        });
        comboDificuldade.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        colDif.add(comboDificuldade);

        // Coluna 3: Número de questões
        JPanel colNum = coluna();
        colNum.add(rotulo("NÚMERO DE QUESTÕES"));
        colNum.add(Box.createVerticalStrut(4));
        Integer[] nOptions = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        comboNumQuestoes = new JComboBox<>(nOptions);
        comboNumQuestoes.setSelectedItem(5);
        comboNumQuestoes.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboNumQuestoes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { reconstruirQuestoes(); }
        });
        colNum.add(comboNumQuestoes);

        faixaConfig.add(colNome);
        faixaConfig.add(colDif);
        faixaConfig.add(colNum);

        // ---- Aviso de validação ----
        lblAviso = new JLabel(" ");
        lblAviso.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblAviso.setForeground(JanelaJogo.COR_VERMELHO);
        lblAviso.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ---- Painel de questões (reconstruído ao mudar o número) ----
        painelQuestoes = new JPanel();
        painelQuestoes.setOpaque(false);
        painelQuestoes.setLayout(new BoxLayout(painelQuestoes, BoxLayout.Y_AXIS));
        painelQuestoes.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ---- Botão Salvar ----
        JButton btnSalvar = new JButton("Salvar Quiz");
        btnSalvar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSalvar.setBackground(JanelaJogo.COR_VERMELHO);
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.setBorderPainted(false);
        btnSalvar.setFocusPainted(false);
        btnSalvar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSalvar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnSalvar.setBorder(BorderFactory.createEmptyBorder(12, 32, 12, 32));
        btnSalvar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { salvarQuiz(); }
        });

        corpo.add(linhaBotaoVoltar);
        corpo.add(Box.createVerticalStrut(12));
        corpo.add(linhaTitulo);
        corpo.add(Box.createVerticalStrut(20));
        corpo.add(faixaConfig);
        corpo.add(Box.createVerticalStrut(8));
        corpo.add(lblAviso);
        corpo.add(Box.createVerticalStrut(8));
        corpo.add(painelQuestoes);
        corpo.add(Box.createVerticalStrut(20));
        corpo.add(btnSalvar);

        reconstruirQuestoes();
        return new JScrollPane(corpo);
    }

    // ------------------------------------------------------------------
    //  Lógica do formulário
    // ------------------------------------------------------------------

    /**
     * Reconstrói o painel de questões com base no número selecionado.
     * Zera as listas paralelas para evitar dados de uma reconstrução anterior.
     */
    private void reconstruirQuestoes() {
        camposEnunciado.clear();
        camposOpcoes.clear();
        combosCorreta.clear();
        camposDica.clear();
        caminhosImagem.clear();
        lblPreviewImagem.clear();

        painelQuestoes.removeAll();

        int n = (Integer) comboNumQuestoes.getSelectedItem();
        for (int i = 0; i < n; i++) {
            // inicializa as listas com valores-padrão; o formulário preenche ao criar
            caminhosImagem.add(null);
            painelQuestoes.add(criarFormularioQuestao(i));
            painelQuestoes.add(Box.createVerticalStrut(16));
        }

        painelQuestoes.revalidate();
        painelQuestoes.repaint();
    }

    /**
     * Cria o card de formulário de uma questão (enunciado + imagem + opções + dica).
     * Obs: os campos são adicionados às listas paralelas DENTRO deste método.
     */
    private JPanel criarFormularioQuestao(final int indice) {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(JanelaJogo.COR_BORDA, 1, true),
            BorderFactory.createEmptyBorder(16, 20, 16, 20)
        ));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Cabeçalho: "QUESTÃO X"
        JLabel lblNumero = new JLabel("QUESTÃO " + (indice + 1));
        lblNumero.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblNumero.setForeground(JanelaJogo.COR_VERMELHO);
        lblNumero.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ---- Enunciado ----
        JTextField campoEnunc = campo();
        camposEnunciado.add(campoEnunc);

        // ---- Seção de imagem ----
        /*
         * O professor pode associar uma imagem (JPG, PNG, GIF) a esta questão.
         * O JFileChooser abre no diretório do projeto; o caminho absoluto
         * é guardado em caminhosImagem[indice] e exibido no lblPreview.
         *
         * Quando a questão é salva, esse caminho vai para o objeto Pergunta.
         * TelaPergunta lê o caminho e carrega o ImageIcon ao exibir a questão.
         */
        JLabel lblPreview = new JLabel("Nenhuma imagem selecionada");
        lblPreview.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblPreview.setForeground(JanelaJogo.COR_TEXTO_CINZA);
        lblPreview.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblPreviewImagem.add(lblPreview); // registra na lista paralela

        JButton btnImagem = new JButton("📎  Adicionar Imagem");
        btnImagem.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnImagem.setBackground(new Color(245, 245, 245));
        btnImagem.setForeground(JanelaJogo.COR_TEXTO_ESCURO);
        btnImagem.setFocusPainted(false);
        btnImagem.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(JanelaJogo.COR_BORDA, 1, true),
            BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));
        btnImagem.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnImagem.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnImagem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selecionarImagem(indice);
            }
        });

        // Botão para remover a imagem selecionada
        JButton btnRemover = new JButton("✕ Remover");
        btnRemover.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnRemover.setBackground(Color.WHITE);
        btnRemover.setForeground(JanelaJogo.COR_VERMELHO);
        btnRemover.setFocusPainted(false);
        btnRemover.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 80, 80), 1, true),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        btnRemover.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRemover.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                caminhosImagem.set(indice, null);
                lblPreviewImagem.get(indice).setText("Nenhuma imagem selecionada");
            }
        });

        JPanel linhaImagem = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        linhaImagem.setOpaque(false);
        linhaImagem.setAlignmentX(Component.LEFT_ALIGNMENT);
        linhaImagem.add(btnImagem);
        linhaImagem.add(btnRemover);

        // ---- Opções A, B, C, D ----
        JPanel gradeOpcoes = new JPanel(new GridLayout(2, 2, 10, 8));
        gradeOpcoes.setOpaque(false);
        gradeOpcoes.setAlignmentX(Component.LEFT_ALIGNMENT);
        gradeOpcoes.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        String[] letras = {"A", "B", "C", "D"};
        JTextField[] opcoes = new JTextField[4];
        for (int j = 0; j < 4; j++) {
            opcoes[j] = new JTextField();
            opcoes[j].setFont(new Font("Segoe UI", Font.PLAIN, 12));
            opcoes[j].setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(JanelaJogo.COR_BORDA),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
            ));
            opcoes[j].setToolTipText("Opção " + letras[j]);
            gradeOpcoes.add(opcoes[j]);
        }
        camposOpcoes.add(opcoes);

        // ---- Resposta Correta ----
        JComboBox<String> comboCorreta = new JComboBox<>(letras);
        comboCorreta.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboCorreta.setMaximumSize(new Dimension(120, 36));
        comboCorreta.setAlignmentX(Component.LEFT_ALIGNMENT);
        combosCorreta.add(comboCorreta);

        // ---- Dica ----
        JTextField campoDica = campo();
        campoDica.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        camposDica.add(campoDica);

        // ---- Monta o card ----
        card.add(lblNumero);
        card.add(Box.createVerticalStrut(12));
        card.add(rotulo("ENUNCIADO"));
        card.add(Box.createVerticalStrut(4));
        card.add(campoEnunc);
        card.add(Box.createVerticalStrut(12));
        card.add(rotulo("IMAGEM DA PERGUNTA (opcional)"));
        card.add(Box.createVerticalStrut(4));
        card.add(linhaImagem);
        card.add(Box.createVerticalStrut(4));
        card.add(lblPreview);
        card.add(Box.createVerticalStrut(12));
        card.add(rotulo("OPÇÕES DE RESPOSTA  (marque a correta no combo abaixo)"));
        card.add(Box.createVerticalStrut(4));
        card.add(gradeOpcoes);
        card.add(Box.createVerticalStrut(12));
        card.add(rotulo("RESPOSTA CORRETA"));
        card.add(Box.createVerticalStrut(4));
        card.add(comboCorreta);
        card.add(Box.createVerticalStrut(12));
        card.add(rotulo("DICA (opcional — custa -5 pts ao aluno)"));
        card.add(Box.createVerticalStrut(4));
        card.add(campoDica);

        return card;
    }

    /**
     * Abre o JFileChooser para o professor escolher uma imagem.
     * Armazena o caminho absoluto na lista paralela e atualiza o label de preview.
     */
    private void selecionarImagem(final int indice) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Selecionar imagem para a Questão " + (indice + 1));

        // Filtra apenas arquivos de imagem comuns
        FileNameExtensionFilter filtro = new FileNameExtensionFilter(
            "Imagens (JPG, PNG, GIF, BMP)", "jpg", "jpeg", "png", "gif", "bmp"
        );
        chooser.setFileFilter(filtro);
        chooser.setAcceptAllFileFilterUsed(false);

        // Abre a pasta do projeto por padrão
        chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

        int resultado = chooser.showOpenDialog(jogo);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File arquivo = chooser.getSelectedFile();
            caminhosImagem.set(indice, arquivo.getAbsolutePath());
            // Exibe só o nome do arquivo para não poluir a interface
            lblPreviewImagem.get(indice).setText("✔ " + arquivo.getName());
            lblPreviewImagem.get(indice).setForeground(JanelaJogo.COR_VERDE);
        }
    }

    /**
     * Valida os campos e persiste o quiz na lista global da JanelaJogo.
     */
    private void salvarQuiz() {
        String nomeQuiz = campoNomeQuiz.getText().trim();

        if (nomeQuiz.isEmpty()) {
            lblAviso.setText("⚠ Preencha o nome do quiz.");
            return;
        }

        int n = (Integer) comboNumQuestoes.getSelectedItem();

        for (int i = 0; i < n; i++) {
            if (camposEnunciado.get(i).getText().trim().isEmpty()) {
                lblAviso.setText("⚠ Preencha o enunciado da questão " + (i + 1) + ".");
                return;
            }
            JTextField[] ops = camposOpcoes.get(i);
            for (int j = 0; j < 4; j++) {
                if (ops[j].getText().trim().isEmpty()) {
                    lblAviso.setText("⚠ Preencha todas as opções da questão " + (i + 1) + ".");
                    return;
                }
            }
        }

        // Determina dificuldade
        Quiz.Dificuldade dif;
        switch (comboDificuldade.getSelectedIndex()) {
            case 1:  dif = Quiz.Dificuldade.MEDIO;   break;
            case 2:  dif = Quiz.Dificuldade.DIFICIL; break;
            default: dif = Quiz.Dificuldade.FACIL;
        }

        String criadoPor = jogo.getUsuarioLogado() != null
            ? jogo.getUsuarioLogado().getNome() : "Professor";

        Quiz novoQuiz = new Quiz(nomeQuiz, dif, criadoPor);
        String[] letras = {"A", "B", "C", "D"};

        for (int i = 0; i < n; i++) {
            String opcaoCorreta = (String) combosCorreta.get(i).getSelectedItem();
            int indiceCorreto = 0;
            for (int j = 0; j < letras.length; j++) {
                if (letras[j].equals(opcaoCorreta)) { indiceCorreto = j; break; }
            }

            JTextField[] ops = camposOpcoes.get(i);
            String[] opcoesTexto = {
                ops[0].getText().trim(), ops[1].getText().trim(),
                ops[2].getText().trim(), ops[3].getText().trim()
            };

            // Usa o construtor COM imagem (pode ser null — tudo bem)
            Pergunta p = new Pergunta(
                camposEnunciado.get(i).getText().trim(),
                opcoesTexto,
                indiceCorreto,
                camposDica.get(i).getText().trim(),
                caminhosImagem.get(i)   // null = sem imagem
            );
            novoQuiz.adicionarPergunta(p);
        }

        jogo.getQuizzesDoDomain().add(novoQuiz);
        lblAviso.setText(" ");

        JOptionPane.showMessageDialog(jogo,
            "Quiz \"" + nomeQuiz + "\" salvo!\nOs alunos já podem jogar.",
            "Quiz Salvo", JOptionPane.INFORMATION_MESSAGE);

        jogo.mostrarTela(JanelaJogo.TELA_MENU_PROFESSOR);
    }

    // ------------------------------------------------------------------
    //  Helpers visuais
    // ------------------------------------------------------------------

    private JLabel rotulo(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(JanelaJogo.COR_TEXTO_CINZA);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField campo() {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(JanelaJogo.COR_BORDA),
            BorderFactory.createEmptyBorder(7, 10, 7, 10)
        ));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        return f;
    }

    private JPanel coluna() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        return p;
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
