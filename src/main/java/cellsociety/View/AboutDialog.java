package cellsociety.View;

import cellsociety.Main;
import cellsociety.config.SimulationConfig;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class AboutDialog extends StackPane{
  private SimulationConfig myConfig;
  private ResourceBundle myBundle;
  private final Button myCloseButton;
  private final Text myTitle;
  private final Text myType;
  private final Text myAuthor;
  private final Text myDescription;
  private final Text myParameters;

  public AboutDialog(){
    setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    myCloseButton = new Button();
    myTitle = new Text();
    myType = new Text();
    myAuthor = new Text();
    myDescription = new Text();
    myParameters = new Text();
    setStyle("""
            -fx-background-color: white;
            -fx-text-fill: black;
        """);

  }

  public void generateInfo(SimulationConfig config){
    myConfig = config;

    getChildren().clear();
    VBox infoPanel = new VBox(10, myTitle, myType, myAuthor, myDescription, myParameters, myCloseButton);
    infoPanel.setAlignment(Pos.TOP_CENTER);

    getChildren().add(infoPanel);

    if (myBundle != null) {
      applyLanguage(myBundle);
    }
  }

  public void closeInfo() {
    if (getParent() instanceof Pane p) {
      p.getChildren().remove(this);
    }
  }

  public Button getMyCloseButton() { return myCloseButton; }

  public void applyLanguage(ResourceBundle bundle){
    myBundle = bundle;
    myTitle.setText(bundle.getString("about.title"));
    myCloseButton.setText(bundle.getString("button.closePanel"));

    if (myConfig == null) return;

    myType.setText(MessageFormat.format(bundle.getString("about.simulationType"), myConfig.getSimulationType()));
    myDescription.setText(MessageFormat.format(bundle.getString("about.description"), myConfig.getDescription()));
    myAuthor.setText(MessageFormat.format(bundle.getString("about.author"), myConfig.getAuthor()));
    myParameters.setText(MessageFormat.format(bundle.getString("about.parameters"), myConfig.getParameters()));
  }

}
