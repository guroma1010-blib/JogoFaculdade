import java.sql.*;
public class TesteConexao {
    public static void main(String[] args) throws Exception {
        System.out.println("Carregando driver...");
        Class.forName("com.mysql.cj.jdbc.Driver");
        System.out.println("Driver OK!");
        System.out.println("Conectando...");
        Connection c = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/QuimQuest?useTimezone=true&serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true",
            "root", "trizal2026");
        System.out.println("CONEXAO OK! Catalogo: " + c.getCatalog());
        c.close();
    }
}
