import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import wava.controller.MainController;
import wava.view.UiStyle;

public class Main {
    public static void main(String[] args) {
        configureLookAndFeel();
        UiStyle.configureDefaults();
        SwingUtilities.invokeLater(() -> new MainController().start());
    }

    private static void configureLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    return;
                }
            }
        } catch (ClassNotFoundException
                | InstantiationException
                | IllegalAccessException
                | UnsupportedLookAndFeelException exception) {
            System.err.println("Failed to apply Nimbus look and feel: " + exception.getMessage());
        }
    }
}
