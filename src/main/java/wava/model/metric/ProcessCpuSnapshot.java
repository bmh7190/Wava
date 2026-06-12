package wava.model.metric;

public class ProcessCpuSnapshot {
    private final long pid;
    private final long timestampMillis;
    private final long cpuTimeMillis;

    public ProcessCpuSnapshot(long pid, long timestampMillis, long cpuTimeMillis) {
        this.pid = pid;
        this.timestampMillis = timestampMillis;
        this.cpuTimeMillis = cpuTimeMillis;
    }

    public long getPid() {
        return pid;
    }

    public long getTimestampMillis() {
        return timestampMillis;
    }

    public long getCpuTimeMillis() {
        return cpuTimeMillis;
    }
}
