package modelo;

/**
 * NUEVA CLASE: Define una promoción o descuento (ej. "Oferta Verano").
 * El recepcionista podrá agregar o eliminar estas promociones.
 */
public class Promocion {
    
    private int id;
    private String nombre;
    private double porcentajeDescuento; // Ej. 15.0 para 15%

    public Promocion(int id, String nombre, double porcentajeDescuento) {
        this.id = id;
        this.nombre = nombre;
        this.porcentajeDescuento = porcentajeDescuento;
    }

    // --- Getters (Para leer los datos) ---
    
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPorcentajeDescuento() {
        return porcentajeDescuento;
    }

    // --- Setters (Para modificar los datos) ---
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setPorcentajeDescuento(double porcentajeDescuento) {
        this.porcentajeDescuento = porcentajeDescuento;
    }
}