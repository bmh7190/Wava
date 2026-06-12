package wava.model.process;

public enum TargetProcessStatus {
    RUNNING("Running"),
    ENDED("Ended"),
    UNKNOWN("Unknown");

    private final String label;

    TargetProcessStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
