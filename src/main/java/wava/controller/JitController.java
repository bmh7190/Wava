package wava.controller;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import wava.model.jit.JitEvent;
import wava.model.jit.JitEventSummary;
import wava.model.jit.JitLogStatus;
import wava.service.jit.JitEventFilter;
import wava.service.jit.JitLogReader;
import wava.service.jit.JitSummaryAnalyzer;
import wava.view.WavaFrame;

public class JitController {
    private final WavaFrame frame;
    private final JitLogReader jitLogReader;
    private final JitSummaryAnalyzer jitSummaryAnalyzer;
    private final List<JitEvent> jitEvents;
    private JitEventFilter jitEventFilter;
    private JitLogStatus jitLogStatus;
    private String jitFilterText;
    private String lastJitLogStatusKey;

    public JitController(WavaFrame frame) {
        this.frame = frame;
        jitLogReader = new JitLogReader();
        jitSummaryAnalyzer = new JitSummaryAnalyzer();
        jitEvents = new ArrayList<>();
        jitEventFilter = new JitEventFilter("");
        jitLogStatus = jitLogReader.inspectStatus();
        jitFilterText = "";
        lastJitLogStatusKey = "";
    }

    public void resetForMonitoring() {
        jitLogReader.reset();
        jitEvents.clear();
        updateJitLogStatus(true);
    }

    public void resetAll() {
        jitLogReader.reset();
        jitEvents.clear();
    }

    public void readNewEvents() {
        updateJitLogStatus(false);
        if (!jitLogStatus.isReadable()) {
            return;
        }
        try {
            List<JitEvent> events = jitLogReader.readNewEvents();
            for (JitEvent event : events) {
                jitEvents.add(event);
                if (jitEventFilter.matches(event)) {
                    frame.getLogPanel().appendJitEvent(event);
                }
            }
        } catch (IOException exception) {
            setJitLogStatus(JitLogStatus.readError(jitLogReader.getLogPath(), exception.getMessage()), true);
            frame.getLogPanel().appendInfo("Failed to read JIT log: " + exception.getMessage());
        }
    }

    public void applySettings() {
        jitLogReader.setLogPath(frame.getLogPanel().getLogPath());
        jitFilterText = frame.getLogPanel().getFilterText().trim();
        jitEventFilter = new JitEventFilter(jitFilterText);
        jitEvents.clear();
        frame.getLogPanel().appendInfo("JIT settings applied. Log: " + jitLogReader.getLogPath());
        updateJitLogStatus(true);
    }

    public JitEventSummary createSummary() {
        return jitSummaryAnalyzer.analyze(jitEvents, jitEventFilter);
    }

    public List<JitEvent> getJitEvents() {
        return jitEvents;
    }

    public JitEventFilter getJitEventFilter() {
        return jitEventFilter;
    }

    public JitLogStatus getJitLogStatus() {
        return jitLogStatus;
    }

    public String getJitFilterText() {
        return jitFilterText;
    }

    public Path getLogPath() {
        return jitLogReader.getLogPath();
    }

    private void updateJitLogStatus(boolean forceLog) {
        setJitLogStatus(jitLogReader.inspectStatus(), forceLog);
    }

    private void setJitLogStatus(JitLogStatus nextStatus, boolean forceLog) {
        jitLogStatus = nextStatus;
        String nextStatusKey = nextStatus.getStatusKey();
        if (forceLog || !nextStatusKey.equals(lastJitLogStatusKey)) {
            frame.getLogPanel().appendInfo(nextStatus.formatLogMessage());
            lastJitLogStatusKey = nextStatusKey;
        }
    }
}
