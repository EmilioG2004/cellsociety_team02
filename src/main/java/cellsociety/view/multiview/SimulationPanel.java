package cellsociety.view.multiview;

import static cellsociety.view.constants.BundleKeys.*;
import static cellsociety.view.constants.SimulationUiConstants.*;

import cellsociety.view.gridviews.GridPaneView;
import cellsociety.view.gridviews.GridView;
import cellsociety.view.SimulationView;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import cellsociety.Model.Grid.ViewGrid;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Lightweight render-only panel for a single simulation (generation label + grid).
 *
 *Purpose: enables multi-simulation dashboards without duplicating the full
 * {@link SimulationView} UI.
 *
 *Assumptions: the caller initializes the grid via {@link #setGrid(int, int)}
 * before calling {@link #render(ViewGrid)}.
 *
 *Dependencies: {@link GridView} implementations, JavaFX layout nodes, and
 * localized strings from {@link ResourceBundle}.
 *
 *Example usage:
 * Example:
 * SimulationPanel panel = new SimulationPanel(bundle);
 * panel.setGrid(rows, cols);
 * panel.render(sim.getViewGrid());
 *
 *
 *Other details: this panel exposes controls for loading, viewing metadata, and rotating
 * the grid. The controller supplies behavior via callbacks.
 *
 * @author Emilio Garcia
 */
public class SimulationPanel implements SimulationRenderer {

  private ResourceBundle myBundle;
  private int myGeneration;

  private final VBox myRoot;
  private final Label myGenerationLabel;
  private final Button myLoadButton;
  private final Button myRotateLeftButton;
  private final Button myRotateRightButton;
  private final Button myAboutButton;
  private final Button mySaveButton;
  private final Button myViewGraphButton;

  private final StackPane myGridContainer;

  private GridView myGridView;
  private Map<Integer, String> myStateColors = new HashMap<>();
  private BiConsumer<Integer, Integer> myOnCellClicked;

  /**
   * Creates a panel with localized labels and a blank grid container.
   *
   *Purpose: sets up the static header and grid container for later rendering.
   *
   *Assumptions: {@code bundle} contains the {@code button.loadsim} and
   * {@code label.generation} keys.
   *
   *Exceptions: {@link java.util.MissingResourceException} if required keys are missing.
   *
   *Return value: none.
   *
   * @param bundle localization bundle for UI text
   */
  public SimulationPanel(ResourceBundle bundle) {
    myBundle = bundle;
    myGeneration = 0;
    myRoot = buildRootContainer();
    myGenerationLabel = buildGenerationLabel();
    myLoadButton = buildButton(BUTTON_LOAD_SIM);
    myRotateLeftButton = buildButton(BUTTON_ROTATE_LEFT);
    myRotateRightButton = buildButton(BUTTON_ROTATE_RIGHT);
    myAboutButton = buildButton(BUTTON_ABOUT_CONFIG);
    mySaveButton = buildButton(BUTTON_SAVE_STATE);
    myViewGraphButton = buildButton(BUTTON_VIEW_GRAPH);
    myGridContainer = buildGridContainer();

    configureHandlers();
    myRoot.getChildren().addAll(buildHeader(), myGridContainer, buildRotationRow());
    VBox.setVgrow(myGridContainer, Priority.ALWAYS);
  }

  private VBox buildRootContainer() {
    VBox root = new VBox(SPACING_SMALL);
    root.setPadding(new Insets(SPACING_SMALL));
    root.setAlignment(Pos.CENTER);
    root.setFillWidth(true);
    return root;
  }

  private Label buildGenerationLabel() {
    Label generation = new Label(myBundle.getString(LABEL_GENERATION_UNSET));
    generation.setMaxWidth(Double.MAX_VALUE);
    generation.setAlignment(Pos.CENTER);
    return generation;
  }

  private Button buildButton(String key) {
    return new Button(myBundle.getString(key));
  }

  private StackPane buildGridContainer() {
    StackPane gridContainer = new StackPane();
    gridContainer.setPadding(new Insets(SPACING_SMALL));
    gridContainer.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    return gridContainer;
  }

  private void configureHandlers() {
    myRotateLeftButton.setOnAction(e -> rotateLeft());
    myRotateRightButton.setOnAction(e -> rotateRight());
  }

  private HBox buildHeader() {
    HBox header = new HBox(SPACING_SMALL, myGenerationLabel, myLoadButton, mySaveButton, myAboutButton);
    header.setAlignment(Pos.CENTER);
    HBox.setHgrow(myGenerationLabel, javafx.scene.layout.Priority.ALWAYS);
    return header;
  }

  private HBox buildRotationRow() {
    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);
    HBox lower = new HBox(SPACING_SMALL, myViewGraphButton, spacer, myRotateLeftButton, myRotateRightButton);
    lower.setAlignment(Pos.CENTER_LEFT);
    return lower;
  }

  /**
   * {@inheritDoc}
   *
   *Assumptions: rows and cols are positive.
   *
   *Exceptions: none.
   *
   *Return value: none.
   */
  @Override
  public void setGrid(int rows, int cols) {
    myGridView = new GridPaneView(rows, cols);
    myGridView.setStateColors(myStateColors);
    myGridView.setOnCellClicked(myOnCellClicked);
    Node gridNode = myGridView.getNode();
    myGridContainer.getChildren().setAll(gridNode);

    myGridView.bindToContainer(myGridContainer);
  }

  /**
   * {@inheritDoc}
   *
   *Assumptions: {@link #setGrid(int, int)} has been called.
   *
   *Exceptions: none.
   *
   *Return value: none.
   */
  @Override
  public void render(ViewGrid grid) {
    if (myGridView == null) return;
    myGridView.render(grid);
  }

  /**
   * {@inheritDoc}
   *
   *Assumptions: generation is non-negative.
   *
   *Exceptions: none.
   *
   *Return value: none.
   */
  @Override
  public void setGeneration(int generation) {
    myGeneration = generation;
    myGenerationLabel.setText(MessageFormat.format(
        myBundle.getString(LABEL_GENERATION), generation));
  }

  /**
   * {@inheritDoc}
   *
   *Assumptions: none.
   *
   *Exceptions: none.
   *
   * @return panel root node
   */
  @Override
  public Node getNode() {
    return myRoot;
  }

  /**
   * Registers the callback for the panel's "Load" button.
   *
   *Purpose: allows the controller to open a config chooser for this slot.
   *
   *Assumptions: {@code action} is non-null.
   *
   *Exceptions: none.
   *
   *Return value: none.
   *
   * @param action callback executed when the load button is pressed
   */
  public void setOnLoad(Runnable action) {
    myLoadButton.setOnAction(e -> action.run());
  }

  /**
   * Registers the callback for the panel's "About" button.
   *
   * @param action callback executed when the about button is pressed
   */
  public void setOnAbout(Runnable action) {
    myAboutButton.setOnAction(e -> action.run());
  }

  public void setOnSave(Runnable action) { mySaveButton.setOnAction(e -> action.run()); }

  public void setOnViewGraph(Runnable action) { myViewGraphButton.setOnAction(e -> action.run()); }

  /**
   * Registers a callback for clicks on this panel's grid cells.
   *
   * @param handler callback receiving clicked (row, col)
   */
  public void setOnCellClicked(BiConsumer<Integer, Integer> handler) {
    myOnCellClicked = handler;
  }

  /**
   * Toggles outline visibility for this panel's grid.
   *
   *Purpose: mirror the global outline toggle in multi-sim mode.
   *
   *Assumptions: {@code grid} dimensions match the current grid.
   *
   *Exceptions: none.
   *
   *Return value: none.
   *
   * @param grid read-only grid used to update outlines
   */
  public void toggleOutline(ViewGrid grid) {
    if (myGridView == null) return;
    myGridView.toggleOutline(grid);
  }

  /**
   * Rotates this panel's grid 90 degrees counterclockwise.
   */
  public void rotateLeft() {
    if (myGridView == null) throw new IllegalStateException(ERROR_MSG_NO_SIMULATION_LOADED);
    myGridView.rotateLeft();
  }

  /**
   * Rotates this panel's grid 90 degrees clockwise.
   */
  public void rotateRight() {
    if (myGridView == null) throw new IllegalStateException(ERROR_MSG_NO_SIMULATION_LOADED);
    myGridView.rotateRight();
  }

  public void applyLanguage(ResourceBundle bundle){
    myBundle = bundle;
    myGenerationLabel.setText(MessageFormat.format(
        myBundle.getString(LABEL_GENERATION), myGeneration));
    myLoadButton.setText(myBundle.getString(BUTTON_LOAD_SIM));
    mySaveButton.setText(myBundle.getString(BUTTON_SAVE_STATE));
    myAboutButton.setText(myBundle.getString(BUTTON_ABOUT_CONFIG));
    myViewGraphButton.setText(myBundle.getString(BUTTON_VIEW_GRAPH));
    myRotateLeftButton.setText(myBundle.getString(BUTTON_ROTATE_LEFT));
    myRotateRightButton.setText(myBundle.getString(BUTTON_ROTATE_RIGHT));
  }

  public void applyStateColors(Map<Integer, String> stateColors) {
    myStateColors = new HashMap<>(stateColors);
    if (myGridView != null) {
      myGridView.setStateColors(myStateColors);
    }
  }
}
