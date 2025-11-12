package vista.componentes;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints; // Para suavizar

/**
 * Un JPanel personalizado que pinta un fondo con degradado vertical.
 * Pasa de un color inicial (arriba) a un color final (abajo).
 */
public class GradientPanel extends JPanel {
    
    private Color colorInicio;
    private Color colorFin;

    /**
     * Constructor que define los dos colores del degradado.
     * @param colorInicio El color de la parte superior.
     * @param colorFin El color de la parte inferior.
     */
    public GradientPanel(Color colorInicio, Color colorFin) {
        this.colorInicio = colorInicio;
        this.colorFin = colorFin;
    }

    /**
     * Sobrescribe el método de pintado para dibujar el fondo.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Pinta el fondo base
        
        Graphics2D g2d = (Graphics2D) g;
        
        // Activa el antialiasing para un degradado más suave
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Crea el degradado vertical (de arriba a abajo)
        GradientPaint gp = new GradientPaint(
            0, 0, colorInicio,             // Color inicial en la coordenada (0, 0)
            0, getHeight(), colorFin       // Color final al final del panel (0, altura)
        );

        g2d.setPaint(gp); // Establece el "pincel" como nuestro degradado
        g2d.fillRect(0, 0, getWidth(), getHeight()); // Dibuja el rectángulo
    }
}