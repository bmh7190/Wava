package sample;

public class WarmupTargetCompileTest {
    public static void main(String[] args) {
        assertClassExists();
    }

    private static void assertClassExists() {
        String className = WarmupTarget.class.getName();
        if (!"sample.WarmupTarget".equals(className)) {
            throw new AssertionError("Unexpected class name " + className);
        }
    }
}
