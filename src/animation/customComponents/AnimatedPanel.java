package animation.customComponents;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.RenderingHints;

public class AnimatedPanel extends JPanel implements AnimatedJComponent {

    public AnimatedPanel() {} //Just used as a 'marker' to mark components needed for manual rendering

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); //Paint as normal first

        Graphics2D g2d = AnimatedJComponent.getAnimatedGraphics(this, g);

        //Draw
        //TODO: CUSTOM COLOUR
        g2d.setColor(Color.BLUE);

        int w = getWidth();
        int h = getHeight();

        //To prevent clipping during rotation, we calculate the size of an inner rectangle
        //that will fit within the component's bounds at any angle.
        //Thanks, Gemini, for explaining the math
        double diagonal = Math.sqrt(w * w + h * h);
        float scaleFactor = (float) (Math.min(w, h) / diagonal);

        int innerWidth = (int) (w * scaleFactor);
        int innerHeight = (int) (h * scaleFactor);

        //Center the smaller rectangle
        int x = (w - innerWidth) / 2;
        int y = (h - innerHeight) / 2;
        g2d.fillRect(x, y, innerWidth, innerHeight);

        //Dispose graphics
        g2d.dispose();
    }
}
