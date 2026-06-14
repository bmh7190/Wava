package wava.model.graph;

public class GraphViewport {
    public static final int MIN_POSITION = 0;
    public static final int MAX_POSITION = 1000;

    private final GraphDisplayRange range;
    private final boolean followLatest;
    private final int position;

    public GraphViewport(GraphDisplayRange range, boolean followLatest, int position) {
        this.range = range;
        this.followLatest = followLatest;
        this.position = clampPosition(position);
    }

    public static GraphViewport defaultViewport() {
        return new GraphViewport(GraphDisplayRange.LAST_60_SECONDS, true, MAX_POSITION);
    }

    public GraphDisplayRange getRange() {
        return range;
    }

    public boolean isFollowLatest() {
        return followLatest;
    }

    public int getPosition() {
        return position;
    }

    public GraphViewport withRange(GraphDisplayRange nextRange) {
        return new GraphViewport(nextRange, followLatest, position);
    }

    public GraphViewport withFollowLatest(boolean nextFollowLatest) {
        int nextPosition = nextFollowLatest ? MAX_POSITION : position;
        return new GraphViewport(range, nextFollowLatest, nextPosition);
    }

    public GraphViewport withPosition(int nextPosition) {
        return new GraphViewport(range, false, nextPosition);
    }

    public GraphViewport shiftPosition(int delta) {
        return withPosition(position + delta);
    }

    private int clampPosition(int nextPosition) {
        if (nextPosition < MIN_POSITION) {
            return MIN_POSITION;
        }
        if (nextPosition > MAX_POSITION) {
            return MAX_POSITION;
        }
        return nextPosition;
    }
}
