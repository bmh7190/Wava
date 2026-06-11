package wava.model;

public class JavaProcessInfo {
    private final long pid;
    private final String displayName;

    public JavaProcessInfo(long pid, String displayName) {
        this.pid = pid;
        this.displayName = displayName;
    }

    public long getPid() {
        return pid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String formatListItem() {
        return pid + "  " + displayName;
    }

    @Override
    public String toString() {
        return formatListItem();
    }
}
