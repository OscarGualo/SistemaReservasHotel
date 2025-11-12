package vista.componentes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;

/**
 * Un JButton personalizado con bordes redondeados y efecto hover.
 * (MODIFICADO para incluir el estado "presionado")
 */
public class RoundedButton extends JButton {

    private Color colorNormal;
    private Color colorHover;
    private Color colorPressed;

    public RoundedButton(String text) {
        super(text);

        this.colorNormal = new Color(0, 102, 204);
        this.colorHover = new Color(0, 132, 255);
        this.colorPressed = new Color(0, 71, 142); // <-- (Azul más oscuro)

        // --- Configuración Esencial ---
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setFocusPainted(false);

        setForeground(Color.WHITE);
        setFont(new Font("Segoe UI", Font.BOLD, 14));
        setPreferredSize(new Dimension(150, 40));
        
        // --- MODIFICADO: MouseListener ---
        // Ahora también escucha los clics para forzar el redibujado
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                repaint(); // Redibuja en hover
            }
            @Override
            public void mouseExited(MouseEvent e) {
                repaint(); // Redibuja al salir
            }
            @Override
            public void mousePressed(MouseEvent e) {
                repaint(); // <-- NUEVO: Redibuja al presionar
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                repaint(); // <-- NUEVO: Redibuja al soltar
            }
        });
    }
    
    // --- Setters para personalizar colores ---
    
    public void setNormalColor(Color colorNormal) {
        this.colorNormal = colorNormal;
        this.colorPressed = colorNormal.darker(); // Calcula el color presionado
        repaint();
    }

    public void setHoverColor(Color colorHover) {
        this.colorHover = colorHover;
        repaint();
    }
    
    // (Opcional)
    public void setPressedColor(Color colorPressed) {
        this.colorPressed = colorPressed;
        repaint();
    }


    /**
     * Sobrescribe el método de pintado para dibujar nuestra forma.
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // --- INICIO DE MODIFICACIÓN (Lógica de estado) ---
        if (getModel().isPressed()) {
            g2d.setColor(colorPressed);
        } else if (getModel().isRollover()) {
            g2d.setColor(colorHover);
        } else {
            g2d.setColor(colorNormal);
        }
        // --- FIN DE MODIFICACIÓN ---
        
        // Dibuja el rectángulo redondeado
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
        
        g2d.dispose();
        
        // Dibuja el texto del botón
        super.paintComponent(g);
    }
}