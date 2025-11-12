package modelo;

import java.util.List; // Importamos la clase List

/**
 * MODIFICADO: Ahora la reserva también almacena una lista de servicios
 * contratados, la promoción aplicada y el precio final que se calculó.
 */
public class Reserva {
    
    // --- Atributos Existentes ---
    private int id;
    private String idCliente;
    private int idHabitacion;
    private String fechaEntrada;
    private String fechaSalida;
    private String estado;
    private List<Servicio> serviciosContratados;
    private Promocion promocionAplicada;
    private double precioFinalCalculado;

    // --- Constructor  ---
    /**
     * Constructor actualizado para incluir los nuevos datos de facturación.
     */
    public Reserva(int id, String idCliente, int idHabitacion, String fechaEntrada, String fechaSalida, String estado,
    List<Servicio> servicios, Promocion promocion, double precioFinal) {

        this.id = id;
        this.idCliente = idCliente;
        this.idHabitacion = idHabitacion;
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        this.estado = estado;
        this.serviciosContratados = servicios;
        this.promocionAplicada = promocion;
        this.precioFinalCalculado = precioFinal;
    }

    // --- Getters ---
    public int getId() { return id; }
    public String getIdCliente() { return idCliente; }
    public int getIdHabitacion() { return idHabitacion; }
    public String getFechaEntrada() { return fechaEntrada; }
    public String getFechaSalida() { return fechaSalida; }
    public String getEstado() { return estado; }
    public List<Servicio> getServiciosContratados() { return serviciosContratados; }
    public Promocion getPromocionAplicada() { return promocionAplicada; }
    public double getPrecioFinalCalculado() { return precioFinalCalculado; }

    // --- Setters ---
    
    public void setEstado(String estado) { this.estado = estado; }
    public void setIdHabitacion(int idHabitacion) { this.idHabitacion = idHabitacion; }
    public void setFechaEntrada(String fechaEntrada) { this.fechaEntrada = fechaEntrada; }
    public void setFechaSalida(String fechaSalida) { this.fechaSalida = fechaSalida; }
}