package cellsociety.View;

import java.io.File;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

/**
 * Builds and exposes the main JavaFX UI layout for the Cell Society application.
 *
 * Purpose:
 * This class creates the visual structure of the application (title, control buttons,
 * configuration upload button, and a central region for displaying a grid). It also
 * provides accessors for UI controls so that a controller can attach event handlers
 * without the view needing to know simulation logic.
 *
 * Assumptions:
 * - This class is used within a JavaFX Application and will be attached to a Scene.
 * - A GridView will be provided through setGrid before the simulation is expected to render.
 * - The UI is primarily controlled externally (e.g., by SimulationController) through the
 *   provided getters for buttons and the grid view.
 *
 * Dependencies:
 * - JavaFX layout and control classes for building the UI.
 * - FileChooser and Window for selecting configuration files.
 * - GridView and GridPaneView for displaying the simulation grid.
 *
 * Example usage:
 * SimulationView view = new SimulationView();
 * Scene scene = new Scene(view.getRoot(), 1200, 750);
 * primaryStage.setScene(scene);
 * primaryStage.show();
 *
 * SimulationController controller = new SimulationController(view);
 *
 * @author Emilio Garcia
 */
public class SimulationView {

  private final BorderPane myRoot = new BorderPane();

  private final Label myTitle = new Label("Cellular Automata Simulator");
  private final Button myUploadButton = new Button("Upload XML Configuartion");
  private final Button myForwardButton = new Button("Step Forward");
  private final Button myStartButton = new Button("Start Simulation");
  private final Button myPauseButton = new Button("Pause Simulation");
  private final Button mySpeedUpButton = new Button("Speed Up Simulation");
  private final Button mySlowDownButton = new Button("Slow Down Simulation");
  private final Button mySaveButton = new Button("Save State");
  private final Label mySpeedLabel = new Label("Current Speed: 1x");
  private final Button myResetButton = new Button("Reset Grid");
  private final Button myBackButton = new Button("Step Backward");

  private StackPane centerPane;

  private GridView myGridView;

  /**
   * Constructs the main application view and builds the top UI components.
   *
   * Purpose:
   * Initializes the layout container and creates the title/control regions so the view
   * is ready to be placed into a Scene.
   *
   * Assumptions:
   * - This view will eventually be attached to a Scene before operations that require a Window
   *   (such as opening a FileChooser).
   */
  public SimulationView() {
    buildTop();
    myRoot.setPadding(new Insets(12));
    myRoot.setStyle("-fx-background-color: #f6f7fb;");
  }

  /**
   * Builds the top section of the UI: title, upload row, and control bar.
   *
   * Purpose:
   * Creates and arranges the UI elements that remain visible regardless of which simulation
   * is currently loaded.
   *
   * Assumptions:
   * - Buttons created as fields are available for layout and later wiring by a controller.
   */
  private void buildTop() {
    myTitle.setFont(Font.font(28));
    VBox.setMargin(myTitle, new Insets(8, 0, 6, 0));
    myTitle.setMaxWidth(Double.MAX_VALUE);
    myTitle.setAlignment(Pos.CENTER);

    HBox myUploadRow = new HBox(myUploadButton);
    myUploadRow.setAlignment(Pos.CENTER_LEFT);
    myUploadRow.setPadding(new Insets(6, 0, 10, 0));

    HBox myControlBar = buildControlBar();
    VBox myTopBox = new VBox(myTitle, myUploadRow, myControlBar);
    myTopBox.setSpacing(8);
    myRoot.setTop(myTopBox);
  }

  /**
   * Builds the control bar containing simulation controls and status labels.
   *
   * Purpose:
   * Groups simulation action buttons into a single consistent layout region that a controller
   * can wire to simulation behavior.
   *
   * Assumptions:
   * - Visual styling is applied via inline CSS strings and is expected to be supported by JavaFX.
   *
   * @return an HBox containing the simulation control UI elements
   */
  private HBox buildControlBar() {
    HBox leftBox =
        new HBox(
            12,
            myStartButton,
            myPauseButton,
            mySpeedUpButton,
            mySlowDownButton,
            mySaveButton,
            myResetButton,
            myForwardButton,
            myBackButton,
            mySpeedLabel);

    leftBox.setAlignment(Pos.CENTER_LEFT);

    Label configLabel = new Label("Configuration: ");
    HBox rightBox = new HBox(10, configLabel);

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    HBox bar = new HBox(12, leftBox, spacer, rightBox);
    bar.setAlignment(Pos.CENTER_LEFT);
    bar.setPadding(new Insets(14));
    bar.setMinHeight(64);

    bar.setStyle(
        """
            -fx-background-color: white;
            -fx-border-color: black;
        """);

    myStartButton.setStyle(
        """
            -fx-background-color: black;
            -fx-text-fill: white;
        """);

    return bar;
  }

  /**
   * Builds or refreshes the center section of the UI that displays the grid view.
   *
   * Purpose:
   * Places the current GridView's Node into a styled "card" container so that
   * the grid is visually centered and consistently framed within the application.
   *
   * Assumptions:
   * - myGridView is non-null when this method is invoked.
   * - The GridView's node is safe to insert into the JavaFX scene graph.
   */
  private void buildCenter() {
    if (myGridView == null) return;

    if (centerPane == null) {
      centerPane = new StackPane();
      centerPane.setPadding(new Insets(18, 0, 18, 0));
      myRoot.setCenter(centerPane);
    }

    StackPane card = new StackPane();
    card.setPadding(new Insets(18));
    card.setPrefSize(760, 520);
    card.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

    card.setStyle(
        """
      -fx-background-color: white;
      -fx-border-color: black;
  """);

    Node gridNode = myGridView.getNode();
    card.getChildren().setAll(gridNode);
    centerPane.getChildren().setAll(card);
  }

  /**
   * Opens a file chooser dialog configured to select simulation configuration files.
   *
   * Purpose:
   * Provides a view-layer method for selecting a configuration file without requiring the
   * controller to construct its own JavaFX FileChooser.
   *
   * Assumptions:
   * - If this view is not attached to a Scene/Window, the dialog may not have an owner window.
   *
   * @return the selected configuration file, or null if the user cancels the dialog
   */
  public File showOpenConfigDialog() {
    FileChooser chooser = new FileChooser();
    chooser.setTitle("Open Simulation Configuration");
    chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));

    Window w = myRoot.getScene() == null ? null : myRoot.getScene().getWindow();
    return chooser.showOpenDialog(w);
  }

  /**
   * Opens a file chooser dialog configured to select simulation configuration files.
   *
   * Purpose:
   * Provides a view-layer method for saving a configuration file without requiring the controller to
   * construct its own FileChooser.
   *
   * Assumptions:
   * - If this view is not attached to a Scene/Window, the dialog may not have an owner window.
   *
   * @return the saved configuration file
   */
  public File showSaveConfigDialog(){
    FileChooser chooser = new FileChooser();
    chooser.setTitle("Save Simulation Configuration");
    chooser.getExtensionFilters().add(new ExtensionFilter("XML Files", "*.xml"));
    chooser.setInitialFileName("simulation_snapshot.xml");

    Window w = myRoot.getScene() == null ? null : myRoot.getScene().getWindow();

    return chooser.showSaveDialog(w);
  }

  /**
   * Returns the root node for this view.
   *
   * Purpose:
   * Allows application code to place this view into a Scene without exposing the internal layout.
   *
   * @return the root Parent node of this view
   */
  public Parent getRoot() {
    return myRoot;
  }

  /**
   * Provides access to the Upload button so a controller can attach behavior.
   *
   * @return the button used to open a configuration file
   */
  public Button getMyUploadButton() {
    return myUploadButton;
  }

  /**
   * Provides access to the Start button so a controller can attach behavior.
   *
   * @return the button used to start the simulation
   */
  public Button getMyStartButton() {
    return myStartButton;
  }

  /**
   * Provides access to the Pause button so a controller can attach behavior.
   *
   * @return the button used to pause the simulation
   */
  public Button getMyPauseButton() {
    return myPauseButton;
  }

  /**
   * Provides access to the Speed Up button so a controller can attach behavior.
   *
   * @return the button used to increase simulation speed
   */
  public Button getMySpeedUpButton() {
    return mySpeedUpButton;
  }

  /**
   * Provides access to the Slow Down button so a controller can attach behavior.
   *
   * @return the button used to decrease simulation speed
   */
  public Button getMySlowDownButton() {
    return mySlowDownButton;
  }

  /**
   * Provides access to the Save button so a controller can attach behavior.
   *
   * @return the button used to save simulation state
   */
  public Button getMySaveButton(){
    return mySaveButton;
  }

  /**
   * Provides access to Step button so a controller can attach behavior.
   *
   * @return the button used to visually advance the simulation one generation
   */
  public Button getMyForwardButton(){
    return myForwardButton;
  }

  /**
   * Provides access to Step button so a controller can attach behavior.
   *
   * @return the button used to visually rewind the simulation one generation
   */
  public Button getMyBackButton() { return myBackButton; }


  /**
   * Returns the current grid view used to render the simulation.
   *
   * Purpose:
   * Allows the controller to trigger rendering without needing to know how the grid is displayed.
   *
   * @return the current GridView, or null if no grid has been set
   */
  public GridView getMyGridView() {
    return myGridView;
  }

  /**
   * Creates and installs a new GridView with the given dimensions and cell size.
   *
   * Purpose:
   * Replaces the current grid display with one sized to the current simulation, then updates the
   * center UI region to show it.
   *
   * Assumptions:
   * - rows and cols describe the simulation dimensions that will be rendered.
   * - cellSize is a positive pixel value for each cell.
   *
   * @param rows number of rows in the grid
   * @param cols number of columns in the grid
   * @param cellSize size (in pixels) of each cell’s width and height
   */
  public void setGrid(int rows, int cols, double cellSize) {
    this.myGridView = new GridPaneView(rows, cols, cellSize);
    buildCenter();
  }


  /**
   * Provides access to the Reset button so a controller can attach behavior.
   *
   * @return the button used to clear and reset the grid to its initial configuration
   */
  public Button getMyResetButton() {
    return myResetButton;
  }
}
