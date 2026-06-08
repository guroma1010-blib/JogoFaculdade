public class Main {

    public static void main(String[] args) {

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

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new JanelaJogo();
                } catch (Exception ex) {
                    System.err.println("======================================================");
                    System.err.println("EXCEÇÃO NA INTERFACE GRÁFICA (AWT EventQueue):");
                    System.err.println("======================================================");
                    ex.printStackTrace();
                }
            }
        });
    }
}
