package wava.service.metric;

import wava.model.process.JavaProcessInfo;
import wava.model.metric.MetricSample;

public interface MetricCollector {
    MetricSample collect(JavaProcessInfo targetProcess, int sampleIndex);
}
