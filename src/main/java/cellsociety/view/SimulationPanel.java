package cellsociety.view;

import static cellsociety.view.constants.BundleKeys.*;
import static cellsociety.view.constants.SimulationUiConstants.*;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import cellsociety.Model.Grid.GridViewData;
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
 * <p>Purpose: enables multi-simulation dashboards without duplicating the full
 * {@link SimulationView} UI.</p>
 *
 * <p>Assumptions: the caller initializes the grid via {@link #setGrid(int, int)}
 * before calling {@link #render(GridViewData)}.</p>
 *
 * <p>Dependencies: {@link GridView} implementations, JavaFX layout nodes, and
 * localized strings from {@link ResourceBundle}.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * SimulationPanel panel = new SimulationPanel(bundle);
 * panel.setGrid(rows, cols);
 * panel.render(sim.getGridViewData());
 * }</pre>
 *
 * <p>Other details: this panel exposes controls for loading, viewing metadata, and rotating
 * the grid. The controller supplies behavior via callbacks.</p>
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

  private final StackPane myGridContainer;

  private GridView myGridView;
  private BiConsumer<Integer, Integer> myOnCellClicked;

  /**
   * Creates a panel with localized labels and a blank grid container.
   *
   * <p>Purpose: sets up the static header and grid container for later rendering.</p>
   *
   * <p>Assumptions: {@code bundle} contains the {@code button.loadsim} and
   * {@code label.generation} keys.</p>
   *
   * <p>Exceptions: {@link java.util.MissingResourceException} if required keys are missing.</p>
   *
   * <p>Return value: none.</p>
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
    HBox lower = new HBox(SPACING_SMALL, myRotateLeftButton, myRotateRightButton);
    lower.setAlignment(Pos.CENTER);
    return lower;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Assumptions: rows and cols are positive.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   */
  @Override
  public void setGrid(int rows, int cols) {
    myGridView = new GridPaneView(rows, cols);
    myGridView.setOnCellClicked(myOnCellClicked);
    Node gridNode = myGridView.getNode();
    myGridContainer.getChildren().setAll(gridNode);

    myGridView.bindToContainer(myGridContainer);
  }

  /**
   * {@inheritDoc}
   *
   * <p>Assumptions: {@link #setGrid(int, int)} has been called.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   */
  @Override
  public void render(GridViewData grid) {
    if (myGridView == null) return;
    myGridView.render(grid);
  }

  /**
   * {@inheritDoc}
   *
   * <p>Assumptions: generation is non-negative.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
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
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
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
   * <p>Purpose: allows the controller to open a config chooser for this slot.</p>
   *
   * <p>Assumptions: {@code action} is non-null.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
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
   * <p>Purpose: mirror the global outline toggle in multi-sim mode.</p>
   *
   * <p>Assumptions: {@code grid} dimensions match the current grid.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   *
   * @param grid read-only grid used to update outlines
   */
  public void toggleOutline(GridViewData grid) {
    if (myGridView == null) return;
    myGridView.toggleOutline(grid);
  }

  /**
   * Rotates this panel's grid 90 degrees counterclockwise.
   */
  public void rotateLeft() {
    if (myGridView == null) throw new IllegalStateException("No simulation loaded.");
    myGridView.rotateLeft();
  }

  /**
   * Rotates this panel's grid 90 degrees clockwise.
   */
  public void rotateRight() {
    if (myGridView == null) throw new IllegalStateException("No simulation loaded.");
    myGridView.rotateRight();
  }

  public void applyLanguage(ResourceBundle bundle){
    myBundle = bundle;
    myGenerationLabel.setText(MessageFormat.format(
        myBundle.getString(LABEL_GENERATION), myGeneration));
    myLoadButton.setText(myBundle.getString(BUTTON_LOAD_SIM));
    mySaveButton.setText(myBundle.getString(BUTTON_SAVE_STATE));
    myAboutButton.setText(myBundle.getString(BUTTON_ABOUT_CONFIG));
    myRotateLeftButton.setText(myBundle.getString(BUTTON_ROTATE_LEFT));
    myRotateRightButton.setText(myBundle.getString(BUTTON_ROTATE_RIGHT));
  }
}
