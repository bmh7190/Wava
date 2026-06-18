package wava.controller;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import wava.model.jit.JitEvent;
import wava.model.jit.JitEventSummary;
import wava.model.jit.JitFilterPreset;
import wava.model.jit.JitLogStatus;
import wava.service.jit.JitEventFilter;
import wava.service.jit.JitFilterSuggestionBuilder;
import wava.service.jit.JitLogPathResolver;
import wava.service.jit.JitLogReader;
import wava.service.jit.JitSummaryAnalyzer;
import wava.view.WavaFrame;
import wava.model.process.JavaProcessInfo;

public class JitController {
    private static final int MAX_FILTER_SUGGESTIONS = 8;

    private final WavaFrame frame;
    private final JitLogReader jitLogReader;
    private final JitLogPathResolver jitLogPathResolver;
    private final JitFilterSuggestionBuilder jitFilterSuggestionBuilder;
    private final JitSummaryAnalyzer jitSummaryAnalyzer;
    private final List<JitEvent> jitEvents;
    private JitEventFilter jitEventFilter;
    private JitLogStatus jitLogStatus;
    private String jitFilterText;
    private String lastJitLogStatusKey;
    private JavaProcessInfo selectedProcess;

    public JitController(WavaFrame frame) {
        this.frame = frame;
        jitLogReader = new JitLogReader();
        jitLogPathResolver = new JitLogPathResolver();
        jitFilterSuggestionBuilder = new JitFilterSuggestionBuilder();
        jitSummaryAnalyzer = new JitSummaryAnalyzer();
        jitEvents = new ArrayList<>();
        jitEventFilter = new JitEventFilter("");
        jitLogStatus = jitLogReader.inspectStatus();
        jitFilterText = "";
        lastJitLogStatusKey = "";
        selectedProcess = null;
        updateFilterSuggestions();
    }

    public void resetForMonitoring() {
        syncSettingsFromView();
        jitLogReader.reset();
        jitEvents.clear();
        updateJitLogStatus(true);
    }

    public void resetAll() {
        jitLogReader.reset();
        jitEvents.clear();
        updateFilterSuggestions();
    }

    public void applyProcessLogSuggestion(JavaProcessInfo process) {
        selectedProcess = process;
        frame.getLogPanel().setCurrentTargetFilter(createCurrentTargetFilter(process));
        frame.getLogPanel().setFilterPreset(JitFilterPreset.CURRENT_TARGET);
        updateFilterSuggestions();
        jitLogPathResolver.resolve(process).ifPresent(path -> {
            frame.getLogPanel().setLogPath(path);
            jitLogReader.setLogPath(path);
            jitLogReader.reset();
            jitEvents.clear();
            frame.getLogPanel().appendInfo("Auto-selected JIT log " + path + " for " + process.getDisplayName() + ".");
            updateJitLogStatus(true);
            updateFilterSuggestions();
        });
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
            if (!events.isEmpty()) {
                updateFilterSuggestions();
            }
        } catch (IOException exception) {
            setJitLogStatus(JitLogStatus.readError(jitLogReader.getLogPath(), exception.getMessage()), true);
            frame.getLogPanel().appendInfo("Failed to read JIT log: " + exception.getMessage());
        }
    }

    public void applySettings() {
        syncSettingsFromView();
        jitEvents.clear();
        frame.getLogPanel().appendInfo("JIT settings applied. Log: " + jitLogReader.getLogPath());
        updateJitLogStatus(true);
        updateFilterSuggestions();
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

    private void syncSettingsFromView() {
        jitLogReader.setLogPath(frame.getLogPanel().getLogPath());
        jitFilterText = frame.getLogPanel().getFilterText().trim();
        jitEventFilter = new JitEventFilter(jitFilterText);
    }

    private void updateFilterSuggestions() {
        frame.getLogPanel().setFilterSuggestions(jitFilterSuggestionBuilder.build(
                selectedProcess,
                jitEvents,
                MAX_FILTER_SUGGESTIONS));
    }

    private String createCurrentTargetFilter(JavaProcessInfo process) {
        if (process == null) {
            return "";
        }
        String displayName = process.getDisplayName();
        int packageSeparator = displayName.lastIndexOf('.');
        if (packageSeparator >= 0 && packageSeparator + 1 < displayName.length()) {
            return displayName.substring(packageSeparator + 1);
        }
        return displayName;
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
