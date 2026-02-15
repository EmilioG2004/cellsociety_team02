package cellsociety.View;

import cellsociety.Main;
import cellsociety.config.SimulationConfig;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class AboutDialog extends StackPane{
  private final Button myCloseButton = new Button("Close Panel");
  public AboutDialog(double width, double height){
    setPrefSize(width, height);
    setStyle("""
            -fx-background-color: white;
            -fx-text-fill: black;
        """);

  }

  public void generateInfo(SimulationConfig config){
    getChildren().clear();
    Text title = new Text("About This Configuration");
    Text info = new Text("Simulation Type: " + config.getSimulationType() +"\n\n"
    + config.getTitle() + "\n\n"
    + "Author: " + config.getAuthor() + "\n\n"
    + config.getDescription() + "\n\n"
    + "Parameters: " + config.getParameters());
    VBox infoPanel = new VBox(10, title, info, myCloseButton);
    infoPanel.setAlignment(Pos.TOP_CENTER);

    getChildren().add(infoPanel);
  }

  public void closeInfo() {
    if (getParent() instanceof Pane p) {
      p.getChildren().remove(this);
    }
  }

  public Button getMyCloseButton() { return myCloseButton; }

}
