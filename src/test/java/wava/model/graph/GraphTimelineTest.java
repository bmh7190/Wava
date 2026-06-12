package wava.model.graph;

public class GraphTimelineTest {
    public static void main(String[] args) {
        mapStartMiddleAndEnd();
        clampOutsideTimeline();
        mapSinglePointTimelineToLeft();
    }

    private static void mapStartMiddleAndEnd() {
        GraphTimeline timeline = new GraphTimeline(1000L, 2000L);

        assertEquals(10, timeline.calculateX(1000L, 10, 110), "start x");
        assertEquals(60, timeline.calculateX(1500L, 10, 110), "middle x");
        assertEquals(110, timeline.calculateX(2000L, 10, 110), "end x");
    }

    private static void clampOutsideTimeline() {
        GraphTimeline timeline = new GraphTimeline(1000L, 2000L);

        assertEquals(10, timeline.calculateX(500L, 10, 110), "before start");
        assertEquals(110, timeline.calculateX(2500L, 10, 110), "after end");
    }

    private static void mapSinglePointTimelineToLeft() {
        GraphTimeline timeline = new GraphTimeline(1000L, 1000L);

        assertEquals(10, timeline.calculateX(1000L, 10, 110), "single point");
    }

    private static void assertEquals(int expected, int actual, String label) {
        if (expected != actual) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }
}
