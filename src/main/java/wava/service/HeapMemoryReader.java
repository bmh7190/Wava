package wava.service;

import wava.model.JavaProcessInfo;

public interface HeapMemoryReader {
    double readHeapUsedMb(JavaProcessInfo targetProcess);
}
