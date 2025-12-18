package animation.customComponents;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.RenderingHints;

/**
 * Special type of panel that has opacity and rotation
 * @since 1.0.0
 * @author Yukurotei
 */
public class AnimatedPanel extends JPanel implements AnimatedJComponent {

    private Color paintColor;

    public AnimatedPanel(Color paintColor) {
        this.paintColor = paintColor;
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Do not call super.paintComponent() to take full control of rendering.
        // The isOpaque() override also helps prevent the default background fill.
        Graphics2D g2d = AnimatedJComponent.getAnimatedGraphics(this, g);

        // Use the component's paintColor and fill its entire bounds.
        // The rotation and alpha transforms from getAnimatedGraphics will be applied.
        g2d.setColor(this.paintColor);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.dispose();
    }

    @Override
    public boolean isOpaque() {
        return false;
    }

    public Color getPaintColor() {
        return paintColor;
    }

    public void setPaintColor(Color paintColor) {
        this.paintColor = paintColor;
        repaint();
    }
}

