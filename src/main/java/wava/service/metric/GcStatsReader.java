package wava.service.metric;

import wava.model.metric.GcStatsSnapshot;
import wava.model.process.JavaProcessInfo;

public interface GcStatsReader {
    GcStatsSnapshot readGcStats(JavaProcessInfo targetProcess);
}
