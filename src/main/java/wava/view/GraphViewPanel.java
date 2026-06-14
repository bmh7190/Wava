package wava.view;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.function.Consumer;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import wava.model.graph.GraphDisplayRange;
import wava.model.graph.GraphViewport;

public class GraphViewPanel extends JPanel {
    private final JComboBox<GraphDisplayRange> windowComboBox;
    private final JSlider timelineSlider;
    private final JCheckBox followLatestCheckBox;
    private boolean updatingControls;

    public GraphViewPanel() {
        super(new GridBagLayout());
        UiStyle.applyPanelStyle(this, "Graph View");

        windowComboBox = new JComboBox<>(GraphDisplayRange.values());
        timelineSlider = new JSlider(GraphViewport.MIN_POSITION, GraphViewport.MAX_POSITION, GraphViewport.MAX_POSITION);
        followLatestCheckBox = new JCheckBox("Follow Latest", true);

        UiStyle.applyComboBoxStyle(windowComboBox);
        UiStyle.applySliderStyle(timelineSlider);
        UiStyle.applyCheckBoxStyle(followLatestCheckBox);
        timelineSlider.setToolTipText("Move the visible graph time window");
        followLatestCheckBox.setToolTipText("Keep the graph window attached to the latest sample");

        addControl(new JLabel("Window"), 0, false, 0.0);
        addControl(windowComboBox, 1, false, 0.0);
        addControl(new JLabel("Timeline"), 2, false, 0.0);
        addControl(timelineSlider, 3, true, 1.0);
        addControl(followLatestCheckBox, 4, false, 0.0);
        setGraphNavigationState(GraphViewport.defaultViewport());
    }

    private void addControl(Component component, int gridX, boolean fillHorizontal, double weightX) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = gridX;
        constraints.gridy = 0;
        constraints.fill = fillHorizontal ? GridBagConstraints.HORIZONTAL : GridBagConstraints.NONE;
        constraints.weightx = weightX;
        constraints.insets = new Insets(0, gridX == 0 ? 0 : 12, 0, 0);
        constraints.anchor = GridBagConstraints.CENTER;
        add(component, constraints);
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

    public void setGraphNavigationState(GraphViewport viewport) {
        updatingControls = true;
        windowComboBox.setSelectedItem(viewport.getRange());
        followLatestCheckBox.setSelected(viewport.isFollowLatest());
        timelineSlider.setValue(viewport.getPosition());
        followLatestCheckBox.setEnabled(true);
        timelineSlider.setEnabled(!viewport.isFollowLatest());
        updatingControls = false;
    }
}
