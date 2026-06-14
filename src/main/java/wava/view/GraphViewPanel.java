package wava.view;

import java.awt.FlowLayout;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import wava.model.graph.GraphDisplayRange;
import wava.model.graph.GraphViewport;

public class GraphViewPanel extends JPanel {
    private final JComboBox<GraphDisplayRange> windowComboBox;
    private final JButton previousButton;
    private final JButton nextButton;
    private final JSlider timelineSlider;
    private final JCheckBox followLatestCheckBox;
    private boolean updatingControls;

    public GraphViewPanel() {
        super(new FlowLayout(FlowLayout.LEFT, 8, 6));
        UiStyle.applyPanelStyle(this, "Graph View");

        windowComboBox = new JComboBox<>(GraphDisplayRange.values());
        previousButton = new JButton("<");
        nextButton = new JButton(">");
        timelineSlider = new JSlider(GraphViewport.MIN_POSITION, GraphViewport.MAX_POSITION, GraphViewport.MAX_POSITION);
        followLatestCheckBox = new JCheckBox("Follow Latest", true);

        UiStyle.applyComboBoxStyle(windowComboBox);
        UiStyle.applyButtonStyle(previousButton);
        UiStyle.applyButtonStyle(nextButton);
        UiStyle.applySliderStyle(timelineSlider);
        UiStyle.applyCheckBoxStyle(followLatestCheckBox);
        previousButton.setToolTipText("Move the graph window backward");
        nextButton.setToolTipText("Move the graph window forward");
        timelineSlider.setToolTipText("Move the visible graph time window");
        followLatestCheckBox.setToolTipText("Keep the graph window attached to the latest sample");

        add(new JLabel("Window"));
        add(windowComboBox);
        add(previousButton);
        add(new JLabel("Timeline"));
        add(timelineSlider);
        add(nextButton);
        add(followLatestCheckBox);
        setGraphNavigationState(GraphViewport.defaultViewport());
    }

    public void setWindowAction(Consumer<GraphDisplayRange> listener) {
        windowComboBox.addActionListener(event -> {
            if (updatingControls) {
                return;
            }
            GraphDisplayRange selectedRange = (GraphDisplayRange) windowComboBox.getSelectedItem();
            if (selectedRange != null) {
                listener.accept(selectedRange);
            }
        });
    }

    public void setFollowLatestAction(Consumer<Boolean> listener) {
        followLatestCheckBox.addActionListener(event -> {
            if (!updatingControls) {
                listener.accept(followLatestCheckBox.isSelected());
            }
        });
    }

    public void setTimelineAction(Consumer<Integer> listener) {
        timelineSlider.addChangeListener(event -> {
            if (!updatingControls) {
                listener.accept(timelineSlider.getValue());
            }
        });
    }

    public void setPreviousAction(Runnable action) {
        previousButton.addActionListener(event -> action.run());
    }

    public void setNextAction(Runnable action) {
        nextButton.addActionListener(event -> action.run());
    }

    public void setGraphNavigationState(GraphViewport viewport) {
        updatingControls = true;
        windowComboBox.setSelectedItem(viewport.getRange());
        followLatestCheckBox.setSelected(viewport.isFollowLatest());
        timelineSlider.setValue(viewport.getPosition());
        followLatestCheckBox.setEnabled(true);
        timelineSlider.setEnabled(!viewport.isFollowLatest());
        previousButton.setEnabled(!viewport.isFollowLatest());
        nextButton.setEnabled(!viewport.isFollowLatest());
        updatingControls = false;
    }
}
