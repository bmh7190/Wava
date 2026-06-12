package wava.service;

import wava.model.JavaProcessInfo;
import wava.model.HeapMemorySnapshot;

public interface HeapMemoryReader {
    HeapMemorySnapshot readHeapMemory(JavaProcessInfo targetProcess);
}
