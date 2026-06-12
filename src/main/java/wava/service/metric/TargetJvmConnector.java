package wava.service.metric;

import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import java.io.IOException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class TargetJvmConnector {
    private static final String LOCAL_CONNECTOR_ADDRESS =
            "com.sun.management.jmxremote.localConnectorAddress";

    public TargetJvmConnection connect(long pid) throws IOException {
        VirtualMachine virtualMachine = null;
        try {
            virtualMachine = VirtualMachine.attach(String.valueOf(pid));
            String connectorAddress = findConnectorAddress(virtualMachine);
            JMXConnector connector = JMXConnectorFactory.connect(new JMXServiceURL(connectorAddress));
            return new TargetJvmConnection(connector);
        } catch (AttachNotSupportedException exception) {
            throw new IOException("Target JVM attach is not supported.", exception);
        } finally {
            detach(virtualMachine);
        }
    }

    private String findConnectorAddress(VirtualMachine virtualMachine) throws IOException {
        String connectorAddress = virtualMachine.getAgentProperties().getProperty(LOCAL_CONNECTOR_ADDRESS);
        if (connectorAddress == null || connectorAddress.isBlank()) {
            connectorAddress = virtualMachine.startLocalManagementAgent();
        }
        return connectorAddress;
    }

    private void detach(VirtualMachine virtualMachine) throws IOException {
        if (virtualMachine != null) {
            virtualMachine.detach();
        }
    }
}
