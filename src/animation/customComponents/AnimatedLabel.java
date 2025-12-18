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
        setOpaque(false);
    }

    public AnimatedLabel() {
        super();
        setOpaque(false);
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

        //remove clipping (HOLY THIS EXISTS?!?!?!)
        g2d.setClip(null);

        if (originalImage != null) {
            //Only do rotation calculation if rotation isn't 0, since if we're not rotating it's not needed
            if (getRotation() != 0) {
                //preserve aspect ratio
                int compWidth = getWidth();
                int compHeight = getHeight();

                int imgWidth = originalImage.getWidth(null);
                int imgHeight = originalImage.getHeight(null);

                //prevent improper div when below or = 0
                if (imgWidth <= 0 || imgHeight <= 0) {
                    super.paintComponent(g2d);
                    g2d.dispose();
                    return;
                }

                float imgAspect = (float) imgWidth / imgHeight;
                float compAspect = (float) compWidth / compHeight;

                int newImgWidth;
                int newImgHeight;
                if (imgAspect > compAspect) {
                    newImgWidth = compWidth;
                    newImgHeight = (int) (newImgWidth / imgAspect);
                } else {
                    newImgHeight = compHeight;
                    newImgWidth = (int) (newImgHeight * imgAspect);
                }

                //center
                int x = (compWidth - newImgWidth) / 2;
                int y = (compHeight - newImgHeight) / 2;

                g2d.drawImage(originalImage, x, y, newImgWidth, newImgHeight, this);
            } else {
                g2d.drawImage(originalImage, 0, 0, getWidth(), getHeight(), this);
            }
        } else {
            super.paintComponent(g2d);
        }

        g2d.dispose();
    }
}
