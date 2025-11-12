package datos;

// Imports de nuestro proyecto
import modelo.Usuario;
import modelo.Habitacion;
import modelo.Reserva;
import modelo.Servicio;
import modelo.Promocion;

// Imports de Java
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList; // <-- Necesario para crear listas vacías
import java.util.List;

/**
 * Simula la base de datos del hotel.
 * Contiene listas y mapas estáticos ("globales") con los datos de prueba.
 * (MODIFICADO para usar el nuevo constructor de Reserva)
 */
public class DatosQuemados {
    
    // --- Almacenes de Datos ---
    public static Map<String, Usuario> USUARIOS = new HashMap<>();
    public static Map<Integer, Habitacion> HABITACIONES = new HashMap<>();
    public static List<Reserva> RESERVAS = new ArrayList<>();
    public static List<Servicio> SERVICIOS = new ArrayList<>();
    public static List<Promocion> PROMOCIONES = new ArrayList<>();

    
    // --- Contadores de IDs (para simular el auto-incremento de una BD) ---
    public static int proximoIdReserva = 3;
    public static int proximoIdServicio = 3;
    public static int proximoIdPromocion = 2;


    /**
     * Llena los almacenes de datos con valores de prueba al iniciar la aplicación.
     */
    public static void inicializar() {
        
        // --- Cargar Usuarios ---
        USUARIOS.put("cliente@hotel.com", new Usuario("cliente1", "123", "cliente"));
        USUARIOS.put("cliente2@hotel.com", new Usuario("cliente2", "456", "cliente"));
        USUARIOS.put("recepcion@hotel.com", new Usuario("recep1", "admin", "recepcionista"));
        
        // --- Cargar Habitaciones ---
        HABITACIONES.put(101, new Habitacion(101, "Simple", 50, "disponible", 1));
        HABITACIONES.put(102, new Habitacion(102, "Simple", 50, "disponible", 1));
        HABITACIONES.put(201, new Habitacion(201, "Doble", 80, "ocupada", 2));
        HABITACIONES.put(202, new Habitacion(202, "Doble", 80, "ocupada", 2));
        HABITACIONES.put(301, new Habitacion(301, "Suite", 150, "mantenimiento", 4));
        HABITACIONES.put(203, new Habitacion(203, "Suite", 150, "disponible", 4));
        HABITACIONES.put(305, new Habitacion(305, "Doble", 70, "disponible", 4));
        // --- Cargar Servicios de ejemplo ---
        SERVICIOS.add(new Servicio(1, "WiFi Premium", 10.0));
        SERVICIOS.add(new Servicio(2, "Acceso al Spa", 25.0));
        SERVICIOS.add(new Servicio(10, "Masaje Terapeutico", 25.0));
        // --- Cargar Promociones de ejemplo ---
        PROMOCIONES.add(new Promocion(1, "Oferta de Verano", 15.0)); // 15% de descuento
        
        
        // --- Cargar Reservas (CORREGIDO) ---
        // Ahora usamos el nuevo constructor de 9 argumentos
        
        // Reserva 1 (confirmada)
        // Simulamos que esta reserva no tuvo servicios ni promos
        double precioReserva1 = HABITACIONES.get(101).getPrecio(); // 80.0
        RESERVAS.add(new Reserva(
            5, "cliente1", 101, "2025-11-08", "2025-11-10", "confirmada",
            new ArrayList<>(), null, precioReserva1));

        // Reserva 2 (pendiente)
        // Simulamos que esta reserva tampoco tuvo servicios ni promos
        double precioReserva2 = HABITACIONES.get(203).getPrecio(); // 80.0
        RESERVAS.add(new Reserva(
            1, "cliente2", 203, "2025-11-11", "2025-11-12", "disponible",
            new ArrayList<>(), null, precioReserva2));
    }
}