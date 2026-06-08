public class Pergunta {

    private String   enunciado;
    private String[] opcoes;
    private int      indiceRespostaCorreta;
    private String   dica;
    private String   caminhoImagem;

    public Pergunta(String enunciado, String[] opcoes,
                    int indiceRespostaCorreta, String dica) {
        this(enunciado, opcoes, indiceRespostaCorreta, dica, null);
    }

    public Pergunta(String enunciado, String[] opcoes,
                    int indiceRespostaCorreta, String dica, String caminhoImagem) {
        this.enunciado             = enunciado;
        this.opcoes                = opcoes;
        this.indiceRespostaCorreta = indiceRespostaCorreta;
        this.dica                  = dica;
        this.caminhoImagem = (caminhoImagem != null && !caminhoImagem.isEmpty())
                             ? caminhoImagem : null;
    }

    public boolean isCorreta(int indiceEscolhido) {
        return indiceEscolhido == indiceRespostaCorreta;
    }

    public boolean temImagem() {
        return caminhoImagem != null;
    }

    public String   getEnunciado()         { return enunciado; }
    public String[] getOpcoes()            { return opcoes; }
    public int      getIndiceCorreto()     { return indiceRespostaCorreta; }
    public String   getDica()              { return dica; }
    public String   getCaminhoImagem()     { return caminhoImagem; }

    public void setEnunciado(String enunciado) { this.enunciado = enunciado; }
}
