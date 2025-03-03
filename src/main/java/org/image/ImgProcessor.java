package org.image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

public class ImgProcessor {

    /**
     * Scales a BufferedImage to fit within a preview area of maximum size 512x512 pixels
     * while maintaining its aspect ratio. Does not upscale images smaller than 512x512.
     *
     * @param source the BufferedImage to scale
     * @return the scaled BufferedImage or the original if no scaling is needed
     */
    public static BufferedImage scaleImageForPreview(BufferedImage source) {
        final int maxSize = 512;
        // Avoid upscaling if the image is already within maxSize bounds
        if (source.getWidth() <= maxSize && source.getHeight() <= maxSize) {
            return source; // Return original image unchanged
        }
        double scaleFactor = Math.min((double) maxSize / source.getWidth(), (double) maxSize / source.getHeight());
        int newWidth = (int) (source.getWidth() * scaleFactor);
        int newHeight = (int) (source.getHeight() * scaleFactor);
        BufferedImage scaledImage = new BufferedImage(newWidth, newHeight, source.getType());
        Graphics2D g = scaledImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(source, 0, 0, newWidth, newHeight, null);
        g.dispose();
        return scaledImage;
    }

    /**
     * Decodes a Base64-encoded string back to a BufferedImage.
     *
     * @param base64ImageString The Base64-encoded string of the image.
     * @return The BufferedImage decoded from the provided Base64-encoded string.
     */
    public static BufferedImage decodeBase64ToImage(String base64ImageString) {
        byte[] imageBytes = Base64.getDecoder().decode(base64ImageString);
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes)) {
            return ImageIO.read(inputStream);
        } catch (IOException e) {
            System.err.println("Failed to decode the image.");
            return null;
        }
    }
}