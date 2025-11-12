package vista;

// Imports de nuestro proyecto
import datos.DatosQuemados;
import modelo.Usuario;

// Imports de Java Swing/AWT
import javax.swing.*;
import java.awt.*;

/**
 * Clase Principal (Main) de la Aplicación.
 * Esta es la ventana que contiene todo. Utiliza un CardLayout
 * para cambiar entre los paneles de Login, Cliente y Recepcionista.
 */
public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel panelContenedor;
    private Usuario usuarioActual;

    // Paneles que se guardarán
    private LoginVista panelLogin;
    private ClienteVista panelCliente;
    private RecepcionistaVista panelRecepcionista;

    public MainFrame() {
        setTitle("Sistema de Reservas del Hotel");
        
        // --- MODIFICADO ---
        // Aumentamos el tamaño para que quepan todas las pestañas y formularios
        setSize(950, 700); 
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrar en pantalla

        // Inicializar el CardLayout (permite cambiar de vistas)
        cardLayout = new CardLayout();
        panelContenedor = new JPanel(cardLayout);
        panelContenedor.setOpaque(false); // Por si acaso

        // --- Crear los 3 paneles principales ---
        // Cada uno de estos paneles extiende GradientPanel
        panelLogin = new LoginVista(this);
        panelCliente = new ClienteVista(this);
        panelRecepcionista = new RecepcionistaVista(this);

        // --- Añadir paneles al contenedor "apilados" ---
        panelContenedor.add(panelLogin, "LOGIN");
        panelContenedor.add(panelCliente, "CLIENTE");
        panelContenedor.add(panelRecepcionista, "RECEPCIONISTA");

        // Añadir el contenedor al frame (ventana)
        add(panelContenedor);

        // Mostrar el panel de Login al inicio
        cardLayout.show(panelContenedor, "LOGIN");
    }

    // --- Métodos de Control ---

    /**
     * Cambia la vista visible en el CardLayout.
     * @param nombre "LOGIN", "CLIENTE", o "RECEPCIONISTA"
     */
    public void mostrarPanel(String nombre) {
        cardLayout.show(panelContenedor, nombre);
    }

    /**
     * Cierra la sesión del usuario actual y vuelve al panel de Login.
     */
    public void logout() {
        usuarioActual = null;
        cardLayout.show(panelContenedor, "LOGIN");
    }

    // Getters y Setters para el usuario que está logueado
    public void setUsuarioActual(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    // --- PUNTO DE ENTRADA DEL PROGRAMA ---
    
    public static void main(String[] args) {
        
        // NO usamos FlatLaf, así que no hay código de LookAndFeel aquí.

        // 1. Cargar todos los datos quemados al iniciar
        DatosQuemados.inicializar();

        // 2. Iniciar la interfaz gráfica de forma segura (EDT)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }
}