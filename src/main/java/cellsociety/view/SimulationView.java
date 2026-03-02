package cellsociety.view;

import static cellsociety.view.constants.BundleKeys.*;

import cellsociety.config.SimulationConfig;
import java.util.ResourceBundle;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;
import java.io.File;

/**
 * Primary JavaFX view for interacting with a single simulation.
 *
 * <p>Purpose: constructs the main application layout (title, upload row, control bar,
 * center grid card, and optional dialogs) and exposes accessors for controller wiring.</p>
 *
 * <p>Assumptions: a valid {@link ResourceBundle} is supplied; controllers will call
 * {@link #setGrid(int, int)} before attempting to render, and apply themes on a live {@link Scene}.</p>
 *
 * <p>Dependencies: {@link ButtonManager} for controls, JavaFX layout panes,
 * {@link GridView} implementations for rendering, and dialog components
 * {@link AboutDialog} and {@link SettingsSplash}.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * SimulationView view = new SimulationView(bundle);
 * Scene scene = new Scene(view.getRoot());
 * stage.setScene(scene);
 * }</pre>
 *
 * <p>Other details: this class is UI-only; controllers own simulation logic and event wiring.</p>
 *
 * @author Emilio Garcia
 */
public class SimulationView {
  public static final String DATA_FILE_FOLDER = System.getProperty("user.dir") + "/data";
  private static final String BASE_CSS =
      SimulationView.class.getResource("/cellsociety/stylesheets/base.css").toExternalForm();

  private static final String LIGHT_CSS =
      SimulationView.class.getResource("/cellsociety/stylesheets/light.css").toExternalForm();

  private static final String DARK_CSS =
      SimulationView.class.getResource("/cellsociety/stylesheets/dark.css").toExternalForm();

  private static final String DUKE_CSS =
      SimulationView.class.getResource("/cellsociety/stylesheets/duke.css").toExternalForm();

  private ResourceBundle myBundle;
  private final ButtonManager myButtons;

  private final StackPane myRoot = new StackPane();
  private final BorderPane myMain = new BorderPane();

  //TODO: pull from backend
  private double mySpeedMultiplier;

  private StackPane centerPane;
  private final AboutDialog myAboutDialog = new AboutDialog();
  private final SettingsSplash mySettings = new SettingsSplash();

  private GridView myGridView;

  // Multi-sim dashboard UI
  private VBox myMultiRoot;
  private TilePane myTile;
  private ScrollPane myScroll;
  private ChoiceBox<Integer> mySlotChoice;
  private Label mySlotLabel;

  /**
   * Creates a SimulationView with optional initial settings splash.
   *
   * <p>Purpose: allows a startup flow where settings are shown before any simulation is loaded.</p>
   *
   * <p>Assumptions: {@code bundle} contains all required keys.</p>
   *
   * <p>Exceptions: {@link java.util.MissingResourceException} if required keys are missing.</p>
   *
   * <p>Return value: none.</p>
   *
   * @param bundle localization bundle for UI strings
   */
  public SimulationView(ResourceBundle bundle){
    myBundle = bundle;
    myButtons = new ButtonManager(myBundle);
    myRoot.getStyleClass().add("app-root");
    myMain.getStyleClass().add("main-pane");
    mySpeedMultiplier = 1.0;
    applyLanguage(myBundle);
    buildTop();
    buildCenter();
    myMain.setPadding(new Insets(12));
    myRoot.getChildren().addAll(myMain, mySettings);
    bindSplashToWindow();
    openSettings();
  }

  private void bindSplashToWindow() {
    mySettings.prefWidthProperty().bind(myRoot.widthProperty());
    mySettings.prefHeightProperty().bind(myRoot.heightProperty());
    mySettings.maxWidthProperty().bind(myRoot.widthProperty());
    mySettings.maxHeightProperty().bind(myRoot.heightProperty());

    myAboutDialog.prefWidthProperty().bind(myRoot.widthProperty());
    myAboutDialog.prefHeightProperty().bind(myRoot.heightProperty());
    myAboutDialog.maxWidthProperty().bind(myRoot.widthProperty());
    myAboutDialog.maxHeightProperty().bind(myRoot.heightProperty());
  }

  private void buildTop(){
    Label myTitle = buildTitle();
    HBox myUploadRow = buildUploadRow();
    HBox myControlBar = buildControlBar();

    VBox myTopBox = new VBox(myTitle, myUploadRow, myControlBar);
    myTopBox.setSpacing(8);
    myMain.setTop(myTopBox);
  }

  private Label buildTitle() {
    Label myTitle = myButtons.getTitleLabel();
    myTitle.setFont(Font.font(28));
    VBox.setMargin(myTitle, new Insets(8,0,6,0));
    myTitle.setMaxWidth(Double.MAX_VALUE);
    myTitle.setAlignment(Pos.CENTER);
    return myTitle;
  }

  private HBox buildUploadRow() {
    HBox myUploadRow = new HBox(12);
    myUploadRow.getChildren().addAll(myButtons.getUploadRowButtons());
    myUploadRow.setAlignment(Pos.CENTER_LEFT);
    myUploadRow.setPadding(new Insets(6,10,10,10));
    myUploadRow.getStyleClass().add("upload-row");
    return myUploadRow;
  }

  private HBox buildControlBar(){
    HBox leftBox = new HBox(12);
    leftBox.getChildren().addAll(myButtons.getControlBarLeftNodes());
    leftBox.setAlignment(Pos.CENTER_LEFT);

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    HBox bar = new HBox(12, leftBox, spacer);
    bar.setAlignment(Pos.CENTER_LEFT);
    bar.setPadding(new Insets(14));
    bar.setMinHeight(64);
    bar.getStyleClass().add("control-bar");

    return bar;
  }

  private void buildCenter(){
    if (centerPane == null) {
      centerPane = new StackPane();
      centerPane.setPadding(new Insets(18,0,18,0));
      myMain.setCenter(centerPane);
    }
    if (myMultiRoot == null) {
      buildMultiSimDashboard();
    }
    setCenterContent(myMultiRoot);
  }

  private void buildMultiSimDashboard() {
    myTile = new TilePane();
    myTile.setPadding(new Insets(12));
    myTile.setHgap(12);
    myTile.setVgap(12);
    myTile.setPrefColumns(2);
    myTile.setPrefTileWidth(420);
    myTile.setPrefTileHeight(420);

    myScroll = new ScrollPane(myTile);
    myScroll.setFitToWidth(true);
    myScroll.setFitToHeight(true);
    myScroll.getStyleClass().add("card-scroll");

    mySlotChoice = new ChoiceBox<>();

    mySlotLabel = new Label(myBundle.getString(LABEL_SLOTS));
    HBox header = new HBox(8, mySlotLabel, mySlotChoice);
    header.setPadding(new Insets(4, 6, 8, 6));

    myMultiRoot = new VBox(8, header, myScroll);
    myMultiRoot.setPadding(new Insets(4));
  }

  private StackPane buildCenterCard() {
    StackPane card = new StackPane();
    card.setPadding(new Insets(18));
    card.prefWidthProperty().bind(centerPane.widthProperty());
    card.prefHeightProperty().bind(centerPane.heightProperty());

    card.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    card.getStyleClass().add("card");
    card.setAlignment(Pos.CENTER);
    return card;
  }

  private void buildBottom(){
    HBox bottomBox = new HBox();
    bottomBox.getChildren().addAll(myButtons.getRotateLeftButton(), myButtons.getRotateRightButton());
    bottomBox.setAlignment(Pos.BOTTOM_CENTER);
    myMain.setBottom(bottomBox);
  }

  /**
   * Shows a file chooser for opening a simulation configuration.
   *
   * <p>Purpose: provides a UI entry point for loading XML configuration files.</p>
   *
   * <p>Assumptions: the {@link Scene} is attached to a {@link Window}.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * @return selected XML file, or {@code null} if the user cancels
   */
  public File showOpenConfigDialog() {
    FileChooser chooser = new FileChooser();
    chooser.setTitle(myBundle.getString(FILE_OPEN_CONFIG_TITLE));
    chooser.setInitialDirectory(new File(DATA_FILE_FOLDER));
    chooser.getExtensionFilters().add(
        new FileChooser.ExtensionFilter(myBundle.getString(FILE_FILTER_XML), "*.xml")
    );

    Window w = myMain.getScene() == null ? null : myMain.getScene().getWindow();
    return chooser.showOpenDialog(w);
  }

  /**
   * Shows a file chooser for saving a simulation configuration.
   *
   * <p>Purpose: provides a UI entry point for exporting the current state.</p>
   *
   * <p>Assumptions: the {@link Scene} is attached to a {@link Window}.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * @return destination XML file, or {@code null} if the user cancels
   */
  public File showSaveConfigDialog(){
    FileChooser chooser = new FileChooser();
    chooser.setTitle(myBundle.getString(FILE_SAVE_CONFIG_TITLE));
    chooser.getExtensionFilters().add(
        new ExtensionFilter(myBundle.getString(FILE_FILTER_XML), "*.xml")
    );

    Window w = myMain.getScene() == null ? null : myMain.getScene().getWindow();
    return chooser.showSaveDialog(w);
  }

  /**
   * Displays the "About" dialog for a specific configuration.
   *
   * <p>Purpose: surface simulation metadata without leaving the main view.</p>
   *
   * <p>Assumptions: {@code config} is non-null.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   *
   * @param config configuration used to populate the dialog
   */
  public void showAboutSplash(SimulationConfig config){
    if (centerPane == null){
      centerPane = new StackPane();
      centerPane.setPadding(new Insets(18,0,18,0));
      myMain.setCenter(centerPane);
    }
    myAboutDialog.generateInfo(config);

    centerPane.getChildren().add(myAboutDialog);

    StackPane.setAlignment(myAboutDialog, Pos.CENTER);
  }

  /**
   * Hides the settings splash and removes it from layout calculations.
   *
   * <p>Purpose: returns focus to the main simulation UI.</p>
   *
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   */
  public void closeSettings(){
    mySettings.setVisible(false);
    mySettings.setManaged(false);
  }

  /**
   * Shows the settings splash and makes it participate in layout.
   *
   * <p>Purpose: allows the user to change language/theme or upload a config.</p>
   *
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   */
  public void openSettings(){
    mySettings.setVisible(true);
    mySettings.setManaged(true);
  }

  /**
   * Returns the root node for scene construction.
   *
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * @return root JavaFX node for this view
   */
  public Parent getRoot(){ return myRoot; }

  /**
   * Returns the settings splash component.
   *
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * @return settings splash UI
   */
  public SettingsSplash getMySettings() { return mySettings; }

  /**
   * Returns the about dialog component.
   *
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * @return about dialog UI
   */
  public AboutDialog getMyAboutDialog() { return myAboutDialog; }

  /**
   * Returns the current grid view instance.
   *
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * @return grid view, or {@code null} if not yet created
   */
  public GridView getMyGridView() { return myGridView; }

  /**
   * Doubles the stored speed multiplier.
   *
   * <p>Purpose: keeps UI speed label in sync with controller speed changes.</p>
   *
   * <p>Assumptions: current multiplier is positive.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   */
  public void doubleMySpeed() { this.mySpeedMultiplier *= 2; }

  /**
   * Halves the stored speed multiplier.
   *
   * <p>Purpose: keeps UI speed label in sync with controller speed changes.</p>
   *
   * <p>Assumptions: current multiplier is positive.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   */
  public void halveMySpeed() { this.mySpeedMultiplier /= 2; }

  /**
   * Refreshes the speed label using the current multiplier.
   *
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   */
  public void updateSpeedLabel() { myButtons.setSpeedLabel(mySpeedMultiplier); }

  /**
   * Applies a new language bundle to all view components.
   *
   * <p>Purpose: updates all visible strings without rebuilding the UI.</p>
   *
   * <p>Assumptions: {@code bundle} contains required keys.</p>
   *
   * <p>Exceptions: {@link java.util.MissingResourceException} if required keys are missing.</p>
   *
   * <p>Return value: none.</p>
   *
   * @param bundle localization bundle for UI strings
   */
  public void applyLanguage(ResourceBundle bundle) {
    myBundle = bundle;
    myButtons.applyLanguage(bundle);
    mySettings.applyLanguage(bundle);
    myAboutDialog.applyLanguage(bundle);
    updateSpeedLabel();
    if (mySlotLabel != null) {
      mySlotLabel.setText(bundle.getString(LABEL_SLOTS));
    }
  }

  /**
   * Applies the selected theme stylesheet to the scene.
   *
   * <p>Purpose: changes the application's visual theme without recreating nodes.</p>
   *
   * <p>Assumptions: {@code scene} is non-null and attached to a Stage.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   *
   * @param scene JavaFX scene to update
   * @param themeChoice theme name (e.g., "Light", "Dark", "Duke")
   */
  public void applyTheme(Scene scene, String themeChoice) {
    String themeCss = switch (themeChoice) {
      case "Dark" -> DARK_CSS;
      case "Duke" -> DUKE_CSS;
      default -> LIGHT_CSS;
    };
    scene.getStylesheets().setAll(BASE_CSS, themeCss);
  }

  /**
   * Returns the resource bundle used by this view.
   *
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * @return current localization bundle
   */
  public ResourceBundle getMyBundle() { return myBundle; }

  /**
   * Returns the button manager that owns the UI controls.
   *
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * @return button manager
   */
  public ButtonManager getMyButtons() { return myButtons; }

  /**
   * Returns the current speed multiplier tracked by the view.
   *
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * @return speed multiplier used for display
   */
  public double getMySpeedMultiplier() { return mySpeedMultiplier; }

  /**
   * Replaces the center content with a provided node wrapped in a card.
   *
   * <p>Purpose: allows controllers to swap the center display (e.g., multi-sim dashboard).</p>
   *
   * <p>Assumptions: {@code content} is non-null.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   *
   * @param content node to display in the center card
   */
  public void setCenterContent(Node content) {
    if (centerPane == null) {
      centerPane = new StackPane();
      centerPane.setPadding(new Insets(18,0,18,0));
      myMain.setCenter(centerPane);
    }
    StackPane card = buildCenterCard();
    card.getChildren().setAll(content);
    centerPane.getChildren().setAll(card);
  }

  /**
   * Returns the TilePane used by the multi-sim dashboard.
   *
   * <p>Assumptions: the dashboard has been built.</p>
   *
   * @return multi-sim TilePane
   */
  public TilePane getMultiSimTile() {
    return myTile;
  }

  /**
   * Returns the slot count choice box used by the multi-sim dashboard.
   *
   * <p>Assumptions: the dashboard has been built.</p>
   *
   * @return slot count choice box
   */
  public ChoiceBox<Integer> getMultiSimSlotChoice() {
    return mySlotChoice;
  }

}
