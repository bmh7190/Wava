package wava.service;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import javax.management.MBeanServerConnection;
import wava.model.JavaProcessInfo;

public class TargetJvmMemoryReader implements HeapMemoryReader {
    private final TargetJvmConnector connector;

    public TargetJvmMemoryReader() {
        this(new TargetJvmConnector());
    }

    public TargetJvmMemoryReader(TargetJvmConnector connector) {
        this.connector = connector;
    }

    @Override
    public double readHeapUsedMb(JavaProcessInfo targetProcess) {
        if (targetProcess == null) {
            return 0.0;
        }

        try (TargetJvmConnection connection = connector.connect(targetProcess.getPid())) {
            MemoryMXBean memoryBean = createMemoryBean(connection.getMBeanServerConnection());
            return MemoryUnit.bytesToMb(memoryBean.getHeapMemoryUsage().getUsed());
        } catch (IOException exception) {
            return 0.0;
        }
    }

    private MemoryMXBean createMemoryBean(MBeanServerConnection connection) throws IOException {
        return ManagementFactory.newPlatformMXBeanProxy(
                connection,
                ManagementFactory.MEMORY_MXBEAN_NAME,
                MemoryMXBean.class);
    }
}
