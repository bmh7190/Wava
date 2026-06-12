package wava.service;

import java.util.Optional;
import java.util.function.LongFunction;
import wava.model.JavaProcessInfo;
import wava.model.TargetProcessStatus;

public class ProcessStatusChecker {
    private final LongFunction<TargetProcessStatus> statusResolver;

    public ProcessStatusChecker() {
        this(ProcessStatusChecker::resolveStatus);
    }

    ProcessStatusChecker(LongFunction<TargetProcessStatus> statusResolver) {
        this.statusResolver = statusResolver;
    }

    public TargetProcessStatus check(JavaProcessInfo process) {
        if (process == null) {
            return TargetProcessStatus.UNKNOWN;
        }
        return statusResolver.apply(process.getPid());
    }

    private static TargetProcessStatus resolveStatus(long pid) {
        if (pid <= 0L) {
            return TargetProcessStatus.UNKNOWN;
        }

        try {
            Optional<ProcessHandle> processHandle = ProcessHandle.of(pid);
            if (processHandle.isEmpty()) {
                return TargetProcessStatus.ENDED;
            }
            return processHandle.get().isAlive()
                    ? TargetProcessStatus.RUNNING
                    : TargetProcessStatus.ENDED;
        } catch (SecurityException exception) {
            return TargetProcessStatus.UNKNOWN;
        }
    }
}
