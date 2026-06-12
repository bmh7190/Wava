package wava.service.jit;

import java.util.Optional;
import wava.model.jit.JitEvent;

public class JitLogParserTest {
    public static void main(String[] args) {
        parseSimpleCompilationLine();
        parseCompilationLineWithFlags();
        ignoreEmptyLine();
        ignoreMalformedLine();
    }

    private static void parseSimpleCompilationLine() {
        JitLogParser parser = new JitLogParser();

        Optional<JitEvent> event = parser.parse(
                "123  1       3       com.example.MyService::process (45 bytes)",
                1000L);

        assertTrue(event.isPresent(), "event present");
        assertEquals(1000L, event.get().getTimestampMillis(), "timestamp");
        assertEquals(1, event.get().getCompileId(), "compile id");
        assertEquals("3", event.get().getLevel(), "level");
        assertEquals("com.example.MyService::process", event.get().getMethodName(), "method");
    }

    private static void parseCompilationLineWithFlags() {
        JitLogParser parser = new JitLogParser();

        Optional<JitEvent> event = parser.parse(
                "456  22 %     4       com.example.Worker::loop @ 2 (80 bytes)",
                2000L);

        assertTrue(event.isPresent(), "flag event present");
        assertEquals(22, event.get().getCompileId(), "flag compile id");
        assertEquals("4", event.get().getLevel(), "flag level");
        assertEquals("com.example.Worker::loop", event.get().getMethodName(), "flag method");
    }

    private static void ignoreEmptyLine() {
        JitLogParser parser = new JitLogParser();

        assertTrue(parser.parse("   ", 1000L).isEmpty(), "empty ignored");
    }

    private static void ignoreMalformedLine() {
        JitLogParser parser = new JitLogParser();

        assertTrue(parser.parse("not a compilation line", 1000L).isEmpty(), "malformed ignored");
    }

    private static void assertTrue(boolean condition, String label) {
        if (!condition) {
            throw new AssertionError(label + " expected true");
        }
    }

    private static void assertEquals(long expected, long actual, String label) {
        if (expected != actual) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }

    private static void assertEquals(String expected, String actual, String label) {
        if (!expected.equals(actual)) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }
}
