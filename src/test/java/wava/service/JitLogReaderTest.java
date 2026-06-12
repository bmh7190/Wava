package wava.service;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import wava.model.JitEvent;

public class JitLogReaderTest {
    public static void main(String[] args) throws Exception {
        readOnlyNewEvents();
        handleMissingLogFile();
        resetReadsFromBeginning();
    }

    private static void readOnlyNewEvents() throws Exception {
        Path logFile = Files.createTempFile("wava-jit", ".log");
        try {
            Files.write(logFile, List.of(
                    "123  1       3       com.example.A::run (10 bytes)"),
                    StandardCharsets.UTF_8);
            JitLogReader reader = new JitLogReader(logFile, new JitLogParser());

            List<JitEvent> firstRead = reader.readNewEvents();
            List<JitEvent> secondRead = reader.readNewEvents();

            assertEquals(1, firstRead.size(), "first read size");
            assertEquals(0, secondRead.size(), "second read size");
        } finally {
            Files.deleteIfExists(logFile);
        }
    }

    private static void handleMissingLogFile() throws Exception {
        Path missingFile = Path.of("missing-jit-" + System.nanoTime() + ".log");
        JitLogReader reader = new JitLogReader(missingFile, new JitLogParser());

        assertEquals(0, reader.readNewEvents().size(), "missing file size");
    }

    private static void resetReadsFromBeginning() throws Exception {
        Path logFile = Files.createTempFile("wava-jit-reset", ".log");
        try {
            Files.write(logFile, List.of(
                    "123  1       3       com.example.A::run (10 bytes)"),
                    StandardCharsets.UTF_8);
            JitLogReader reader = new JitLogReader(logFile, new JitLogParser());

            reader.readNewEvents();
            reader.reset();
            List<JitEvent> events = reader.readNewEvents();

            assertEquals(1, events.size(), "reset size");
        } finally {
            Files.deleteIfExists(logFile);
        }
    }

    private static void assertEquals(int expected, int actual, String label) {
        if (expected != actual) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }
}
