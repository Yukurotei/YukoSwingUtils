package animation.customComponents;

import javax.swing.JLabel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.AlphaComposite;
import java.awt.RenderingHints;

/**
 * Special type of label that has opacity and rotation
 * @since 1.0.0
 * @author Yukurotei
 */
public class AnimatedLabel extends JLabel implements AnimatedJComponent {

    public AnimatedLabel(String text) {
        super(text);
    }

    public AnimatedLabel() {
        super();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = AnimatedJComponent.getAnimatedGraphics(this, g);
        super.paintComponent(g2d);
        g2d.dispose();
    }
}
