package modelo;

public class Usuario {
    private String idUsuario;
    private String password;
    private String rol;

    public Usuario(String idUsuario, String password, String rol) {
        this.idUsuario = idUsuario;
        this.password = password;
        this.rol = rol;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public String getPassword() {
        return password;
    }

    public String getRol() {
        return rol;
    }
}