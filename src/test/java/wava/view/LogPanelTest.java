package wava.view;

import wava.model.jit.JitFilterPreset;
import wava.model.jit.JitFilterSuggestion;

public class LogPanelTest {
    public static void main(String[] args) {
        createLogPanel();
        updateCurrentTargetFilterWhenSelected();
        applySelectedSuggestionToFilterText();
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

    private static void applySelectedSuggestionToFilterText() {
        LogPanel panel = new LogPanel();

        panel.setFilterSuggestions(java.util.List.of(
                new JitFilterSuggestion("[Top] sample.Target.run", "sample.Target::run")));
        panel.selectFilterSuggestion(0);
        panel.useSelectedFilterSuggestion();

        assertEquals(JitFilterPreset.CUSTOM, panel.getFilterPreset(), "preset");
        assertEquals("sample.Target::run", panel.getFilterText(), "filter text");
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
