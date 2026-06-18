package wava.service.jit;

import java.nio.file.Path;
import java.util.Optional;
import wava.model.process.JavaProcessInfo;

public class JitLogPathResolverTest {
    public static void main(String[] args) {
        resolveSampleTargetLogPath();
        ignoreUnknownProcess();
    }

    private static void resolveSampleTargetLogPath() {
        JitLogPathResolver resolver = new JitLogPathResolver();
        JavaProcessInfo process = new JavaProcessInfo(1234L, "sample.GcPulseTarget");

        Optional<Path> path = resolver.resolve(process);

        assertTrue(path.isPresent(), "path present");
        assertEquals(Path.of("logs", "gc-pulse-jit.log"), path.get(), "path");
    }

    private static void ignoreUnknownProcess() {
        JitLogPathResolver resolver = new JitLogPathResolver();
        JavaProcessInfo process = new JavaProcessInfo(1234L, "com.example.Main");

        assertTrue(resolver.resolve(process).isEmpty(), "unknown process");
    }

    private static void assertTrue(boolean condition, String label) {
        if (!condition) {
            throw new AssertionError(label + " expected true");
        }
    }

    private static void assertEquals(Path expected, Path actual, String label) {
        if (!expected.equals(actual)) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }
}
