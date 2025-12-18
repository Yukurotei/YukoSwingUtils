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

        Object opacityProp = component.getClientProperty("animation.opacity");
        float opacity = (opacityProp instanceof Float) ? (Float) opacityProp : 1.0f;

        opacity = Math.max(0.0f, Math.min(1.0f, opacity));
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));

        Object rotation = component.getClientProperty("animation.rotation");
        if (rotation instanceof Float) {
            g2d.rotate((Float) rotation, component.getWidth() / 2.0, component.getHeight() / 2.0);
        }

        return g2d;
    }

    default float getOpacity() {
        Object opacityProp = ((JComponent)this).getClientProperty("animation.opacity");
        return (opacityProp instanceof Float) ? (Float) opacityProp : 1.0f;
    }

    default void setOpacity(float opacity) {
        ((JComponent)this).putClientProperty("animation.opacity", 0f);
    }

    default float getRotation() {
        Object rotationProp = ((JComponent)this).getClientProperty("animation.rotation");
        return (rotationProp instanceof Float) ? (Float) rotationProp : 0f;
    }

    default void setRotation(float rotation) {
        ((JComponent)this).putClientProperty("animation.rotation", rotation);
    }
}
