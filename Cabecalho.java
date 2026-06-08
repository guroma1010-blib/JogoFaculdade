import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Cabecalho extends JPanel {

    private JanelaJogo jogo;
    private String     telaInicio;

    private JLabel labelEmail;
    private JLabel labelPontos;

    public Cabecalho(JanelaJogo jogo, String telaInicio) {
        this.jogo       = jogo;
        this.telaInicio = telaInicio;

        setBackground(JanelaJogo.COR_VERMELHO);
        setPreferredSize(new Dimension(0, 54));
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));

        construirCabecalho();
    }

    private void construirCabecalho() {
        JPanel esquerda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        esquerda.setOpaque(false);

        JLabel lblQuimQuest = new JLabel("QUIMQUEST");
        lblQuimQuest.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblQuimQuest.setForeground(Color.WHITE);

        JLabel lblEscola = new JLabel("ETEC Júlio de Mesquita");
        lblEscola.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblEscola.setForeground(new Color(255, 180, 180));

        esquerda.add(lblQuimQuest);
        esquerda.add(lblEscola);

        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        direita.setOpaque(false);

        labelEmail = new JLabel("");
        labelEmail.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        labelEmail.setForeground(new Color(255, 210, 210));

        labelPontos = new JLabel("0 pts");
        labelPontos.setFont(new Font("Segoe UI", Font.BOLD, 13));
        labelPontos.setForeground(Color.BLACK);
        labelPontos.setBackground(new Color(204, 175, 0));
        labelPontos.setOpaque(true);
        labelPontos.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(140, 120, 0), 1, true),
            BorderFactory.createEmptyBorder(3, 10, 3, 10)
        ));
        labelPontos.setVisible(false);

        JButton btnInicio = criarBotao("Início");
        btnInicio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jogo.mostrarTela(telaInicio);
            }
        });

        JButton btnSair = criarBotao("Sair");
        btnSair.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jogo.fazerLogout();
            }
        });

        direita.add(labelEmail);
        direita.add(labelPontos);
        direita.add(btnInicio);
        direita.add(btnSair);

        add(esquerda, BorderLayout.WEST);
        add(direita,  BorderLayout.EAST);
    }

    public void atualizar() {
        Usuario u = jogo.getUsuarioLogado();
        if (u == null) return;

        labelEmail.setText(u.getEmail());

        if (!u.isProfessor()) {
            labelPontos.setText(u.getPontos() + " pts");
            labelPontos.setVisible(true);
        } else {
            labelPontos.setVisible(false);
        }

        revalidate();
        repaint();
    }

    private JButton criarBotao(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setBackground(Color.WHITE);
        btn.setForeground(JanelaJogo.COR_VERMELHO);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1, true),
            BorderFactory.createEmptyBorder(4, 14, 4, 14)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
