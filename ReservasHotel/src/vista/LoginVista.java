package vista;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import modelo.Usuario;
import servicio.HotelServicio;
import vista.componentes.GradientPanel;
import vista.componentes.RoundedButton;

/**
 * MODIFICADO: Ahora usa GradientPanel como fondo y RoundedButton.
 * Llama al constructor super() para evitar el error.
 * Llama a RegistroDialog en lugar de JOptionPane.
 */
public class LoginVista extends GradientPanel { // <-- EXTENDS MODIFICADO

    private MainFrame mainFrame; // Necesario para pasarlo como "padre" al diálogo
    private JTextField emailField;
    private JPasswordField passField;
    private JLabel statusLabel;

    public LoginVista(MainFrame mainFrame) {
        
        // --- ¡CORRECCIÓN! ---
        // 1. Llama al constructor del GradientPanel PRIMERO.
        // Un degradado suave de azul muy claro (casi blanco) a blanco.
        super(new Color(230, 245, 255), Color.WHITE); 
        // --- FIN DE LA CORRECCIÓN ---
        
        this.mainFrame = mainFrame;
        
        setLayout(new GridBagLayout());
        // Añadimos márgenes (aire) alrededor de todo el panel
        setBorder(new EmptyBorder(40, 60, 40, 60)); 
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Espacio entre componentes
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- 1. Encabezado con Logo (Nombre del Hotel) ---
        JLabel lblLogo = new JLabel("Hotel Aurora");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 50));
        lblLogo.setForeground(new Color(0, 51, 102)); // Azul Oscuro
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Ocupa 2 columnas
        gbc.anchor = GridBagConstraints.CENTER;
        add(lblLogo, gbc);

        // --- 2. Campo de Email con Ícono ---
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE; // Para que el ícono no se estire
        
        // Intenta cargar el ícono, si falla, no muestra nada.
        try {
            // Asume que tienes una carpeta 'icons' en la raíz (ej. ReservasHotel/icons/user.png)
            add(new JLabel(new ImageIcon("icons/user.png")), gbc);
        } catch (Exception e) {
            System.err.println("No se encontró el ícono user.png");
            add(new JLabel(""), gbc); // Espacio reservado
        }

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        emailField = new JTextField(25);
        emailField.setText("cliente@hotel.com");
        configurarTextField(emailField); // Método helper para el estilo
        add(emailField, gbc);

        // --- 3. Campo de Contraseña con Ícono ---
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        
        try {
            // Asume que tienes una carpeta 'icons' en la raíz (ej. ReservasHotel/icons/key.png)
            add(new JLabel(new ImageIcon("icons/key.png")), gbc);
        } catch (Exception e) {
            System.err.println("No se encontró el ícono key.png");
            add(new JLabel(""), gbc); // Espacio reservado
        }

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        passField = new JPasswordField(25);
        passField.setText("123");
        configurarTextField(passField); // Método helper para el estilo
        add(passField, gbc);

        // --- 4. Panel de Botones ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false); // Hacer el panel transparente

        // Usamos nuestro botón redondeado
        JButton loginButton = new RoundedButton("Iniciar Sesión");
        loginButton.setPreferredSize(new Dimension(150, 40));
        
        // Botón de Registrar (estilo más sutil)
        JButton registerButton = new RoundedButton("Registrar");
        registerButton.setPreferredSize(new Dimension(150, 40));
        // Casteamos para acceder a los métodos de RoundedButton
        ((RoundedButton) registerButton).setNormalColor(new Color(100, 100, 100)); // Gris
        ((RoundedButton) registerButton).setHoverColor(new Color(130, 130, 130)); // Gris claro

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);

        // --- 5. Etiqueta de Estado ---
        gbc.gridy = 4;
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setForeground(Color.RED);
        add(statusLabel, gbc);

        // --- Acciones ---
        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> handleRegister()); // Esta línea llama al nuevo método
    }

    /**
     * Método helper para dar estilo a los campos de texto
     */
    private void configurarTextField(JTextField field) {
        field.setOpaque(false); // Fondo transparente para ver el degradado
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        // Un borde sutil con padding interno
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Color.GRAY, 1),
            new EmptyBorder(5, 5, 5, 5) // Padding interno (arriba, izq, abajo, der)
        ));
    }

    /**
     * Lógica para el botón de Iniciar Sesión.
     */
    private void handleLogin() {
        String email = emailField.getText();
        String password = new String(passField.getPassword());
        
        Usuario user = HotelServicio.login(email, password);

        if (user != null) {
            mainFrame.setUsuarioActual(user);
            if (user.getRol().equals("cliente")) {
                mainFrame.mostrarPanel("CLIENTE");
            } else if (user.getRol().equals("recepcionista")) {
                mainFrame.mostrarPanel("RECEPCIONISTA");
            }
            statusLabel.setText(" ");
            passField.setText("");
        } else {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Email o contraseña incorrectos.");
        }
    }

    /**
     * MODIFICADO: Lógica para el botón de Registrar.
     * Ahora abre nuestro diálogo personalizado.
     */
    private void handleRegister() {
        // Crea y muestra el nuevo diálogo de registro.
        // Pasa 'mainFrame' como el padre para que el diálogo se centre sobre él.
        RegistroDialog registroDialog = new RegistroDialog(mainFrame);
        registroDialog.setVisible(true);
        
        // La etiqueta de estado 'statusLabel' ya no se usa aquí,
        // porque el diálogo de registro maneja sus propios mensajes de error.
        statusLabel.setText(" "); // Limpiamos la etiqueta de login
    }
}