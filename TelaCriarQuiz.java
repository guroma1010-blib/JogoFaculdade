import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TelaCriarQuiz extends JPanel {

    private JanelaJogo jogo;
    private Cabecalho  cabecalho;

    private JTextField         campoNomeQuiz;
    private JComboBox<String>  comboDificuldade;
    private JComboBox<Integer> comboNumQuestoes;
    private JPanel             painelQuestoes;
    private JLabel             lblAviso;

    private List<JTextField>        camposEnunciado  = new ArrayList<>();
    private List<JTextField[]>      camposOpcoes     = new ArrayList<>();
    private List<JComboBox<String>> combosCorreta    = new ArrayList<>();
    private List<JTextField>        camposDica       = new ArrayList<>();
    private List<String>            caminhosImagem   = new ArrayList<>();
    private List<JLabel>            lblPreviewImagem = new ArrayList<>();

    public TelaCriarQuiz(JanelaJogo jogo) {
        this.jogo = jogo;
        setBackground(JanelaJogo.COR_FUNDO);
        setLayout(new BorderLayout());
        cabecalho = new Cabecalho(jogo, JanelaJogo.TELA_MENU_PROFESSOR);
        add(cabecalho, BorderLayout.NORTH);
        add(construirFormulario(), BorderLayout.CENTER);
    }

    private JScrollPane construirFormulario() {
        JPanel corpo = new JPanel();
        corpo.setBackground(JanelaJogo.COR_FUNDO);
        corpo.setLayout(new BoxLayout(corpo, BoxLayout.Y_AXIS));
        corpo.setBorder(BorderFactory.createEmptyBorder(24, 36, 24, 36));

        JPanel linhaBotaoVoltar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linhaBotaoVoltar.setOpaque(false);
        linhaBotaoVoltar.setAlignmentX(Component.LEFT_ALIGNMENT);
        linhaBotaoVoltar.add(criarBotaoVoltar());

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

        JPanel faixaConfig = new JPanel(new GridLayout(1, 3, 16, 0));
        faixaConfig.setBackground(Color.WHITE);
        faixaConfig.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(JanelaJogo.COR_BORDA, 1, true),
            BorderFactory.createEmptyBorder(16, 20, 16, 20)
        ));
        faixaConfig.setAlignmentX(Component.LEFT_ALIGNMENT);
        faixaConfig.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JPanel colNome = coluna();
        colNome.add(rotulo("NOME DO QUIZ"));
        colNome.add(Box.createVerticalStrut(4));
        campoNomeQuiz = campo();
        campoNomeQuiz.setToolTipText("Ex: Quiz de Vidrarias");
        colNome.add(campoNomeQuiz);

        JPanel colDif = coluna();
        colDif.add(rotulo("DIFICULDADE"));
        colDif.add(Box.createVerticalStrut(4));
        comboDificuldade = new JComboBox<>(new String[]{
            "Fácil (+10 pts)", "Médio (+20 pts)", "Difícil (+30 pts)"
        });
        comboDificuldade.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        colDif.add(comboDificuldade);

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

        lblAviso = new JLabel(" ");
        lblAviso.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblAviso.setForeground(JanelaJogo.COR_VERMELHO);
        lblAviso.setAlignmentX(Component.LEFT_ALIGNMENT);

        painelQuestoes = new JPanel();
        painelQuestoes.setOpaque(false);
        painelQuestoes.setLayout(new BoxLayout(painelQuestoes, BoxLayout.Y_AXIS));
        painelQuestoes.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnSalvar = new JButton("Salvar Quiz");
        btnSalvar.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnSalvar.setBackground(JanelaJogo.COR_VERMELHO);
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.setBorderPainted(false);
        btnSalvar.setFocusPainted(false);
        btnSalvar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSalvar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnSalvar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btnSalvar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { salvarQuiz(); }
        });

        corpo.add(linhaBotaoVoltar);
        corpo.add(Box.createVerticalStrut(12));
        corpo.add(linhaTitulo);
        corpo.add(Box.createVerticalStrut(20));
        corpo.add(faixaConfig);
        corpo.add(Box.createVerticalStrut(16));
        corpo.add(painelQuestoes);
        corpo.add(Box.createVerticalStrut(20));
        corpo.add(btnSalvar);
        corpo.add(Box.createVerticalStrut(10));
        corpo.add(lblAviso);

        reconstruirQuestoes();
        return new JScrollPane(corpo);
    }

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
            caminhosImagem.add(null);
            painelQuestoes.add(criarFormularioQuestao(i));
            painelQuestoes.add(Box.createVerticalStrut(16));
        }

        painelQuestoes.revalidate();
        painelQuestoes.repaint();
    }

    private JPanel criarFormularioQuestao(final int indice) {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(JanelaJogo.COR_BORDA, 1, true),
            BorderFactory.createEmptyBorder(16, 20, 20, 20)
        ));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblNumero = new JLabel("QUESTÃO " + (indice + 1));
        lblNumero.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblNumero.setForeground(JanelaJogo.COR_VERMELHO);
        lblNumero.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField campoEnunc = campo();
        campoEnunc.setToolTipText("Digite o enunciado da pergunta");
        camposEnunciado.add(campoEnunc);

        JLabel lblPreview = new JLabel("Nenhuma imagem selecionada");
        lblPreview.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblPreview.setForeground(JanelaJogo.COR_TEXTO_CINZA);
        lblPreview.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblPreviewImagem.add(lblPreview);

        JButton btnImagem = new JButton("Adicionar Imagem (opcional)");
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
            public void actionPerformed(ActionEvent e) { selecionarImagem(indice); }
        });

        JButton btnRemover = new JButton("Remover imagem");
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
                lblPreviewImagem.get(indice).setForeground(JanelaJogo.COR_TEXTO_CINZA);
            }
        });

        JPanel linhaImagem = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        linhaImagem.setOpaque(false);
        linhaImagem.setAlignmentX(Component.LEFT_ALIGNMENT);
        linhaImagem.add(btnImagem);
        linhaImagem.add(btnRemover);

        String[] letras = {"A", "B", "C", "D"};
        JTextField[] opcoes = new JTextField[4];

        JPanel painelAlternativas = new JPanel();
        painelAlternativas.setOpaque(false);
        painelAlternativas.setLayout(new BoxLayout(painelAlternativas, BoxLayout.Y_AXIS));
        painelAlternativas.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (int j = 0; j < 4; j++) {
            opcoes[j] = new JTextField();
            opcoes[j].setFont(new Font("Segoe UI", Font.PLAIN, 13));
            opcoes[j].setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(JanelaJogo.COR_BORDA),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)
            ));
            opcoes[j].setToolTipText("Alternativa " + letras[j]);

            JLabel lblLetra = new JLabel(letras[j] + ")");
            lblLetra.setFont(new Font("Segoe UI", Font.BOLD, 15));
            lblLetra.setForeground(JanelaJogo.COR_TEXTO_ESCURO);
            lblLetra.setMinimumSize(new Dimension(30, 40));
            lblLetra.setPreferredSize(new Dimension(30, 40));
            lblLetra.setMaximumSize(new Dimension(30, 40));

            JPanel linhaAlt = new JPanel();
            linhaAlt.setOpaque(false);
            linhaAlt.setLayout(new BoxLayout(linhaAlt, BoxLayout.X_AXIS));
            linhaAlt.setAlignmentX(Component.LEFT_ALIGNMENT);
            linhaAlt.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            linhaAlt.add(lblLetra);
            linhaAlt.add(Box.createHorizontalStrut(8));
            linhaAlt.add(opcoes[j]);

            painelAlternativas.add(linhaAlt);
            if (j < 3) painelAlternativas.add(Box.createVerticalStrut(8));
        }
        camposOpcoes.add(opcoes);

        JComboBox<String> comboCorreta = new JComboBox<>(new String[]{"A", "B", "C", "D"});
        comboCorreta.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboCorreta.setMaximumSize(new Dimension(120, 36));
        comboCorreta.setAlignmentX(Component.LEFT_ALIGNMENT);
        combosCorreta.add(comboCorreta);

        JTextField campoDica = campo();
        campoDica.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        campoDica.setToolTipText("Dica exibida ao aluno ao custo de -5 pts (opcional)");
        camposDica.add(campoDica);

        card.add(lblNumero);
        card.add(Box.createVerticalStrut(12));
        card.add(rotulo("ENUNCIADO DA PERGUNTA"));
        card.add(Box.createVerticalStrut(4));
        card.add(campoEnunc);
        card.add(Box.createVerticalStrut(14));
        card.add(rotulo("IMAGEM (opcional)"));
        card.add(Box.createVerticalStrut(6));
        card.add(linhaImagem);
        card.add(Box.createVerticalStrut(4));
        card.add(lblPreview);
        card.add(Box.createVerticalStrut(16));
        card.add(rotulo("ALTERNATIVAS"));
        card.add(Box.createVerticalStrut(8));
        card.add(painelAlternativas);
        card.add(Box.createVerticalStrut(14));
        card.add(rotulo("RESPOSTA CORRETA"));
        card.add(Box.createVerticalStrut(4));
        card.add(comboCorreta);
        card.add(Box.createVerticalStrut(14));
        card.add(rotulo("DICA (opcional — custa -5 pts ao aluno)"));
        card.add(Box.createVerticalStrut(4));
        card.add(campoDica);

        return card;
    }

    private void selecionarImagem(final int indice) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Selecionar imagem para a Questão " + (indice + 1));
        chooser.setFileFilter(new FileNameExtensionFilter(
            "Imagens (JPG, PNG, GIF, BMP)", "jpg", "jpeg", "png", "gif", "bmp"
        ));
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

        if (chooser.showOpenDialog(jogo) == JFileChooser.APPROVE_OPTION) {
            File arquivo = chooser.getSelectedFile();
            caminhosImagem.set(indice, arquivo.getAbsolutePath());
            lblPreviewImagem.get(indice).setText("✔ " + arquivo.getName());
            lblPreviewImagem.get(indice).setForeground(JanelaJogo.COR_VERDE);
        }
    }

    private void salvarQuiz() {
        lblAviso.setText(" ");
        String nomeQuiz = campoNomeQuiz.getText().trim();

        if (nomeQuiz.isEmpty()) {
            mostrarErroValidacao("Preencha o nome do quiz antes de salvar.");
            return;
        }

        int n = (Integer) comboNumQuestoes.getSelectedItem();

        for (int i = 0; i < n; i++) {
            if (camposEnunciado.get(i).getText().trim().isEmpty()) {
                mostrarErroValidacao("Preencha o enunciado da questão " + (i + 1) + ".");
                return;
            }
            JTextField[] ops = camposOpcoes.get(i);
            for (int j = 0; j < 4; j++) {
                if (ops[j].getText().trim().isEmpty()) {
                    mostrarErroValidacao("Preencha todas as alternativas da questão " + (i + 1) + ".");
                    return;
                }
            }
        }

        Quiz.Dificuldade dif;
        switch (comboDificuldade.getSelectedIndex()) {
            case 1:  dif = Quiz.Dificuldade.MEDIO;   break;
            case 2:  dif = Quiz.Dificuldade.DIFICIL; break;
            default: dif = Quiz.Dificuldade.FACIL;
        }

        String criadoPor = (jogo.getUsuarioLogado() != null)
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
            String[] textos = {
                ops[0].getText().trim(),
                ops[1].getText().trim(),
                ops[2].getText().trim(),
                ops[3].getText().trim()
            };

            novoQuiz.adicionarPergunta(new Pergunta(
                camposEnunciado.get(i).getText().trim(),
                textos,
                indiceCorreto,
                camposDica.get(i).getText().trim(),
                caminhosImagem.get(i)
            ));
        }

        if (!salvarNoBanco(novoQuiz)) return;

        jogo.getQuizzesDoDomain().add(novoQuiz);
        JOptionPane.showMessageDialog(jogo,
            "Quiz \"" + nomeQuiz + "\" salvo com sucesso!\nOs alunos já podem jogar.",
            "Quiz Salvo", JOptionPane.INFORMATION_MESSAGE);
        jogo.mostrarTela(JanelaJogo.TELA_MENU_PROFESSOR);
    }

    private boolean salvarNoBanco(Quiz quiz) {
        String sqlQuiz =
            "INSERT INTO quizzes (nome, dificuldade, criado_por, pontos_por_acerto) " +
            "VALUES (?, ?, ?, ?)";
        String sqlPergunta =
            "INSERT INTO perguntas " +
            "(id_quiz, enunciado, opcao_a, opcao_b, opcao_c, opcao_d, " +
            "indice_correto, dica, caminho_imagem) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection con = null;
        try {
            con = DatabaseConnection.getConexao();
            con.setAutoCommit(false);

            int idQuiz;
            try (PreparedStatement psQuiz = con.prepareStatement(
                    sqlQuiz, Statement.RETURN_GENERATED_KEYS)) {

                psQuiz.setString(1, quiz.getNome());
                psQuiz.setString(2, quiz.getDificuldade().name());
                psQuiz.setString(3, quiz.getCriadoPor());
                psQuiz.setInt(4, quiz.getDificuldade().getPontosPorAcerto());
                psQuiz.executeUpdate();

                try (ResultSet keys = psQuiz.getGeneratedKeys()) {
                    if (!keys.next()) throw new SQLException("Nenhuma chave gerada para o quiz.");
                    idQuiz = keys.getInt(1);
                    quiz.setId(idQuiz);
                }
            }

            try (PreparedStatement psPerg = con.prepareStatement(sqlPergunta)) {
                for (Pergunta p : quiz.getPerguntas()) {
                    String[] ops = p.getOpcoes();
                    String   dica = p.getDica();
                    String   img  = p.getCaminhoImagem();

                    psPerg.setInt(1, idQuiz);
                    psPerg.setString(2, p.getEnunciado());
                    psPerg.setString(3, ops[0]);
                    psPerg.setString(4, ops[1]);
                    psPerg.setString(5, ops[2]);
                    psPerg.setString(6, ops[3]);
                    psPerg.setInt(7, p.getIndiceCorreto());

                    if (dica == null || dica.isEmpty()) {
                        psPerg.setNull(8, Types.VARCHAR);
                    } else {
                        psPerg.setString(8, dica);
                    }

                    if (img == null) {
                        psPerg.setNull(9, Types.VARCHAR);
                    } else {
                        psPerg.setString(9, img);
                    }

                    psPerg.addBatch();
                }
                psPerg.executeBatch();
            }

            con.commit();
            return true;

        } catch (SQLException e) {
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            JOptionPane.showMessageDialog(jogo,
                "Erro ao salvar quiz no banco de dados:\n\n" + e.getMessage() +
                "\n\nVerifique se as tabelas 'quizzes' e 'perguntas' existem no MySQL.",
                "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
            return false;

        } finally {
            if (con != null) {
                try { con.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    private void mostrarErroValidacao(String mensagem) {
        lblAviso.setText("⚠  " + mensagem);
        JOptionPane.showMessageDialog(jogo, mensagem, "Campo Obrigatório", JOptionPane.WARNING_MESSAGE);
    }

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
