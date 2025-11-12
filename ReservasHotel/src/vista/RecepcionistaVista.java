package vista;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import servicio.HotelServicio;
import vista.componentes.GradientPanel;
import vista.componentes.RoundedButton;

/**
 * Clase de la Vista (GUI) para el panel del Recepcionista.
 * (MODIFICADA: Totalmente rediseñada con GradientPanel, RoundedButton
 * y con el botón de logout corregido).
 */
public class RecepcionistaVista extends GradientPanel { // <-- MODIFICADO

    private MainFrame mainFrame; // Referencia a la ventana principal
    
    // Áreas de texto para las diferentes pestañas
    private JTextArea areaPendientes, areaReporte, areaGestion, areaListaHabitaciones;
    private JTextArea areaServicios;
    private JTextArea areaPromociones;
    // 'areaControl' ha sido eliminada

    public RecepcionistaVista(MainFrame mainFrame) {
        
        // --- ¡CORRECCIÓN! ---
        // 1. Llama al constructor del GradientPanel PRIMERO.
        super(new Color(230, 245, 255), Color.WHITE); // Degradado azul claro a blanco
        
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10)); // Márgenes

        // --- 2. JTabbedPane (Pestañas) ---
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setOpaque(false); // Transparente para ver el fondo
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabbedPane.setForeground(new Color(0, 51, 102)); // Azul oscuro

        // --- Pestaña "Control Habitaciones" ELIMINADA ---
        
        // Pestaña 1 (antes 2): Confirmar Reservas
        tabbedPane.addTab("Confirmar Reservas", crearPanelConfirmar());
        
        // Pestaña 2 (antes 3): Gestionar Reservas (Mod/Cancel/Ver Factura)
        tabbedPane.addTab("Gestionar Reservas", crearPanelGestionarReservas());
        
        // Pestaña 3 (antes 4): Gestionar Habitaciones (Add/Mod)
        tabbedPane.addTab("Gestionar Habitaciones", crearPanelGestionarHabitaciones());
        
        // Pestaña 4 (antes 5): Reporte Ocupación
        tabbedPane.addTab("Reporte Ocupación", crearPanelReporte());

        // Pestaña 5 (antes 6): Gestionar Servicios
        tabbedPane.addTab("Gestionar Servicios", crearPanelGestionarServicios());
        
        // Pestaña 6 (antes 7): Gestionar Promociones
        tabbedPane.addTab("Gestionar Promociones", crearPanelGestionarPromociones());
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // --- 3. Botón de Cerrar Sesión ---
        JButton logoutButton = new RoundedButton("Cerrar Sesión");
        ((RoundedButton) logoutButton).setNormalColor(new Color(200, 50, 50)); // Rojo
        ((RoundedButton) logoutButton).setHoverColor(new Color(230, 80, 80)); // Rojo claro
        logoutButton.setPreferredSize(new Dimension(150, 40));
        
        // --- ¡CORRECCIÓN! ---
        // Conecta el botón a la función logout() del MainFrame
        logoutButton.addActionListener(e -> mainFrame.logout());
        // --- FIN DE LA CORRECCIÓN ---

        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        southPanel.setOpaque(false); // Fondo transparente
        southPanel.add(logoutButton);
        add(southPanel, BorderLayout.SOUTH);
    }

    /**
     * Pestaña 1: Muestra las reservas pendientes y permite confirmarlas.
     */
    private JPanel crearPanelConfirmar() {
        JPanel panel = new GradientPanel(Color.WHITE, new Color(245, 250, 255));
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        areaPendientes = new JTextArea("Presione 'Actualizar' para ver reservas pendientes...");
        configurarTextArea(areaPendientes); // Aplica estilo
        
        JScrollPane scroll = new JScrollPane(areaPendientes);
        configurarScrollPane(scroll); // Aplica estilo
        panel.add(scroll, BorderLayout.CENTER);
        
        // Panel inferior para la acción de confirmar
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        bottomPanel.setOpaque(false);
        bottomPanel.add(new JLabel("ID Reserva a Confirmar:"));
        JTextField idField = new JTextField(5);
        bottomPanel.add(idField);
        
        JButton confirmButton = new RoundedButton("Confirmar");
        confirmButton.setPreferredSize(new Dimension(110, 35));
        bottomPanel.add(confirmButton);
        
        JButton refreshButton = new RoundedButton("Actualizar Lista");
        ((RoundedButton) refreshButton).setNormalColor(new Color(100, 100, 100)); // Gris
        ((RoundedButton) refreshButton).setHoverColor(new Color(130, 130, 130));
        refreshButton.setPreferredSize(new Dimension(130, 35));
        bottomPanel.add(refreshButton);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);

        // Acciones
        refreshButton.addActionListener(e -> areaPendientes.setText(HotelServicio.getReservasPendientes()));
        
        confirmButton.addActionListener(e -> {
            try {
                int idRes = Integer.parseInt(idField.getText());
                String res = HotelServicio.confirmarReservaPorId(idRes);
                JOptionPane.showMessageDialog(this, res); // Muestra resultado en pop-up
                areaPendientes.setText(HotelServicio.getReservasPendientes()); // Actualiza lista
                idField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Error: El ID debe ser un número.");
            }
        });
        return panel;
    }

    /**
     * Pestaña 2: Permite Modificar, Cancelar o VER FACTURA de CUALQUIER reserva.
     */
    private JPanel crearPanelGestionarReservas() {
        JPanel panel = new GradientPanel(Color.WHITE, new Color(245, 250, 255));
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        areaGestion = new JTextArea("Presione 'Actualizar' para ver TODAS las reservas (activas y pendientes)...");
        configurarTextArea(areaGestion);
        
        JScrollPane scroll = new JScrollPane(areaGestion);
        configurarScrollPane(scroll);
        panel.add(scroll, BorderLayout.CENTER);

        // Panel de acciones (Modificar/Cancelar/Ver Factura)
        JPanel actionsPanel = new JPanel();
        actionsPanel.setLayout(new BoxLayout(actionsPanel, BoxLayout.Y_AXIS));
        actionsPanel.setOpaque(false);
        
        // Formulario de Cancelar
        JPanel cancelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cancelPanel.setOpaque(false);
        cancelPanel.setBorder(BorderFactory.createTitledBorder("Cancelar Reserva"));
        JTextField cancelIdField = new JTextField(5);
        JButton cancelButton = new RoundedButton("Cancelar");
        ((RoundedButton) cancelButton).setNormalColor(new Color(200, 50, 50));
        ((RoundedButton) cancelButton).setHoverColor(new Color(230, 80, 80));
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelPanel.add(new JLabel("ID Reserva:"));
        cancelPanel.add(cancelIdField);
        cancelPanel.add(cancelButton);

        // Formulario de Modificar
        JPanel modifyPanel = new JPanel(new GridBagLayout());
        modifyPanel.setOpaque(false);
        modifyPanel.setBorder(BorderFactory.createTitledBorder("Modificar Reserva"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.gridx=0; gbc.gridy=0; modifyPanel.add(new JLabel("ID Reserva:"), gbc);
        JTextField modIdField = new JTextField(5);
        gbc.gridx=1; gbc.gridy=0; modifyPanel.add(modIdField, gbc);
        gbc.gridx=0; gbc.gridy=1; modifyPanel.add(new JLabel("Nueva Hab:"), gbc);
        JTextField modHabField = new JTextField(5);
        gbc.gridx=1; gbc.gridy=1; modifyPanel.add(modHabField, gbc);
        gbc.gridx=0; gbc.gridy=2; modifyPanel.add(new JLabel("Nueva F. Entrada:"), gbc);
        JTextField modFechaInField = new JTextField(10);
        gbc.gridx=1; gbc.gridy=2; modifyPanel.add(modFechaInField, gbc);
        gbc.gridx=0; gbc.gridy=3; modifyPanel.add(new JLabel("Nueva F. Salida:"), gbc);
        JTextField modFechaOutField = new JTextField(10);
        gbc.gridx=1; gbc.gridy=3; modifyPanel.add(modFechaOutField, gbc);
        
        JButton modifyButton = new RoundedButton("Modificar");
        modifyButton.setPreferredSize(new Dimension(100, 35));
        gbc.gridx=0; gbc.gridy=4; gbc.gridwidth=2; gbc.anchor = GridBagConstraints.CENTER;
        modifyPanel.add(modifyButton, gbc);
        
        // --- Panel para Ver Factura ---
        JPanel viewFacturaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        viewFacturaPanel.setOpaque(false);
        viewFacturaPanel.setBorder(BorderFactory.createTitledBorder("Ver Factura"));
        JTextField facturaIdField = new JTextField(5);
        JButton facturaButton = new RoundedButton("Ver");
        ((RoundedButton) facturaButton).setNormalColor(new Color(0, 153, 153)); // Color verde azulado
        ((RoundedButton) facturaButton).setHoverColor(new Color(0, 183, 183));
        facturaButton.setPreferredSize(new Dimension(80, 35));
        viewFacturaPanel.add(new JLabel("ID Reserva:"));
        viewFacturaPanel.add(facturaIdField);
        viewFacturaPanel.add(facturaButton);

        actionsPanel.add(cancelPanel);
        actionsPanel.add(modifyPanel);
        actionsPanel.add(viewFacturaPanel); // <-- Añadido el nuevo panel
        panel.add(actionsPanel, BorderLayout.EAST);

        JButton refreshButton = new RoundedButton("Actualizar Lista (Todas las Reservas)");
        refreshButton.setPreferredSize(new Dimension(250, 35));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // --- Acciones ---
        refreshButton.addActionListener(e -> {
            areaGestion.setText(HotelServicio.getTodasReservas());
        });
        
        cancelButton.addActionListener(e -> {
            try {
                int id = Integer.parseInt(cancelIdField.getText());
                String res = HotelServicio.cancelarReserva(id, mainFrame.getUsuarioActual());
                JOptionPane.showMessageDialog(this, res);
                areaGestion.setText(HotelServicio.getTodasReservas()); // Actualiza lista
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID debe ser un número.");
            }
        });

        modifyButton.addActionListener(e -> {
            try {
                int id = Integer.parseInt(modIdField.getText());
                int hab = Integer.parseInt(modHabField.getText());
                String fIn = modFechaInField.getText();
                String fOut = modFechaOutField.getText();
                String res = HotelServicio.modificarReserva(id, hab, fIn, fOut, mainFrame.getUsuarioActual());
                JOptionPane.showMessageDialog(this, res);
                areaGestion.setText(HotelServicio.getTodasReservas()); // Actualiza lista
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID y Habitación deben ser números.");
            }
        });

        // --- Nueva Acción para Factura ---
        facturaButton.addActionListener(e -> {
            try {
                int id = Integer.parseInt(facturaIdField.getText());
                String factura = HotelServicio.getFacturaPorId(id);
                
                JTextArea textArea = new JTextArea(factura);
                textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                textArea.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(textArea);
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);
                scrollPane.setPreferredSize(new Dimension(400, 450));
                
                JOptionPane.showMessageDialog(this, scrollPane, "Factura Reserva #" + id, JOptionPane.INFORMATION_MESSAGE);
                facturaIdField.setText("");
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID debe ser un número.");
            }
        });
        
        return panel;
    }


    /**
     * Pestaña 3: Permite Agregar y Modificar habitaciones.
     */
    private JPanel crearPanelGestionarHabitaciones() {
        JPanel panel = new GradientPanel(Color.WHITE, new Color(245, 250, 255));
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.X_AXIS));
        formPanel.setOpaque(false);

        // Formulario de Agregar Habitación
        JPanel addPanel = new JPanel(new GridBagLayout());
        addPanel.setOpaque(false);
        addPanel.setBorder(BorderFactory.createTitledBorder("Agregar Nueva Habitación"));
        GridBagConstraints gbcAdd = new GridBagConstraints();
        gbcAdd.insets = new Insets(2, 5, 2, 5);
        
        gbcAdd.gridx=0; gbcAdd.gridy=0; addPanel.add(new JLabel("Número:"), gbcAdd);
        JTextField addNumField = new JTextField(5);
        gbcAdd.gridx=1; gbcAdd.gridy=0; addPanel.add(addNumField, gbcAdd);

        gbcAdd.gridx=0; gbcAdd.gridy=1; addPanel.add(new JLabel("Tipo (Simple/Doble):"), gbcAdd);
        JTextField addTipoField = new JTextField(10);
        gbcAdd.gridx=1; gbcAdd.gridy=1; addPanel.add(addTipoField, gbcAdd);
        
        gbcAdd.gridx=0; gbcAdd.gridy=2; addPanel.add(new JLabel("Precio:"), gbcAdd);
        JTextField addPrecioField = new JTextField(5);
        gbcAdd.gridx=1; gbcAdd.gridy=2; addPanel.add(addPrecioField, gbcAdd);

        gbcAdd.gridx=0; gbcAdd.gridy=3; addPanel.add(new JLabel("Capacidad:"), gbcAdd);
        JTextField addCapField = new JTextField(5);
        gbcAdd.gridx=1; gbcAdd.gridy=3; addPanel.add(addCapField, gbcAdd);
        
        JButton addButton = new RoundedButton("Agregar Habitación");
        addButton.setPreferredSize(new Dimension(160, 35));
        gbcAdd.gridx=0; gbcAdd.gridy=4; gbcAdd.gridwidth=2; gbcAdd.anchor=GridBagConstraints.CENTER;
        addPanel.add(addButton, gbcAdd);
        
        // Formulario de Modificar Habitación
        JPanel modPanel = new JPanel(new GridBagLayout());
        modPanel.setOpaque(false);
        modPanel.setBorder(BorderFactory.createTitledBorder("Modificar Habitación"));
        GridBagConstraints gbcMod = new GridBagConstraints();
        gbcMod.insets = new Insets(2, 5, 2, 5);

        gbcMod.gridx=0; gbcMod.gridy=0; modPanel.add(new JLabel("Número:"), gbcMod);
        JTextField modNumField = new JTextField(5);
        gbcMod.gridx=1; gbcMod.gridy=0; modPanel.add(modNumField, gbcMod);

        gbcMod.gridx=0; gbcMod.gridy=1; modPanel.add(new JLabel("Nuevo Tipo:"), gbcMod);
        JTextField modTipoField = new JTextField(10);
        gbcMod.gridx=1; gbcMod.gridy=1; modPanel.add(modTipoField, gbcMod);
        
        gbcMod.gridx=0; gbcMod.gridy=2; modPanel.add(new JLabel("Nuevo Precio:"), gbcMod);
        JTextField modPrecioField = new JTextField(5);
        gbcMod.gridx=1; gbcMod.gridy=2; modPanel.add(modPrecioField, gbcMod);

        gbcMod.gridx=0; gbcMod.gridy=3; modPanel.add(new JLabel("Nuevo Estado:"), gbcMod);
        JTextField modEstadoField = new JTextField(10);
        modEstadoField.setText("disponible");
        gbcMod.gridx=1; gbcMod.gridy=3; modPanel.add(modEstadoField, gbcMod);
        
        JButton modButton = new RoundedButton("Modificar Habitación");
        modButton.setPreferredSize(new Dimension(160, 35));
        gbcMod.gridx=0; gbcMod.gridy=4; gbcMod.gridwidth=2; gbcMod.anchor=GridBagConstraints.CENTER;
        modPanel.add(modButton, gbcMod);

        formPanel.add(addPanel);
        formPanel.add(Box.createHorizontalStrut(10));
        formPanel.add(modPanel);
        panel.add(formPanel, BorderLayout.NORTH);

        // Lista de habitaciones (que sirve como 'Control de Habitaciones')
        areaListaHabitaciones = new JTextArea("Presione 'Actualizar' para ver la lista de habitaciones...");
        configurarTextArea(areaListaHabitaciones);
        JScrollPane scroll = new JScrollPane(areaListaHabitaciones);
        configurarScrollPane(scroll);
        panel.add(scroll, BorderLayout.CENTER);
        
        JButton refreshButton = new RoundedButton("Actualizar Lista de Habitaciones");
        refreshButton.setPreferredSize(new Dimension(240, 35));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Acciones
        refreshButton.addActionListener(e -> {
            areaListaHabitaciones.setText(HotelServicio.getControlHabitaciones());
        });

        addButton.addActionListener(e -> {
            try {
                int num = Integer.parseInt(addNumField.getText());
                String tipo = addTipoField.getText();
                double precio = Double.parseDouble(addPrecioField.getText());
                int cap = Integer.parseInt(addCapField.getText());
                String res = HotelServicio.agregarHabitacion(num, tipo, precio, cap);
                JOptionPane.showMessageDialog(this, res);
                areaListaHabitaciones.setText(HotelServicio.getControlHabitaciones());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error en los datos. Verifique que los números sean correctos.");
            }
        });
        
        modButton.addActionListener(e -> {
            try {
                int num = Integer.parseInt(modNumField.getText());
                String tipo = modTipoField.getText();
                double precio = Double.parseDouble(modPrecioField.getText());
                String estado = modEstadoField.getText();
                String res = HotelServicio.modificarHabitacion(num, tipo, precio, estado);
                JOptionPane.showMessageDialog(this, res);
                areaListaHabitaciones.setText(HotelServicio.getControlHabitaciones());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error en los datos. Verifique que los números sean correctos.");
            }
        });
        
        return panel;
    }

    /**
     * Pestaña 4: Muestra el reporte de ocupación.
     */
    private JPanel crearPanelReporte() {
        JPanel panel = new GradientPanel(Color.WHITE, new Color(245, 250, 255));
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        areaReporte = new JTextArea("Presione 'Generar' para ver el reporte...");
        configurarTextArea(areaReporte);
        
        JScrollPane scroll = new JScrollPane(areaReporte);
        configurarScrollPane(scroll);
        panel.add(scroll, BorderLayout.CENTER);
        
        JButton refreshButton = new RoundedButton("Generar Reporte");
        refreshButton.setPreferredSize(new Dimension(150, 35));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        refreshButton.addActionListener(e -> {
            areaReporte.setText(HotelServicio.getReporteOcupacion());
        });
        return panel;
    }


    /**
     * Pestaña 5: Permite Agregar, Modificar y Eliminar Servicios.
     */
    private JPanel crearPanelGestionarServicios() {
        JPanel panel = new GradientPanel(Color.WHITE, new Color(245, 250, 255));
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Lista de servicios
        areaServicios = new JTextArea("Presione 'Actualizar' para ver la lista de servicios...");
        configurarTextArea(areaServicios);
        JScrollPane scroll = new JScrollPane(areaServicios);
        configurarScrollPane(scroll);
        panel.add(scroll, BorderLayout.CENTER);

        // Panel de formularios
        JPanel formsPanel = new JPanel();
        formsPanel.setLayout(new BoxLayout(formsPanel, BoxLayout.Y_AXIS));
        formsPanel.setOpaque(false);

        // Formulario de Agregar
        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addPanel.setOpaque(false);
        addPanel.setBorder(BorderFactory.createTitledBorder("Agregar Servicio"));
        JTextField addNombreField = new JTextField(10);
        JTextField addPrecioField = new JTextField(5);
        JButton addButton = new RoundedButton("Agregar");
        addButton.setPreferredSize(new Dimension(100, 35));
        addPanel.add(new JLabel("Nombre:"));
        addPanel.add(addNombreField);
        addPanel.add(new JLabel("Precio:"));
        addPanel.add(addPrecioField);
        addPanel.add(addButton);

        // Formulario de Modificar
        JPanel modPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        modPanel.setOpaque(false);
        modPanel.setBorder(BorderFactory.createTitledBorder("Modificar Servicio (y Precio)"));
        JTextField modIdField = new JTextField(3);
        JTextField modNombreField = new JTextField(10);
        JTextField modPrecioField = new JTextField(5);
        JButton modButton = new RoundedButton("Modificar");
        modButton.setPreferredSize(new Dimension(100, 35));
        modPanel.add(new JLabel("ID:"));
        modPanel.add(modIdField);
        modPanel.add(new JLabel("Nuevo Nombre:"));
        modPanel.add(modNombreField);
        modPanel.add(new JLabel("Nuevo Precio:"));
        modPanel.add(modPrecioField);
        modPanel.add(modButton);
        
        // Formulario de Eliminar
        JPanel delPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        delPanel.setOpaque(false);
        delPanel.setBorder(BorderFactory.createTitledBorder("Eliminar Servicio"));
        JTextField delIdField = new JTextField(3);
        JButton delButton = new RoundedButton("Eliminar");
        ((RoundedButton) delButton).setNormalColor(new Color(200, 50, 50));
        ((RoundedButton) delButton).setHoverColor(new Color(230, 80, 80));
        delButton.setPreferredSize(new Dimension(100, 35));
        delPanel.add(new JLabel("ID:"));
        delPanel.add(delIdField);
        delPanel.add(delButton);

        formsPanel.add(addPanel);
        formsPanel.add(modPanel);
        formsPanel.add(delPanel);
        panel.add(formsPanel, BorderLayout.EAST);

        JButton refreshButton = new RoundedButton("Actualizar Lista de Servicios");
        refreshButton.setPreferredSize(new Dimension(220, 35));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // --- ACCIONES ---
        refreshButton.addActionListener(e -> {
            areaServicios.setText(HotelServicio.getServicios());
        });

        addButton.addActionListener(e -> {
            try {
                String nombre = addNombreField.getText();
                double precio = Double.parseDouble(addPrecioField.getText());
                String res = HotelServicio.agregarServicio(nombre, precio);
                JOptionPane.showMessageDialog(this, res);
                areaServicios.setText(HotelServicio.getServicios());
                addNombreField.setText("");
                addPrecioField.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Datos inválidos. El precio debe ser un número.");
            }
        });

        modButton.addActionListener(e -> {
            try {
                int id = Integer.parseInt(modIdField.getText());
                String nombre = modNombreField.getText();
                double precio = Double.parseDouble(modPrecioField.getText());
                String res = HotelServicio.modificarServicio(id, nombre, precio);
                JOptionPane.showMessageDialog(this, res);
                areaServicios.setText(HotelServicio.getServicios());
                modIdField.setText("");
                modNombreField.setText("");
                modPrecioField.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Datos inválidos. El ID y el precio deben ser números.");
            }
        });
        
        delButton.addActionListener(e -> {
            try {
                int id = Integer.parseInt(delIdField.getText());
                String res = HotelServicio.eliminarServicio(id);
                JOptionPane.showMessageDialog(this, res);
                areaServicios.setText(HotelServicio.getServicios());
                delIdField.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "ID inválido. Debe ser un número.");
            }
        });

        return panel;
    }
    
    /**
     * Pestaña 6: Permite Agregar y Eliminar Promociones.
     */
    private JPanel crearPanelGestionarPromociones() {
        JPanel panel = new GradientPanel(Color.WHITE, new Color(245, 250, 255));
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Lista
        areaPromociones = new JTextArea("Presione 'Actualizar' para ver la lista de promociones...");
        configurarTextArea(areaPromociones);
        JScrollPane scroll = new JScrollPane(areaPromociones);
        configurarScrollPane(scroll);
        panel.add(scroll, BorderLayout.CENTER);

        // Formularios
        JPanel formsPanel = new JPanel();
        formsPanel.setLayout(new BoxLayout(formsPanel, BoxLayout.Y_AXIS));
        formsPanel.setOpaque(false);

        // Formulario de Agregar
        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addPanel.setOpaque(false);
        addPanel.setBorder(BorderFactory.createTitledBorder("Agregar Promoción"));
        JTextField addNombreField = new JTextField(10);
        JTextField addDescField = new JTextField(5);
        JButton addButton = new RoundedButton("Agregar");
        addButton.setPreferredSize(new Dimension(100, 35));
        addPanel.add(new JLabel("Nombre:"));
        addPanel.add(addNombreField);
        addPanel.add(new JLabel("% Descuento:"));
        addPanel.add(addDescField);
        addPanel.add(addButton);
        
        // Formulario de Eliminar
        JPanel delPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        delPanel.setOpaque(false);
        delPanel.setBorder(BorderFactory.createTitledBorder("Eliminar Promoción"));
        JTextField delIdField = new JTextField(3);
        JButton delButton = new RoundedButton("Eliminar");
        ((RoundedButton) delButton).setNormalColor(new Color(200, 50, 50));
        ((RoundedButton) delButton).setHoverColor(new Color(230, 80, 80));
        delButton.setPreferredSize(new Dimension(100, 35));
        delPanel.add(new JLabel("ID:"));
        delPanel.add(delIdField);
        delPanel.add(delButton);

        formsPanel.add(addPanel);
        formsPanel.add(delPanel);
        panel.add(formsPanel, BorderLayout.EAST);

        // Botón de Actualizar
        JButton refreshButton = new RoundedButton("Actualizar Lista de Promociones");
        refreshButton.setPreferredSize(new Dimension(240, 35));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // --- ACCIONES ---
        refreshButton.addActionListener(e -> {
            areaPromociones.setText(HotelServicio.getPromociones());
        });

        addButton.addActionListener(e -> {
            try {
                String nombre = addNombreField.getText();
                double desc = Double.parseDouble(addDescField.getText());
                String res = HotelServicio.agregarPromocion(nombre, desc);
                JOptionPane.showMessageDialog(this, res);
                areaPromociones.setText(HotelServicio.getPromociones());
                addNombreField.setText("");
                addDescField.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Datos inválidos. El descuento debe ser un número.");
            }
        });
        
        delButton.addActionListener(e -> {
            try {
                int id = Integer.parseInt(delIdField.getText());
                String res = HotelServicio.eliminarPromocion(id);
                JOptionPane.showMessageDialog(this, res);
                areaPromociones.setText(HotelServicio.getPromociones());
                delIdField.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "ID inválido. Debe ser un número.");
            }
        });

        return panel;
    }

    // --- MÉTODOS HELPER DE ESTILO ---
    
    /**
     * Aplica un estilo estándar a un JTextArea.
     */
    private void configurarTextArea(JTextArea textArea) {
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);
        textArea.setOpaque(false); // Transparente
        textArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), // Borde gris claro
            new EmptyBorder(5, 5, 5, 5) // Padding interno
        ));
    }
    
    /**
     * Aplica un estilo estándar a un JScrollPane.
     */
    private void configurarScrollPane(JScrollPane scrollPane) {
        scrollPane.setOpaque(false); // Transparente
        scrollPane.getViewport().setOpaque(false); // Transparente
        scrollPane.setBorder(null); // Sin borde
    }
}