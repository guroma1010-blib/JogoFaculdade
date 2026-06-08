import java.util.ArrayList;
import java.util.List;

public class Usuario {

    private String nome;
    private String email;
    private String senha;
    private boolean professor;

    private int pontos;
    private List<ResultadoQuiz> historico;

    public Usuario(String nome, String email, String senha, boolean professor) {
        this.nome       = nome;
        this.email      = email;
        this.senha      = senha;
        this.professor  = professor;
        this.pontos     = 0;
        this.historico  = new ArrayList<>();
    }

    public void adicionarPontos(int quantidade) {
        this.pontos += quantidade;
    }

    public void adicionarResultado(ResultadoQuiz resultado) {
        historico.add(resultado);
    }

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

    public String getNome()                    { return nome; }
    public String getEmail()                   { return email; }
    public String getSenha()                   { return senha; }
    public boolean isProfessor()               { return professor; }
    public int getPontos()                     { return pontos; }
    public List<ResultadoQuiz> getHistorico()  { return historico; }
}
