package animation.customComponents;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

/**
 * Special type of label that has opacity and rotation
 * @since 1.0.0
 * @author Yukurotei
 */
public class AnimatedLabel extends JLabel implements AnimatedJComponent {

    private Image originalImage;

    public AnimatedLabel(String text) {
        super(text);
    }

    public AnimatedLabel() {
        super();
    }

    @Override
    public void setIcon(Icon icon) {
        if (icon instanceof ImageIcon) {
            this.originalImage = ((ImageIcon) icon).getImage();
        } else {
            this.originalImage = null;
        }
        super.setIcon(icon);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = AnimatedJComponent.getAnimatedGraphics(this, g);

        if (originalImage != null) {
            // Using a padding factor allows for rotation without clipping.
            // The image is scaled to fit within 80% of the component's bounds.
            float padding = 0.8f;

            int compWidth = (int) (getWidth() * padding);
            int compHeight = (int) (getHeight() * padding);

            int imgWidth = originalImage.getWidth(null);
            int imgHeight = originalImage.getHeight(null);
            float imgAspect = (float) imgWidth / imgHeight;

            // Calculate the new image size, preserving aspect ratio, to fit within the padded component bounds
            int newImgWidth = compWidth;
            int newImgHeight = (int) (newImgWidth / imgAspect);

            if (newImgHeight > compHeight) {
                newImgHeight = compHeight;
                newImgWidth = (int) (newImgHeight * imgAspect);
            }

            // Center the resized image within the original component bounds
            int x = (getWidth() - newImgWidth) / 2;
            int y = (getHeight() - newImgHeight) / 2;

            g2d.drawImage(originalImage, x, y, newImgWidth, newImgHeight, this);

        } else {
            super.paintComponent(g2d);
        }

        g2d.dispose();
    }
}
