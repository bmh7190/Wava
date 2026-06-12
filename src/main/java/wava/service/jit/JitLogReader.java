package wava.service.jit;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import wava.model.jit.JitEvent;
import wava.model.jit.JitLogStatus;

public class JitLogReader {
    private static final Path DEFAULT_LOG_PATH = Path.of("logs", "jit.log");
    private static final List<Charset> SUPPORTED_CHARSETS = Arrays.asList(
            StandardCharsets.UTF_8,
            StandardCharsets.UTF_16,
            StandardCharsets.UTF_16LE,
            Charset.defaultCharset());

    private Path logPath;
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

        List<String> lines = readLines();
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

    public JitLogStatus inspectStatus() {
        if (!Files.exists(logPath)) {
            return JitLogStatus.missing(logPath);
        }
        if (!Files.isRegularFile(logPath) || !Files.isReadable(logPath)) {
            return JitLogStatus.unreadable(logPath);
        }
        try {
            return JitLogStatus.ready(logPath, Files.size(logPath));
        } catch (IOException exception) {
            return JitLogStatus.readError(logPath, exception.getMessage());
        }
    }

    public void setLogPath(Path logPath) {
        this.logPath = logPath;
        reset();
    }

    private List<String> readLines() throws IOException {
        IOException lastException = null;
        for (Charset charset : SUPPORTED_CHARSETS) {
            try {
                return Files.readAllLines(logPath, charset);
            } catch (IOException exception) {
                lastException = exception;
            }
        }
        throw lastException;
    }
}
