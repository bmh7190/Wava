package wava.service.metric;

import java.io.IOException;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;

public class TargetJvmConnection implements AutoCloseable {
    private final JMXConnector connector;

    public TargetJvmConnection(JMXConnector connector) {
        this.connector = connector;
    }

    public MBeanServerConnection getMBeanServerConnection() throws IOException {
        return connector.getMBeanServerConnection();
    }

    @Override
    public void close() throws IOException {
        connector.close();
    }
}
