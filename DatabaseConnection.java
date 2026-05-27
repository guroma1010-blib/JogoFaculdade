import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utilitário de conexão com o banco MySQL — QuimQuest.
 *
 * Uso:
 *   try (Connection con = DatabaseConnection.getConexao()) {
 *       // usar con normalmente
 *   } catch (SQLException e) {
 *       e.printStackTrace();
 *   }
 */
public class DatabaseConnection {

    private static final String URL     = "jdbc:mysql://localhost:3306/QuimQuest"
                                        + "?useTimezone=true&serverTimezone=UTC"
                                        + "&useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USUARIO = "root";
    private static final String SENHA   = "trizal2026";

    private DatabaseConnection() {}

    /**
     * Abre e retorna uma conexão com o banco.
     * O chamador é responsável por fechar a conexão (use try-with-resources).
     *
     * @throws SQLException se o driver não for encontrado ou a conexão falhar
     */
    public static Connection getConexao() throws SQLException {
        System.out.println("[DB] Tentando conectar em: " + URL);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("[DB] Driver MySQL carregado com sucesso.");
        } catch (ClassNotFoundException e) {
            System.err.println("[DB] ERRO: Driver não encontrado! Verifique se o .jar está em lib/");
            throw new SQLException(
                "Driver MySQL não encontrado.\n" +
                "Coloque o arquivo 'mysql-connector-j-*.jar' dentro da pasta 'lib/' do projeto.", e
            );
        }
        Connection con = DriverManager.getConnection(URL, USUARIO, SENHA);
        System.out.println("[DB] Conexão estabelecida com sucesso!");
        return con;
    }

    /**
     * Testa a conexão ao iniciar o sistema.
     * Imprime resultado detalhado no terminal.
     */
    public static void testarConexaoNoTerminal() {
        System.out.println("=== TESTE DE CONEXÃO COM O BANCO ===");
        try (Connection con = getConexao()) {
            if (con != null && !con.isClosed()) {
                System.out.println("[DB] OK — banco 'QuimQuest' acessível.");
            }
        } catch (SQLException e) {
            System.err.println("[DB] FALHA NA CONEXÃO:");
            e.printStackTrace();
            System.err.println("=====================================");
            System.err.println("Causas mais comuns:");
            System.err.println("  1) O arquivo .jar do MySQL não está em lib/");
            System.err.println("  2) O MySQL Server não está em execução");
            System.err.println("  3) Senha errada (verifique DatabaseConnection.java)");
            System.err.println("  4) O banco 'QuimQuest' não foi criado");
            System.err.println("=====================================");
        }
    }
}
