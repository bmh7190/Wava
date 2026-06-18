package wava.view;

import wava.model.jit.JitFilterPreset;

public class LogPanelTest {
    public static void main(String[] args) {
        createLogPanel();
        updateCurrentTargetFilterWhenSelected();
    }

    private static void createLogPanel() {
        LogPanel panel = new LogPanel();

        assertTrue(panel.getSettingsPanel().getComponentCount() > 0, "settings components");
    }

    private static void updateCurrentTargetFilterWhenSelected() {
        LogPanel panel = new LogPanel();

        panel.setCurrentTargetFilter("CpuWarmupTarget");
        panel.setFilterPreset(JitFilterPreset.CURRENT_TARGET);

        assertEquals(JitFilterPreset.CURRENT_TARGET, panel.getFilterPreset(), "preset");
        assertEquals("CpuWarmupTarget", panel.getFilterText(), "filter text");
    }

    private static void assertTrue(boolean condition, String label) {
        if (!condition) {
            throw new AssertionError(label + " expected true");
        }
    }

    private static void assertEquals(String expected, String actual, String label) {
        if (!expected.equals(actual)) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }

    private static void assertEquals(JitFilterPreset expected, JitFilterPreset actual, String label) {
        if (expected != actual) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }
}
