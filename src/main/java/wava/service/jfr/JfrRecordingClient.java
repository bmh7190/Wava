package wava.service.jfr;

import java.io.IOException;
import java.nio.file.Path;

interface JfrRecordingClient {
    long startRecording(long pid) throws IOException;

    Path stopRecording(long pid, long recordingId) throws IOException;
}
