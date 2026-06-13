package wava.service.metric;

import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;
import javax.management.MBeanServerConnection;
import wava.model.metric.GcStatsSnapshot;
import wava.model.process.JavaProcessInfo;

public class TargetJvmGcReader implements GcStatsReader {
    private final TargetJvmConnector connector;

    public TargetJvmGcReader() {
        this(new TargetJvmConnector());
    }

    public TargetJvmGcReader(TargetJvmConnector connector) {
        this.connector = connector;
    }

    @Override
    public GcStatsSnapshot readGcStats(JavaProcessInfo targetProcess) {
        if (targetProcess == null) {
            return GcStatsSnapshot.unavailable();
        }

        try (TargetJvmConnection connection = connector.connect(targetProcess.getPid())) {
            return readFromConnection(connection.getMBeanServerConnection());
        } catch (IOException exception) {
            return GcStatsSnapshot.unavailable();
        }
    }

    private GcStatsSnapshot readFromConnection(MBeanServerConnection connection) throws IOException {
        List<GarbageCollectorMXBean> beans = ManagementFactory.getPlatformMXBeans(
                connection,
                GarbageCollectorMXBean.class);
        long totalCount = 0L;
        long totalTimeMillis = 0L;
        for (GarbageCollectorMXBean bean : beans) {
            totalCount += supportedValue(bean.getCollectionCount());
            totalTimeMillis += supportedValue(bean.getCollectionTime());
        }
        return GcStatsSnapshot.available(totalCount, totalTimeMillis);
    }

    private long supportedValue(long value) {
        return Math.max(0L, value);
    }
}
