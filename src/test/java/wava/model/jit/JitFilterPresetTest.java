package wava.model.jit;

public class JitFilterPresetTest {
    public static void main(String[] args) {
        exposePresetFilterText();
        identifyCurrentTargetPreset();
    }

    private static void exposePresetFilterText() {
        assertEquals("", JitFilterPreset.ALL.getFilterText(), "all filter");
        assertEquals("sample.", JitFilterPreset.SAMPLE_ONLY.getFilterText(), "sample filter");
        assertEquals("jdk.", JitFilterPreset.JDK_RUNTIME.getFilterText(), "jdk filter");
    }

    private static void identifyCurrentTargetPreset() {
        assertTrue(JitFilterPreset.CURRENT_TARGET.usesCurrentTarget(), "current target");
        assertTrue(!JitFilterPreset.SAMPLE_ONLY.usesCurrentTarget(), "sample only");
    }

    private static void assertEquals(String expected, String actual, String label) {
        if (!expected.equals(actual)) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }

    private static void assertTrue(boolean condition, String label) {
        if (!condition) {
            throw new AssertionError(label + " expected true");
        }
    }
}
