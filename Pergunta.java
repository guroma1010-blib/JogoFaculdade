/**
 * Modelo de dados de uma pergunta de múltipla escolha.
 *
 * Cada pergunta tem:
 *   - Um enunciado (texto da pergunta)
 *   - Quatro opções de resposta (A, B, C, D)
 *   - O índice da opção correta (0=A, 1=B, 2=C, 3=D)
 *   - Uma dica opcional (exibida ao clicar em "Dica", custa -5 pts)
 *   - Um caminho de imagem opcional (null se não houver imagem)
 *
 * Dois construtores:
 *   - Sem imagem (mantém compatibilidade com todo o código existente)
 *   - Com imagem  (usado pelo professor ao criar a questão com foto)
 */
public class Pergunta {

    private String   enunciado;
    private String[] opcoes;               // sempre 4: [A, B, C, D]
    private int      indiceRespostaCorreta; // 0=A, 1=B, 2=C, 3=D
    private String   dica;
    private String   caminhoImagem;         // caminho absoluto do arquivo; null = sem imagem

    // ------------------------------------------------------------------
    //  Construtores
    // ------------------------------------------------------------------

    /**
     * Construtor SEM imagem (mantém compatibilidade com as questões de demo).
     * Define caminhoImagem como null.
     */
    public Pergunta(String enunciado, String[] opcoes,
                    int indiceRespostaCorreta, String dica) {
        this(enunciado, opcoes, indiceRespostaCorreta, dica, null);
    }

    /**
     * Construtor COM imagem — usado pelo professor ao criar questões com foto.
     *
     * @param caminhoImagem caminho absoluto do arquivo de imagem no disco,
     *                      ou null / "" se não houver imagem.
     */
    public Pergunta(String enunciado, String[] opcoes,
                    int indiceRespostaCorreta, String dica, String caminhoImagem) {
        this.enunciado             = enunciado;
        this.opcoes                = opcoes;
        this.indiceRespostaCorreta = indiceRespostaCorreta;
        this.dica                  = dica;
        // normaliza: string vazia vira null para simplificar as verificações
        this.caminhoImagem = (caminhoImagem != null && !caminhoImagem.isEmpty())
                             ? caminhoImagem : null;
    }

    // ------------------------------------------------------------------
    //  Métodos de negócio
    // ------------------------------------------------------------------

    /**
     * Verifica se a opção escolhida pelo aluno está correta.
     * @param indiceEscolhido 0=A, 1=B, 2=C, 3=D
     */
    public boolean isCorreta(int indiceEscolhido) {
        return indiceEscolhido == indiceRespostaCorreta;
    }

    /** Retorna true se esta pergunta possui uma imagem associada. */
    public boolean temImagem() {
        return caminhoImagem != null;
    }

    // ------------------------------------------------------------------
    //  Getters
    // ------------------------------------------------------------------

    public String   getEnunciado()         { return enunciado; }
    public String[] getOpcoes()            { return opcoes; }
    public int      getIndiceCorreto()     { return indiceRespostaCorreta; }
    public String   getDica()              { return dica; }
    /** Retorna o caminho absoluto da imagem, ou null se não houver. */
    public String   getCaminhoImagem()     { return caminhoImagem; }
}
