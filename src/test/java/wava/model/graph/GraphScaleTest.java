package wava.model.graph;

import java.util.Arrays;
import java.util.Collections;

public class GraphScaleTest {
    public static void main(String[] args) {
        createFixedScale();
        createAutoScaleWithHeadroom();
        createAutoScaleForEmptyValues();
        normalizeValues();
        calculateTickValues();
    }

    private static void createFixedScale() {
        GraphScale scale = GraphScale.fixed(0.0, 100.0);

        assertEquals(0.0, scale.getMin(), "fixed min");
        assertEquals(100.0, scale.getMax(), "fixed max");
    }

    private static void createAutoScaleWithHeadroom() {
        GraphScale scale = GraphScale.auto(Arrays.asList(10.0, 20.0, 30.0));

        assertEquals(0.0, scale.getMin(), "auto min");
        assertEquals(36.0, scale.getMax(), "auto max");
    }

    private static void createAutoScaleForEmptyValues() {
        GraphScale scale = GraphScale.auto(Collections.emptyList());

        assertEquals(0.0, scale.getMin(), "empty min");
        assertEquals(1.0, scale.getMax(), "empty max");
    }

    private static void normalizeValues() {
        GraphScale scale = GraphScale.fixed(0.0, 100.0);

        assertEquals(0.0, scale.normalize(-10.0), "low clamp");
        assertEquals(0.5, scale.normalize(50.0), "middle ratio");
        assertEquals(1.0, scale.normalize(120.0), "high clamp");
    }

    private static void calculateTickValues() {
        GraphScale scale = GraphScale.fixed(0.0, 100.0);

        assertEquals(0.0, scale.getTickValue(0, 5), "first tick");
        assertEquals(50.0, scale.getTickValue(2, 5), "middle tick");
        assertEquals(100.0, scale.getTickValue(4, 5), "last tick");
    }

    private static void assertEquals(double expected, double actual, String label) {
        if (Double.compare(expected, actual) != 0) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }
}
