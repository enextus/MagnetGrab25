package org.image;

import org.junit.jupiter.api.Test;
import java.awt.image.BufferedImage;
import static org.junit.jupiter.api.Assertions.*;

public class ImgProcessorTest {

    @Test
    public void testDecodeBase64ToImage_validBase64() {
        String validBase64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+P+/HgAFhAJ/wlseKgAAAABJRU5ErkJggg==";
        BufferedImage image = ImgProcessor.decodeBase64ToImage(validBase64);
        assertNotNull(image, "Изображение должно быть декодировано");
        assertEquals(1, image.getWidth(), "Ширина должна быть 1");
        assertEquals(1, image.getHeight(), "Высота должна быть 1");
    }

    @Test
    public void testDecodeBase64ToImage_invalidBase64() {
        String invalidBase64 = "invalid_base64_string";
        assertThrows(IllegalArgumentException.class, () -> ImgProcessor.decodeBase64ToImage(invalidBase64),
                "Ожидается исключение для неверной Base64 строки");
    }

    @Test
    public void testDecodeBase64ToImage_nullBase64() {
        assertThrows(NullPointerException.class, () -> ImgProcessor.decodeBase64ToImage(null),
                "Ожидается NullPointerException для null строки");
    }

    @Test
    public void testScaleImageForPreview_smallImage() {
        String validBase64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+P+/HgAFhAJ/wlseKgAAAABJRU5ErkJggg==";
        BufferedImage image = ImgProcessor.decodeBase64ToImage(validBase64);
        assertNotNull(image, "Изображение должно быть декодировано");
        BufferedImage scaled = ImgProcessor.scaleImageForPreview(image);
        assertEquals(1, scaled.getWidth(), "Ширина должна остаться 1");
        assertEquals(1, scaled.getHeight(), "Высота должна остаться 1");
    }

    @Test
    public void testScaleImageForPreview_largeImage() {
        BufferedImage largeImage = new BufferedImage(1024, 768, BufferedImage.TYPE_INT_ARGB);
        BufferedImage scaled = ImgProcessor.scaleImageForPreview(largeImage);
        assertEquals(512, scaled.getWidth(), "Ширина должна быть 512");
        assertEquals(384, scaled.getHeight(), "Высота должна быть 384");
    }
}