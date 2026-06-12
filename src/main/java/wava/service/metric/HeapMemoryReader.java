package wava.service.metric;

import wava.model.process.JavaProcessInfo;
import wava.model.metric.HeapMemorySnapshot;

public interface HeapMemoryReader {
    HeapMemorySnapshot readHeapMemory(JavaProcessInfo targetProcess);
}
