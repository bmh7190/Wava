package sample;

public class WarmupTargetCompileTest {
    public static void main(String[] args) {
        assertClassExists();
        assertSampleTargetsExist();
        assertLauncherTargets();
    }

    private static void assertClassExists() {
        String className = WarmupTarget.class.getName();
        if (!"sample.WarmupTarget".equals(className)) {
            throw new AssertionError("Unexpected class name " + className);
        }
    }

    private static void assertSampleTargetsExist() {
        assertClassName("sample.CpuWarmupTarget", CpuWarmupTarget.class.getName());
        assertClassName("sample.GcPulseTarget", GcPulseTarget.class.getName());
        assertClassName("sample.SteadyStateTarget", SteadyStateTarget.class.getName());
        assertClassName("sample.BurstyMixedTarget", BurstyMixedTarget.class.getName());
        assertClassName("sample.SampleTargetLauncher", SampleTargetLauncher.class.getName());
    }

    private static void assertLauncherTargets() {
        if (SampleTargetLauncher.defaultTargetClassNames().size() != 4) {
            throw new AssertionError("Unexpected launcher target count");
        }
        if (!SampleTargetLauncher.defaultTargetClassNames().contains("sample.GcPulseTarget")) {
            throw new AssertionError("Launcher should include GC target");
        }
    }

    private static void assertClassName(String expected, String actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected " + expected + " but was " + actual);
        }
    }
}
