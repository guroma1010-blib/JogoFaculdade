import java.util.ArrayList;
import java.util.List;

/**
 * Modelo de dados de um quiz completo.
 *
 * Um quiz tem um nome, uma dificuldade (que define quantos pontos
 * cada acerto vale) e uma lista de perguntas.
 */
public class Quiz {

    /**
     * Enum com os três níveis de dificuldade.
     * Cada nível define quantos pontos o aluno ganha por acerto.
     */
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

    // ---- atributos do quiz ----
    private String            nome;
    private Dificuldade       dificuldade;
    private List<Pergunta>    perguntas;
    private String            criadoPor;  // "Sistema" ou e-mail do professor

    /**
     * @param nome        título do quiz
     * @param dificuldade FACIL, MEDIO ou DIFICIL
     * @param criadoPor   quem criou ("Sistema" ou nome do professor)
     */
    public Quiz(String nome, Dificuldade dificuldade, String criadoPor) {
        this.nome        = nome;
        this.dificuldade = dificuldade;
        this.criadoPor   = criadoPor;
        this.perguntas   = new ArrayList<>();
    }

    /** Adiciona uma pergunta ao quiz. */
    public void adicionarPergunta(Pergunta p) {
        perguntas.add(p);
    }

    // ---- getters ----

    public String          getNome()             { return nome; }
    public Dificuldade     getDificuldade()       { return dificuldade; }
    public List<Pergunta>  getPerguntas()         { return perguntas; }
    public String          getCriadoPor()         { return criadoPor; }
    public int             getNumeroDePerguntas() { return perguntas.size(); }
}
