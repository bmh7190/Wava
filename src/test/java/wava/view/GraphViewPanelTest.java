package wava.view;

import wava.model.graph.GraphViewport;

public class GraphViewPanelTest {
    public static void main(String[] args) {
        createGraphViewPanel();
        updateGraphNavigationState();
        bindGraphActions();
    }

    private static void createGraphViewPanel() {
        GraphViewPanel panel = new GraphViewPanel();

        assertTrue(panel.getComponentCount() > 0, "component count");
    }

    private static void updateGraphNavigationState() {
        GraphViewPanel panel = new GraphViewPanel();

        panel.setGraphNavigationState(GraphViewport.defaultViewport().withFollowLatest(false).withPosition(500));
        panel.setGraphNavigationState(GraphViewport.defaultViewport());
    }

    private static void bindGraphActions() {
        GraphViewPanel panel = new GraphViewPanel();

        panel.setWindowAction(range -> { });
        panel.setFollowLatestAction(followLatest -> { });
        panel.setTimelineAction(position -> { });
        panel.setPreviousAction(() -> { });
        panel.setNextAction(() -> { });
    }

    private static void assertTrue(boolean condition, String label) {
        if (!condition) {
            throw new AssertionError(label + " expected true");
        }
    }
}
