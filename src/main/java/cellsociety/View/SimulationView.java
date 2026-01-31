package cellsociety.View;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

public class SimulationView {

  private final BorderPane myRoot = new BorderPane();

  private final Label myTitle = new Label("Cellular Automata Simulator");
  private final Button myUploadButton = new Button("Upload XML Configuartion");

  private final Button myStartButton = new Button("Start Simulation");
  private final Button mySpeedUpButton = new Button ("Speed Up Simulation");
  private final Button mySlowDownButton = new Button("Slow Down Simulation");
  private final Button mySaveButton = new Button("Save State");
  private final Label mySpeedLabel = new Label("Current Speed: 1x");

  private final Region myGridView = new Region();

  public SimulationView(){
    buildTop();
    buildCenter();

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
    HBox leftBox = new HBox(12, myStartButton, mySpeedUpButton, mySlowDownButton, mySaveButton, mySpeedLabel);
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

    myGridView.setPrefSize(720,480);
    myGridView.setStyle("-fx-background-color: #f9fafb; -fx-background-radius: 10; -fx-border-color: #eef0f6; -fx-border-radius: 10;");
    card.getChildren().add(myGridView);

    StackPane center = new StackPane(card);
    center.setPadding(new Insets(18,0,18,0));
    myRoot.setCenter(center);
  }

  public Parent getRoot(){ return myRoot; }


}
