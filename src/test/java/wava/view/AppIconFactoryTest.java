package wava.view;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.List;

public class AppIconFactoryTest {
    public static void main(String[] args) {
        createFrameIcons();
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
