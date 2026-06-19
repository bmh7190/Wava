package wava.model.jit;

public class JitEventTest {
    public static void main(String[] args) {
        formatEventWithLevel();
        formatEventWithoutLevel();
        formatEventWithJvmElapsedTime();
    }

    private static void formatEventWithLevel() {
        JitEvent event = new JitEvent(1000L, 12, "3", "com.example.Target::run", "raw");

        assertEquals("#12 L3 com.example.Target.run compiled", event.formatLogMessage(), "level message");
    }

    private static void formatEventWithoutLevel() {
        JitEvent event = new JitEvent(1000L, 12, "", "com.example.Target::run", "raw");

        assertEquals("#12 com.example.Target.run compiled", event.formatLogMessage(), "plain message");
    }

    private static void formatEventWithJvmElapsedTime() {
        JitEvent event = new JitEvent(1000L, 4684695L, 12, "4", "com.example.Target::run", "raw");

        assertEquals(4684695L, event.getJvmElapsedMillis(), "elapsed millis");
        assertTrue(event.hasJvmElapsedMillis(), "elapsed available");
        assertEquals(
                "[JVM +01:18:04.695] #12 L4 com.example.Target.run compiled",
                event.formatLogMessage(),
                "elapsed message");
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

    private static void assertEquals(long expected, long actual, String label) {
        if (expected != actual) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }
}
