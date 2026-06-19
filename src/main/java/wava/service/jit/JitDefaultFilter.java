package wava.service.jit;

import wava.model.jit.JitFilterPreset;

public class JitDefaultFilter {
    private final JitFilterPreset preset;
    private final String currentTargetFilter;

    public JitDefaultFilter(JitFilterPreset preset, String currentTargetFilter) {
        this.preset = preset;
        this.currentTargetFilter = currentTargetFilter;
    }

    public JitFilterPreset getPreset() {
        return preset;
    }

    public String getCurrentTargetFilter() {
        return currentTargetFilter;
    }
}
