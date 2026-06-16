import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class TelaLogin extends JPanel {

    private JanelaJogo     jogo;
    private JTextField     campoEmail;
    private JPasswordField campoSenha;
    private JLabel         labelErro;

    public TelaLogin(JanelaJogo jogo) {
        this.jogo = jogo;
        setBackground(JanelaJogo.COR_FUNDO);
        setLayout(new GridBagLayout());
        construirCard();
    }

    private void construirCard() {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(460, 540));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(JanelaJogo.COR_BORDA, 1, true),
            BorderFactory.createEmptyBorder(36, 48, 36, 48)
        ));

        JPanel painelLogo = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        painelLogo.setBackground(Color.WHITE);
        painelLogo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblT = new JLabel("T");
        lblT.setFont(new Font("Segoe UI", Font.BOLD, 40));
        lblT.setForeground(JanelaJogo.COR_VERMELHO);

        JLabel lblEtec = new JLabel("Etec");
        lblEtec.setFont(new Font("Segoe UI", Font.BOLD, 40));

        painelLogo.add(lblT);
        painelLogo.add(lblEtec);

        JLabel lblEscola = new JLabel("Júlio de Mesquita · São Caetano do Sul");
        lblEscola.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblEscola.setForeground(JanelaJogo.COR_TEXTO_CINZA);
        JPanel painelEscola = centralizar(lblEscola);

        JSeparator sep = new JSeparator();
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        JLabel lblTitulo = new JLabel("Jogo Educativo de Química");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        JPanel painelTitulo = centralizar(lblTitulo);

        JLabel lblCodigo = rotuloCampo("E-MAIL INSTITUCIONAL");
        campoEmail = new JTextField();
        campoEmail.setToolTipText("ex: 26085@aluno.cps.sp.gov.br");
        estilizarCampo(campoEmail);

        JLabel lblSenha = rotuloCampo("SENHA");
        campoSenha = new JPasswordField();
        estilizarCampo(campoSenha);

        JButton btnEntrar = new JButton("ENTRAR");
        btnEntrar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnEntrar.setBackground(JanelaJogo.COR_VERMELHO);
        btnEntrar.setForeground(Color.WHITE);
        btnEntrar.setBorderPainted(false);
        btnEntrar.setFocusPainted(false);
        btnEntrar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnEntrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btnEntrar.setPreferredSize(new Dimension(200, 50));
        btnEntrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btnEntrar.addActionListener(e -> tentarLogin());
        campoEmail.addActionListener(e -> tentarLogin());
        campoSenha.addActionListener(e -> tentarLogin());

        labelErro = new JLabel(" ");
        labelErro.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelErro.setForeground(JanelaJogo.COR_VERMELHO);
        JPanel painelErro = centralizar(labelErro);

        JButton btnCadastro = new JButton("Não tem conta? Cadastre-se");
        btnCadastro.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCadastro.setForeground(JanelaJogo.COR_AZUL);
        btnCadastro.setBackground(Color.WHITE);
        btnCadastro.setBorderPainted(false);
        btnCadastro.setFocusPainted(false);
        btnCadastro.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCadastro.addActionListener(e -> {
            if (jogo != null) {
                jogo.mostrarTela(JanelaJogo.TELA_CADASTRO);
            }
        });
        JPanel painelCadastro = centralizar(btnCadastro);

        card.add(painelLogo);
        card.add(Box.createVerticalStrut(6));
        card.add(painelEscola);
        card.add(Box.createVerticalStrut(20));
        card.add(sep);
        card.add(Box.createVerticalStrut(16));
        card.add(painelTitulo);
        card.add(Box.createVerticalStrut(28));
        card.add(lblCodigo);
        card.add(Box.createVerticalStrut(5));
        card.add(campoEmail);
        card.add(Box.createVerticalStrut(16));
        card.add(lblSenha);
        card.add(Box.createVerticalStrut(5));
        card.add(campoSenha);
        card.add(Box.createVerticalStrut(24));
        card.add(btnEntrar);
        card.add(Box.createVerticalStrut(10));
        card.add(painelErro);
        card.add(Box.createVerticalStrut(10));
        card.add(painelCadastro);

        add(card);
    }

    private void tentarLogin() {
        String email = campoEmail.getText().trim().toLowerCase();
        String senha = new String(campoSenha.getPassword());

        if (email.isEmpty() || senha.isEmpty()) {
            labelErro.setText("Preencha o e-mail e a senha.");
            return;
        }

        String sql = "SELECT id_usuario, codigo_num_individual, nome, email_completo, tipo_usuario " +
                     "FROM usuarios WHERE email_completo = ? AND senha = ?";

        try (Connection con = DatabaseConnection.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, senha);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    SessaoUsuario.getInstancia().iniciarSessao(
                        rs.getInt("id_usuario"),
                        rs.getString("codigo_num_individual"),
                        rs.getString("nome"),
                        rs.getString("email_completo"),
                        rs.getString("tipo_usuario")
                    );

                    labelErro.setText(" ");
                    campoEmail.setText("");
                    campoSenha.setText("");

                    SessaoUsuario s = SessaoUsuario.getInstancia();
                    Usuario u = new Usuario(s.getNome(), s.getEmail(), "", s.isProfessor());
                    
                    if (jogo != null) {
                        jogo.fazerLogin(u);
                    } else {
                        JOptionPane.showMessageDialog(this, "Login efetuado com sucesso no banco! (Modo Teste)");
                    }

                } else {
                    labelErro.setText("E-mail ou senha incorretos.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            labelErro.setText("Erro de conexão com o banco de dados.");
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
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        campo.setPreferredSize(new Dimension(200, 46));
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
    }

    // MÉTODO MAIN ADICIONADO PARA PERMITIR EXECUTAR A TELA DIRETO
    public static void main(String[] args) {
        // Inicializa algumas constantes simuladas caso a JanelaJogo ainda não tenha rodado
        // Isso evita erros caso seu design dependa de cores estáticas
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("QuimQuest - Teste de Tela");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 650);
            frame.setLocationRelativeTo(null);
            
            // Passa null para o construtor só para testar a interface isolada
            TelaLogin tela = new TelaLogin(null); 
            frame.add(tela);
            
            frame.setVisible(true);
        });
    }
}