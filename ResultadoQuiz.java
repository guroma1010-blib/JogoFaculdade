/**
 * Guarda o resultado de um quiz feito por um aluno.
 * É armazenado no histórico do Usuario.
 */
public class ResultadoQuiz {

    private String           nomeQuiz;
    private Quiz.Dificuldade dificuldade;
    private int              acertos;
    private int              totalPerguntas;
    private int              pontos;

    public ResultadoQuiz(String nomeQuiz, Quiz.Dificuldade dificuldade,
                         int acertos, int totalPerguntas, int pontos) {
        this.nomeQuiz       = nomeQuiz;
        this.dificuldade    = dificuldade;
        this.acertos        = acertos;
        this.totalPerguntas = totalPerguntas;
        this.pontos         = pontos;
    }

    /** Retorna a porcentagem de acertos neste quiz (0 a 100). */
    public int getPorcentagem() {
        if (totalPerguntas == 0) return 0;
        return (acertos * 100) / totalPerguntas;
    }

    // ---- getters ----

    public String           getNomeQuiz()       { return nomeQuiz; }
    public Quiz.Dificuldade getDificuldade()     { return dificuldade; }
    public int              getAcertos()         { return acertos; }
    public int              getTotalPerguntas()  { return totalPerguntas; }
    public int              getPontos()          { return pontos; }
}
