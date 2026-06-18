package wava.model.jit;

public enum JitFilterPreset {
    ALL("All", ""),
    SAMPLE_ONLY("Sample only", "sample."),
    CURRENT_TARGET("Current target", ""),
    JDK_RUNTIME("JDK runtime", "jdk."),
    CUSTOM("Custom", "");

    private final String label;
    private final String filterText;

    JitFilterPreset(String label, String filterText) {
        this.label = label;
        this.filterText = filterText;
    }

    public String getFilterText() {
        return filterText;
    }

    public boolean usesCurrentTarget() {
        return this == CURRENT_TARGET;
    }

    @Override
    public String toString() {
        return label;
    }
}
