import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TelaMenuProfessor extends JPanel {

    private JanelaJogo jogo;
    private Cabecalho  cabecalho;

    public TelaMenuProfessor(JanelaJogo jogo) {
        this.jogo = jogo;

        setBackground(JanelaJogo.COR_FUNDO);
        setLayout(new BorderLayout());

        cabecalho = new Cabecalho(jogo, JanelaJogo.TELA_MENU_PROFESSOR);
        add(cabecalho, BorderLayout.NORTH);

        JPanel conteudo = new JPanel();
        conteudo.setBackground(JanelaJogo.COR_FUNDO);
        conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));
        conteudo.setBorder(BorderFactory.createEmptyBorder(28, 36, 28, 36));

        conteudo.add(construirTitulo());
        conteudo.add(Box.createVerticalStrut(28));
        conteudo.add(construirCardsDeAcao());
        conteudo.add(Box.createVerticalStrut(20));
        conteudo.add(construirStats());

        add(new JScrollPane(conteudo), BorderLayout.CENTER);
    }

    private JPanel construirTitulo() {
        JPanel painel = new JPanel();
        painel.setOpaque(false);
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel linha = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linha.setOpaque(false);

        JLabel lblPainel = new JLabel("Painel do");
        lblPainel.setFont(new Font("Segoe UI", Font.BOLD, 30));

        JLabel lblProf = new JLabel("Professor");
        lblProf.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblProf.setForeground(JanelaJogo.COR_VERMELHO);

        linha.add(lblPainel);
        linha.add(new JLabel(" "));
        linha.add(lblProf);

        JLabel lblSub = new JLabel("Gerencie o conteúdo do jogo");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(JanelaJogo.COR_TEXTO_CINZA);

        painel.add(linha);
        painel.add(Box.createVerticalStrut(4));
        painel.add(lblSub);

        return painel;
    }

    private JPanel construirCardsDeAcao() {
        JPanel painel = new JPanel();
        painel.setOpaque(false);
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel linha = new JPanel(new GridLayout(1, 4, 16, 0));
        linha.setOpaque(false);
        linha.setAlignmentX(Component.LEFT_ALIGNMENT);
        linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        linha.add(criarCard("CRIAR QUIZ", "Montar quiz completo",
            JanelaJogo.COR_VERMELHO,
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    jogo.mostrarTela(JanelaJogo.TELA_CRIAR_QUIZ);
                }
            }
        ));

        linha.add(criarCard("QUIZES PRONTOS", "Gerenciar questões",
            new Color(200, 100, 0),
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    jogo.abrirQuizesProntos();
                }
            }
        ));

        linha.add(criarCard("MATÉRIA", "Ver conteúdo",
            JanelaJogo.COR_ROXO,
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    jogo.mostrarTela(JanelaJogo.TELA_MATERIA);
                }
            }
        ));

        linha.add(criarCard("DESEMPENHO", "Ver alunos",
            JanelaJogo.COR_VERDE,
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    jogo.abrirDesempenhoGeral();
                }
            }
        ));

        painel.add(linha);

        return painel;
    }

    private JPanel criarCard(String titulo, String subtitulo,
                              Color cor, ActionListener acao) {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(JanelaJogo.COR_BORDA, 1, true),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitulo.setForeground(cor);

        JLabel lblSub = new JLabel(subtitulo);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(JanelaJogo.COR_TEXTO_CINZA);

        card.add(Box.createVerticalGlue());
        card.add(lblTitulo);
        card.add(Box.createVerticalStrut(6));
        card.add(lblSub);
        card.add(Box.createVerticalGlue());

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                acao.actionPerformed(null);
            }
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBackground(new Color(248, 248, 248));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBackground(Color.WHITE);
            }
        });

        return card;
    }

    private JPanel construirStats() {
        JPanel linha = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        linha.setOpaque(false);
        linha.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblQuizAtivos = new JLabel(
            String.valueOf(jogo.getQuizzesDoDomain().size())
        );
        linha.add(criarMiniStat(lblQuizAtivos, "Quizes ativos",
            JanelaJogo.COR_VERMELHO));

        JLabel lblAlunos = new JLabel("1");
        linha.add(criarMiniStat(lblAlunos, "Alunos", JanelaJogo.COR_TEXTO_ESCURO));

        return linha;
    }

    private JPanel criarMiniStat(JLabel lblNum, String desc, Color cor) {
        JPanel stat = new JPanel();
        stat.setBackground(Color.WHITE);
        stat.setLayout(new BoxLayout(stat, BoxLayout.Y_AXIS));
        stat.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 3, 0, 0, cor),
            BorderFactory.createEmptyBorder(10, 14, 10, 40)
        ));

        lblNum.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblNum.setForeground(cor);

        JLabel lblDesc = new JLabel(desc);
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDesc.setForeground(JanelaJogo.COR_TEXTO_CINZA);

        stat.add(lblNum);
        stat.add(lblDesc);
        return stat;
    }

    public void atualizarDados() {
        cabecalho.atualizar();
        revalidate();
        repaint();
    }
}
