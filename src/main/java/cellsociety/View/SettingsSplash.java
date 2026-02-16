package cellsociety.View;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class SettingsSplash extends StackPane {
  private final ChoiceBox<String> myLanguages = new ChoiceBox<>();
  private final ChoiceBox<String> myThemes = new ChoiceBox<>();
  private final Button myApplyChanges = new Button("Apply Changes");
  private final Button myUploadButton = new Button("Upload Configuration");
  private final Button myLoadButton = new Button("Load Simulation");
  public SettingsSplash(double width, double height){
    setPrefSize(width, height);
    setStyle("""
            -fx-background-color: white;
            -fx-text-fill: black;
        """);
    buildDropdowns();
    buildDialog();
  }

  private void buildDialog(){
    getChildren().clear();
    Text title = new Text("Cellular Automata");
    Text subtitle = new Text("Settings");
    VBox myPane = new VBox(12, title, subtitle, myLanguages, myThemes, myUploadButton, myLoadButton, myApplyChanges);
    myPane.setPadding(new Insets(20,20,20,20));
    myPane.setAlignment(Pos.CENTER);

    getChildren().add(myPane);
  }

  public String getChoice(ChoiceBox<String> choiceBox){
    return choiceBox.getValue();
  }

  public void buildDropdowns(){
    myLanguages.getItems().addAll("English", "Spanish", "Portuguese");
    myThemes.getItems().addAll("Light", "Dark", "Duke");
  }

  public Button getMyApplyChanges(){ return myApplyChanges; }

  public Button getMyUploadButton() { return myUploadButton; }

}
