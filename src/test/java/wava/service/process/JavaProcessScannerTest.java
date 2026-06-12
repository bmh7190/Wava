package wava.service.process;

import java.util.Arrays;
import java.util.List;
import wava.model.process.JavaProcessInfo;

public class JavaProcessScannerTest {
    public static void main(String[] args) {
        parseProcessLines();
        ignoreJpsProcess();
        ignoreInvalidLines();
    }

    private static void parseProcessLines() {
        JavaProcessScanner scanner = new JavaProcessScanner();
        List<JavaProcessInfo> processes = scanner.parseLines(Arrays.asList(
                "12345 com.example.DemoApplication",
                "23456 org.springframework.boot.loader.JarLauncher"));

        assertEquals(2, processes.size(), "process count");
        assertEquals(12345L, processes.get(0).getPid(), "first pid");
        assertEquals("com.example.DemoApplication", processes.get(0).getDisplayName(), "first name");
        assertEquals("12345  com.example.DemoApplication", processes.get(0).formatListItem(), "list item");
    }

    private static void ignoreJpsProcess() {
        JavaProcessScanner scanner = new JavaProcessScanner();
        List<JavaProcessInfo> processes = scanner.parseLines(Arrays.asList(
                "10000 jdk.jcmd/sun.tools.jps.Jps",
                "10001 Jps",
                "10002 com.example.Target"));

        assertEquals(1, processes.size(), "filtered process count");
        assertEquals("com.example.Target", processes.get(0).getDisplayName(), "remaining process");
    }

    private static void ignoreInvalidLines() {
        JavaProcessScanner scanner = new JavaProcessScanner();
        List<JavaProcessInfo> processes = scanner.parseLines(Arrays.asList(
                "",
                "not-a-pid com.example.Invalid",
                "33333",
                "44444 com.example.Valid"));

        assertEquals(1, processes.size(), "valid process count");
        assertEquals(44444L, processes.get(0).getPid(), "valid pid");
    }

    private static void assertEquals(long expected, long actual, String label) {
        if (expected != actual) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }

    private static void assertEquals(String expected, String actual, String label) {
        if (!expected.equals(actual)) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }
}
