package wava.controller;

import java.util.function.Consumer;
import wava.model.jfr.JfrAvailabilityStatus;
import wava.model.jfr.JfrEventSummary;
import wava.model.jfr.JfrRecordingStatus;
import wava.model.process.JavaProcessInfo;
import wava.service.jfr.JfrAvailabilityChecker;
import wava.service.jfr.JfrEventSummaryReader;
import wava.service.jfr.TargetJfrRecorder;

public class JfrController {
    private final JfrAvailabilityChecker jfrAvailabilityChecker;
    private final JfrEventSummaryReader jfrEventSummaryReader;
    private final TargetJfrRecorder targetJfrRecorder;
    private JfrAvailabilityStatus jfrStatus;
    private JfrRecordingStatus jfrRecordingStatus;
    private JfrEventSummary jfrEventSummary;

    public JfrController() {
        jfrAvailabilityChecker = new JfrAvailabilityChecker();
        jfrEventSummaryReader = new JfrEventSummaryReader();
        targetJfrRecorder = new TargetJfrRecorder();
        jfrStatus = jfrAvailabilityChecker.check();
        jfrRecordingStatus = JfrRecordingStatus.idle();
        jfrEventSummary = JfrEventSummary.empty();
    }

    public void startRecording(JavaProcessInfo selectedProcess, Consumer<String> logAction) {
        if (!jfrStatus.isAvailable()) {
            jfrRecordingStatus = JfrRecordingStatus.failed("JFR is not available in the current runtime.");
            logAction.accept(jfrRecordingStatus.formatLogMessage());
            return;
        }
        jfrRecordingStatus = targetJfrRecorder.start(selectedProcess);
        jfrEventSummary = JfrEventSummary.empty();
        logAction.accept(jfrRecordingStatus.formatLogMessage());
    }

    public void stopRecording(JavaProcessInfo selectedProcess, Consumer<String> logAction) {
        if (!jfrRecordingStatus.isRecording()) {
            return;
        }
        jfrRecordingStatus = targetJfrRecorder.stop(selectedProcess, jfrRecordingStatus);
        logAction.accept(jfrRecordingStatus.formatLogMessage());
        if (jfrRecordingStatus.hasOutputPath()) {
            jfrEventSummary = jfrEventSummaryReader.read(jfrRecordingStatus.getOutputPath());
            logAction.accept(jfrEventSummary.formatLogMessage());
        }
    }

    public void reset() {
        jfrRecordingStatus = JfrRecordingStatus.idle();
        jfrEventSummary = JfrEventSummary.empty();
    }

    public JfrAvailabilityStatus getJfrStatus() {
        return jfrStatus;
    }

    public JfrRecordingStatus getJfrRecordingStatus() {
        return jfrRecordingStatus;
    }

    public JfrEventSummary getJfrEventSummary() {
        return jfrEventSummary;
    }
}
