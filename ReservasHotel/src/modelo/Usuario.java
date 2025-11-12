package modelo;

public class Usuario {
    private String idUsuario;
    private String password;
    private String rol;
    private String nombreCompleto;
    private String apellidoCompleto;
    private String cedula;
    private String direccion;
    public Usuario(String idUsuario, String password, String rol,String nombreCompleto, String apellidoCompleto, String cedula, String direccion) {
        this.idUsuario = idUsuario;
        this.password = password;
        this.rol = rol;
        this.nombreCompleto = nombreCompleto;
        this.apellidoCompleto = apellidoCompleto;
        this.cedula = cedula;
        this.direccion = direccion;
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