package wava.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import wava.model.JavaProcessInfo;
import wava.model.MetricSample;

public class MonitorService {
    private static final int DEFAULT_MAX_SAMPLES = 120;
    private static final long DEFAULT_INTERVAL_MILLIS = 1000L;

    private final MetricCollector metricCollector;
    private final List<MetricSample> samples;
    private final int maxSamples;
    private ScheduledExecutorService executor;
    private ScheduledFuture<?> samplingTask;
    private int sampleIndex;

    public MonitorService() {
        this(new TargetProcessMetricCollector(), DEFAULT_MAX_SAMPLES);
    }

    public MonitorService(MetricCollector metricCollector, int maxSamples) {
        this.metricCollector = metricCollector;
        this.maxSamples = maxSamples;
        samples = new ArrayList<>();
    }

    public synchronized void start(JavaProcessInfo targetProcess, Consumer<List<MetricSample>> sampleListener) {
        if (isRunning()) {
            return;
        }
        executor = Executors.newSingleThreadScheduledExecutor();
        samplingTask = executor.scheduleAtFixedRate(
                () -> collectSample(targetProcess, sampleListener),
                0L,
                DEFAULT_INTERVAL_MILLIS,
                TimeUnit.MILLISECONDS);
    }

    public synchronized void stop() {
        if (samplingTask != null) {
            samplingTask.cancel(false);
            samplingTask = null;
        }
        if (executor != null) {
            executor.shutdownNow();
            executor = null;
        }
    }

    public synchronized void reset() {
        stop();
        samples.clear();
        sampleIndex = 0;
    }

    public synchronized boolean isRunning() {
        return samplingTask != null && !samplingTask.isCancelled();
    }

    public synchronized List<MetricSample> getSamples() {
        return copySamples();
    }

    public synchronized MetricSample addSample(JavaProcessInfo targetProcess) {
        MetricSample sample = metricCollector.collect(targetProcess, sampleIndex);
        sampleIndex++;
        samples.add(sample);
        trimSamples();
        return sample;
    }

    private void collectSample(JavaProcessInfo targetProcess, Consumer<List<MetricSample>> sampleListener) {
        List<MetricSample> snapshot;
        synchronized (this) {
            addSample(targetProcess);
            snapshot = copySamples();
        }
        sampleListener.accept(snapshot);
    }

    private List<MetricSample> copySamples() {
        return Collections.unmodifiableList(new ArrayList<>(samples));
    }

    private void trimSamples() {
        while (samples.size() > maxSamples) {
            samples.remove(0);
        }
    }
}
