package sample;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SampleTargetLauncher {
    private static final int DEFAULT_DURATION_SECONDS = 120;
    private static final List<TargetSpec> DEFAULT_TARGETS = List.of(
            new TargetSpec("CPU", "sample.CpuWarmupTarget", "cpu-warmup-jit.log", "96m"),
            new TargetSpec("GC", "sample.GcPulseTarget", "gc-pulse-jit.log", "96m"),
            new TargetSpec("Steady", "sample.SteadyStateTarget", "steady-state-jit.log", "96m"),
            new TargetSpec("Burst", "sample.BurstyMixedTarget", "bursty-mixed-jit.log", "128m"));

    public static void main(String[] args) throws IOException, InterruptedException {
        int durationSeconds = parseDurationSeconds(args);
        List<Process> processes = launchTargets(durationSeconds);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> destroyProcesses(processes)));
        for (Process process : processes) {
            process.waitFor();
        }
        System.out.println("All sample targets finished.");
    }

    static List<String> defaultTargetClassNames() {
        List<String> classNames = new ArrayList<>();
        for (TargetSpec target : DEFAULT_TARGETS) {
            classNames.add(target.className);
        }
        return classNames;
    }

    private static List<Process> launchTargets(int durationSeconds) throws IOException, InterruptedException {
        Path logDirectory = Path.of("logs");
        Files.createDirectories(logDirectory);
        List<Process> processes = new ArrayList<>();
        for (TargetSpec target : DEFAULT_TARGETS) {
            Process process = startTarget(target, durationSeconds, logDirectory);
            processes.add(process);
            System.out.println("Started " + target.label + " target: "
                    + target.className + ", pid=" + process.pid()
                    + ", jitLog=" + logDirectory.resolve(target.logFileName));
            Thread.sleep(350L);
        }
        return processes;
    }

    private static Process startTarget(TargetSpec target, int durationSeconds, Path logDirectory) throws IOException {
        List<String> command = new ArrayList<>();
        command.add(javaExecutable());
        command.add("-Xms32m");
        command.add("-Xmx" + target.maxHeap);
        command.add("-XX:+PrintCompilation");
        command.add("-cp");
        command.add(System.getProperty("java.class.path"));
        command.add(target.className);
        command.add(Integer.toString(durationSeconds));

        File logFile = logDirectory.resolve(target.logFileName).toFile();
        return new ProcessBuilder(command)
                .redirectErrorStream(true)
                .redirectOutput(ProcessBuilder.Redirect.to(logFile))
                .start();
    }

    private static String javaExecutable() {
        String javaHome = System.getProperty("java.home");
        return Path.of(javaHome, "bin", isWindows() ? "java.exe" : "java").toString();
    }

    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    private static int parseDurationSeconds(String[] args) {
        if (args.length == 0) {
            return DEFAULT_DURATION_SECONDS;
        }
        try {
            int seconds = Integer.parseInt(args[0]);
            return seconds > 0 ? seconds : DEFAULT_DURATION_SECONDS;
        } catch (NumberFormatException exception) {
            return DEFAULT_DURATION_SECONDS;
        }
    }

    private static void destroyProcesses(List<Process> processes) {
        for (Process process : processes) {
            if (process.isAlive()) {
                process.destroy();
            }
        }
    }

    private static class TargetSpec {
        private final String label;
        private final String className;
        private final String logFileName;
        private final String maxHeap;

        private TargetSpec(String label, String className, String logFileName, String maxHeap) {
            this.label = label;
            this.className = className;
            this.logFileName = logFileName;
            this.maxHeap = maxHeap;
        }
    }
}
