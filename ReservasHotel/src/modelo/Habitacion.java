package modelo;

/**
 * Clase Modelo (POJO) que define la estructura de una Habitación.
 * Contiene los atributos, un constructor para crearlas,
 * getters para leer sus datos y setters para modificarlos.
 */
public class Habitacion {
    
    // --- Atributos ---
    private int numero;
    private String tipo;
    private double precio;
    private String estado; // "disponible", "ocupada", "mantenimiento"
    private int capacidad;

    // --- Constructor ---
    public Habitacion(int numero, String tipo, double precio, String estado, int capacidad) {
        this.numero = numero;
        this.tipo = tipo;
        this.precio = precio;
        this.estado = estado;
        this.capacidad = capacidad;
    }

    // --- Getters (Métodos para obtener valores) ---
    
    public int getNumero() {
        return numero;
    }

    public String getTipo() {
        return tipo;
    }

    public double getPrecio() {
        return precio;
    }

    public String getEstado() {
        return estado;
    }

    public int getCapacidad() {
        return capacidad;
    }

    // --- Setters (Métodos para cambiar valores) ---
    
    /**
     * Modifica el estado actual de la habitación.
     * @param estado El nuevo estado ("disponible", "ocupada", "mantenimiento")
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    /**
     * MODIFICADO: Modifica el tipo de habitación (ej. "Simple", "Doble").
     * @param tipo El nuevo tipo.
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * MODIFICADO: Modifica el precio por noche de la habitación.
     * @param precio El nuevo precio.
     */
    public void setPrecio(double precio) {
        this.precio = precio;
    }
}