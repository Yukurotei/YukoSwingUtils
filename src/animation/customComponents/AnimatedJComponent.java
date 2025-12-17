package animation.customComponents;

import javax.swing.JComponent;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.AlphaComposite;
import java.awt.RenderingHints;

public interface AnimatedJComponent {

    //Custom handler for painting to support opacity and rotation
    static Graphics2D getAnimatedGraphics(JComponent component, Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Object opacity = component.getClientProperty("animation.opacity");
        if (opacity instanceof Float) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (Float) opacity));
        }

        Object rotation = component.getClientProperty("animation.rotation");
        if (rotation instanceof Float) {
            g2d.rotate((Float) rotation, component.getWidth() / 2.0, component.getHeight() / 2.0);
        }
        return g2d;
    }
}
