package animation;

import javax.swing.JComponent;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public final class Utils {

    /**
     * @param sourceImage the original image to be resized
     * @param newWidth    the desired width of the resized image
     * @param newHeight   the desired height of the resized image
     * @return a new Image object resized to the specified dimensions
     */
    public static Image resizeTo(Image sourceImage, int newWidth, int newHeight) {
        if (newWidth <= 0 || newHeight <= 0) throw new IllegalArgumentException("Dimensions must be positive");
        return sourceImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
    }

    /**
     * @param sourceImage the original image to be resized
     * @param percentage  the percentage to scale the image by (e.g., 50 for 50%)
     * @return a new Image object resized by the specified percentage
     */
    public static Image resizeTo(Image sourceImage, double percentage) {
        if (percentage <= 0) throw new IllegalArgumentException("Percentage must be positive.");

        float scale = (float) (percentage / 100.0);
        int newWidth = Math.round(sourceImage.getWidth(null) * scale);
        int newHeight = Math.round(sourceImage.getHeight(null) * scale);

        if (newWidth <= 0) newWidth = 1;
        if (newHeight <= 0) newHeight = 1;

        return resizeTo(sourceImage, newWidth, newHeight);
    }

    /**
     * @param component1 the first component
     * @param component2 the second component
     * @return the Euclidean distance between the two components
     */
    public static double distanceOfComponents(JComponent component1, JComponent component2) {
        double dx = (component1.getX() + component1.getWidth() / 2.0) - (component2.getX() + component2.getWidth() / 2.0);
        double dy = (component1.getY() + component1.getHeight() / 2.0) - (component2.getY() + component2.getHeight() / 2.0);
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Rotates an image by a given angle (multiples of 90 degrees).
     *
     * @param sourceImage The original image to rotate.
     * @param degrees     The rotation angle in degrees (e.g., 90, 180, 270).
     * @return A new, rotated BufferedImage.
     */
    public static BufferedImage rotateImageRightAngles(BufferedImage sourceImage, float degrees) {
        if (sourceImage == null) throw new IllegalArgumentException("Source image cannot be null.");

        int rotationAngle = ((int) degrees % 360 + 360) % 360;
        if (rotationAngle % 90 != 0) return sourceImage;

        int width = sourceImage.getWidth();
        int height = sourceImage.getHeight();
        int newWidth = width;
        int newHeight = height;

        if (rotationAngle == 90 || rotationAngle == 270) {
            newWidth = height;
            newHeight = width;
        }

        BufferedImage rotatedImage = new BufferedImage(newWidth, newHeight, sourceImage.getType());
        Graphics2D g2d = rotatedImage.createGraphics();

        AffineTransform transform = new AffineTransform();
        transform.translate(newWidth / 2.0, newHeight / 2.0);
        transform.rotate(Math.toRadians(rotationAngle));
        transform.translate(-width / 2.0, -height / 2.0);

        g2d.drawImage(sourceImage, transform, null);
        g2d.dispose();

        return rotatedImage;
    }

    /**
     * Wraps text by inserting newline characters after a specified number of words.
     *
     * @param text         The input string to wrap.
     * @param wordsPerLine The approximate number of words before a line break.
     * @return The wrapped string.
     */
    public static String wrapText(String text, int wordsPerLine) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        StringBuilder wrappedText = new StringBuilder();
        String[] words = text.split(" ");
        int wordCount = 0;

        for (String word : words) {
            wrappedText.append(word).append(" ");
            wordCount++;
            if (wordCount >= wordsPerLine) {
                wrappedText.append("\n");
                wordCount = 0;
            }
        }
        return wrappedText.toString().trim();
    }
}
