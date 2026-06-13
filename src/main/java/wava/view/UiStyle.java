package wava.view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public final class UiStyle {
    public static final Color BACKGROUND = new Color(241, 245, 249);
    public static final Color PANEL_BACKGROUND = Color.WHITE;
    public static final Color SURFACE = new Color(248, 250, 252);
    public static final Color BORDER = new Color(203, 213, 225);
    public static final Color TEXT = new Color(15, 23, 42);
    public static final Color MUTED_TEXT = new Color(71, 85, 105);
    public static final Color PRIMARY = new Color(37, 99, 235);
    public static final Color PRIMARY_HOVER = new Color(29, 78, 216);
    public static final Color PRIMARY_SOFT = new Color(219, 234, 254);
    public static final Color DANGER = new Color(220, 38, 38);
    public static final Color FIELD_BACKGROUND = new Color(255, 255, 255);
    public static final Font APP_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
    public static final Font APP_FONT_BOLD = APP_FONT.deriveFont(Font.BOLD);

    private UiStyle() {
    }

    public static void configureDefaults() {
        UIManager.put("control", BACKGROUND);
        UIManager.put("info", SURFACE);
        UIManager.put("nimbusBase", new Color(71, 85, 105));
        UIManager.put("nimbusBlueGrey", BORDER);
        UIManager.put("nimbusFocus", PRIMARY);
        UIManager.put("text", TEXT);
        UIManager.put("defaultFont", APP_FONT);
    }

    public static JLabel titleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT);
        label.setFont(APP_FONT_BOLD.deriveFont(20f));
        return label;
    }

    public static JLabel mutedLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(MUTED_TEXT);
        label.setFont(APP_FONT);
        return label;
    }

    public static void applyPanelStyle(JComponent component, String title) {
        component.setBackground(PANEL_BACKGROUND);
        component.setBorder(createPanelBorder(title));
        component.setFont(APP_FONT);
    }

    public static Border createPanelBorder(String title) {
        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER),
                title);
        titledBorder.setTitleColor(TEXT);
        titledBorder.setTitleFont(APP_FONT_BOLD);
        return new CompoundBorder(titledBorder, new EmptyBorder(10, 10, 10, 10));
    }

    public static void applyButtonStyle(JButton button) {
        button.setFont(APP_FONT_BOLD);
        button.setForeground(TEXT);
        button.setBackground(PRIMARY_SOFT);
        button.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(new Color(148, 163, 184)),
                new EmptyBorder(5, 12, 5, 12)));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(button.getPreferredSize().width, 30));
    }

    public static void applyPrimaryButtonStyle(JButton button) {
        applyButtonStyle(button);
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY);
        button.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_HOVER),
                new EmptyBorder(5, 12, 5, 12)));
    }

    public static void applyTextFieldStyle(JTextField field) {
        field.setFont(APP_FONT);
        field.setForeground(TEXT);
        field.setBackground(FIELD_BACKGROUND);
        field.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(4, 6, 4, 6)));
    }

    public static void applyTextAreaStyle(JTextArea textArea) {
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        textArea.setForeground(TEXT);
        textArea.setBackground(SURFACE);
        textArea.setCaretColor(PRIMARY);
        textArea.setBorder(new EmptyBorder(6, 6, 6, 6));
    }

    public static void applyListStyle(JList<?> list) {
        list.setFont(APP_FONT);
        list.setForeground(TEXT);
        list.setBackground(SURFACE);
        list.setSelectionBackground(PRIMARY_SOFT);
        list.setSelectionForeground(TEXT);
        list.setFixedCellHeight(22);
    }

    public static void applyScrollPaneStyle(JScrollPane scrollPane) {
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));
        scrollPane.getViewport().setBackground(SURFACE);
    }
}
