import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.util.List;

/**
 * Tela de Matéria — Conteúdo de Química de Laboratório.
 *
 * Carrega os materiais dinamicamente do banco MySQL (tabela 'materiais').
 * Os dados são agrupados por 'categoria' (aba) e 'grupo' (card dentro da aba).
 *
 * Esquema esperado da tabela 'materiais':
 *   id_material INT
 *   nome        VARCHAR  — ex: "Béquer"
 *   categoria   VARCHAR  — ex: "Vidraria", "Equipamento", "Sistema"
 *   grupo       VARCHAR  — ex: "MEDIÇÃO", "PIPETAS" (título do card; padrão = categoria)
 *   descricao   VARCHAR  — ex: "misturar e aquecer"
 */
public class TelaMateria extends JPanel {

    private JanelaJogo jogo;
    private Cabecalho  cabecalho;

    private JButton[] botoesTema;
    private String[]  nomesTema = {"Vidrarias", "Equipamentos", "Sistemas"};

    private JPanel areConteudo;

    // Dados carregados do banco: categoria → grupo → lista de descrições
    private Map<String, Map<String, List<String>>> dadosDB = new LinkedHashMap<>();

    public TelaMateria(JanelaJogo jogo) {
        this.jogo = jogo;

        setBackground(JanelaJogo.COR_FUNDO);
        setLayout(new BorderLayout());

        cabecalho = new Cabecalho(jogo, JanelaJogo.TELA_MENU_ALUNO);
        add(cabecalho, BorderLayout.NORTH);

        JPanel corpo = new JPanel();
        corpo.setBackground(JanelaJogo.COR_FUNDO);
        corpo.setLayout(new BoxLayout(corpo, BoxLayout.Y_AXIS));
        corpo.setBorder(BorderFactory.createEmptyBorder(24, 36, 24, 36));

        JPanel linhaBotao = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linhaBotao.setOpaque(false);
        linhaBotao.add(criarBotaoVoltar());
        linhaBotao.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel linhaTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        linhaTitulo.setOpaque(false);
        linhaTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblMateria = new JLabel("Matéria");
        lblMateria.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblMateria.setForeground(JanelaJogo.COR_ROXO);

        JLabel lblResto = new JLabel(" — Química de Laboratório");
        lblResto.setFont(new Font("Segoe UI", Font.BOLD, 26));

        linhaTitulo.add(lblMateria);
        linhaTitulo.add(lblResto);

        JPanel abas = construirAbas();
        abas.setAlignmentX(Component.LEFT_ALIGNMENT);

        areConteudo = new JPanel();
        areConteudo.setOpaque(false);
        areConteudo.setLayout(new BoxLayout(areConteudo, BoxLayout.Y_AXIS));
        areConteudo.setAlignmentX(Component.LEFT_ALIGNMENT);

        corpo.add(linhaBotao);
        corpo.add(Box.createVerticalStrut(12));
        corpo.add(linhaTitulo);
        corpo.add(Box.createVerticalStrut(16));
        corpo.add(abas);
        corpo.add(Box.createVerticalStrut(14));
        corpo.add(areConteudo);

        add(new JScrollPane(corpo), BorderLayout.CENTER);

        carregarDoBanco();
        ativarAba(0);
    }

    /**
     * Executa SELECT * FROM materiais e monta a estrutura em memória.
     * Se o banco falhar, mantém os dados hardcoded como fallback.
     */
    private void carregarDoBanco() {
        dadosDB.clear();

        // Inicializa a estrutura com as categorias na ordem correta
        for (String cat : nomesTema) {
            dadosDB.put(cat, new LinkedHashMap<>());
        }

        String sql = "SELECT nome, categoria, COALESCE(grupo, categoria) AS grupo, descricao FROM materiais ORDER BY categoria, grupo, id_material";

        try (Connection con = DatabaseConnection.getConexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String nome      = rs.getString("nome");
                String categoria = rs.getString("categoria");
                String grupo     = rs.getString("grupo");
                String descricao = rs.getString("descricao");

                // Resolve categoria para o nome exato do nomesTema (case-insensitive)
                String categoriaNorm = resolverCategoria(categoria);
                if (categoriaNorm == null) continue;

                Map<String, List<String>> grupos = dadosDB.get(categoriaNorm);
                grupos.computeIfAbsent(grupo, k -> new ArrayList<>())
                      .add(nome + ": " + descricao);
            }

        } catch (SQLException e) {
            System.err.println("[TelaMateria] Banco indisponível — usando dados locais. " + e.getMessage());
            carregarDadosFallback();
        }
    }

    /** Encontra o nome correto da categoria comparando sem case. */
    private String resolverCategoria(String valor) {
        if (valor == null) return null;
        for (String cat : nomesTema) {
            if (cat.equalsIgnoreCase(valor.trim())) return cat;
        }
        // Tentativa por prefixo (ex: "vidraria" → "Vidrarias")
        String v = valor.trim().toLowerCase();
        for (String cat : nomesTema) {
            if (cat.toLowerCase().startsWith(v) || v.startsWith(cat.toLowerCase().substring(0, Math.min(5, cat.length())))) {
                return cat;
            }
        }
        return null;
    }

    /** Dados fixos usados quando o banco não está disponível. */
    private void carregarDadosFallback() {
        Map<String, List<String>> vidrarias = dadosDB.get("Vidrarias");
        vidrarias.put("MEDIÇÃO",   Arrays.asList("Béquer: misturar e aquecer.", "Erlenmeyer: agitar sem respingos.", "Proveta: medir volumes em mL.", "Balão volumétrico: volume exato."));
        vidrarias.put("PIPETAS",   Arrays.asList("Pipeta volumétrica: volume único.", "Pipeta graduada: volumes variados.", "Micropipeta: volumes em μL.", "Bureta: titulações."));
        vidrarias.put("FILTRAÇÃO", Arrays.asList("Funil comum: suporta papel de filtro.", "Funil de Büchner: filtração a vácuo.", "Papel de filtro: retém sólidos.", "Placa de Petri: cultivo e reações."));
        vidrarias.put("OUTROS",    Arrays.asList("Cadinho: calcinar a altas temp.", "Cápsula de porcelana: evaporar.", "Picnômetro: medir densidade.", "Vidro de relógio: pesar sólidos."));

        Map<String, List<String>> equip = dadosDB.get("Equipamentos");
        equip.put("AQUECIMENTO", Arrays.asList("Bico de Bunsen: chama a gás.", "Manta elétrica: aquecimento suave.", "Banho-maria: temperatura uniforme.", "Mufla: altas temperaturas."));
        equip.put("SUPORTE",     Arrays.asList("Tripé: suporte para aquecimento.", "Tela de amianto: distribui calor.", "Garra: fixa vidrarias.", "Suporte universal: estrutura de apoio."));

        Map<String, List<String>> sist = dadosDB.get("Sistemas");
        sist.put("SEPARAÇÃO", Arrays.asList("Filtração: separa sólido de líquido.", "Destilação: separa por ponto de ebulição.", "Decantação: separa líquidos imiscíveis.", "Centrifugação: usa força centrífuga."));
        sist.put("REAÇÕES",   Arrays.asList("Síntese: A + B → AB.", "Decomposição: AB → A + B.", "Neutralização: ácido + base → sal + H₂O.", "Oxirredução: transferência de elétrons."));

    }

    // ------------------------------------------------------------------
    //  Construção das abas e conteúdo
    // ------------------------------------------------------------------

    private JPanel construirAbas() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        painel.setOpaque(false);

        botoesTema = new JButton[nomesTema.length];
        for (int i = 0; i < nomesTema.length; i++) {
            final int indice = i;
            JButton btn = new JButton(nomesTema[i]);
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setBackground(JanelaJogo.COR_FUNDO);
            btn.setForeground(JanelaJogo.COR_TEXTO_CINZA);
            btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) { ativarAba(indice); }
            });
            botoesTema[i] = btn;
            painel.add(btn);
        }
        return painel;
    }

    private void ativarAba(int indice) {
        for (int i = 0; i < botoesTema.length; i++) {
            if (i == indice) {
                botoesTema[i].setForeground(JanelaJogo.COR_ROXO);
                botoesTema[i].setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, JanelaJogo.COR_ROXO));
                botoesTema[i].setFont(new Font("Segoe UI", Font.BOLD, 14));
            } else {
                botoesTema[i].setForeground(JanelaJogo.COR_TEXTO_CINZA);
                botoesTema[i].setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
                botoesTema[i].setFont(new Font("Segoe UI", Font.PLAIN, 14));
            }
        }

        areConteudo.removeAll();
        areConteudo.add(conteudoCategoria(nomesTema[indice]));
        areConteudo.revalidate();
        areConteudo.repaint();
    }

    /**
     * Monta o painel de conteúdo de uma categoria a partir de dadosDB.
     * Os grupos formam cards em grade (máx. 2 colunas).
     */
    private JPanel conteudoCategoria(String categoria) {
        JPanel painel = new JPanel();
        painel.setOpaque(false);
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));

        Map<String, List<String>> grupos = dadosDB.get(categoria);

        if (grupos == null || grupos.isEmpty()) {
            JLabel vazio = new JLabel("Nenhum conteúdo encontrado para " + categoria + ".");
            vazio.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            vazio.setForeground(JanelaJogo.COR_TEXTO_CINZA);
            painel.add(vazio);
            return painel;
        }

        // Frase introdutória baseada na categoria
        JPanel intro = criarFraseIntro(fraseIntro(categoria));
        intro.setAlignmentX(Component.LEFT_ALIGNMENT);
        painel.add(intro);
        painel.add(Box.createVerticalStrut(16));

        // Grade de cards: 2 colunas, N linhas
        List<String> chaves = new ArrayList<>(grupos.keySet());
        int colunas = Math.min(2, chaves.size());
        int linhas  = (int) Math.ceil((double) chaves.size() / colunas);

        JPanel grade = new JPanel(new GridLayout(linhas, colunas, 14, 14));
        grade.setOpaque(false);
        grade.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (String grupo : chaves) {
            List<String> itens = grupos.get(grupo);
            grade.add(criarCartao(grupo, itens.toArray(new String[0])));
        }

        // GridLayout exige células iguais — preenche células vazias
        int total = linhas * colunas;
        for (int i = chaves.size(); i < total; i++) {
            JPanel vazio = new JPanel();
            vazio.setOpaque(false);
            grade.add(vazio);
        }

        painel.add(grade);
        return painel;
    }

    private String fraseIntro(String categoria) {
        switch (categoria) {
            case "Vidrarias":    return "Vidrarias são os recipientes usados para conter, misturar, medir e transferir substâncias.";
            case "Equipamentos": return "Equipamentos auxiliam nas operações de laboratório.";
            case "Sistemas":     return "Sistemas de separação de misturas e reações químicas.";
            default:             return "Conteúdo de " + categoria + ".";
        }
    }

    // ------------------------------------------------------------------
    //  Helpers visuais
    // ------------------------------------------------------------------

    private JPanel criarFraseIntro(String texto) {
        JPanel faixa = new JPanel(new BorderLayout());
        faixa.setBackground(Color.WHITE);
        faixa.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, JanelaJogo.COR_ROXO),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        faixa.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        faixa.add(lbl);
        return faixa;
    }

    private JPanel criarCartao(String titulo, String[] itens) {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(JanelaJogo.COR_BORDA, 1, true),
            BorderFactory.createEmptyBorder(16, 18, 16, 18)
        ));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitulo.setForeground(JanelaJogo.COR_VERMELHO);

        card.add(lblTitulo);
        card.add(Box.createVerticalStrut(10));

        for (String item : itens) {
            JLabel linha = new JLabel("→  " + item);
            linha.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            card.add(linha);
            card.add(Box.createVerticalStrut(6));
        }

        return card;
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
                Usuario u = jogo.getUsuarioLogado();
                if (u != null && u.isProfessor()) {
                    jogo.mostrarTela(JanelaJogo.TELA_MENU_PROFESSOR);
                } else {
                    jogo.mostrarTela(JanelaJogo.TELA_MENU_ALUNO);
                }
            }
        });
        return btn;
    }
}
