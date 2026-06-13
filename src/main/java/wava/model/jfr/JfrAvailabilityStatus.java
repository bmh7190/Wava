package wava.model.jfr;

public class JfrAvailabilityStatus {
    private final boolean available;
    private final String stateLabel;
    private final String detail;

    private JfrAvailabilityStatus(boolean available, String stateLabel, String detail) {
        this.available = available;
        this.stateLabel = stateLabel;
        this.detail = detail;
    }

    public static JfrAvailabilityStatus available() {
        return new JfrAvailabilityStatus(true, "Available", "JFR APIs are available in the current JDK.");
    }

    public static JfrAvailabilityStatus unavailable(String detail) {
        return new JfrAvailabilityStatus(false, "Unavailable", detail);
    }

    public boolean isAvailable() {
        return available;
    }

    public String getStateLabel() {
        return stateLabel;
    }

    public String getDetail() {
        return detail;
    }

    public String formatLogMessage() {
        return "JFR status: " + stateLabel + ". " + detail;
    }
}
