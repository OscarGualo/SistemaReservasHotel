package modelo;

/**
 * NUEVA CLASE: Define un servicio extra del hotel (ej. Spa, WiFi).
 * El recepcionista gestionará estos servicios, incluyendo sus precios.
 */
public class Servicio {

    private int id;
    private String nombre;
    private double precio;

    public Servicio(int id, String nombre, double precio) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
    }

    // --- Getters (Para leer los datos) ---
    
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }

    // --- Setters (Para modificar los datos) ---
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }
}