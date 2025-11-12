package vista;

// Imports de nuestros componentes personalizados
import vista.componentes.GradientPanel;
import vista.componentes.RoundedButton;

// Imports de Java
import servicio.HotelServicio;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * NUEVA CLASE: Un JDialog personalizado para el registro de clientes.
 * Reemplaza el JOptionPane feo.
 */
public class RegistroDialog extends JDialog {

    private JTextField emailField;
    private JPasswordField passField;
    private JPasswordField confirmPassField;
    private JLabel statusLabel;

    public RegistroDialog(Frame parent) {
        // 1. Configuración del Diálogo
        super(parent, "Registro de Nuevo Cliente", true); // 'true' lo hace modal
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(600, 350);
        setLocationRelativeTo(parent); // Se centra sobre la ventana principal
        setResizable(false);

        // 2. Establecer el fondo con degradado
        GradientPanel panel = new GradientPanel(new Color(230, 245, 255), Color.WHITE);
        panel.setLayout(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(panel); // Se usa como el panel principal del diálogo

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Fila 0: Título ---
        JLabel lblTitulo = new JLabel("Crear Nueva Cuenta");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblTitulo.setForeground(new Color(0, 51, 102));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(lblTitulo, gbc);

        // --- Fila 1: Email ---
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        emailField = new JTextField(20);
        panel.add(emailField, gbc);

        // --- Fila 2: Contraseña ---
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Contraseña:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        passField = new JPasswordField(20);
        panel.add(passField, gbc);

        // --- Fila 3: Confirmar Contraseña ---
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Confirmar Contraseña:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        confirmPassField = new JPasswordField(20);
        panel.add(confirmPassField, gbc);

        // --- Fila 4: Botón de Registro ---
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        RoundedButton registerButton = new RoundedButton("Confirmar Registro");
        registerButton.setPreferredSize(new Dimension(180, 40));
        panel.add(registerButton, gbc);

        // --- Fila 5: Etiqueta de Estado ---
        gbc.gridy = 5;
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setForeground(Color.RED);
        panel.add(statusLabel, gbc);

        // 3. Acción del Botón
        registerButton.addActionListener(e -> handleConfirmRegistro());
    }

    /**
     * Lógica que se ejecuta al presionar "Confirmar Registro".
     */
    private void handleConfirmRegistro() {
        String email = emailField.getText();
        String pass = new String(passField.getPassword());
        String confirmPass = new String(confirmPassField.getPassword());

        // Check 1: Campos vacíos
        if (email.isEmpty() || pass.isEmpty()) {
            statusLabel.setText("Email y contraseña no pueden estar vacíos.");
            return;
        }
        
        // Check 2: Contraseñas coinciden
        if (!pass.equals(confirmPass)) {
            statusLabel.setText("Las contraseñas no coinciden.");
            return;
        }

        // Check 3: Llamar al servicio
        // Usamos el email como idUsuario por simplicidad
        String resultado = HotelServicio.registrarUsuario(email, pass, email); 

        if (resultado.contains("exitosamente")) {
            // Si el registro es exitoso, mostrar pop-up y cerrar.
            JOptionPane.showMessageDialog(this, resultado, "Registro Exitoso", JOptionPane.INFORMATION_MESSAGE);
            dispose(); // Cierra la ventana de registro
        } else {
            // Si falla (ej. email ya existe), mostrar error en la etiqueta
            statusLabel.setText(resultado); 
        }
    }
}