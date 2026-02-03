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

  private final Label myTitle = new Label("Cellular Automata Simulator");
  private final Button myUploadButton = new Button("Upload XML Configuartion");

  private final Button myStartButton = new Button("Start Simulation");
  private final Button myPauseButton = new Button("Pause Simulation");
  private final Button mySpeedUpButton = new Button ("Speed Up Simulation");
  private final Button mySlowDownButton = new Button("Slow Down Simulation");
  private final Button mySaveButton = new Button("Save State");
  private final Label mySpeedLabel = new Label("Current Speed: 1x");

  private final int myCols = 8;
  private final int myRows = 8;
  private final double myCellSize = 20.0;

  private final GridView myGridView = new GridPaneView(myRows, myCols, myCellSize);

  private boolean isRunning;

  public SimulationView(){
    buildTop();
    buildCenter();
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
    HBox leftBox = new HBox(12, myStartButton, myPauseButton, mySpeedUpButton, mySlowDownButton, mySaveButton, mySpeedLabel);
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
    StackPane card = new StackPane();
    card.setPadding(new Insets(18));
    card.setPrefSize(760,520);
    card.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

    card.setStyle("""
            -fx-background-color: white;
            -fx-border-color: black;
        """);

    Node GridNode = myGridView.getNode();
    card.getChildren().add(GridNode);

    StackPane center = new StackPane(card);
    center.setPadding(new Insets(18,0,18,0));
    myRoot.setCenter(center);
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

  public Button getMySaveButton(){
    return mySaveButton;
  }

  public GridView getMyGridView() { return myGridView; }
}
