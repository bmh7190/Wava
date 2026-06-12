package wava.model;

public class JitEventTest {
    public static void main(String[] args) {
        formatEventWithLevel();
        formatEventWithoutLevel();
    }

    private static void formatEventWithLevel() {
        JitEvent event = new JitEvent(1000L, 12, "3", "com.example.Target::run", "raw");

        assertEquals("#12 L3 com.example.Target.run compiled", event.formatLogMessage(), "level message");
    }

    private static void formatEventWithoutLevel() {
        JitEvent event = new JitEvent(1000L, 12, "", "com.example.Target::run", "raw");

        assertEquals("#12 com.example.Target.run compiled", event.formatLogMessage(), "plain message");
    }

    private static void assertEquals(String expected, String actual, String label) {
        if (!expected.equals(actual)) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }
}
