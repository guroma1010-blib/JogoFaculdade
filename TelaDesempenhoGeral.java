import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.List;

/**
 * Tela "Desempenho dos Alunos" — visão geral do professor.
 *
 * Exibe uma JTable com:
 *   Aluno | Quizzes Feitos | Acertos Totais | % Acertos | Pontos
 *
 * Pré-carrega linhas com alunos fictícios de demonstração.
 * Se o usuário logado for aluno e tiver histórico, aparece como última linha.
 *
 * Chamada por JanelaJogo.abrirDesempenhoGeral().
 */
public class TelaDesempenhoGeral extends JPanel {

    private JanelaJogo         jogo;
    private Cabecalho          cabecalho;
    private DefaultTableModel  modeloTabela;

    public TelaDesempenhoGeral(JanelaJogo jogo) {
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

        // Título "Desempenho dos Alunos"
        JPanel linhaTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        linhaTitulo.setOpaque(false);
        linhaTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblDesempenho = new JLabel("Desempenho ");
        lblDesempenho.setFont(new Font("Segoe UI", Font.BOLD, 24));
        JLabel lblAlunos = new JLabel("dos Alunos");
        lblAlunos.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblAlunos.setForeground(JanelaJogo.COR_VERMELHO);
        linhaTitulo.add(lblDesempenho);
        linhaTitulo.add(lblAlunos);

        // Tabela
        String[] colunas = {"Aluno", "Quizzes Feitos", "Acertos Totais", "% Acertos", "Pontos"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // somente leitura
            }
        };

        JTable tabela = construirTabela(colunas);

        JScrollPane scrollTabela = new JScrollPane(tabela);
        scrollTabela.setBackground(Color.WHITE);
        scrollTabela.getViewport().setBackground(Color.WHITE);
        scrollTabela.setBorder(BorderFactory.createLineBorder(JanelaJogo.COR_BORDA, 1, true));
        scrollTabela.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Máximo de 400px de altura; a tabela cresce junto com o JScrollPane
        scrollTabela.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));
        scrollTabela.setPreferredSize(new Dimension(800, 300));

        // Legenda abaixo da tabela
        JLabel lblLegenda = new JLabel(
            "Alunos carregados do banco de dados. "
            + "O aluno logado aparece com estatísticas reais quando possui partidas registradas."
        );
        lblLegenda.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblLegenda.setForeground(JanelaJogo.COR_TEXTO_CINZA);
        lblLegenda.setAlignmentX(Component.LEFT_ALIGNMENT);

        conteudo.add(linhaBotao);
        conteudo.add(Box.createVerticalStrut(12));
        conteudo.add(linhaTitulo);
        conteudo.add(Box.createVerticalStrut(24));
        conteudo.add(scrollTabela);
        conteudo.add(Box.createVerticalStrut(8));
        conteudo.add(lblLegenda);

        add(new JScrollPane(conteudo), BorderLayout.CENTER);
    }

    /**
     * Reconstrói o conteúdo da tabela.
     * Chamado por JanelaJogo.abrirDesempenhoGeral() antes de exibir a tela.
     */
    public void carregarDados() {
        cabecalho.atualizar();
        modeloTabela.setRowCount(0);

        Usuario u = jogo.getUsuarioLogado();
        String nomeLogado = (u != null && !u.isProfessor()) ? u.getNome() : null;

        // Carrega todos os alunos do banco; o aluno logado com histórico é
        // omitido aqui e re-adicionado abaixo com estatísticas reais.
        try (Connection con = DatabaseConnection.getConexao();
             PreparedStatement ps = con.prepareStatement(
                 "SELECT nome FROM usuarios WHERE UPPER(tipo_usuario) = 'ALUNO' ORDER BY nome");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String nome = rs.getString("nome");
                boolean eLogadoComHistorico = nome.equals(nomeLogado)
                        && u != null && !u.getHistorico().isEmpty();
                if (!eLogadoComHistorico) {
                    adicionarLinha(nome, 0, 0, "—", 0);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Aluno logado com dados reais de histórico em memória
        if (u != null && !u.isProfessor() && !u.getHistorico().isEmpty()) {
            List<ResultadoQuiz> hist = u.getHistorico();
            int totalAcertos   = 0;
            int totalPerguntas = 0;
            for (ResultadoQuiz r : hist) {
                totalAcertos   += r.getAcertos();
                totalPerguntas += r.getTotalPerguntas();
            }
            String pct = totalPerguntas > 0
                ? Math.round(100.0 * totalAcertos / totalPerguntas) + "%"
                : "—";
            adicionarLinha(u.getNome() + " (você)", hist.size(), totalAcertos, pct, u.getPontos());
        }
    }

    private void adicionarLinha(String nome, int quizzes, int acertos, String pct, int pontos) {
        modeloTabela.addRow(new Object[]{nome, quizzes, acertos, pct, pontos});
    }

    // ------------------------------------------------------------------
    //  Construção da JTable
    // ------------------------------------------------------------------

    private JTable construirTabela(String[] colunas) {
        JTable tabela = new JTable(modeloTabela);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.setRowHeight(36);
        tabela.setShowGrid(false);
        tabela.setIntercellSpacing(new Dimension(0, 0));
        tabela.setBackground(Color.WHITE);
        tabela.setSelectionBackground(new Color(230, 230, 240));
        tabela.setSelectionForeground(JanelaJogo.COR_TEXTO_ESCURO);
        tabela.setFillsViewportHeight(true);

        // Cabeçalho
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabela.getTableHeader().setBackground(JanelaJogo.COR_FUNDO);
        tabela.getTableHeader().setForeground(JanelaJogo.COR_TEXTO_CINZA);
        tabela.getTableHeader().setReorderingAllowed(false);

        // Alinha ao centro as colunas numéricas (1 em diante)
        DefaultTableCellRenderer centralizador = new DefaultTableCellRenderer();
        centralizador.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 1; i < colunas.length; i++) {
            tabela.getColumnModel().getColumn(i).setCellRenderer(centralizador);
        }

        // Largura preferencial da coluna "Aluno"
        tabela.getColumnModel().getColumn(0).setPreferredWidth(220);

        // Linhas alternadas em zebra
        tabela.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object value, boolean isSelected,
                    boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 248, 248));
                }
                if (col > 0) {
                    setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return this;
            }
        });

        return tabela;
    }

    // ------------------------------------------------------------------
    //  Helpers
    // ------------------------------------------------------------------

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
