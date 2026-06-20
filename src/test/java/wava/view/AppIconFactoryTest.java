package wava.view;

import java.awt.Image;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.List;

public class AppIconFactoryTest {
    public static void main(String[] args) {
        createFrameIcons();
        paintDistinctIconRegions();
        rejectTooSmallIcon();
    }

    private static void createFrameIcons() {
        List<Image> icons = AppIconFactory.createFrameIcons();

        assertTrue(icons.size() >= 4, "icon count");
        for (Image icon : icons) {
            assertTrue(icon.getWidth(null) >= 16, "icon width");
            assertTrue(icon.getHeight(null) >= 16, "icon height");
        }
    }

    private static void paintDistinctIconRegions() {
        BufferedImage icon = AppIconFactory.createIcon(64);

        Color center = new Color(icon.getRGB(32, 32), true);
        int brightPixels = 0;
        for (int y = 0; y < icon.getHeight(); y++) {
            for (int x = 0; x < icon.getWidth(); x++) {
                Color pixel = new Color(icon.getRGB(x, y), true);
                if (pixel.getAlpha() > 0
                        && pixel.getRed() > 220
                        && pixel.getGreen() > 220
                        && pixel.getBlue() > 220) {
                    brightPixels++;
                }
            }
        }

        assertTrue(center.getAlpha() > 0, "center alpha");
        assertTrue(brightPixels > 80, "bright wave pixels");
    }

    private static void rejectTooSmallIcon() {
        boolean thrown = false;
        try {
            AppIconFactory.createIcon(8);
        } catch (IllegalArgumentException exception) {
            thrown = true;
        }

        assertTrue(thrown, "small icon rejection");
    }

    private static void assertTrue(boolean condition, String label) {
        if (!condition) {
            throw new AssertionError(label + " expected true");
        }
    }
}
