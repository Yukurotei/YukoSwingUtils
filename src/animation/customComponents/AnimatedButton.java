package animation.customComponents;

import javax.swing.JButton;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.AlphaComposite;
import java.awt.RenderingHints;

/**
 * Special type of button that has opacity and rotation
 * @since 1.0.0
 * @author Yukurotei
 */
public class AnimatedButton extends JButton implements AnimatedJComponent {

    public AnimatedButton(String text) {
        super(text);
    }

    public AnimatedButton() {
        super();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = AnimatedJComponent.getAnimatedGraphics(this, g);
        super.paintComponent(g2d);
        g2d.dispose();
    }
}
