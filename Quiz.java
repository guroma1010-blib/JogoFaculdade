import java.util.ArrayList;
import java.util.List;

public class Quiz {

    public enum Dificuldade {
        FACIL  ("Fácil",   10),
        MEDIO  ("Médio",   20),
        DIFICIL("Difícil", 30);

        private final String rotulo;
        private final int    pontosPorAcerto;

        Dificuldade(String rotulo, int pontosPorAcerto) {
            this.rotulo          = rotulo;
            this.pontosPorAcerto = pontosPorAcerto;
        }

        public String getRotulo()       { return rotulo; }
        public int getPontosPorAcerto() { return pontosPorAcerto; }
    }

    private int               id = 0;
    private String            nome;
    private Dificuldade       dificuldade;
    private List<Pergunta>    perguntas;
    private String            criadoPor;

    public Quiz(String nome, Dificuldade dificuldade, String criadoPor) {
        this.nome        = nome;
        this.dificuldade = dificuldade;
        this.criadoPor   = criadoPor;
        this.perguntas   = new ArrayList<>();
    }

    public void adicionarPergunta(Pergunta p) {
        perguntas.add(p);
    }

    public int             getId()               { return id; }
    public void            setId(int id)         { this.id = id; }
    public String          getNome()             { return nome; }
    public Dificuldade     getDificuldade()       { return dificuldade; }
    public List<Pergunta>  getPerguntas()         { return perguntas; }
    public String          getCriadoPor()         { return criadoPor; }
    public int             getNumeroDePerguntas() { return perguntas.size(); }
}
