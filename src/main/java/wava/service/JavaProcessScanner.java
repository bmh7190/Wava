package wava.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import wava.model.JavaProcessInfo;

public class JavaProcessScanner {
    private static final String JPS_PROCESS_SUFFIX = ".Jps";
    private static final String JPS_PROCESS_NAME = "Jps";

    public List<JavaProcessInfo> scan() throws IOException {
        Process process = new ProcessBuilder("jps", "-l").start();
        List<String> lines = readLines(process);
        waitForExit(process);
        return parseLines(lines);
    }

    public List<JavaProcessInfo> parseLines(List<String> lines) {
        List<JavaProcessInfo> processes = new ArrayList<>();
        for (String line : lines) {
            parseLine(line).ifPresent(processes::add);
        }
        return processes;
    }

    private java.util.Optional<JavaProcessInfo> parseLine(String line) {
        String normalizedLine = line.trim();
        if (normalizedLine.isEmpty()) {
            return java.util.Optional.empty();
        }

        String[] parts = normalizedLine.split("\\s+", 2);
        if (parts.length < 2) {
            return java.util.Optional.empty();
        }

        long pid = parsePid(parts[0]);
        String displayName = parts[1].trim();
        if (pid < 0 || isJpsProcess(displayName)) {
            return java.util.Optional.empty();
        }

        return java.util.Optional.of(new JavaProcessInfo(pid, displayName));
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

    private List<String> readLines(Process process) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), Charset.defaultCharset()))) {
            String line = reader.readLine();
            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }
        }
        return lines;
    }

    private void waitForExit(Process process) throws IOException {
        try {
            process.waitFor();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while waiting for jps.", exception);
        }
    }
}
