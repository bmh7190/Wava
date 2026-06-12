package wava.service;

import wava.model.JavaProcessInfo;
import wava.model.TargetProcessStatus;

public class ProcessStatusCheckerTest {
    public static void main(String[] args) {
        returnUnknownForNullProcess();
        useInjectedResolver();
        detectCurrentProcessAsRunning();
    }

    private static void returnUnknownForNullProcess() {
        ProcessStatusChecker checker = new ProcessStatusChecker(pid -> TargetProcessStatus.RUNNING);

        TargetProcessStatus status = checker.check(null);

        assertEquals(TargetProcessStatus.UNKNOWN, status, "null status");
    }

    private static void useInjectedResolver() {
        ProcessStatusChecker checker = new ProcessStatusChecker(pid -> TargetProcessStatus.ENDED);
        JavaProcessInfo process = new JavaProcessInfo(1234L, "sample.Target");

        TargetProcessStatus status = checker.check(process);

        assertEquals(TargetProcessStatus.ENDED, status, "injected status");
    }

    private static void detectCurrentProcessAsRunning() {
        ProcessStatusChecker checker = new ProcessStatusChecker();
        JavaProcessInfo process = new JavaProcessInfo(ProcessHandle.current().pid(), "current");

        TargetProcessStatus status = checker.check(process);

        assertEquals(TargetProcessStatus.RUNNING, status, "current process status");
    }

    private static void assertEquals(TargetProcessStatus expected, TargetProcessStatus actual, String label) {
        if (expected != actual) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }
}
