import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class TelaCadastro extends JPanel {

    private JanelaJogo jogo;

    private JTextField     campoCodigo;
    private JTextField     campoNome;
    private JPasswordField campoSenha;
    private JPasswordField campoConfirmarSenha;
    private JComboBox<String> comboPerfil;
    private JPasswordField campoChaveSecreta;
    private JLabel         labelChaveSecreta;
    private JLabel         labelErro;

    private static final String CHAVE_PROFESSOR = "ETEC_QUIMICA_2026";

    public TelaCadastro(JanelaJogo jogo) {
        this.jogo = jogo;

        setBackground(JanelaJogo.COR_FUNDO);
        setLayout(new GridBagLayout());

        construirCard();
    }

    private void construirCard() {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(480, 640));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(JanelaJogo.COR_BORDA, 1, true),
            BorderFactory.createEmptyBorder(32, 48, 32, 48)
        ));

        JPanel painelLogo = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        painelLogo.setBackground(Color.WHITE);
        painelLogo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblT = new JLabel("T");
        lblT.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblT.setForeground(JanelaJogo.COR_VERMELHO);

        JLabel lblEtec = new JLabel("Etec");
        lblEtec.setFont(new Font("Segoe UI", Font.BOLD, 36));

        painelLogo.add(lblT);
        painelLogo.add(lblEtec);

        JLabel lblTitulo = new JLabel("Criar conta");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        JPanel painelTitulo = centralizar(lblTitulo);

        JLabel lblSubTitulo = new JLabel("Preencha seus dados institucionais");
        lblSubTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubTitulo.setForeground(JanelaJogo.COR_TEXTO_CINZA);
        JPanel painelSub = centralizar(lblSubTitulo);

        JSeparator sep = new JSeparator();
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        JLabel lblCodigo = rotuloCampo("CÓDIGO DE IDENTIFICAÇÃO (RM / MATRÍCULA)");
        campoCodigo = new JTextField();
        campoCodigo.setToolTipText("Apenas números — ex: 12345");
        estilizarCampo(campoCodigo);

        JLabel lblNome = rotuloCampo("NOME COMPLETO");
        campoNome = new JTextField();
        estilizarCampo(campoNome);

        JLabel lblPerfil = rotuloCampo("PERFIL");
        comboPerfil = new JComboBox<>(new String[]{"Aluno", "Professor"});
        comboPerfil.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboPerfil.setAlignmentX(Component.LEFT_ALIGNMENT);
        comboPerfil.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        labelChaveSecreta = rotuloCampo("CHAVE SECRETA (somente professores)");
        campoChaveSecreta = new JPasswordField();
        estilizarCampo(campoChaveSecreta);
        labelChaveSecreta.setVisible(false);
        campoChaveSecreta.setVisible(false);

        comboPerfil.addActionListener(e -> {
            boolean prof = "Professor".equals(comboPerfil.getSelectedItem());
            labelChaveSecreta.setVisible(prof);
            campoChaveSecreta.setVisible(prof);
            revalidate();
        });

        JLabel lblSenha = rotuloCampo("SENHA");
        campoSenha = new JPasswordField();
        estilizarCampo(campoSenha);

        JLabel lblConfirmar = rotuloCampo("CONFIRMAR SENHA");
        campoConfirmarSenha = new JPasswordField();
        estilizarCampo(campoConfirmarSenha);

        labelErro = new JLabel(" ");
        labelErro.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelErro.setForeground(JanelaJogo.COR_VERMELHO);
        JPanel painelErro = centralizar(labelErro);

        JButton btnCadastrar = new JButton("CRIAR CONTA");
        btnCadastrar.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnCadastrar.setBackground(JanelaJogo.COR_VERMELHO);
        btnCadastrar.setForeground(Color.WHITE);
        btnCadastrar.setBorderPainted(false);
        btnCadastrar.setFocusPainted(false);
        btnCadastrar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnCadastrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btnCadastrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCadastrar.addActionListener(e -> tentarCadastro());

        JButton btnVoltar = new JButton("Já tem conta? Entrar");
        btnVoltar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnVoltar.setForeground(JanelaJogo.COR_AZUL);
        btnVoltar.setBackground(Color.WHITE);
        btnVoltar.setBorderPainted(false);
        btnVoltar.setFocusPainted(false);
        btnVoltar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        JPanel painelVoltar = centralizar(btnVoltar);
        btnVoltar.addActionListener(e -> jogo.mostrarTela(JanelaJogo.TELA_LOGIN));

        card.add(painelLogo);
        card.add(Box.createVerticalStrut(8));
        card.add(painelTitulo);
        card.add(Box.createVerticalStrut(4));
        card.add(painelSub);
        card.add(Box.createVerticalStrut(16));
        card.add(sep);
        card.add(Box.createVerticalStrut(20));
        card.add(lblCodigo);
        card.add(Box.createVerticalStrut(5));
        card.add(campoCodigo);
        card.add(Box.createVerticalStrut(14));
        card.add(lblNome);
        card.add(Box.createVerticalStrut(5));
        card.add(campoNome);
        card.add(Box.createVerticalStrut(14));
        card.add(lblPerfil);
        card.add(Box.createVerticalStrut(5));
        card.add(comboPerfil);
        card.add(Box.createVerticalStrut(14));
        card.add(labelChaveSecreta);
        card.add(Box.createVerticalStrut(5));
        card.add(campoChaveSecreta);
        card.add(Box.createVerticalStrut(14));
        card.add(lblSenha);
        card.add(Box.createVerticalStrut(5));
        card.add(campoSenha);
        card.add(Box.createVerticalStrut(14));
        card.add(lblConfirmar);
        card.add(Box.createVerticalStrut(5));
        card.add(campoConfirmarSenha);
        card.add(Box.createVerticalStrut(20));
        card.add(btnCadastrar);
        card.add(Box.createVerticalStrut(8));
        card.add(painelErro);
        card.add(Box.createVerticalStrut(6));
        card.add(painelVoltar);

        add(card);
    }

    private void tentarCadastro() {
        String codigo  = campoCodigo.getText().trim();
        String nome    = campoNome.getText().trim();
        String senha   = new String(campoSenha.getPassword());
        String confirmar = new String(campoConfirmarSenha.getPassword());
        String perfil  = (String) comboPerfil.getSelectedItem();

        if (codigo.isEmpty() || nome.isEmpty() || senha.isEmpty()) {
            labelErro.setText("Preencha todos os campos obrigatórios.");
            return;
        }

        if (!codigo.matches("\\d+")) {
            labelErro.setText("O código deve conter apenas números.");
            return;
        }

        if (!senha.equals(confirmar)) {
            labelErro.setText("As senhas não coincidem.");
            return;
        }

        if (senha.length() < 4) {
            labelErro.setText("A senha deve ter no mínimo 4 caracteres.");
            return;
        }

        boolean eProfessor = "Professor".equals(perfil);
        if (eProfessor) {
            String chave = new String(campoChaveSecreta.getPassword());
            if (!CHAVE_PROFESSOR.equals(chave)) {
                labelErro.setText("Chave secreta de professor incorreta.");
                return;
            }
        }

        String tipoUsuario = eProfessor ? "professor" : "aluno";
        String sufixo = eProfessor ? "@cps.sp.gov.br" : "@aluno.cps.sp.gov.br";
        String email  = codigo + sufixo;

        String sql = "INSERT INTO usuarios (codigo_num_individual, nome, email_completo, senha, tipo_usuario) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigo);
            ps.setString(2, nome);
            ps.setString(3, email);
            ps.setString(4, senha);
            ps.setString(5, tipoUsuario);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,
                "Conta criada com sucesso!\nSeu e-mail institucional: " + email,
                "Cadastro realizado",
                JOptionPane.INFORMATION_MESSAGE
            );
            jogo.mostrarTela(JanelaJogo.TELA_LOGIN);

        } catch (SQLException e) {
            e.printStackTrace();
            if (e.getErrorCode() == 1062 || (e.getMessage() != null && e.getMessage().contains("Duplicate"))) {
                labelErro.setText("Código " + codigo + " já cadastrado no sistema.");
            } else {
                labelErro.setText("Erro ao salvar: " + e.getMessage());
            }
        }
    }

    private JPanel centralizar(JComponent comp) {
        JPanel w = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        w.setOpaque(false);
        w.setAlignmentX(Component.LEFT_ALIGNMENT);
        w.add(comp);
        return w;
    }

    private JLabel rotuloCampo(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(JanelaJogo.COR_TEXTO_CINZA);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private void estilizarCampo(JTextField campo) {
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        campo.setPreferredSize(new Dimension(200, 44));
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
    }
}
