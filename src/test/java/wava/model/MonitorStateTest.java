package wava.model;

public class MonitorStateTest {
    public static void main(String[] args) {
        assertLabel(MonitorState.IDLE, "Idle");
        assertLabel(MonitorState.RUNNING, "Running");
        assertLabel(MonitorState.STOPPED, "Stopped");
    }

    private static void assertLabel(MonitorState state, String expectedLabel) {
        if (!expectedLabel.equals(state.getLabel())) {
            throw new AssertionError("Unexpected label for " + state.name());
        }
    }
}
