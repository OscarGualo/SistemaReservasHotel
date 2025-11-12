package vista;

import datos.DatosQuemados;
import java.awt.*; // Importamos los datos para leer las listas
import java.util.ArrayList;    // Importamos el modelo
import java.util.HashMap; // Importamos el modelo
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import modelo.Promocion;
import modelo.Servicio;
import servicio.HotelServicio;
import vista.componentes.GradientPanel;
import vista.componentes.RoundedButton;

/**
 * Clase de la Vista (GUI) para el panel del Cliente.
 * MODIFICADA para extender GradientPanel, usar RoundedButton y llamar a super().
 * CORREGIDO: El botón de Logout ahora funciona.
 */
public class ClienteVista extends GradientPanel { 

    private MainFrame mainFrame;
    private JTextArea areaDisponibilidad;
    private JTextArea areaMisReservas;

    // Campos para la pestaña de reserva
    private Map<JCheckBox, Servicio> mapaCheckboxesServicios;
    private JComboBox<String> comboPromociones;
    private Map<String, Promocion> mapaPromociones; // Para vincular el String del ComboBox al objeto Promocion

    public ClienteVista(MainFrame mainFrame) {
        
        // --- ¡CORRECCIÓN! ---
        // 1. Llama al constructor del GradientPanel PRIMERO
        super(new Color(230, 245, 255), Color.WHITE); // Degradado azul claro a blanco
        
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10)); // Márgenes

        // --- 2. JTabbedPane (Pestañas) ---
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setOpaque(false); // Transparente para ver el fondo
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabbedPane.setForeground(new Color(0, 51, 102)); // Azul oscuro

        // Añadimos las pestañas
        tabbedPane.addTab("Disponibilidad", crearPanelDisponibilidad());
        tabbedPane.addTab("Realizar Reserva", crearPanelReservar());
        tabbedPane.addTab("Gestionar Mis Reservas", crearPanelGestionar());

        add(tabbedPane, BorderLayout.CENTER);

        // --- 3. Botón de Cerrar Sesión ---
        // Usamos nuestro botón redondeado
        JButton logoutButton = new RoundedButton("Cerrar Sesión");
        ((RoundedButton) logoutButton).setNormalColor(new Color(200, 50, 50)); // Rojo
        ((RoundedButton) logoutButton).setHoverColor(new Color(230, 80, 80)); // Rojo claro
        logoutButton.setPreferredSize(new Dimension(150, 40));
        
        // --- ¡CORRECCIÓN! ---
        // Conecta el botón a la función logout() del MainFrame
        logoutButton.addActionListener(e -> mainFrame.logout());
        // --- FIN DE LA CORRECCIÓN ---
        
        // Ponemos el botón en un panel transparente para que se alinee a la derecha
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        southPanel.setOpaque(false); // Fondo transparente
        southPanel.add(logoutButton);
        add(southPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Pestaña 1: Consultar Disponibilidad
     */
    private JPanel crearPanelDisponibilidad() {
        // Usamos un GradientPanel para el fondo de la pestaña
        JPanel panel = new GradientPanel(Color.WHITE, new Color(245, 250, 255));
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15)); // Márgenes internos

        areaDisponibilidad = new JTextArea("Presione 'Actualizar' para ver...");
        configurarTextArea(areaDisponibilidad); // Aplica estilo

        JScrollPane scroll = new JScrollPane(areaDisponibilidad);
        configurarScrollPane(scroll); // Aplica estilo
        panel.add(scroll, BorderLayout.CENTER);
        
        // Usamos nuestro botón redondeado
        JButton refreshButton = new RoundedButton("Actualizar");
        refreshButton.setPreferredSize(new Dimension(120, 35));
        refreshButton.addActionListener(e -> areaDisponibilidad.setText(HotelServicio.getDisponibilidad()));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false); // Transparente
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    /**
     * Pestaña 2: Realizar Reserva (con Factura, Servicios y Promos)
     */
    private JScrollPane crearPanelReservar() {
        // Panel principal con fondo degradado
        JPanel panel = new GradientPanel(Color.WHITE, new Color(245, 250, 255));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // --- 1. Panel de Habitación y Fechas ---
        JPanel panelDatos = new JPanel(new GridBagLayout());
        panelDatos.setOpaque(false); // Transparente
        panelDatos.setBorder(BorderFactory.createTitledBorder("1. Datos de la Reserva"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; panelDatos.add(new JLabel("Num. Habitación:"), gbc);
        JTextField habField = new JTextField(10);
        gbc.gridx = 1; gbc.gridy = 0; panelDatos.add(habField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; panelDatos.add(new JLabel("Fecha Entrada (YYYY-MM-DD):"), gbc);
        JTextField fechaInField = new JTextField(10);
        gbc.gridx = 1; gbc.gridy = 1; panelDatos.add(fechaInField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; panelDatos.add(new JLabel("Fecha Salida (YYYY-MM-DD):"), gbc);
        JTextField fechaOutField = new JTextField(10);
        gbc.gridx = 1; gbc.gridy = 2; panelDatos.add(fechaOutField, gbc);

        // --- 2. Panel de Servicios ---
        JPanel panelServicios = new JPanel();
        panelServicios.setOpaque(false);
        panelServicios.setLayout(new BoxLayout(panelServicios, BoxLayout.Y_AXIS));
        panelServicios.setBorder(BorderFactory.createTitledBorder("2. Servicios Adicionales"));
        mapaCheckboxesServicios = new HashMap<>();
        for (Servicio s : DatosQuemados.SERVICIOS) {
            String texto = String.format("%s ($%.2f)", s.getNombre(), s.getPrecio());
            JCheckBox cb = new JCheckBox(texto);
            cb.setOpaque(false); // Transparente
            mapaCheckboxesServicios.put(cb, s);
            panelServicios.add(cb);
        }

        // --- 3. Panel de Promociones ---
        JPanel panelPromocion = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelPromocion.setOpaque(false);
        panelPromocion.setBorder(BorderFactory.createTitledBorder("3. Aplicar Promoción"));
        comboPromociones = new JComboBox<>();
        mapaPromociones = new HashMap<>();
        comboPromociones.addItem("Sin promoción");
        mapaPromociones.put("Sin promoción", null);
        for (Promocion p : DatosQuemados.PROMOCIONES) {
            String texto = String.format("%s (%.1f%% Dcto.)", p.getNombre(), p.getPorcentajeDescuento());
            comboPromociones.addItem(texto);
            mapaPromociones.put(texto, p);
        }
        panelPromocion.add(comboPromociones);

        // --- 4. Botón de Reserva ---
        JButton reservarButton = new RoundedButton("Revisar Factura y Pagar");
        reservarButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        reservarButton.setPreferredSize(new Dimension(220, 40));

        // --- Añadimos todos los paneles al panel principal ---
        panel.add(panelDatos);
        panel.add(Box.createVerticalStrut(10)); // Espaciador
        panel.add(panelServicios);
        panel.add(Box.createVerticalStrut(10)); // Espaciador
        panel.add(panelPromocion);
        panel.add(Box.createVerticalStrut(20)); // Espaciador
        panel.add(reservarButton);

        // --- ACCIÓN DEL BOTÓN ---
        reservarButton.addActionListener(e -> {
            try {
                // a. Recolectar datos básicos
                int numHab = Integer.parseInt(habField.getText());
                String fechaIn = fechaInField.getText();
                String fechaOut = fechaOutField.getText();

                // b. Recolectar IDs de servicios seleccionados
                List<Integer> idsServicios = new ArrayList<>();
                for (Map.Entry<JCheckBox, Servicio> entry : mapaCheckboxesServicios.entrySet()) {
                    if (entry.getKey().isSelected()) {
                        idsServicios.add(entry.getValue().getId());
                    }
                }

                // c. Recolectar ID de promoción seleccionada
                String promoSeleccionadaStr = (String) comboPromociones.getSelectedItem();
                Promocion promoSeleccionada = mapaPromociones.get(promoSeleccionadaStr);
                int idPromocion = (promoSeleccionada != null) ? promoSeleccionada.getId() : 0;

                // d. Llamar al servicio con toda la información
                String factura = HotelServicio.realizarReserva(
                    mainFrame.getUsuarioActual(), numHab, fechaIn, fechaOut, idsServicios, idPromocion
                );
                
                // Si el servicio devuelve un error, mostrarlo como error
                if (factura.startsWith("Error:")) {
                    JOptionPane.showMessageDialog(this, factura, "Error en la Reserva", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // e. Mostrar la factura en un Pop-up
                JTextArea textArea = new JTextArea(factura);
                textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                textArea.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(textArea);
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);
                scrollPane.setPreferredSize(new Dimension(400, 450));
                JOptionPane.showMessageDialog(this, scrollPane, "Factura de Reserva", JOptionPane.INFORMATION_MESSAGE);

                // f. Limpiar campos y actualizar listas
                habField.setText("");
                fechaInField.setText("");
                fechaOutField.setText("");
                for (JCheckBox cb : mapaCheckboxesServicios.keySet()) {
                    cb.setSelected(false);
                }
                comboPromociones.setSelectedIndex(0);
                areaDisponibilidad.setText(HotelServicio.getDisponibilidad()); // Actualiza pestaña 1
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Error: El número de habitación debe ser un número.");
            } catch (Exception ex) {
                // Captura otros errores (como mal formato de fecha devuelto por el servicio)
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error en la Reserva", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Devolvemos el panel dentro de un JScrollPane
        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(null); // Quita el borde del scrollpane
        return scroll;
    }

    /**
     * Pestaña 3: Gestionar Mis Reservas
     */
    private JPanel crearPanelGestionar() {
        JPanel panel = new GradientPanel(Color.WHITE, new Color(245, 250, 255));
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        areaMisReservas = new JTextArea("Presione 'Actualizar' para ver sus reservas...");
        configurarTextArea(areaMisReservas);
        
        JScrollPane scroll = new JScrollPane(areaMisReservas);
        configurarScrollPane(scroll);
        panel.add(scroll, BorderLayout.CENTER);
        
        // --- Paneles de acciones (Modificar/Cancelar) ---
        JPanel actionsPanel = new JPanel();
        actionsPanel.setLayout(new BoxLayout(actionsPanel, BoxLayout.Y_AXIS));
        actionsPanel.setOpaque(false);
        
        JPanel cancelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cancelPanel.setOpaque(false);
        cancelPanel.setBorder(BorderFactory.createTitledBorder("Cancelar Reserva"));
        JTextField cancelIdField = new JTextField(5);
        JButton cancelButton = new RoundedButton("Cancelar");
        ((RoundedButton) cancelButton).setNormalColor(new Color(200, 50, 50)); // Rojo
        ((RoundedButton) cancelButton).setHoverColor(new Color(230, 80, 80)); // Rojo claro
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelPanel.add(new JLabel("ID Reserva:"));
        cancelPanel.add(cancelIdField);
        cancelPanel.add(cancelButton);

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
        
        actionsPanel.add(cancelPanel);
        actionsPanel.add(modifyPanel);
        panel.add(actionsPanel, BorderLayout.EAST);
        
        JButton refreshButton = new RoundedButton("Actualizar Lista de Mis Reservas");
        refreshButton.setPreferredSize(new Dimension(240, 35));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // --- ACCIONES ---
        
        // (MODIFICADO) Esta acción ahora muestra los detalles de la factura
        refreshButton.addActionListener(e -> {
            areaMisReservas.setText(HotelServicio.getMisReservas(mainFrame.getUsuarioActual()));
        });
        
        cancelButton.addActionListener(e -> {
            try {
                int id = Integer.parseInt(cancelIdField.getText());
                String res = HotelServicio.cancelarReserva(id, mainFrame.getUsuarioActual());
                JOptionPane.showMessageDialog(this, res);
                areaMisReservas.setText(HotelServicio.getMisReservas(mainFrame.getUsuarioActual()));
                areaDisponibilidad.setText(HotelServicio.getDisponibilidad());
                cancelIdField.setText("");
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
                areaMisReservas.setText(HotelServicio.getMisReservas(mainFrame.getUsuarioActual()));
                areaDisponibilidad.setText(HotelServicio.getDisponibilidad());
                modIdField.setText("");
                modHabField.setText("");
                modFechaInField.setText("");
                modFechaOutField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID y Habitación deben ser números.");
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