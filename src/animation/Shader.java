package animation;

import animation.customComponents.AnimatedJComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class Shader extends JComponent implements AnimatedJComponent {

    private final JComponent target;
    private Color filterColor = Color.RED;

    public Shader(JComponent target) {
        this.target = target;
        setOpaque(false);

        putClientProperty("animation.opacity", 1f);
        putClientProperty("animation.rotation", 0f);

        attach();
    }

    private void attach() {
        Container parent = target.getParent();
        if (parent == null)
            throw new IllegalStateException("Target must be added before Shader");

        parent.setLayout(null);

        setBounds(target.getBounds());
        parent.add(this);
        parent.setComponentZOrder(this, 0);

        //sync
        target.addComponentListener(new ComponentAdapter() {
            @Override public void componentMoved(ComponentEvent e) { sync(); }
            @Override public void componentResized(ComponentEvent e) { sync(); }
        });
    }

    private void sync() {
        Rectangle old = getBounds();
        Rectangle now = target.getBounds();

        setBounds(now);

        Container p = getParent();
        if (p != null) {
            p.repaint(old.x, old.y, old.width, old.height);
            p.repaint(now.x, now.y, now.width, now.height);
            p.repaint();
        }
    }



    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = AnimatedJComponent.getAnimatedGraphics(this, g);

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
        Container parent = getParent();
        if (parent != null) {
            parent.remove(this);
            parent.repaint();
        }
    }
}
