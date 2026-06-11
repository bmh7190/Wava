package wava.service;

import wava.model.JavaProcessInfo;
import wava.model.MetricSample;

public interface MetricCollector {
    MetricSample collect(JavaProcessInfo targetProcess, int sampleIndex);
}
