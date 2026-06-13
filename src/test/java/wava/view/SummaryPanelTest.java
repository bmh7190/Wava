package wava.view;

import wava.model.monitor.MonitorState;
import wava.model.process.JavaProcessInfo;

public class SummaryPanelTest {
    public static void main(String[] args) {
        createSummaryPanel();
        updateBasicSummaryStates();
    }

    private static void createSummaryPanel() {
        SummaryPanel panel = new SummaryPanel();

        assertTrue(panel.getComponentCount() == 2, "component count");
    }

    private static void updateBasicSummaryStates() {
        SummaryPanel panel = new SummaryPanel();

        panel.showState(MonitorState.IDLE);
        panel.showMessage("No process selected.");
        panel.showSelectedProcess(null);
        panel.showSelectedProcess(new JavaProcessInfo(1234L, "sample.WarmupTarget"));
    }

    private static void assertTrue(boolean condition, String label) {
        if (!condition) {
            throw new AssertionError(label + " expected true");
        }
    }
}
