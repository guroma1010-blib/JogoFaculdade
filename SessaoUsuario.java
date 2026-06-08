public class SessaoUsuario {

    private static SessaoUsuario instancia;

    private int    id;
    private String codigoIndividual;
    private String nome;
    private String email;
    private String tipoUsuario;

    private SessaoUsuario() {}

    public static SessaoUsuario getInstancia() {
        if (instancia == null) {
            instancia = new SessaoUsuario();
        }
        return instancia;
    }

    public void iniciarSessao(int id, String codigoIndividual,
                               String nome, String email, String tipoUsuario) {
        this.id               = id;
        this.codigoIndividual = codigoIndividual;
        this.nome             = nome;
        this.email            = email;
        this.tipoUsuario      = tipoUsuario;
    }

    public void encerrarSessao() {
        this.id               = 0;
        this.codigoIndividual = null;
        this.nome             = null;
        this.email            = null;
        this.tipoUsuario      = null;
    }

    public boolean estaLogado()     { return nome != null; }
    public boolean isProfessor()    { return "professor".equalsIgnoreCase(tipoUsuario); }

    public int    getId()                { return id; }
    public String getCodigoIndividual() { return codigoIndividual; }
    public String getNome()             { return nome; }
    public String getEmail()            { return email; }
    public String getTipoUsuario()      { return tipoUsuario; }
}
