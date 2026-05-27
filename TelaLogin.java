import javax.swing.*;
import java.awt.*;
import java.sql.*;

/**
 * Tela de Login do QuimQuest.
 *
 * Autenticação via banco MySQL:
 *   SELECT por 'email_completo' e 'senha' na tabela 'usuarios'.
 *   Ao autenticar, os dados ficam em SessaoUsuario (singleton) e o
 *   usuário é redirecionado para TelaMenuAluno ou TelaMenuProfessor.
 */
public class TelaLogin extends JPanel {

    private JanelaJogo     jogo;
    private JTextField     campoCodigo;
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

        // Logo "TEtec"
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

        // Escola
        JLabel lblEscola = new JLabel("Júlio de Mesquita · São Caetano do Sul");
        lblEscola.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblEscola.setForeground(JanelaJogo.COR_TEXTO_CINZA);
        JPanel painelEscola = centralizar(lblEscola);

        // Separador
        JSeparator sep = new JSeparator();
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        // Título
        JLabel lblTitulo = new JLabel("Jogo Educativo de Química");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        JPanel painelTitulo = centralizar(lblTitulo);

        // Campo: E-mail institucional
        JLabel lblCodigo = rotuloCampo("E-MAIL INSTITUCIONAL");
        campoCodigo = new JTextField();
        campoCodigo.setToolTipText("ex: 26085@aluno.cps.sp.gov.br");
        estilizarCampo(campoCodigo);

        // Campo: Senha
        JLabel lblSenha = rotuloCampo("SENHA");
        campoSenha = new JPasswordField();
        estilizarCampo(campoSenha);

        // Botão Entrar
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
        campoCodigo.addActionListener(e -> tentarLogin());
        campoSenha.addActionListener(e -> tentarLogin());

        // Label de erro
        labelErro = new JLabel(" ");
        labelErro.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelErro.setForeground(JanelaJogo.COR_VERMELHO);
        JPanel painelErro = centralizar(labelErro);

        // Link para cadastro
        JButton btnCadastro = new JButton("Não tem conta? Cadastre-se");
        btnCadastro.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCadastro.setForeground(JanelaJogo.COR_AZUL);
        btnCadastro.setBackground(Color.WHITE);
        btnCadastro.setBorderPainted(false);
        btnCadastro.setFocusPainted(false);
        btnCadastro.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCadastro.addActionListener(e -> jogo.mostrarTela(JanelaJogo.TELA_CADASTRO));
        JPanel painelCadastro = centralizar(btnCadastro);

        // Montagem
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
        card.add(campoCodigo);
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

    /**
     * Busca o usuário no banco pelo código e senha.
     * Se encontrado, inicia a sessão e redireciona para o menu correto.
     */
    private void tentarLogin() {
        String email = campoCodigo.getText().trim().toLowerCase();
        String senha = new String(campoSenha.getPassword());

        if (email.isEmpty() || senha.isEmpty()) {
            labelErro.setText("Preencha o e-mail e a senha.");
            return;
        }

        String sql = "SELECT id, codigo_num_individual, nome, email_completo, tipo_usuario " +
                     "FROM usuarios WHERE email_completo = ? AND senha = ?";

        try (Connection con = DatabaseConnection.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, senha);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Login bem-sucedido — inicia a sessão
                    SessaoUsuario.getInstancia().iniciarSessao(
                        rs.getInt("id"),
                        rs.getString("codigo_num_individual"),
                        rs.getString("nome"),
                        rs.getString("email_completo"),
                        rs.getString("tipo_usuario")
                    );

                    labelErro.setText(" ");
                    campoCodigo.setText("");
                    campoSenha.setText("");

                    // Cria o objeto Usuario legado para compatibilidade com JanelaJogo
                    SessaoUsuario s = SessaoUsuario.getInstancia();
                    Usuario u = new Usuario(s.getNome(), s.getEmail(), "", s.isProfessor());
                    jogo.fazerLogin(u);

                } else {
                    labelErro.setText("E-mail ou senha incorretos.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace(); // imprime o erro completo no terminal do VS Code
            labelErro.setText("Erro de conexão com o banco de dados.");
        }
    }

    // ---- helpers de layout ----

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
}
