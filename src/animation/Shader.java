package animation;

import animation.customComponents.AnimatedJComponent;

import javax.swing.*;
import java.awt.*;

public class Shader extends JComponent implements AnimatedJComponent {

    private final RootPaneContainer root;
    private Color filterColor = new Color(255, 0, 0, 100);

    public Shader(JFrame frame) {
        this.root = frame;
        init();
    }

    public Shader(JDialog dialog) {
        this.root = dialog;
        init();
    }

    private void init() {
        setOpaque(false);

        putClientProperty("animation.opacity", 1f);
        putClientProperty("animation.rotation", 0f);

        root.setGlassPane(this);
        setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = AnimatedJComponent.getAnimatedGraphics(this, g);

        float opacity = (float) getClientProperty("animation.opacity");
        g2d.setComposite(
                AlphaComposite.SrcOver.derive(opacity)
        );

        g2d.setColor(filterColor);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.dispose();
    }

    public void setColorFilter(Color color) {
        this.filterColor = color;
        repaint();
    }

    public void setAlpha(int alpha) {
        float a = Math.max(0, Math.min(255, alpha)) / 255f;
        putClientProperty("animation.opacity", a);
        repaint();
    }

    public void setRotation(float radians) {
        putClientProperty("animation.rotation", radians);
        repaint();
    }

    public void remove() {
        setVisible(false);
    }
}
