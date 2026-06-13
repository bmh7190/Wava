package wava.service.jfr;

import java.io.IOException;

interface JfrRecordingClient {
    long startRecording(long pid) throws IOException;

    void stopRecording(long pid, long recordingId) throws IOException;
}
