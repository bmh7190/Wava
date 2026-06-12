package wava.model.monitor;

public enum MonitorState {
    IDLE("Idle"),
    RUNNING("Running"),
    STOPPED("Stopped");

    private final String label;

    MonitorState(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
