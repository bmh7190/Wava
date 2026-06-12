package wava.view;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public final class UiStyle {
    public static final Color BACKGROUND = new Color(245, 247, 250);
    public static final Color PANEL_BACKGROUND = Color.WHITE;
    public static final Color BORDER = new Color(214, 220, 229);
    public static final Color TEXT = new Color(32, 39, 51);
    public static final Color MUTED_TEXT = new Color(96, 105, 120);
    public static final Color PRIMARY = new Color(37, 99, 235);
    public static final Color DANGER = new Color(220, 38, 38);

    private UiStyle() {
    }

    public static JLabel titleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 20f));
        return label;
    }

    public static JLabel mutedLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(MUTED_TEXT);
        return label;
    }

    public static void applyPanelStyle(JComponent component, String title) {
        component.setBackground(PANEL_BACKGROUND);
        component.setBorder(createPanelBorder(title));
    }

    public static Border createPanelBorder(String title) {
        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER),
                title);
        titledBorder.setTitleColor(TEXT);
        titledBorder.setTitleFont(titledBorder.getTitleFont().deriveFont(Font.BOLD));
        return new CompoundBorder(titledBorder, new EmptyBorder(8, 8, 8, 8));
    }
}
