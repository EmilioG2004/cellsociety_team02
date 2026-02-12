package cellsociety.View;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import java.io.File;

public class SimulationView {

  private final BorderPane myRoot = new BorderPane();
  private double mySpeedMultiplier = 1;

  private final Label myTitle = new Label("Cellular Automata Simulator");
  private final Button myUploadButton = new Button("Upload XML Configuartion");
  private final Button myForwardButton = new Button("Step Forward");
  private final Button myBackButton = new Button("Step Backward");
  private final Button myStartButton = new Button("Start Simulation");
  private final Button myPauseButton = new Button("Pause Simulation");
  private final Button mySpeedUpButton = new Button ("Speed Up Simulation");
  private final Button mySlowDownButton = new Button("Slow Down Simulation");
  private final Button mySaveButton = new Button("Save State");
  private final Button myResetButton = new Button("Reset Grid");
  private final Button myChangeDirectionButton = new Button("Change Direction");
  private final Label mySpeedLabel = new Label("Current Speed: 1x");
  private final Label myStateStackSizeLabel = new Label("State Stack Size: 0");
  private StackPane centerPane;

  private final double myCellSize = 20.0;

  private GridView myGridView;

  private boolean isRunning;

  public SimulationView(){
    buildTop();
    isRunning = false;

    myRoot.setPadding(new Insets(12));
    myRoot.setStyle("-fx-background-color: #f6f7fb;");
  }

  private void buildTop(){
    myTitle.setFont(Font.font(28));
    VBox.setMargin(myTitle, new Insets(8,0,6,0));
    myTitle.setMaxWidth(Double.MAX_VALUE);
    myTitle.setAlignment(Pos.CENTER);

    HBox myUploadRow = new HBox(myUploadButton);
    myUploadRow.setAlignment(Pos.CENTER_LEFT);
    myUploadRow.setPadding(new Insets(6,0,10,0));

    HBox myControlBar = buildControlBar();
    VBox myTopBox = new VBox(myTitle, myUploadRow, myControlBar);
    myTopBox.setSpacing(8);
    myRoot.setTop(myTopBox);
  }

  private HBox buildControlBar(){
    HBox leftBox = new HBox(12, myStartButton, myPauseButton, mySpeedUpButton, mySlowDownButton, mySaveButton, myResetButton, myForwardButton, myBackButton, myChangeDirectionButton, mySpeedLabel, myStateStackSizeLabel);
    leftBox.setAlignment(Pos.CENTER_LEFT);

    Label configLabel = new Label("Configuration: ");
    HBox rightBox = new HBox(10, configLabel);

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    HBox bar = new HBox(12, leftBox, spacer, rightBox);
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
      myRoot.setCenter(centerPane);
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
    chooser.getExtensionFilters().add(
        new FileChooser.ExtensionFilter("XML Files", "*.xml")
    );

    Window w = myRoot.getScene() == null ? null : myRoot.getScene().getWindow();
    return chooser.showOpenDialog(w);
  }

  public Parent getRoot(){
    return myRoot;
  }

  public boolean isRunning() {
    return isRunning;
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

  public Label getMyStateStackSizeLabel(){
    return myStateStackSizeLabel;
  }

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
