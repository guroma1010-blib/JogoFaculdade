/**
 * ============================================================
 *  QUIMQUEST — Jogo Educativo de Química (ETEC Júlio de Mesquita)
 * ============================================================
 *
 *  Ponto de entrada do programa.
 *  Esta classe só inicia a aplicação na thread certa do Swing.
 */
public class Main {

    public static void main(String[] args) {

        // ============================================================
        //  DIAGNÓSTICO DE CONEXÃO — roda ANTES de abrir qualquer tela
        //  Leia o terminal do VS Code para ver o resultado.
        // ============================================================
        try {
            System.out.println(">>> [1/2] Tentando carregar o driver do MySQL...");
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println(">>> [1/2] Driver carregado com sucesso!");

            System.out.println(">>> [2/2] Tentando conectar ao banco QuimQuest...");
            java.sql.Connection conn = DatabaseConnection.getConexao();
            if (conn != null) {
                System.out.println(">>> [2/2] CONEXÃO COM O MYSQL ESTABELECIDA COM SUCESSO!");
                conn.close();
            }

        } catch (ClassNotFoundException e) {
            System.err.println("======================================================");
            System.err.println("ERRO CRÍTICO: Driver MySQL (.jar) não encontrado!");
            System.err.println("SOLUÇÃO: Coloque o arquivo 'mysql-connector-j-*.jar'");
            System.err.println("         dentro da pasta  lib/  do projeto e reinicie");
            System.err.println("         o VS Code (Ctrl+Shift+P → Clean Java Workspace)");
            System.err.println("======================================================");
            e.printStackTrace();

        } catch (java.sql.SQLException e) {
            System.err.println("======================================================");
            System.err.println("ERRO CRÍTICO: Falha ao conectar ao MySQL!");
            System.err.println("Verifique: senha correta? MySQL Server em execução?");
            System.err.println("           banco 'QuimQuest' criado?");
            System.err.println("======================================================");
            e.printStackTrace();
        }

        // ============================================================
        //  Inicia a interface gráfica na thread do Swing (EDT)
        // ============================================================
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new JanelaJogo();
                } catch (Exception ex) {
                    // Captura qualquer exceção não tratada dentro do Swing
                    // e imprime no terminal, em vez de silenciar na AWT EventQueue
                    System.err.println("======================================================");
                    System.err.println("EXCEÇÃO NA INTERFACE GRÁFICA (AWT EventQueue):");
                    System.err.println("======================================================");
                    ex.printStackTrace();
                }
            }
        });
    }
}
