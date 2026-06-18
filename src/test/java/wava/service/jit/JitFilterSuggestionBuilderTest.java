package wava.service.jit;

import java.util.List;
import wava.model.jit.JitEvent;
import wava.model.jit.JitFilterSuggestion;
import wava.model.process.JavaProcessInfo;

public class JitFilterSuggestionBuilderTest {
    public static void main(String[] args) {
        buildCurrentPackageAndTopMethodSuggestions();
        respectSuggestionLimit();
    }

    private static void buildCurrentPackageAndTopMethodSuggestions() {
        JitFilterSuggestionBuilder builder = new JitFilterSuggestionBuilder();
        JavaProcessInfo process = new JavaProcessInfo(1L, "sample.CpuWarmupTarget");

        List<JitFilterSuggestion> suggestions = builder.build(process, events(), 8);

        assertEquals("CpuWarmupTarget", suggestions.get(0).getFilterText(), "current target filter");
        assertEquals("sample.", suggestions.get(1).getFilterText(), "sample package filter");
        assertEquals("java.", suggestions.get(2).getFilterText(), "java package filter");
        assertEquals("sample.CpuWarmupTarget::calculate", suggestions.get(3).getFilterText(), "top method filter");
    }

    private static void respectSuggestionLimit() {
        JitFilterSuggestionBuilder builder = new JitFilterSuggestionBuilder();

        List<JitFilterSuggestion> suggestions = builder.build(null, events(), 2);

        assertEquals(2, suggestions.size(), "limited suggestion count");
    }

    private static List<JitEvent> events() {
        return List.of(
                event(1, "sample.CpuWarmupTarget::calculate"),
                event(2, "sample.CpuWarmupTarget::calculate"),
                event(3, "java.lang.String::hashCode"),
                event(4, "sample.CpuWarmupTarget::run"));
    }

    private static JitEvent event(int compileId, String methodName) {
        return new JitEvent(compileId, compileId, "3", methodName, methodName);
    }

    private static void assertEquals(int expected, int actual, String label) {
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
