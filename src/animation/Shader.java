package animation;

import animation.customComponents.AnimatedJComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Shader extends JComponent implements AnimatedJComponent {

    private final JFrame frame;

    private float time = 0f;
    private float opacity = 1f;

   //wave
    private boolean waveEnabled = false;
    private float amplitude = 0f;
    private float frequency = 0f;
    private float speed = 0f;

    //filter
    private Color filterColor = new Color(255, 0, 0, 0);

    private final Timer timer;

    public Shader(JFrame frame) {
        this.frame = frame;
        setOpaque(false);

        frame.getLayeredPane().add(this, JLayeredPane.DRAG_LAYER);

        frame.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                setBounds(0, 0, frame.getWidth(), frame.getHeight());
            }
        });

        setBounds(0, 0, frame.getWidth(), frame.getHeight());
        setVisible(true);

        timer = new Timer(16, e -> {
            time += 0.016f;
            repaint();
        });
        timer.start();
    }

    public void applyWave(float amplitude, float frequency, float speed) {
        this.waveEnabled = true;
        this.amplitude = amplitude;
        this.frequency = frequency;
        this.speed = speed;
        repaint();
    }

    public void clearWave() {
        this.waveEnabled = false;
        repaint();
    }

    public void setColorFilter(Color color) {
        this.filterColor = color;
        repaint();
    }

    public void setAlpha(int alpha) {
        this.opacity = Math.max(0, Math.min(255, alpha)) / 255f;
        repaint();
    }

    public void remove() {
        timer.stop();
        frame.getLayeredPane().remove(this);
        frame.repaint();
    }

    //render
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = AnimatedJComponent.getAnimatedGraphics(this, g);

        BufferedImage scene = captureFrame();
        BufferedImage result = waveEnabled ? wave(scene) : scene;

        g2d.setComposite(AlphaComposite.SrcOver.derive(opacity));
        g2d.drawImage(result, 0, 0, null);

        // color filter overlay (like your original)
        if (filterColor.getAlpha() > 0) {
            g2d.setComposite(AlphaComposite.SrcOver);
            g2d.setColor(filterColor);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        g2d.dispose();
    }

    //cap frame
    private BufferedImage captureFrame() {
        int w = frame.getWidth();
        int h = frame.getHeight();

        BufferedImage img =
                new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = img.createGraphics();
        frame.getContentPane().paint(g2);
        g2.dispose();

        return img;
    }

    //wave shader
    private BufferedImage wave(BufferedImage src) {
        int w = src.getWidth();
        int h = src.getHeight();

        BufferedImage dst =
                new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        int[] s = src.getRGB(0, 0, w, h, null, 0, w);
        int[] d = new int[s.length];

        for (int y = 0; y < h; y++) {
            float offset =
                    (float) Math.sin(y * frequency + time * speed) * amplitude;

            for (int x = 0; x < w; x++) {
                int sx = clamp((int) (x + offset), 0, w - 1);
                d[y * w + x] = s[y * w + sx];
            }
        }

        dst.setRGB(0, 0, w, h, d, 0, w);
        return dst;
    }

    // helpful stuff
    private int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }
}
