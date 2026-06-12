package wava.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import wava.model.JitEvent;

public class JitLogReader {
    private static final Path DEFAULT_LOG_PATH = Path.of("logs", "jit.log");

    private final Path logPath;
    private final JitLogParser parser;
    private int nextLineIndex;

    public JitLogReader() {
        this(DEFAULT_LOG_PATH, new JitLogParser());
    }

    public JitLogReader(Path logPath, JitLogParser parser) {
        this.logPath = logPath;
        this.parser = parser;
    }

    public List<JitEvent> readNewEvents() throws IOException {
        if (!Files.exists(logPath)) {
            return List.of();
        }

        List<String> lines = Files.readAllLines(logPath, StandardCharsets.UTF_8);
        if (lines.size() < nextLineIndex) {
            nextLineIndex = 0;
        }

        List<JitEvent> events = new ArrayList<>();
        long timestamp = System.currentTimeMillis();
        for (int index = nextLineIndex; index < lines.size(); index++) {
            parser.parse(lines.get(index), timestamp).ifPresent(events::add);
        }
        nextLineIndex = lines.size();
        return events;
    }

    public void reset() {
        nextLineIndex = 0;
    }

    public Path getLogPath() {
        return logPath;
    }
}
