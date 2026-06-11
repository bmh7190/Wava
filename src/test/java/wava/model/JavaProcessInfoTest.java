package wava.model;

public class JavaProcessInfoTest {
    public static void main(String[] args) {
        formatListItem();
        toStringUsesListFormat();
    }

    private static void formatListItem() {
        JavaProcessInfo process = new JavaProcessInfo(12345L, "com.example.Target");

        assertEquals("12345  com.example.Target", process.formatListItem(), "list item");
    }

    private static void toStringUsesListFormat() {
        JavaProcessInfo process = new JavaProcessInfo(54321L, "sample.Main");

        assertEquals("54321  sample.Main", process.toString(), "string format");
    }

    private static void assertEquals(String expected, String actual, String label) {
        if (!expected.equals(actual)) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }
}
