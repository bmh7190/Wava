import javax.swing.SwingUtilities;
import wava.controller.MainController;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainController().start());
    }
}
