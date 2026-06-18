package wava.model.jit;

public class JitFilterSuggestion {
    private final String label;
    private final String filterText;

    public JitFilterSuggestion(String label, String filterText) {
        this.label = label;
        this.filterText = filterText;
    }

    public String getFilterText() {
        return filterText;
    }

    @Override
    public String toString() {
        return label;
    }
}
