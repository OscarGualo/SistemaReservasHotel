package servicio;

// Imports de nuestro proyecto
import datos.DatosQuemados;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import modelo.Habitacion;
import modelo.Promocion;
import modelo.Reserva;
import modelo.Servicio;
import modelo.Usuario;

/**
 * Clase de Servicio (Lógica de Negocio).
 * Contiene todos los métodos que manipulan los datos.
 * Esta clase se comunica entre la VISTA (GUI) y los DATOS (DatosQuemados).
 */
public class HotelServicio {

    // --- LÓGICA DE SISTEMA ---

    /**
     * Valida las credenciales de un usuario.
     * @param email El email del usuario.
     * @param password La contraseña del usuario.
     * @return El objeto Usuario si el login es exitoso, o null si falla.
     */
    public static Usuario login(String email, String password) {
        Usuario user = DatosQuemados.USUARIOS.get(email);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    /**
     * Registra un nuevo usuario cliente en el sistema.
     * @param email El email para el nuevo usuario (usado como login).
     * @param password La contraseña para el nuevo usuario.
     * @param idUsuario Un ID único para el usuario (simplificado como email).
     * @return Un String con el mensaje de éxito o error.
     */
    public static String registrarUsuario(String email, String password, String idUsuario) {
        if (DatosQuemados.USUARIOS.containsKey(email)) {
            return "Error: El email ya está registrado.";
        }if(contieneArroba(email)){
            Usuario nuevoUsuario = new Usuario(idUsuario, password, "cliente");
            DatosQuemados.USUARIOS.put(email, nuevoUsuario);
            return "¡Usuario registrado exitosamente!";
        }else{
            return "Error: El email ingresado no es el correcto";
        }

    }
    public static boolean contieneArroba(String emailNuevo){
       String regex = "^^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        Pattern pat = Pattern.compile(regex);
        Matcher matcher = pat.matcher(emailNuevo);
        return matcher.matches();
    }
    // --- LÓGICA DE CLIENTE / CONSULTA ---

    /**
     * Obtiene un reporte de todas las habitaciones disponibles.
     * @return Un String formateado con la lista de habitaciones.
     */
    public static String getDisponibilidad() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- Habitaciones Disponibles ---\n");
        int encontradas = 0;
        for (Habitacion hab : DatosQuemados.HABITACIONES.values()) {
            if (hab.getEstado().equals("disponible")) {
                sb.append(String.format("  Habitación %d: %s - $%.2f/noche (Capacidad: %d)\n",
                        hab.getNumero(), hab.getTipo(), hab.getPrecio(), hab.getCapacidad()));
                encontradas++;
            }
        }
        if (encontradas == 0) {
            sb.append("Lo sentimos, no hay habitaciones disponibles en este momento.\n");
        }
        return sb.toString();
    }

    /**
     * Simula el procesamiento de un pago (<<Include>> de Realizar Reserva).
     * @param monto El monto a procesar.
     * @return true si el pago es exitoso, false si falla.
     */
    private static boolean procesarPago(double monto) {
        // En un sistema real, aquí iría la lógica de pasarela de pagos.
        System.out.println("Procesando pago por $" + monto + "...");
        return true; 
    }

    /**
     * MODIFICADO: Procesa la reserva, calcula el precio final, genera factura y procesa el pago.
     * AHORA CALCULA EL NÚMERO DE NOCHES.
     */
    public static String realizarReserva(Usuario cliente, int numHab, String fechaIn, String fechaOut,
                                         List<Integer> idsServiciosSeleccionados, int idPromocionSeleccionada) {
        
        // 1. Validar Habitación
        if (!DatosQuemados.HABITACIONES.containsKey(numHab)) {
            return "Error: Esa habitación no existe.";
        }
        Habitacion hab = DatosQuemados.HABITACIONES.get(numHab);
        if (!hab.getEstado().equals("disponible")) {
            return "Error: Esa habitación no está disponible.";
        }

        // --- INICIO DE CORRECCIÓN: Calcular Noches ---
        long numeroDeNoches;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            LocalDate dateIn = LocalDate.parse(fechaIn, formatter);
            LocalDate dateOut = LocalDate.parse(fechaOut, formatter);
            
            // Validación de fechas
            if (!dateOut.isAfter(dateIn)) {
                return "Error: La fecha de salida debe ser posterior a la fecha de entrada.";
            }
            
            // Calcula el número de días (noches)
            numeroDeNoches = ChronoUnit.DAYS.between(dateIn, dateOut);
            
        } catch (DateTimeParseException e) {
            return "Error: El formato de fecha debe ser YYYY-MM-DD.";
        }
        // --- FIN DE CORRECCIÓN: Calcular Noches ---


        DecimalFormat df = new DecimalFormat("#.00"); // Para formatear precios
        StringBuilder factura = new StringBuilder();
        factura.append("--- FACTURA DE CONSUMIDOR FINAL ---\n");
        factura.append("Cliente: " + cliente.getIdUsuario() + "\n");
        factura.append("-----------------------------------\n");
        factura.append("DETALLE:\n");

        // 2. Calcular costos (CORREGIDO)
        double precioTotalHabitacion = hab.getPrecio() * numeroDeNoches; // <-- ¡CORREGIDO!
        factura.append(String.format("- Habitación %d (%s) x %d noches: $ %s\n", 
            hab.getNumero(), hab.getTipo(), numeroDeNoches, df.format(precioTotalHabitacion)));

        // 3. Buscar y sumar servicios (los servicios se cobran una vez)
        List<Servicio> serviciosContratados = new ArrayList<>();
        double precioServicios = 0;
        for (int idServicio : idsServiciosSeleccionados) {
            for (Servicio s : DatosQuemados.SERVICIOS) {
                if (s.getId() == idServicio) {
                    serviciosContratados.add(s);
                    precioServicios += s.getPrecio();
                    factura.append(String.format("- Servicio: %s: $ %s\n", s.getNombre(), df.format(s.getPrecio())));
                }
            }
        }

        // 4. Calcular Subtotal (CORREGIDO)
        double subtotal = precioTotalHabitacion + precioServicios; // <-- ¡CORREGIDO!
        factura.append("-----------------------------------\n");
        factura.append(String.format("SUBTOTAL: $ %s\n", df.format(subtotal)));

        // 5. Buscar y aplicar promoción
        Promocion promocionAplicada = null;
        double descuento = 0;
        if (idPromocionSeleccionada != 0) {
            for (Promocion p : DatosQuemados.PROMOCIONES) {
                if (p.getId() == idPromocionSeleccionada) {
                    promocionAplicada = p;
                    descuento = subtotal * (p.getPorcentajeDescuento() / 100.0);
                    factura.append(String.format("Promoción (%s -%.1f%%): - $ %s\n",
                        p.getNombre(), p.getPorcentajeDescuento(), df.format(descuento)));
                    break;
                }
            }
        }

        // 6. Calcular Total y Procesar Pago
        double precioFinal = subtotal - descuento;
        factura.append("-----------------------------------\n");
        factura.append(String.format("TOTAL A PAGAR: $ %s\n", df.format(precioFinal)));
        factura.append("-----------------------------------\n");

        boolean pagoExitoso = procesarPago(precioFinal);
        if (!pagoExitoso) {
            return "Error: El pago ha fallado.";
        }

        // 7. Crear la Reserva (Usando el constructor de 9 argumentos)
        Reserva nuevaReserva = new Reserva(
            DatosQuemados.proximoIdReserva,
            cliente.getIdUsuario(),
            numHab, fechaIn, fechaOut, "pendiente",
            serviciosContratados,
            promocionAplicada,
            precioFinal
        );
        
        DatosQuemados.RESERVAS.add(nuevaReserva);
        DatosQuemados.proximoIdReserva++;
        hab.setEstado("ocupada");

        factura.append("Estado: PAGO APROBADO. Reserva #" + nuevaReserva.getId() + " pendiente de confirmación.");
        return factura.toString();
    }


    /**
     * MODIFICADO: Obtiene un reporte de todas las reservas de un cliente, AHORA CON DETALLES.
     * @param cliente El objeto Usuario del cliente.
     * @return Un String formateado con la lista de sus reservas.
     */
    public static String getMisReservas(Usuario cliente) {
        StringBuilder sb = new StringBuilder();
        sb.append("--- Mis Reservas ---\n");
        int encontradas = 0;
        String idClienteActual = cliente.getIdUsuario();
        DecimalFormat df = new DecimalFormat("#.00");

        for (Reserva r : DatosQuemados.RESERVAS) {
            if (r.getIdCliente().equals(idClienteActual)) {
                encontradas++;
                sb.append("-----------------------------------\n");
                sb.append(String.format("ID Reserva: %d (Estado: %s)\n", r.getId(), r.getEstado().toUpperCase()));
                sb.append(String.format("  Habitación: %d, Fechas: %s a %s\n",
                    r.getIdHabitacion(), r.getFechaEntrada(), r.getFechaSalida()));

                if (r.getServiciosContratados() != null && !r.getServiciosContratados().isEmpty()) {
                    sb.append("  Servicios:\n");
                    for (Servicio s : r.getServiciosContratados()) {
                        sb.append(String.format("    - %s ($ %.2f)\n", s.getNombre(), s.getPrecio()));
                    }
                }
                
                if (r.getPromocionAplicada() != null) {
                    sb.append(String.format("  Promoción: %s (%.1f%%)\n",
                        r.getPromocionAplicada().getNombre(), r.getPromocionAplicada().getPorcentajeDescuento()));
                }

                sb.append(String.format("  PRECIO FINAL PAGADO: $ %s\n", df.format(r.getPrecioFinalCalculado())));
            }
        }
        if (encontradas == 0) {
            sb.append("No tienes ninguna reserva registrada.\n");
        }
        sb.append("-----------------------------------\n");
        return sb.toString();
    }


    // --- LÓGICA DE GESTIÓN DE RESERVAS ---

    /**
     * Cancela una reserva existente.
     * @param idReserva El ID de la reserva a cancelar.
     * @param usuario El usuario que realiza la acción (para permisos).
     * @return Un String con el mensaje de éxito o error.
     */
    public static String cancelarReserva(int idReserva, Usuario usuario) {
        Reserva reservaACancelar = null;
        for (Reserva r : DatosQuemados.RESERVAS) {
            if (r.getId() == idReserva) {
                reservaACancelar = r;
                break;
            }
        }
        if (reservaACancelar == null) { return "Error: Reserva no encontrada."; }
        // Chequeo de permisos: O eres el dueño o eres recepcionista
        if (!reservaACancelar.getIdCliente().equals(usuario.getIdUsuario()) && !usuario.getRol().equals("recepcionista")) {
            return "Error: No tienes permisos para cancelar esta reserva.";
        }
        reservaACancelar.setEstado("cancelada");
        // Liberar la habitación
        Habitacion hab = DatosQuemados.HABITACIONES.get(reservaACancelar.getIdHabitacion());
        if (hab != null) {
            hab.setEstado("disponible");
        }
        return String.format("Reserva #%d cancelada. Habitación %d ahora disponible.", 
               idReserva, (hab != null ? hab.getNumero() : 0));
    }

    /**
     * Modifica una reserva existente.
     * @param idReserva El ID de la reserva a modificar.
     * @param nuevaHabNum El número de la nueva habitación.
     * @param nuevaFechaIn La nueva fecha de entrada.
     * @param nuevaFechaOut La nueva fecha de salida.
     * @param usuario El usuario que realiza la acción (para permisos).
     * @return Un String con el mensaje de éxito o error.
     */
    public static String modificarReserva(int idReserva, int nuevaHabNum, String nuevaFechaIn, String nuevaFechaOut, Usuario usuario) {
        Reserva reservaAModificar = null;
        for (Reserva r : DatosQuemados.RESERVAS) {
            if (r.getId() == idReserva) {
                reservaAModificar = r;
                break;
            }
        }
        if (reservaAModificar == null) { return "Error: Reserva no encontrada."; }
        // Chequeo de permisos
        if (!reservaAModificar.getIdCliente().equals(usuario.getIdUsuario()) && !usuario.getRol().equals("recepcionista")) {
            return "Error: No tienes permisos para modificar esta reserva.";
        }
        // (<<Include>> Consultar disponibilidad)
        if (!DatosQuemados.HABITACIONES.containsKey(nuevaHabNum)) {
            return "Error: La nueva habitación no existe.";
        }
        Habitacion nuevaHab = DatosQuemados.HABITACIONES.get(nuevaHabNum);
        // Si la habitación es diferente, verificar disponibilidad
        if (reservaAModificar.getIdHabitacion() != nuevaHabNum) {
            if (!nuevaHab.getEstado().equals("disponible")) {
                return "Error: La nueva habitación " + nuevaHabNum + " no está disponible.";
            }
            // Liberar la habitación antigua
            Habitacion habAntigua = DatosQuemados.HABITACIONES.get(reservaAModificar.getIdHabitacion());
            if (habAntigua != null) {
                habAntigua.setEstado("disponible");
            }
            // Ocupar la nueva habitación
            nuevaHab.setEstado("ocupada");
        }
        // Actualizar la reserva
        reservaAModificar.setIdHabitacion(nuevaHabNum);
        reservaAModificar.setFechaEntrada(nuevaFechaIn);
        reservaAModificar.setFechaSalida(nuevaFechaOut);
        reservaAModificar.setEstado("confirmada");
        return "Reserva #" + idReserva + " modificada exitosamente.";
    }

    // --- LÓGICA EXCLUSIVA DE RECEPCIONISTA ---

    /**
     * Obtiene un reporte del estado de todas las habitaciones. (Control de Habitaciones)
     * @return Un String formateado con la lista de todas las habitaciones.
     */
    public static String getControlHabitaciones() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- Control General de Habitaciones ---\n");
        for (Habitacion hab : DatosQuemados.HABITACIONES.values()) {
            sb.append(String.format("  Habitación %d: %s - $%.2f - Estado: %s\n",
                    hab.getNumero(), hab.getTipo(), hab.getPrecio(), hab.getEstado().toUpperCase()));
        }
        return sb.toString();
    }

    /**
     * Obtiene un reporte de TODAS las reservas activas o pendientes.
     * @return Un String formateado con la lista de todas las reservas.
     */
    public static String getTodasReservas() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- Todas las Reservas (Activas y Pendientes) ---\n");
        int encontradas = 0;
        for (Reserva r : DatosQuemados.RESERVAS) {
            if (r.getEstado().equals("confirmada") || r.getEstado().equals("pendiente")) {
                sb.append(String.format("  ID: %d, Hab: %d, Cliente: %s, Fechas: %s a %s, Estado: %s\n",
                    r.getId(), r.getIdHabitacion(), r.getIdCliente(), r.getFechaEntrada(), r.getFechaSalida(), r.getEstado()));
                encontradas++;
            }
        }
        if (encontradas == 0) {
            sb.append("No hay reservas activas o pendientes en el sistema.\n");
        }
        return sb.toString();
    }

    /**
     * Obtiene un reporte de todas las reservas en estado "pendiente".
     * @return Un String formateado con la lista de reservas pendientes.
     */
    public static String getReservasPendientes() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- Reservas Pendientes ---\n");
        boolean pendientes = false;
        for (Reserva r : DatosQuemados.RESERVAS) {
            if (r.getEstado().equals("pendiente")) {
                sb.append(String.format("  ID Reserva: %d, Habitación: %d, Cliente: %s\n",
                        r.getId(), r.getIdHabitacion(), r.getIdCliente()));
                pendientes = true;
            }
        }
        if (!pendientes) {
            sb.append("No hay reservas pendientes de confirmación.\n");
        }
        return sb.toString();
    }
    
    /**
     * Confirma una reserva pendiente específica por su ID.
     * @param idRes El ID de la reserva a confirmar.
     * @return Un String con el mensaje de éxito o error.
     */
    public static String confirmarReservaPorId(int idRes) {
        Reserva reservaAConfirmar = null;
        for (Reserva r : DatosQuemados.RESERVAS) {
            if (r.getId() == idRes && r.getEstado().equals("pendiente")) {
                reservaAConfirmar = r;
                break;
            }
        }
        if (reservaAConfirmar != null) {
            reservaAConfirmar.setEstado("confirmada");
            DatosQuemados.HABITACIONES.get(reservaAConfirmar.getIdHabitacion()).setEstado("ocupada");
            return String.format("¡Reserva #%d confirmada exitosamente!\n", idRes);
        } else {
            return "Error: No se encontró una reserva pendiente con ese ID.";
        }
    }
    
    /**
     * Genera un reporte de la tasa de ocupación del hotel.
     * @return Un String formateado con el reporte.
     */
    public static String getReporteOcupacion() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- Reporte de Ocupación ---\n");
        int total = DatosQuemados.HABITACIONES.size(), ocupadas = 0, disponibles = 0, mantenimiento = 0;
        for (Habitacion hab : DatosQuemados.HABITACIONES.values()) {
            switch (hab.getEstado()) {
                case "ocupada": ocupadas++; break;
                case "disponible": disponibles++; break;
                case "mantenimiento": mantenimiento++; break;
            }
        }
        sb.append(String.format("Totales: %d\nOcupadas: %d\nDisponibles: %d\nMantenimiento: %d\n", total, ocupadas, disponibles, mantenimiento));
        if (total > 0) {
            double tasaOcupacion = (double) ocupadas / total * 100;
            sb.append(String.format("Tasa de Ocupación: %.2f%%\n", tasaOcupacion));
        }
        return sb.toString();
    }

    /**
     * Agrega una nueva habitación al sistema.
     * @param numero El número de la habitación (ID).
     * @param tipo (Simple, Doble, Suite)
     * @param precio El costo por noche.
     * @param capacidad Cuántas personas caben.
     * @return Un String con el mensaje de éxito o error.
     */
    public static String agregarHabitacion(int numero, String tipo, double precio, int capacidad) {
        if (DatosQuemados.HABITACIONES.containsKey(numero)) {
            return "Error: La habitación " + numero + " ya existe.";
        }
        Habitacion nuevaHab = new Habitacion(numero, tipo, precio, "disponible", capacidad);
        DatosQuemados.HABITACIONES.put(numero, nuevaHab);
        return "Habitación " + numero + " agregada exitosamente.";
    }

    /**
     * Modifica los detalles de una habitación existente.
     * @param numero El número de la habitación a modificar.
     * @param nuevoTipo El nuevo tipo.
     * @param nuevoPrecio El nuevo precio.
     * @param nuevoEstado El nuevo estado.
     * @return Un String con el mensaje de éxito o error.
     */
    public static String modificarHabitacion(int numero, String nuevoTipo, double nuevoPrecio, String nuevoEstado) {
        if (!DatosQuemados.HABITACIONES.containsKey(numero)) {
            return "Error: La habitación " + numero + " no existe.";
        }
        // Validación simple del estado
        if (!nuevoEstado.equals("disponible") && !nuevoEstado.equals("ocupada") && !nuevoEstado.equals("mantenimiento")) {
            return "Error: Estado inválido. Use 'disponible', 'ocupada' o 'mantenimiento'.";
        }
        Habitacion hab = DatosQuemados.HABITACIONES.get(numero);
        hab.setTipo(nuevoTipo);
        hab.setPrecio(nuevoPrecio);
        hab.setEstado(nuevoEstado);
        return "Habitación " + numero + " modificada exitosamente.";
    }

    
    // --- LÓGICA DE GESTIÓN DE SERVICIOS Y PROMOCIONES ---

    /**
     * Obtiene una lista de todos los servicios disponibles.
     * @return Un String formateado con la lista de servicios.
     */
    public static String getServicios() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- Lista de Servicios ---\n");
        if (DatosQuemados.SERVICIOS.isEmpty()) {
            sb.append("No hay servicios registrados.");
        }
        for (Servicio s : DatosQuemados.SERVICIOS) {
            sb.append(String.format("  ID: %d, Nombre: %s, Precio: $%.2f\n",
                s.getId(), s.getNombre(), s.getPrecio()));
        }
        return sb.toString();
    }

    /**
     * Agrega un nuevo servicio (y configura su precio).
     * @param nombre El nombre del servicio.
     * @param precio El precio del servicio.
     * @return Un String con el mensaje de éxito.
     */
    public static String agregarServicio(String nombre, double precio) {
        int nuevoId = DatosQuemados.proximoIdServicio++;
        Servicio nuevoServicio = new Servicio(nuevoId, nombre, precio);
        DatosQuemados.SERVICIOS.add(nuevoServicio);
        return "Servicio '" + nombre + "' agregado con ID " + nuevoId + ".";
    }

    /**
     * Modifica un servicio existente (nombre y precio).
     * @param id El ID del servicio a modificar.
     * @param nuevoNombre El nuevo nombre.
     * @param nuevoPrecio El nuevo precio.
     * @return Un String con el mensaje de éxito o error.
     */
    public static String modificarServicio(int id, String nuevoNombre, double nuevoPrecio) {
        for (Servicio s : DatosQuemados.SERVICIOS) {
            if (s.getId() == id) {
                s.setNombre(nuevoNombre);
                s.setPrecio(nuevoPrecio);
                return "Servicio ID " + id + " modificado exitosamente.";
            }
        }
        return "Error: Servicio con ID " + id + " no encontrado.";
    }

    /**
     * Elimina un servicio usando su ID.
     * @param id El ID del servicio a eliminar.
     * @return Un String con el mensaje de éxito o error.
     */
    public static String eliminarServicio(int id) {
        Iterator<Servicio> iter = DatosQuemados.SERVICIOS.iterator();
        while (iter.hasNext()) {
            Servicio s = iter.next();
            if (s.getId() == id) {
                iter.remove();
                return "Servicio ID " + id + " eliminado exitosamente.";
            }
        }
        return "Error: Servicio con ID " + id + " no encontrado.";
    }

    /**
     * Obtiene una lista de todas las promociones.
     * @return Un String formateado con la lista de promociones.
     */
    public static String getPromociones() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- Lista de Promociones ---\n");
        if (DatosQuemados.PROMOCIONES.isEmpty()) {
            sb.append("No hay promociones registradas.");
        }
        for (Promocion p : DatosQuemados.PROMOCIONES) {
            sb.append(String.format("  ID: %d, Nombre: %s, Descuento: %.1f%%\n",
                p.getId(), p.getNombre(), p.getPorcentajeDescuento()));
        }
        return sb.toString();
    }

    /**
     * Agrega una nueva promoción.
     * @param nombre El nombre de la promoción.
     * @param descuento El porcentaje de descuento.
     * @return Un String con el mensaje de éxito.
     */
    public static String agregarPromocion(String nombre, double descuento) {
        if (descuento <= 0 || descuento > 100) {
            return "Error: El descuento debe ser un porcentaje entre 1 y 100.";
        }
        int nuevoId = DatosQuemados.proximoIdPromocion++;
        Promocion nuevaPromo = new Promocion(nuevoId, nombre, descuento);
        DatosQuemados.PROMOCIONES.add(nuevaPromo);
        return "Promoción '" + nombre + "' agregada con ID " + nuevoId + ".";
    }

    /**
     * Elimina una promoción usando su ID.
     * @param id El ID de la promoción a eliminar.
     * @return Un String con el mensaje de éxito o error.
     */
    public static String eliminarPromocion(int id) {
        Iterator<Promocion> iter = DatosQuemados.PROMOCIONES.iterator();
        while (iter.hasNext()) {
            Promocion p = iter.next();
            if (p.getId() == id) {
                iter.remove();
                return "Promoción ID " + id + " eliminada exitosamente.";
            }
        }
        return "Error: Promoción con ID " + id + " no encontrada.";
    }

    
    // --- NUEVO: Ver Factura (Recepcionista) ---
    /**
     * Reconstruye y devuelve la factura de una reserva específica.
     * @param idReserva El ID de la reserva a consultar.
     * @return Un String formateado de la factura, o un error.
     */
    public static String getFacturaPorId(int idReserva) {
        Reserva r = null;
        for (Reserva res : DatosQuemados.RESERVAS) {
            if (res.getId() == idReserva) {
                r = res;
                break;
            }
        }

        if (r == null) {
            return "Error: No se encontró la reserva con ID " + idReserva;
        }

        // --- Reconstruimos la factura ---
        DecimalFormat df = new DecimalFormat("#.00");
        StringBuilder factura = new StringBuilder();
        factura.append("--- FACTURA DE CONSUMIDOR FINAL (COPIA) ---\n");
        factura.append("Reserva ID: " + r.getId() + "\n");
        factura.append("Cliente ID: " + r.getIdCliente() + "\n");
        factura.append("Fechas: " + r.getFechaEntrada() + " al " + r.getFechaSalida() + "\n");
        factura.append("Estado: " + r.getEstado().toUpperCase() + "\n");
        factura.append("-----------------------------------\n");
        factura.append("DETALLE:\n");

        // Obtenemos la habitación (para el precio base)
        Habitacion hab = DatosQuemados.HABITACIONES.get(r.getIdHabitacion());
        if (hab == null) {
            return "Error: Datos de la habitación corruptos.";
        }

        // Calculamos el número de noches
        long noches = 1; // Por defecto
        try {
            LocalDate dateIn = LocalDate.parse(r.getFechaEntrada(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate dateOut = LocalDate.parse(r.getFechaSalida(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            noches = ChronoUnit.DAYS.between(dateIn, dateOut);
        } catch (Exception e) {
            // Ignorar, usar 1 noche si el formato falla
        }
        
        double precioHabitacion = hab.getPrecio() * noches;
        factura.append(String.format("- Habitación %d (%s) x %d noches: $ %s\n", 
            hab.getNumero(), hab.getTipo(), noches, df.format(precioHabitacion)));

        // Añadimos los servicios
        double precioServicios = 0;
        if (r.getServiciosContratados() != null && !r.getServiciosContratados().isEmpty()) {
            factura.append("  Servicios:\n");
            for (Servicio s : r.getServiciosContratados()) {
                factura.append(String.format("    - %s ($ %.2f)\n", s.getNombre(), s.getPrecio()));
                precioServicios += s.getPrecio();
            }
        }
        
        // Calculamos el subtotal
        double subtotal = precioHabitacion + precioServicios;
        factura.append(String.format("SUBTOTAL: $ %s\n", df.format(subtotal)));

        // Aplicamos la promoción (si existe)
        if (r.getPromocionAplicada() != null) {
            Promocion p = r.getPromocionAplicada();
            double descuento = subtotal * (p.getPorcentajeDescuento() / 100.0);
            factura.append(String.format("Promoción (%s -%.1f%%): - $ %s\n",
                p.getNombre(), p.getPorcentajeDescuento(), df.format(descuento)));
        }

        // Total Final
        factura.append("-----------------------------------\n");
        factura.append(String.format("TOTAL PAGADO: $ %s\n", df.format(r.getPrecioFinalCalculado())));
        factura.append("-----------------------------------\n");

        return factura.toString();
    }
}