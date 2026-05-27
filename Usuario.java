import java.util.ArrayList;
import java.util.List;

/**
 * Modelo de dados de um usuário do sistema.
 *
 * Um usuário pode ser ALUNO ou PROFESSOR (campo isProfessor).
 * O aluno acumula pontos e tem um histórico de quizzes feitos.
 */
public class Usuario {

    // ---- atributos do usuário ----
    private String nome;
    private String email;
    private String senha;
    private boolean professor;  // true = professor, false = aluno

    // ---- dados de progresso (apenas alunos usam) ----
    private int pontos;
    private List<ResultadoQuiz> historico;

    /**
     * Construtor principal.
     *
     * @param nome       nome de exibição ("aluno", "Professor Silva"...)
     * @param email      login do usuário
     * @param senha      senha em texto simples (para um projeto real use hash!)
     * @param professor  true se for professor
     */
    public Usuario(String nome, String email, String senha, boolean professor) {
        this.nome       = nome;
        this.email      = email;
        this.senha      = senha;
        this.professor  = professor;
        this.pontos     = 0;
        this.historico  = new ArrayList<>();
    }

    // ---- métodos de negócio ----

    /** Adiciona pontos ao aluno. */
    public void adicionarPontos(int quantidade) {
        this.pontos += quantidade;
    }

    /** Registra o resultado de um quiz no histórico do aluno. */
    public void adicionarResultado(ResultadoQuiz resultado) {
        historico.add(resultado);
    }

    /** Calcula a porcentagem de acertos em todos os quizzes feitos. */
    public int calcularPorcentagemAcertos() {
        if (historico.isEmpty()) return 0;

        int totalAcertos = 0;
        int totalPerguntas = 0;
        for (ResultadoQuiz r : historico) {
            totalAcertos   += r.getAcertos();
            totalPerguntas += r.getTotalPerguntas();
        }
        if (totalPerguntas == 0) return 0;
        return (totalAcertos * 100) / totalPerguntas;
    }

    // ---- getters ----

    public String getNome()                    { return nome; }
    public String getEmail()                   { return email; }
    public String getSenha()                   { return senha; }
    public boolean isProfessor()               { return professor; }
    public int getPontos()                     { return pontos; }
    public List<ResultadoQuiz> getHistorico()  { return historico; }
}
