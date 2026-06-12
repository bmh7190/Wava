package wava.service.metric;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import javax.management.MBeanServerConnection;
import wava.model.metric.HeapMemorySnapshot;
import wava.model.process.JavaProcessInfo;

public class TargetJvmMemoryReader implements HeapMemoryReader {
    private final TargetJvmConnector connector;

    public TargetJvmMemoryReader() {
        this(new TargetJvmConnector());
    }

    public TargetJvmMemoryReader(TargetJvmConnector connector) {
        this.connector = connector;
    }

    @Override
    public HeapMemorySnapshot readHeapMemory(JavaProcessInfo targetProcess) {
        if (targetProcess == null) {
            return HeapMemorySnapshot.unavailable();
        }

        try (TargetJvmConnection connection = connector.connect(targetProcess.getPid())) {
            MemoryMXBean memoryBean = createMemoryBean(connection.getMBeanServerConnection());
            return HeapMemorySnapshot.available(MemoryUnit.bytesToMb(memoryBean.getHeapMemoryUsage().getUsed()));
        } catch (IOException exception) {
            return HeapMemorySnapshot.unavailable();
        }
    }

    private MemoryMXBean createMemoryBean(MBeanServerConnection connection) throws IOException {
        return ManagementFactory.newPlatformMXBeanProxy(
                connection,
                ManagementFactory.MEMORY_MXBEAN_NAME,
                MemoryMXBean.class);
    }
}
