package wava.service.jfr;

import java.io.IOException;
import javax.management.JMException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import wava.service.metric.TargetJvmConnection;
import wava.service.metric.TargetJvmConnector;

class TargetJfrRecordingClient implements JfrRecordingClient {
    private static final String FLIGHT_RECORDER_MXBEAN = "jdk.management.jfr:type=FlightRecorder";

    private final TargetJvmConnector connector;

    TargetJfrRecordingClient() {
        this(new TargetJvmConnector());
    }

    TargetJfrRecordingClient(TargetJvmConnector connector) {
        this.connector = connector;
    }

    @Override
    public long startRecording(long pid) throws IOException {
        try (TargetJvmConnection targetConnection = connector.connect(pid)) {
            MBeanServerConnection connection = targetConnection.getMBeanServerConnection();
            ObjectName objectName = createObjectName();
            long recordingId = (Long) connection.invoke(objectName, "newRecording", new Object[0], new String[0]);
            applyProfileConfiguration(connection, objectName, recordingId);
            connection.invoke(
                    objectName,
                    "startRecording",
                    new Object[] {recordingId},
                    new String[] {"long"});
            return recordingId;
        } catch (JMException exception) {
            throw new IOException("Failed to start target JFR recording.", exception);
        }
    }

    @Override
    public void stopRecording(long pid, long recordingId) throws IOException {
        try (TargetJvmConnection targetConnection = connector.connect(pid)) {
            MBeanServerConnection connection = targetConnection.getMBeanServerConnection();
            ObjectName objectName = createObjectName();
            connection.invoke(
                    objectName,
                    "stopRecording",
                    new Object[] {recordingId},
                    new String[] {"long"});
            connection.invoke(
                    objectName,
                    "closeRecording",
                    new Object[] {recordingId},
                    new String[] {"long"});
        } catch (JMException exception) {
            throw new IOException("Failed to stop target JFR recording.", exception);
        }
    }

    private ObjectName createObjectName() throws JMException {
        return new ObjectName(FLIGHT_RECORDER_MXBEAN);
    }

    private void applyProfileConfiguration(
            MBeanServerConnection connection,
            ObjectName objectName,
            long recordingId) throws IOException {
        try {
            connection.invoke(
                    objectName,
                    "setPredefinedConfiguration",
                    new Object[] {recordingId, "profile"},
                    new String[] {"long", "java.lang.String"});
        } catch (JMException exception) {
            throw new IOException("Failed to apply JFR profile configuration.", exception);
        }
    }
}
