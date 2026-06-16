import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class TelaEditarMaterial extends JPanel {

    private JanelaJogo jogo;
    private Cabecalho  cabecalho;

    private static final String[] CATEGORIAS = {"Vidrarias", "Equipamentos", "Sistemas"};

    private DefaultTableModel  modeloTabela;
    private JTable             tabela;

    private JTextField        campoNome;
    private JComboBox<String> comboCat;
    private JTextField        campoGrupo;
    private JTextField        campoDescricao;
    private JLabel            lblStatus;

    private int idMaterialSelecionado = -1;

    public TelaEditarMaterial(JanelaJogo jogo) {
        this.jogo = jogo;
        setBackground(JanelaJogo.COR_FUNDO);
        setLayout(new BorderLayout());
        cabecalho = new Cabecalho(jogo, JanelaJogo.TELA_MENU_PROFESSOR);
        add(cabecalho, BorderLayout.NORTH);
        add(construirConteudo(), BorderLayout.CENTER);
    }

    private JPanel construirConteudo() {
        JPanel corpo = new JPanel(new BorderLayout(20, 0));
        corpo.setBackground(JanelaJogo.COR_FUNDO);
        corpo.setBorder(BorderFactory.createEmptyBorder(24, 36, 24, 36));
        corpo.add(construirPainelLista(),  BorderLayout.WEST);
        corpo.add(construirPainelEdicao(), BorderLayout.CENTER);
        return corpo;
    }

    private JPanel construirPainelLista() {
        JPanel painel = new JPanel(new BorderLayout(0, 12));
        painel.setBackground(JanelaJogo.COR_FUNDO);
        painel.setPreferredSize(new Dimension(370, 0));

        JLabel lblTitulo = new JLabel("Materiais Cadastrados");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setForeground(JanelaJogo.COR_TEXTO_ESCURO);

        String[] cols = {"#", "Nome", "Categoria"};
        modeloTabela = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tabela = new JTable(modeloTabela);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.setRowHeight(34);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setShowGrid(false);
        tabela.setIntercellSpacing(new Dimension(0, 0));
        tabela.setBackground(Color.WHITE);
        tabela.setSelectionBackground(new Color(220, 230, 245));
        tabela.setFillsViewportHeight(true);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tabela.getTableHeader().setBackground(new Color(235, 235, 235));
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.getColumnModel().getColumn(0).setMaxWidth(40);

        tabela.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object value, boolean selected, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, value, selected, focus, row, col);
                if (!selected) setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 248, 248));
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return this;
            }
        });

        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabela.getSelectedRow() >= 0) {
                int id = (int) modeloTabela.getValueAt(tabela.getSelectedRow(), 0);
                carregarMaterialNoForm(id);
            }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(JanelaJogo.COR_BORDA, 1, true));

        JPanel linhaBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        linhaBotoes.setOpaque(false);

        JButton btnVoltar = criarBotaoSecundario("← Voltar");
        btnVoltar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jogo.mostrarTela(JanelaJogo.TELA_MENU_PROFESSOR);
            }
        });

        JButton btnNovo = criarBotaoSecundario("+ Novo Material");
        btnNovo.setForeground(JanelaJogo.COR_VERDE);
        btnNovo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(JanelaJogo.COR_VERDE, 1, true),
            BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));
        btnNovo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { limparForm(); }
        });

        linhaBotoes.add(btnVoltar);
        linhaBotoes.add(btnNovo);

        painel.add(lblTitulo,  BorderLayout.NORTH);
        painel.add(scroll,     BorderLayout.CENTER);
        painel.add(linhaBotoes, BorderLayout.SOUTH);

        return painel;
    }

    private JPanel construirPainelEdicao() {
        JPanel painel = new JPanel();
        painel.setBackground(Color.WHITE);
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(JanelaJogo.COR_BORDA, 1, true),
            BorderFactory.createEmptyBorder(28, 30, 28, 30)
        ));

        JLabel lblTitulo = new JLabel("Editar Material");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(JanelaJogo.COR_AZUL);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        campoNome      = campo();
        campoGrupo     = campo();
        campoDescricao = campo();

        comboCat = new JComboBox<>(CATEGORIAS);
        comboCat.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboCat.setAlignmentX(Component.LEFT_ALIGNMENT);
        comboCat.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        comboCat.setBorder(BorderFactory.createLineBorder(JanelaJogo.COR_BORDA));

        lblStatus = new JLabel(" ");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatus.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnSalvar = new JButton("Salvar Alterações");
        btnSalvar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSalvar.setBackground(JanelaJogo.COR_AZUL);
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.setBorderPainted(false);
        btnSalvar.setFocusPainted(false);
        btnSalvar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSalvar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnSalvar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btnSalvar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { salvar(); }
        });

        JButton btnExcluir = criarBotaoSecundario("Excluir Material");
        btnExcluir.setForeground(JanelaJogo.COR_VERMELHO);
        btnExcluir.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(JanelaJogo.COR_VERMELHO, 1, true),
            BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));
        btnExcluir.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnExcluir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { excluir(); }
        });

        painel.add(lblTitulo);
        painel.add(Box.createVerticalStrut(22));
        painel.add(rotulo("NOME DO MATERIAL"));
        painel.add(Box.createVerticalStrut(4));
        painel.add(campoNome);
        painel.add(Box.createVerticalStrut(14));
        painel.add(rotulo("CATEGORIA"));
        painel.add(Box.createVerticalStrut(4));
        painel.add(comboCat);
        painel.add(Box.createVerticalStrut(14));
        painel.add(rotulo("GRUPO / SUBCATEGORIA (opcional)"));
        painel.add(Box.createVerticalStrut(4));
        painel.add(campoGrupo);
        painel.add(Box.createVerticalStrut(14));
        painel.add(rotulo("DESCRIÇÃO"));
        painel.add(Box.createVerticalStrut(4));
        painel.add(campoDescricao);
        painel.add(Box.createVerticalStrut(22));
        painel.add(lblStatus);
        painel.add(Box.createVerticalStrut(10));
        painel.add(btnSalvar);
        painel.add(Box.createVerticalStrut(10));
        painel.add(btnExcluir);

        return painel;
    }

    public void carregarMateriais() {
        cabecalho.atualizar();
        modeloTabela.setRowCount(0);
        idMaterialSelecionado = -1;
        limparForm();

        try (Connection con = DatabaseConnection.getConexao();
             PreparedStatement ps = con.prepareStatement(
                "SELECT id_material, nome, categoria FROM materiais ORDER BY categoria, nome");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                modeloTabela.addRow(new Object[]{
                    rs.getInt("id_material"),
                    rs.getString("nome"),
                    rs.getString("categoria")
                });
            }

        } catch (SQLException e) {
            mostrarStatus("Erro ao carregar materiais: " + e.getMessage(), true);
        }
    }

    private void carregarMaterialNoForm(int idMaterial) {
        this.idMaterialSelecionado = idMaterial;
        lblStatus.setText(" ");

        try (Connection con = DatabaseConnection.getConexao();
             PreparedStatement ps = con.prepareStatement(
                "SELECT nome, categoria, grupo, descricao FROM materiais WHERE id_material = ?")) {

            ps.setInt(1, idMaterial);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    campoNome.setText(rs.getString("nome"));
                    String cat = rs.getString("categoria");
                    for (int i = 0; i < CATEGORIAS.length; i++) {
                        if (CATEGORIAS[i].equalsIgnoreCase(cat)) {
                            comboCat.setSelectedIndex(i);
                            break;
                        }
                    }
                    String grupo = rs.getString("grupo");
                    campoGrupo.setText(grupo != null ? grupo : "");
                    String desc = rs.getString("descricao");
                    campoDescricao.setText(desc != null ? desc : "");
                }
            }

        } catch (SQLException e) {
            mostrarStatus("Erro ao carregar material: " + e.getMessage(), true);
        }
    }

    private void salvar() {
        String nome      = campoNome.getText().trim();
        String cat       = (String) comboCat.getSelectedItem();
        String grupo     = campoGrupo.getText().trim();
        String descricao = campoDescricao.getText().trim();

        if (nome.isEmpty() || descricao.isEmpty()) {
            mostrarStatus("Preencha ao menos o nome e a descrição.", true);
            return;
        }

        String grupoVal = grupo.isEmpty() ? null : grupo;

        if (idMaterialSelecionado < 0) {
            inserir(nome, cat, grupoVal, descricao);
        } else {
            atualizar(idMaterialSelecionado, nome, cat, grupoVal, descricao);
        }
    }

    private void atualizar(int id, String nome, String cat, String grupo, String descricao) {
        try (Connection con = DatabaseConnection.getConexao();
             PreparedStatement ps = con.prepareStatement(
                "UPDATE materiais SET nome = ?, categoria = ?, grupo = ?, descricao = ? " +
                "WHERE id_material = ?")) {

            ps.setString(1, nome);
            ps.setString(2, cat);
            if (grupo == null) ps.setNull(3, Types.VARCHAR); else ps.setString(3, grupo);
            ps.setString(4, descricao);
            ps.setInt(5, id);
            ps.executeUpdate();

            mostrarStatus("Material atualizado com sucesso.", false);
            carregarMateriais();
            resselecionarTabela(id);

        } catch (SQLException e) {
            mostrarStatus("Erro ao atualizar: " + e.getMessage(), true);
        }
    }

    private void inserir(String nome, String cat, String grupo, String descricao) {
        try (Connection con = DatabaseConnection.getConexao();
             PreparedStatement ps = con.prepareStatement(
                "INSERT INTO materiais (nome, categoria, grupo, descricao) VALUES (?, ?, ?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, nome);
            ps.setString(2, cat);
            if (grupo == null) ps.setNull(3, Types.VARCHAR); else ps.setString(3, grupo);
            ps.setString(4, descricao);
            ps.executeUpdate();

            int novoId = -1;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) novoId = keys.getInt(1);
            }

            mostrarStatus("Novo material inserido com sucesso.", false);
            carregarMateriais();
            if (novoId > 0) resselecionarTabela(novoId);

        } catch (SQLException e) {
            mostrarStatus("Erro ao inserir: " + e.getMessage(), true);
        }
    }

    private void excluir() {
        if (idMaterialSelecionado < 0) {
            mostrarStatus("Selecione um material para excluir.", true);
            return;
        }
        int opcao = JOptionPane.showConfirmDialog(jogo,
            "Deseja realmente excluir este material?",
            "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (opcao != JOptionPane.YES_OPTION) return;

        try (Connection con = DatabaseConnection.getConexao();
             PreparedStatement ps = con.prepareStatement(
                "DELETE FROM materiais WHERE id_material = ?")) {

            ps.setInt(1, idMaterialSelecionado);
            ps.executeUpdate();
            mostrarStatus("Material excluído.", false);
            limparForm();
            carregarMateriais();

        } catch (SQLException e) {
            mostrarStatus("Erro ao excluir: " + e.getMessage(), true);
        }
    }

    private void resselecionarTabela(int id) {
        for (int i = 0; i < modeloTabela.getRowCount(); i++) {
            if ((int) modeloTabela.getValueAt(i, 0) == id) {
                tabela.setRowSelectionInterval(i, i);
                tabela.scrollRectToVisible(tabela.getCellRect(i, 0, true));
                carregarMaterialNoForm(id);
                break;
            }
        }
    }

    private void limparForm() {
        idMaterialSelecionado = -1;
        campoNome.setText("");
        comboCat.setSelectedIndex(0);
        campoGrupo.setText("");
        campoDescricao.setText("");
        lblStatus.setText(" ");
        tabela.clearSelection();
    }

    private void mostrarStatus(String msg, boolean erro) {
        lblStatus.setText(erro ? "⚠ " + msg : "✔ " + msg);
        lblStatus.setForeground(erro ? JanelaJogo.COR_VERMELHO : JanelaJogo.COR_VERDE);
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

    private JButton criarBotaoSecundario(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setBackground(Color.WHITE);
        btn.setForeground(JanelaJogo.COR_TEXTO_ESCURO);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(JanelaJogo.COR_BORDA, 1, true),
            BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
