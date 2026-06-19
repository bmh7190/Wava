package wava.service.jit;

import wava.model.jit.JitFilterPreset;
import wava.model.process.JavaProcessInfo;

public class JitDefaultFilterPolicyTest {
    public static void main(String[] args) {
        classProcessUsesCurrentTarget();
        jarProcessUsesAllPreset();
        warProcessUsesAllPreset();
        nullProcessUsesAllPreset();
    }

    private static void classProcessUsesCurrentTarget() {
        JitDefaultFilterPolicy policy = new JitDefaultFilterPolicy();

        JitDefaultFilter filter = policy.resolve(new JavaProcessInfo(1L, "sample.WarmupTarget"));

        assertEquals(JitFilterPreset.CURRENT_TARGET, filter.getPreset(), "preset");
        assertEquals("WarmupTarget", filter.getCurrentTargetFilter(), "filter");
    }

    private static void jarProcessUsesAllPreset() {
        JitDefaultFilterPolicy policy = new JitDefaultFilterPolicy();

        JitDefaultFilter filter = policy.resolve(new JavaProcessInfo(
                1L,
                ".\\build\\libs\\spring-warmup-lab-0.0.1-SNAPSHOT.jar"));

        assertEquals(JitFilterPreset.ALL, filter.getPreset(), "preset");
        assertEquals("", filter.getCurrentTargetFilter(), "filter");
    }

    private static void warProcessUsesAllPreset() {
        JitDefaultFilterPolicy policy = new JitDefaultFilterPolicy();

        JitDefaultFilter filter = policy.resolve(new JavaProcessInfo(1L, "build/libs/app.war"));

        assertEquals(JitFilterPreset.ALL, filter.getPreset(), "preset");
        assertEquals("", filter.getCurrentTargetFilter(), "filter");
    }

    private static void nullProcessUsesAllPreset() {
        JitDefaultFilterPolicy policy = new JitDefaultFilterPolicy();

        JitDefaultFilter filter = policy.resolve(null);

        assertEquals(JitFilterPreset.ALL, filter.getPreset(), "preset");
        assertEquals("", filter.getCurrentTargetFilter(), "filter");
    }

    private static void assertEquals(JitFilterPreset expected, JitFilterPreset actual, String label) {
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
