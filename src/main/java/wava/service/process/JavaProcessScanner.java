package wava.service.process;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import wava.model.process.JavaProcessInfo;

public class JavaProcessScanner {
    private static final String JPS_PROCESS_SUFFIX = ".Jps";
    private static final String JPS_PROCESS_NAME = "Jps";

    public List<JavaProcessInfo> scan() throws IOException {
        List<JavaProcessInfo> processes = new ArrayList<>();
        for (VirtualMachineDescriptor descriptor : VirtualMachine.list()) {
            parseDescriptor(descriptor).ifPresent(processes::add);
        }
        return processes;
    }

    public List<JavaProcessInfo> parseLines(List<String> lines) {
        List<JavaProcessInfo> processes = new ArrayList<>();
        for (String line : lines) {
            parseLine(line).ifPresent(processes::add);
        }
        return processes;
    }

    private Optional<JavaProcessInfo> parseDescriptor(VirtualMachineDescriptor descriptor) {
        long pid = parsePid(descriptor.id());
        String displayName = descriptor.displayName().trim();
        if (displayName.isEmpty()) {
            displayName = descriptor.id();
        }
        if (pid < 0 || isJpsProcess(displayName)) {
            return Optional.empty();
        }
        return Optional.of(new JavaProcessInfo(pid, displayName));
    }

    private Optional<JavaProcessInfo> parseLine(String line) {
        String normalizedLine = line.trim();
        if (normalizedLine.isEmpty()) {
            return Optional.empty();
        }

        String[] parts = normalizedLine.split("\\s+", 2);
        if (parts.length < 2) {
            return Optional.empty();
        }

        long pid = parsePid(parts[0]);
        String displayName = parts[1].trim();
        if (pid < 0 || isJpsProcess(displayName)) {
            return Optional.empty();
        }

        return Optional.of(new JavaProcessInfo(pid, displayName));
    }

    private long parsePid(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    private boolean isJpsProcess(String displayName) {
        return JPS_PROCESS_NAME.equals(displayName) || displayName.endsWith(JPS_PROCESS_SUFFIX);
    }
}
