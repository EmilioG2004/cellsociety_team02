package cellsociety.view;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.ReadOnlyGrid;
import java.text.MessageFormat;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * A lightweight, render-only view for showing a simulation (generation label + grid).
 * Contains no buttons, dialogs, or settings.
 *
 * Purpose:
 * Allows multiple simulations to be displayed concurrently without duplicating
 * the full interactive SimulationView.
 *
 * @author Emilio Garcia
 */
public class SimulationPanel implements SimulationRenderer {

  private static final double SPACING = 8;

  private final ResourceBundle myBundle;

  private final VBox myRoot;
  private final Label myGenerationLabel;

  private final StackPane myGridContainer;

  private GridView myGridView;

  public SimulationPanel(ResourceBundle bundle) {
    myBundle = bundle;

    myRoot = new VBox(SPACING);
    myRoot.setPadding(new Insets(8));
    myRoot.setAlignment(Pos.CENTER);

    myGenerationLabel = new Label();
    myGenerationLabel.setMaxWidth(Double.MAX_VALUE);
    myGenerationLabel.setAlignment(Pos.CENTER);

    myGridContainer = new StackPane();
    myGridContainer.setPadding(new Insets(8));

    myRoot.getChildren().addAll(myGenerationLabel, myGridContainer);
  }

  @Override
  public void setGrid(int rows, int cols) {
    myGridView = new GridPaneView(rows, cols);
    Node gridNode = myGridView.getNode();
    myGridContainer.getChildren().setAll(gridNode);

    // match your existing pattern in GridPaneView usage
    myGridView.bindToContainer((Region) myGridContainer);
  }

  @Override
  public void render(ReadOnlyGrid grid, Map<CellState, String> stateColors) {
    if (myGridView == null) return;
    myGridView.render(grid, stateColors);
  }

  @Override
  public void setGeneration(int generation) {
    myGenerationLabel.setText(MessageFormat.format(
        myBundle.getString("label.generation"), generation));
  }

  @Override
  public Node getNode() {
    return myRoot;
  }
}