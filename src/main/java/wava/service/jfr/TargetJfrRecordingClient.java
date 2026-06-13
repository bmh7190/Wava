package wava.service.jfr;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.management.JMException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import wava.service.metric.TargetJvmConnection;
import wava.service.metric.TargetJvmConnector;

class TargetJfrRecordingClient implements JfrRecordingClient {
    private static final String FLIGHT_RECORDER_MXBEAN = "jdk.management.jfr:type=FlightRecorder";
    private static final Path JFR_EXPORT_DIRECTORY = Path.of("exports", "jfr");
    private static final DateTimeFormatter FILE_TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

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
    public Path stopRecording(long pid, long recordingId) throws IOException {
        Path outputPath = createOutputPath(pid, recordingId);
        Files.createDirectories(outputPath.getParent());

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
                    "copyTo",
                    new Object[] {recordingId, outputPath.toAbsolutePath().normalize().toString()},
                    new String[] {"long", "java.lang.String"});
            connection.invoke(
                    objectName,
                    "closeRecording",
                    new Object[] {recordingId},
                    new String[] {"long"});
            return outputPath;
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

    private Path createOutputPath(long pid, long recordingId) {
        String timestamp = LocalDateTime.now().format(FILE_TIMESTAMP_FORMATTER);
        String fileName = "wava-jfr-pid-" + pid + "-recording-" + recordingId + "-" + timestamp + ".jfr";
        return JFR_EXPORT_DIRECTORY.resolve(fileName);
    }
}
