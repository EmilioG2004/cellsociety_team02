package cellsociety.View;

import cellsociety.Main;
import cellsociety.config.SimulationConfig;
import java.util.Stack;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;
import java.io.File;

public class SimulationView {
  public static final String DATA_FILE_FOLDER = System.getProperty("user.dir") + "/data";


  private final StackPane myRoot = new StackPane();
  private final BorderPane myMain = new BorderPane();
  private double mySpeedMultiplier = 1;

  private final Label myTitle = new Label("Cellular Automata Simulator");
  private final Button myUploadButton = new Button("Upload XML Configuration");
  private final Button myForwardButton = new Button("Step Forward");
  private final Button myBackButton = new Button("Step Backward");
  private final Button myStartButton = new Button("Start Simulation");
  private final Button myPauseButton = new Button("Pause Simulation");
  private final Button mySpeedUpButton = new Button ("Speed Up Simulation");
  private final Button mySlowDownButton = new Button("Slow Down Simulation");
  private final Button mySaveButton = new Button("Save State");
  private final Button myResetButton = new Button("Reset Grid");
  private final Button myChangeDirectionButton = new Button("Forward");
  private final Label mySpeedLabel = new Label("Current Speed: 1x");
  private final Label myStateStackSizeLabel = new Label("Generation: 0");
  private final Button myAboutButton = new Button("About This Configuration");
  private final Button myToggleOutlineButton = new Button("Toggle Outlines");
  private final Button mySettingsButton = new Button("Settings");
  private StackPane centerPane;
  private final AboutDialog myAboutDialog = new AboutDialog(Main.APP_WIDTH, Main.APP_HEIGHT);
  private final SettingsSplash mySettings = new SettingsSplash(Main.APP_WIDTH, Main.APP_HEIGHT);


  private final double myCellSize = 20.0;

  private GridView myGridView;

  public SimulationView(){
    buildTop();
    myMain.setPadding(new Insets(12));
    myMain.setStyle("-fx-background-color: #f6f7fb;");

    myRoot.getChildren().addAll(myMain, mySettings);

    mySettings.prefWidthProperty().bind(myRoot.widthProperty());
    mySettings.prefHeightProperty().bind(myRoot.heightProperty());
    mySettings.maxWidthProperty().bind(myRoot.widthProperty());
    mySettings.maxHeightProperty().bind(myRoot.heightProperty());
  }

  private void buildTop(){
    myTitle.setFont(Font.font(28));
    VBox.setMargin(myTitle, new Insets(8,0,6,0));
    myTitle.setMaxWidth(Double.MAX_VALUE);
    myTitle.setAlignment(Pos.CENTER);

    HBox myUploadRow = new HBox(myUploadButton, myAboutButton, mySettingsButton);
    myUploadRow.setAlignment(Pos.CENTER_LEFT);
    myUploadRow.setPadding(new Insets(6,10,10,10));

    HBox myControlBar = buildControlBar();
    VBox myTopBox = new VBox(myTitle, myUploadRow, myControlBar);
    myTopBox.setSpacing(8);
    myMain.setTop(myTopBox);
  }

  private HBox buildControlBar(){
    HBox leftBox = new HBox(12, myStartButton, myPauseButton, mySpeedUpButton, mySlowDownButton, mySaveButton, myResetButton, myForwardButton, myBackButton, myChangeDirectionButton, myToggleOutlineButton, mySpeedLabel, myStateStackSizeLabel);
    leftBox.setAlignment(Pos.CENTER_LEFT);

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    HBox bar = new HBox(12, leftBox, spacer);
    bar.setAlignment(Pos.CENTER_LEFT);
    bar.setPadding(new Insets(14));
    bar.setMinHeight(64);

    bar.setStyle("""
            -fx-background-color: white;
            -fx-border-color: black;
        """);

    myStartButton.setStyle("""
            -fx-background-color: black;
            -fx-text-fill: white;
        """);



    return bar;
  }

  private void buildCenter(){
    if (myGridView == null) return;
    if (centerPane == null) {
      centerPane = new StackPane();
      centerPane.setPadding(new Insets(18,0,18,0));
      myMain.setCenter(centerPane);
    }

    StackPane card = new StackPane();
    card.setPadding(new Insets(18));
    card.setPrefSize(760,520);
    card.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

    card.setStyle("""
      -fx-background-color: white;
      -fx-border-color: black;
  """);

    Node gridNode = myGridView.getNode();
    card.getChildren().setAll(gridNode);
    centerPane.getChildren().setAll(card);
  }

  public File showOpenConfigDialog() {
    FileChooser chooser = new FileChooser();
    chooser.setTitle("Open Simulation Configuration");
    chooser.setInitialDirectory(new File(DATA_FILE_FOLDER));
    chooser.getExtensionFilters().add(
        new FileChooser.ExtensionFilter("XML Files", "*.xml")
    );

    Window w = myMain.getScene() == null ? null : myMain.getScene().getWindow();
    return chooser.showOpenDialog(w);
  }

  public File showSaveConfigDialog(){
    FileChooser chooser = new FileChooser();
    chooser.setTitle("Open Simulation Configuration");
    chooser.getExtensionFilters().add(
        new ExtensionFilter("XML Files", "*.xml")
    );

    Window w = myMain.getScene() == null ? null : myMain.getScene().getWindow();
    return chooser.showSaveDialog(w);
  }

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

  public void closeSettings(){
    mySettings.setVisible(false);
    mySettings.setManaged(false);
  }

  public void openSettings(){
    mySettings.setVisible(true);
    mySettings.setManaged(true);
  }

  public Parent getRoot(){
    return myRoot;
  }

  public Button getMyUploadButton(){
    return myUploadButton;
  }

  public Button getMyStartButton(){
    return myStartButton;
  }

  public Button getMyPauseButton(){
    return myPauseButton;
  }

  public Button getMySpeedUpButton(){
    return mySpeedUpButton;
  }

  public Button getMySlowDownButton(){
    return mySlowDownButton;
  }

  public Button getMyForwardButton(){
    return myForwardButton;
  }

  public Button getMyBackButton(){
    return myBackButton;
  }

  public Button getMyChangeDirectionButton(){
    return myChangeDirectionButton;
  }

  public Button getMySaveButton(){
    return mySaveButton;
  }

  public Button getMyAboutButton(){
    return myAboutButton;
  }

  public Button getMyToggleOutlineButton() { return myToggleOutlineButton; }

  public Button getMySettingsButton() { return mySettingsButton; }

  public Label getMyStateStackSizeLabel(){
    return myStateStackSizeLabel;
  }

  public SettingsSplash getMySettings() { return mySettings; }

  public AboutDialog getMyAboutDialog() { return myAboutDialog; }

  public GridView getMyGridView() { return myGridView; }

  public void setGrid(int rows, int cols, double cellSize){
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

  public double getMySpeedMultiplier(){
    return mySpeedMultiplier;
  }

  public void doubleMySpeed() { this.mySpeedMultiplier *= 2; }

  public void halveMySpeed() { this.mySpeedMultiplier /= 2; }

  public void updateSpeedLabel(){
    mySpeedLabel.setText("Current Speed: " + mySpeedMultiplier + "x");
  }
}
