package wava.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public final class AppIconFactory {
    private static final int[] FRAME_ICON_SIZES = {16, 24, 32, 48, 64, 128};
    private static final Color BACKGROUND_TOP = new Color(15, 23, 42);
    private static final Color BACKGROUND_BOTTOM = new Color(30, 64, 96);
    private static final Color BORDER = new Color(148, 163, 184, 90);
    private static final Color WAVE = new Color(226, 246, 255);

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
            paintBackground(graphics, size);
            paintWave(graphics, size);
        } finally {
            graphics.dispose();
        }
        return image;
    }

    private static void paintBackground(Graphics2D graphics, int size) {
        float arc = Math.max(4.0f, size * 0.22f);
        RoundRectangle2D.Float background = new RoundRectangle2D.Float(
                0.5f,
                0.5f,
                size - 1.0f,
                size - 1.0f,
                arc,
                arc);
        graphics.setPaint(new GradientPaint(0, 0, BACKGROUND_TOP, 0, size, BACKGROUND_BOTTOM));
        graphics.fill(background);

        graphics.setColor(BORDER);
        graphics.setStroke(new BasicStroke(Math.max(1.0f, size / 32.0f)));
        graphics.draw(background);
    }

    private static void paintWave(Graphics2D graphics, int size) {
        GeneralPath path = new GeneralPath();
        path.moveTo(size * 0.16f, size * 0.35f);
        path.lineTo(size * 0.31f, size * 0.68f);
        path.lineTo(size * 0.49f, size * 0.39f);
        path.lineTo(size * 0.66f, size * 0.68f);
        path.lineTo(size * 0.84f, size * 0.35f);

        float waveStroke = Math.max(3.0f, size * 0.14f);
        graphics.setColor(WAVE);
        graphics.setStroke(new BasicStroke(waveStroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics.draw(path);
    }
}
