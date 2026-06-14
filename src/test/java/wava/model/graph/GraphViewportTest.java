package wava.model.graph;

public class GraphViewportTest {
    public static void main(String[] args) {
        createDefaultViewport();
        clampPosition();
        enableFollowLatestMovesToLatestPosition();
        movingPositionDisablesFollowLatest();
        shiftPosition();
    }

    private static void createDefaultViewport() {
        GraphViewport viewport = GraphViewport.defaultViewport();

        assertEquals(GraphDisplayRange.LAST_60_SECONDS, viewport.getRange(), "range");
        assertTrue(viewport.isFollowLatest(), "follow latest");
        assertEquals(GraphViewport.MAX_POSITION, viewport.getPosition(), "position");
    }

    private static void clampPosition() {
        GraphViewport lowViewport = new GraphViewport(GraphDisplayRange.LAST_60_SECONDS, false, -1);
        GraphViewport highViewport = new GraphViewport(GraphDisplayRange.LAST_60_SECONDS, false, 2000);

        assertEquals(GraphViewport.MIN_POSITION, lowViewport.getPosition(), "low position");
        assertEquals(GraphViewport.MAX_POSITION, highViewport.getPosition(), "high position");
    }

    private static void enableFollowLatestMovesToLatestPosition() {
        GraphViewport viewport = new GraphViewport(GraphDisplayRange.LAST_180_SECONDS, false, 250)
                .withFollowLatest(true);

        assertTrue(viewport.isFollowLatest(), "follow latest");
        assertEquals(GraphViewport.MAX_POSITION, viewport.getPosition(), "position");
    }

    private static void movingPositionDisablesFollowLatest() {
        GraphViewport viewport = GraphViewport.defaultViewport().withPosition(500);

        assertTrue(!viewport.isFollowLatest(), "follow latest");
        assertEquals(500, viewport.getPosition(), "position");
    }

    private static void shiftPosition() {
        GraphViewport viewport = new GraphViewport(GraphDisplayRange.LAST_60_SECONDS, false, 500);

        assertEquals(600, viewport.shiftPosition(100).getPosition(), "next position");
        assertEquals(GraphViewport.MIN_POSITION, viewport.shiftPosition(-600).getPosition(), "min position");
        assertEquals(GraphViewport.MAX_POSITION, viewport.shiftPosition(600).getPosition(), "max position");
    }

    private static void assertTrue(boolean condition, String label) {
        if (!condition) {
            throw new AssertionError(label + " expected true");
        }
    }

    private static void assertEquals(int expected, int actual, String label) {
        if (expected != actual) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }

    private static void assertEquals(GraphDisplayRange expected, GraphDisplayRange actual, String label) {
        if (expected != actual) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }
}
