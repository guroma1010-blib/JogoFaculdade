import java.util.ArrayList;
import java.util.List;

public class DadosDemostracao {

    public static List<Quiz> criarQuizzesDemostracao() {
        List<Quiz> lista = new ArrayList<>();

        lista.add(criarQuizVidrarias());
        lista.add(criarQuizQuimicaGeral());
        lista.add(criarQuizAvancado());

        return lista;
    }

    private static Quiz criarQuizVidrarias() {
        Quiz quiz = new Quiz("Quiz de Vidrarias", Quiz.Dificuldade.FACIL, "Sistema");

        quiz.adicionarPergunta(new Pergunta(
            "O que faz o funil de separação?",
            new String[]{
                "Filtra sólidos",
                "Separa líquidos imiscíveis",
                "Mede temperatura",
                "Aquece substâncias"
            },
            1,
            "Pense em dois líquidos que não se misturam, como óleo e água."
        ));

        quiz.adicionarPergunta(new Pergunta(
            "Para que serve o béquer?",
            new String[]{
                "Medir volumes exatos",
                "Misturar e aquecer líquidos",
                "Filtrar substâncias",
                "Armazenar gases"
            },
            1,
            "O béquer tem formato de copo largo e escala aproximada."
        ));

        quiz.adicionarPergunta(new Pergunta(
            "Para que serve a proveta?",
            new String[]{
                "Aquecer líquidos",
                "Filtrar soluções",
                "Medir volumes com precisão",
                "Armazenar ácidos"
            },
            2,
            "É uma vidraria cilíndrica alta e graduada em mL."
        ));

        quiz.adicionarPergunta(new Pergunta(
            "Qual vidraria é usada para agitar sem respingos?",
            new String[]{
                "Béquer",
                "Erlenmeyer",
                "Balão volumétrico",
                "Bureta"
            },
            1,
            "Tem formato cônico com gargalo estreito."
        ));

        quiz.adicionarPergunta(new Pergunta(
            "Qual vidraria fornece um volume único e exato?",
            new String[]{
                "Proveta",
                "Erlenmeyer",
                "Pipeta volumétrica",
                "Béquer"
            },
            2,
            "Tem apenas uma marca de calibração."
        ));

        return quiz;
    }

    private static Quiz criarQuizQuimicaGeral() {
        Quiz quiz = new Quiz("Quiz de Química Geral", Quiz.Dificuldade.MEDIO, "Sistema");

        quiz.adicionarPergunta(new Pergunta(
            "Qual é o símbolo do elemento Sódio?",
            new String[]{"So", "Na", "Sd", "Sa"},
            1,
            "Vem do nome latino Natrium."
        ));

        quiz.adicionarPergunta(new Pergunta(
            "O que é uma reação endotérmica?",
            new String[]{
                "Libera calor para o ambiente",
                "Absorve calor do ambiente",
                "Não envolve variação de energia",
                "Produz explosão"
            },
            1,
            "Endo = para dentro. A energia entra no sistema."
        ));

        quiz.adicionarPergunta(new Pergunta(
            "Quantos elétrons tem o átomo de Oxigênio?",
            new String[]{"6", "8", "10", "12"},
            1,
            "O número atômico do Oxigênio na tabela periódica é 8."
        ));

        quiz.adicionarPergunta(new Pergunta(
            "Qual é a fórmula molecular da água?",
            new String[]{"H2O2", "HO", "H2O", "OH2"},
            2,
            "Dois átomos de Hidrogênio e um de Oxigênio."
        ));

        return quiz;
    }

    private static Quiz criarQuizAvancado() {
        Quiz quiz = new Quiz("Quiz Avançado", Quiz.Dificuldade.DIFICIL, "Sistema");

        quiz.adicionarPergunta(new Pergunta(
            "Em uma titulação ácido-base, qual vidraria dispensa o reagente?",
            new String[]{"Erlenmeyer", "Bureta", "Pipeta", "Béquer"},
            1,
            "É uma vidraria longa e graduada com torneira na ponta."
        ));

        quiz.adicionarPergunta(new Pergunta(
            "O que é pH?",
            new String[]{
                "Potencial hidrogeniônico",
                "Potencial hidrostático",
                "Pressão hidráulica",
                "Peso do hidrogênio"
            },
            0,
            "Mede a concentração de íons H⁺ em uma solução."
        ));

        quiz.adicionarPergunta(new Pergunta(
            "Qual é o produto de uma reação de neutralização (ácido + base)?",
            new String[]{"Óxido e água", "Sal e água", "Gás e sólido", "Dois ácidos"},
            1,
            "Reação de neutralização: ácido + base → sal + água."
        ));

        return quiz;
    }
}
