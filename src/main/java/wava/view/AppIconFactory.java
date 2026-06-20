package wava.view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public final class AppIconFactory {
    private static final int[] FRAME_ICON_SIZES = {16, 24, 32, 48, 64, 128};
    private static final Color BACKGROUND = new Color(20, 35, 62);
    private static final Color WAVE = new Color(59, 130, 246);
    private static final Color ACCENT = new Color(34, 211, 238);
    private static final Color FOREGROUND = Color.WHITE;

    private AppIconFactory() {
    }

    public static List<Image> createFrameIcons() {
        List<Image> icons = new ArrayList<>();
        for (int size : FRAME_ICON_SIZES) {
            icons.add(createIcon(size));
        }
        return icons;
    }

    static BufferedImage createIcon(int size) {
        if (size < 16) {
            throw new IllegalArgumentException("Icon size must be at least 16.");
        }

        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        try {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setColor(BACKGROUND);
            graphics.fillRoundRect(0, 0, size, size, Math.max(4, size / 5), Math.max(4, size / 5));

            int padding = Math.max(2, size / 8);
            int baseline = size - padding - Math.max(2, size / 8);
            int peak = padding + Math.max(2, size / 8);
            int middle = size / 2;

            graphics.setColor(WAVE);
            graphics.fillPolygon(
                    new int[] {padding, middle, size - padding, size - padding, middle, padding},
                    new int[] {baseline, peak, baseline, size - padding, middle + padding, size - padding},
                    6);

            graphics.setColor(ACCENT);
            int dotSize = Math.max(3, size / 5);
            graphics.fillOval(size - padding - dotSize, padding, dotSize, dotSize);

            graphics.setColor(FOREGROUND);
            int stemWidth = Math.max(2, size / 8);
            int stemHeight = Math.max(7, size / 2);
            int stemY = size - padding - stemHeight;
            graphics.fillRoundRect(padding, stemY, stemWidth, stemHeight, stemWidth, stemWidth);
            graphics.fillRoundRect(size - padding - stemWidth, stemY, stemWidth, stemHeight, stemWidth, stemWidth);
        } finally {
            graphics.dispose();
        }
        return image;
    }
}
