package wava.service.jfr;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordingFile;
import wava.model.jfr.JfrEventSummary;

public class JfrEventSummaryReader {
    public JfrEventSummary read(Path recordingPath) {
        if (recordingPath == null) {
            return JfrEventSummary.empty();
        }
        if (!Files.exists(recordingPath)) {
            return JfrEventSummary.unavailable(recordingPath, "JFR recording file does not exist.");
        }

        int totalEventCount = 0;
        int compilationEventCount = 0;
        int gcEventCount = 0;

        try (RecordingFile recordingFile = new RecordingFile(recordingPath)) {
            while (recordingFile.hasMoreEvents()) {
                RecordedEvent event = recordingFile.readEvent();
                String eventTypeName = event.getEventType().getName();
                totalEventCount++;
                if (isCompilationEvent(eventTypeName)) {
                    compilationEventCount++;
                }
                if (isGcEvent(eventTypeName)) {
                    gcEventCount++;
                }
            }
        } catch (IOException exception) {
            return JfrEventSummary.unavailable(recordingPath, exception.getMessage());
        }

        return JfrEventSummary.available(
                recordingPath,
                totalEventCount,
                compilationEventCount,
                gcEventCount);
    }

    boolean isCompilationEvent(String eventTypeName) {
        if (eventTypeName == null) {
            return false;
        }
        return eventTypeName.equals("jdk.Compilation")
                || eventTypeName.startsWith("jdk.Compiler")
                || eventTypeName.startsWith("jdk.CodeCache");
    }

    boolean isGcEvent(String eventTypeName) {
        if (eventTypeName == null) {
            return false;
        }
        return eventTypeName.contains("GarbageCollection")
                || eventTypeName.startsWith("jdk.GC");
    }
}
